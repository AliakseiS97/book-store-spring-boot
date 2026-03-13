package mate.academy.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.request.CreateBookRequestDto;
import mate.academy.dto.response.BookDto;
import mate.academy.service.BookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @RequestMapping
    public List<BookDto> getAll() {
        return bookService.getAll();
    }

    @RequestMapping("/{id}")
    public BookDto getBookById(@RequestParam long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public BookDto createBook(@RequestParam CreateBookRequestDto bookDto) {
        return bookService.createBook(bookDto);
    }
}
