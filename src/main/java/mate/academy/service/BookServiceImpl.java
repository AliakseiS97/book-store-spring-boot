package mate.academy.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toModel(bookDto);
        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();

    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book not found with id: " + id));
        return bookMapper.toDto(book);
    }
}
