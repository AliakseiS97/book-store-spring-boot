package mate.academy.specification;

import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import mate.academy.model.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecificationProvider {

    public static Specification<Book> hasTitleIn(String[] title) {
        return ((root, query, criteriaBuilder) -> {
            Predicate[] predicates = Arrays.stream(title)
                    .map(t -> criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            "%" + t.toLowerCase() + "%"
                    ))
                    .toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        });
    }

    public static Specification<Book> hasAuthorIn(String[] author) {
        return ((root, query, criteriaBuilder) -> {
            Predicate[] predicates = Arrays.stream(author)
                    .map(a -> criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("author")),
                            "%" + a.toLowerCase() + "%"
                    ))
                    .toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        });
    }

    public static Specification<Book> hasIsbnIn(String[] isbn) {
        return ((root, query, criteriaBuilder) -> {
            Predicate[] predicates = Arrays.stream(isbn)
                    .map(i -> criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("isbn")),
                            "%" + i.toLowerCase() + "%"))
                    .toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        });
    }
}
