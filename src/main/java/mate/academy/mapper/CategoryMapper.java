package mate.academy.mapper;

import mate.academy.dto.response.CategoryDto;
import mate.academy.mapper.config.MappingConfig;
import mate.academy.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryDto categoryDto);
}
