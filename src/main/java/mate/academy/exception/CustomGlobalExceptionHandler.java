package mate.academy.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request) {
        Map<String, Object> bodyFound = new LinkedHashMap<>();
        bodyFound.put("timestamp", LocalDateTime.now());
        bodyFound.put("status", HttpStatus.NOT_FOUND.value());
        bodyFound.put("path", request.getDescription(false).replace("uri=", ""));
        bodyFound.put("errors", List.of(ex.getMessage()));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(bodyFound);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> bodyArguments = new LinkedHashMap<>();
        bodyArguments.put("timestamp", LocalDateTime.now());
        bodyArguments.put("status", HttpStatus.BAD_REQUEST.value());
        bodyArguments.put("path", request.getDescription(false).replace("uri=", ""));
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::errorMessage)
                .toList();
        bodyArguments.put("errors", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(bodyArguments);
    }

    private String errorMessage(ObjectError e) {
        if (e instanceof FieldError fieldError) {
            return fieldError.getField() + " " + e.getDefaultMessage();
        }
        return e.getDefaultMessage();
    }
}
