package mate.academy.dto.request;

public record BookSearchParametersDto(
        String[] titles,
        String[] authors,
        String[] isbns
) {}
