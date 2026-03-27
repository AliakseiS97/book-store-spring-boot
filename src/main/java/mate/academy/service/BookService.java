package mate.academy.service;

import java.util.List;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;

public interface BookService {

    BookDto save(CreateBookRequestDto book);

    List<BookDto> getAll();

    BookDto getBookById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void delete(Long id);
}
