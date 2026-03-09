package mate.academy;

import java.math.BigDecimal;
import mate.academy.model.Book;
import mate.academy.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    CommandLineRunner run(BookService bookService) {
        return args -> {
            Book book = new Book();
            book.setTitle("Lord of the Rings");
            book.setAuthor("John Ronald Reuel Tolkien");
            book.setIsbn("9788845292613");
            book.setPrice(BigDecimal.valueOf(371.06));
            book.setDescription("An epic fantasy saga following Frodo Baggins and the Fellowship");

            bookService.save(book);

            System.out.println("Book saved! Total books: " + bookService.findAll().size());
        };
    }

}
