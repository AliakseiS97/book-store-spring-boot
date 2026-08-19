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
import java.util.List;
import mate.academy.dto.request.CreateCategoryRequestDto;
import mate.academy.dto.response.CategoryDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.security.JwtUtil;
import mate.academy.service.BookService;
import mate.academy.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void getAll_CategoriesExist_ReturnsListOfDtos() throws Exception {
        CategoryDto first = new CategoryDto();
        first.setId(1L);
        first.setName("Fantasy");
        CategoryDto second = new CategoryDto();
        second.setId(2L);
        second.setName("Sci-Fi");
        when(categoryService.findAll(any(Pageable.class))).thenReturn(List.of(first, second));
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fantasy"))
                .andExpect(jsonPath("$[1].name").value("Sci-Fi"));
    }

    @Test
    public void getCategoryById_ExistingId_ReturnsDto() throws Exception {
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Fantasy");
        expected.setDescription("Magic and dragons");
        when(categoryService.getById(1L)).thenReturn(expected);
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fantasy"));
    }

    @Test
    public void getCategoryById_NotExistingId_ReturnsNotFound() throws Exception {
        when(categoryService.getById(99L))
                .thenThrow(new EntityNotFoundException("Category with id: 99"));
        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createCategory_ValidRequest_ReturnsDto() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Fantasy");
        requestDto.setDescription("Magic and dragons");
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Fantasy");
        expected.setDescription("Magic and dragons");
        when(categoryService.save(any(CreateCategoryRequestDto.class))).thenReturn(expected);
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fantasy"));
    }

    @Test
    public void deleteCategory_ValidId_CallsService() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk());
        verify(categoryService).deleteById(1L);
    }
}
