package mate.academy.service;

import java.util.List;
import mate.academy.dto.request.BookSearchParametersDto;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookDto save(CreateBookRequestDto book);

    List<BookDto> getAll(Pageable pageable);

    List<BookDto> search(BookSearchParametersDto params);

    BookDto getBookById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void delete(Long id);
}
