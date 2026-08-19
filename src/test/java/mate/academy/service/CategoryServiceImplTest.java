package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.request.CreateCategoryRequestDto;
import mate.academy.dto.response.CategoryDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    public void findAll_CategoriesExist_ReturnsListOfDtos() {
        Category category = new Category();
        category.setId(1L);
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Category 1");
        expected.setDescription("Description 1");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1L);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        List<CategoryDto> actual = categoryServiceImpl.findAll(pageable);
        assertEquals(1, actual.size());
        assertEquals(expected.getName(), actual.get(0).getName());
    }

    @Test
    public void findAll_CategoriesDoNotExist_ReturnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));
        List<CategoryDto> actual = categoryServiceImpl.findAll(pageable);
        assertEquals(0, actual.size());
    }

    @Test
    public void getById_CategoryExists_ReturnsDto() {
        Category category = new Category();
        category.setId(1L);
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Category 1");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryServiceImpl.getById(1L);
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void getById_CategoryDoesNotExist_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.getById(1L));
    }

    @Test
    public void save_ValidRequestDto_ReturnsDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        Category category = new Category();
        category.setId(1L);
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Category 1");
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryServiceImpl.save(requestDto);
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void update_ValidRequestDto_ReturnsUpdatedDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setDescription("Updated Description");
        Category category = new Category();
        category.setId(1L);
        category.setName("Old Name");
        category.setDescription("Old Description");
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Updated Name");
        expected.setDescription("Updated Description");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryServiceImpl.update(1L, requestDto);
        assertEquals(expected.getName(), actual.getName());
        assertEquals("Updated Name", category.getName());
        assertEquals("Updated Description", category.getDescription());
        verify(categoryRepository).save(category);
    }
}
