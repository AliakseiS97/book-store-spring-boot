package mate.academy.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.response.CategoryDto;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id: " + id)));
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toModel(categoryDto)));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + id)
                );
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + id)
                );
        categoryRepository.delete(category);
    }
}
