package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import mate.academy.model.Book;
import mate.academy.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class BookRepositoryTest {
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Fantasy");
        category.setDescription("Magic and dragons");
        savedCategory = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Book Title");
        book.setAuthor("Book Author");
        book.setIsbn("1234567890123");
        book.setPrice(BigDecimal.TEN);
        book.setCategories(Set.of(savedCategory));
        bookRepository.save(book);
    }

    @Test
    void findAllByCategoriesId_CategoryHasBooks_ReturnsBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAllByCategoriesId(savedCategory.getId(), pageable);
        assertEquals(1, actual.size());
        assertEquals("Book Title", actual.get(0).getTitle());
    }

    @Test
    void findAllByCategoriesId_CategoryHasNoBooks_ReturnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAllByCategoriesId(999L, pageable);
        assertTrue(actual.isEmpty());
    }
}