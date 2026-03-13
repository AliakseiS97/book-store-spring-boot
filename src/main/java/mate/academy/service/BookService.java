package mate.academy.service;

import java.util.List;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;

public interface BookService {

    BookDto createBook(CreateBookRequestDto bookDto);

    List<BookDto> getAll();

    BookDto getBookById(Long id);
}
