package mate.academy.mapper;

import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
