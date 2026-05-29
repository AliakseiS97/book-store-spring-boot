package mate.academy.mapper;

import java.util.HashSet;
import java.util.Set;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.dto.response.BookDtoWithoutCategoryIds;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.Book;
import mate.academy.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto dto) {
        if (dto.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>();
            for (Long id : dto.getCategoryIds()) {
                Category category = new Category();
                category.setId(id);
                categories.add(category);
            }
            book.setCategories(categories);
        }
    }
}
