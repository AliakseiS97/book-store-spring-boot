package mate.academy.mapper;

import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);
}
