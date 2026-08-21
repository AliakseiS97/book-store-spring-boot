package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
class ShoppingCartRepositoryTest {
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRoles(new HashSet<>());
        savedUser = userRepository.save(user);

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(savedUser);
        shoppingCartRepository.save(cart);
    }

    @Test
    void findByUserId_UserHasCart_ReturnsCart() {
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(savedUser.getId());
        assertTrue(actual.isPresent());
        assertEquals(savedUser.getId(), actual.get().getUser().getId());
    }

    @Test
    void findByUserId_UserHasNoCart_ReturnsEmptyOptional() {
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(999L);
        assertTrue(actual.isEmpty());
    }
}
