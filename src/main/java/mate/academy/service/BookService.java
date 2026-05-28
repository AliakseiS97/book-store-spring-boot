package mate.academy.service;

import java.util.List;
import mate.academy.dto.request.BookSearchParametersDto;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.dto.response.BookDtoWithoutCategoryIds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id, Pageable pageable);

    BookDto save(CreateBookRequestDto book);

    Page<BookDto> getAll(Pageable pageable);

    Page<BookDto> search(BookSearchParametersDto params, Pageable pageable);

    BookDto getBookById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void delete(Long id);
}
