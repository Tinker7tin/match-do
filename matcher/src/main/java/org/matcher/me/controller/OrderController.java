package org.matcher.me.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/depth/{symbol}")
    public ResponseEntity getDepth(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "10") int depth
    ) {
        return (ResponseEntity) ResponseEntity.ok();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Symbol not found");
    }
}
