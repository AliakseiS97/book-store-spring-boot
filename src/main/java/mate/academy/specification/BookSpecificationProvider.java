package mate.academy.specification;

import java.util.Arrays;
import mate.academy.model.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecificationProvider {

    public static Specification<Book> hasTitleIn(String[] title) {
        return ((root, query, criteriaBuilder) ->
                root.get("title").in(Arrays.asList(title))
            );
    }

    public static Specification<Book> hasAuthorIn(String[] author) {
        return ((root, query, criteriaBuilder) ->
                root.get("author").in(Arrays.asList(author))
            );
    }

    public static Specification<Book> hasIsbnIn(String[] isbn) {
        return ((root, query, criteriaBuilder) ->
                root.get("isbn").in(Arrays.asList(isbn))
            );
    }
}
