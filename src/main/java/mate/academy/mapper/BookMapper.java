package mate.academy.mapper;

import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    mate.academy.model.Book toModel(CreateBookRequestDto requestDto);
}
