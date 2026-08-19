package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.dto.request.BookSearchParametersDto;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Test
    public void getBookById_ExistingId_ReturnsBookDto() {
        Book book = new Book();
        book.setId(1L);
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Book Title");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookServiceImpl.getBookById(1L);
        assertEquals(expected.getTitle(), actual.getTitle());
    }

    @Test
    public void getBookById_NotExistingId_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookServiceImpl.getBookById(1L));
    }

    @Test
    public void getAll_BooksExist_ReturnsAllBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Book first = new Book();
        first.setId(1L);
        Book second = new Book();
        second.setId(2L);
        BookDto firstDto = new BookDto();
        firstDto.setId(1L);
        firstDto.setTitle("Book Title");
        BookDto secondDto = new BookDto();
        secondDto.setId(2L);
        secondDto.setTitle("Book Title2");
        Page<Book> bookPage = new PageImpl<>(List.of(first, second), pageable, 2L);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(first)).thenReturn(firstDto);
        when(bookMapper.toDto(second)).thenReturn(secondDto);
        Page<BookDto> actual = bookServiceImpl.getAll(pageable);
        assertEquals(2, actual.getContent().size());
        assertEquals(firstDto.getTitle(), actual.getContent().get(0).getTitle());
    }

    @Test
    public void getAll_EmptyDb_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(List.of());
        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);
        Page<BookDto> actual = bookServiceImpl.getAll(pageable);
        assertTrue(actual.getContent().isEmpty());
    }

    @Test
    public void save_ValidRequestDto_ReturnsSavedBook() {
        Book book = new Book();
        book.setId(1L);
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Book Title");
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Book Title");
        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookServiceImpl.save(requestDto);
        assertEquals(expected.getTitle(), actual.getTitle());
        verify(bookRepository).save(book);
    }

    @Test
    public void save_NonExistingCategoryIds_ThrowsException() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Set<Long> categoryIds = Set.of(1L, 2L, 3L);
        requestDto.setCategoryIds(categoryIds);
        Book book = new Book();
        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(categoryRepository.findAllById(categoryIds)).thenReturn(List.of());
        assertThrows(EntityNotFoundException.class, () -> bookServiceImpl.save(requestDto));
    }

    @Test
    public void delete_ExistingId_DeletesBook() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookServiceImpl.delete(1L);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    public void delete_NotExistingId_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookServiceImpl.delete(1L));
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    public void search_ValidParameters_ReturnsMatchingBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        String[] titles = new String[]{"Book Title"};
        String[] authors = new String[]{"Book Author"};
        String[] isbns = new String[]{"9999999999"};
        BookSearchParametersDto params = new BookSearchParametersDto(titles, authors, isbns);
        Book book = new Book();
        book.setId(1L);
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Book Title");
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1L);
        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(expected);
        Page<BookDto> actual = bookServiceImpl.search(params, pageable);
        assertEquals(1, actual.getContent().size());
        assertEquals(expected.getTitle(), actual.getContent().get(0).getTitle());
    }
}
