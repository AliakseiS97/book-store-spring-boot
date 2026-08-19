package mate.academy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.security.JwtUtil;
import mate.academy.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookService bookService;
    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void getBookById_ExistingId_ReturnsBookDto() throws Exception {
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Book Title");
        expected.setAuthor("Book Author");
        when(bookService.getBookById(1L)).thenReturn(expected);
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    public void getBookById_NotExistingId_ReturnsNotFound() throws Exception {
        when(bookService.getBookById(99L))
                .thenThrow(new EntityNotFoundException("Book not found with id: 99"));
        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAll_BooksExist_ReturnsPageOfBookDtos() throws Exception {
        BookDto first = new BookDto();
        first.setId(1L);
        first.setTitle("Book Title1");
        BookDto second = new BookDto();
        second.setId(2L);
        second.setTitle("Book Title2");
        Page<BookDto> expected = new PageImpl<>(List.of(first, second));
        when(bookService.getAll(any(Pageable.class))).thenReturn(expected);
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Book Title1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Book Title2"));
    }

    @Test
    public void createBook_ValidRequest_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Book Title");
        requestDto.setAuthor("Book Author");
        requestDto.setIsbn("1234567890123");
        requestDto.setPrice(BigDecimal.TEN);
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Book Title");
        expected.setAuthor("Book Author");
        expected.setIsbn("1234567890123");
        expected.setPrice(BigDecimal.TEN);
        when(bookService.save(any(CreateBookRequestDto.class))).thenReturn(expected);
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book Title"))
                .andExpect(jsonPath("$.isbn").value("1234567890123"));
    }

    @Test
    public void deleteBook_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
        verify(bookService).delete(1L);
    }
}
