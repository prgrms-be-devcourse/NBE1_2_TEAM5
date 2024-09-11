package hello.gccoffee.controller.advice;

import hello.gccoffee.exception.OrderTaskException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class OrderApiControllerAdvice {

    @ExceptionHandler(OrderTaskException.class)
    public ResponseEntity<Map<String, String>> handleException(OrderTaskException e) {
        log.info("OrderTaskException : " + e.getMessage());

        Map<String, String> errMap = Map.of("error", e.getMessage());
        return ResponseEntity.status(e.getCode()).body(errMap);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(NoSuchElementException e) {
        log.info("NoSuchException error ",e);

        Map<String, String> error = Map.of("error", e.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Invalid argument",
                        "message", e.getMessage()
                ));
    }
}
