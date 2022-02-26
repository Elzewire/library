package it.raccoon.library.controllers;

import it.raccoon.library.domain.Book;
import it.raccoon.library.domain.Filter;
import it.raccoon.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    private BookService bookService;

    @Autowired
    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping()
    public List<Book> index(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return bookService.getAll(page, size);
    }

    @GetMapping("/{id}")
    public Book one(@PathVariable long id) {
        return bookService.getByID(id);
    }

    @PostMapping()
    public Book add(@RequestBody Book book) {
        return bookService.save(book);
    }

    @PutMapping("/{id}")
    public Book update(@RequestBody Book book, @PathVariable long id) {
        return bookService.update(book, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        bookService.deleteById(id);
    }

    @GetMapping("/search")
    public List<Book> filter(@RequestParam String filter, @RequestParam String q, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return bookService.filter(Filter.valueOf(filter), q, page, size);
    }

    @GetMapping("/{id}/like")
    public void like(@PathVariable long id) {
        bookService.like(id);
    }
}
