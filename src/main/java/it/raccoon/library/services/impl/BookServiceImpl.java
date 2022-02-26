package it.raccoon.library.services.impl;

import it.raccoon.library.domain.Book;
import it.raccoon.library.domain.Filter;
import it.raccoon.library.domain.LibUser;
import it.raccoon.library.repositories.BookRepository;
import it.raccoon.library.repositories.UserRepository;
import it.raccoon.library.services.BookService;
import it.raccoon.library.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, UserService userService, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public List<Book> getAll(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Book> pageBooks;
        pageBooks = bookRepository.findAll(paging);
        return pageBooks.getContent();

    }

    @Override
    public Book getByID(long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Book save(Book book) {
       return bookRepository.save(book);
    }

    @Override
    public Book update(Book book, long id) {
        Book oldBook = bookRepository.findById(id).orElse(null);
        if (oldBook != null) {
            oldBook.setAuthor(book.getAuthor());
            oldBook.setGenre(book.getGenre());
            oldBook.setName(book.getName());
        } else {
            oldBook = book;
        }
        return bookRepository.save(oldBook);
    }

    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> filter(Filter filter, String q, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Book> pageBooks;
        switch (filter) {
            case NAME:
                pageBooks = bookRepository.findByNameContaining(q, paging);
                break;

            case GENRE:
                pageBooks = bookRepository.findByGenre_NameContaining(q, paging);
                break;

            case AUTHOR:
                pageBooks = bookRepository.findByAuthor_NameContaining(q, paging);
                break;

            default:
                pageBooks = bookRepository.findAll(paging);
                break;
        }

        return pageBooks.getContent();
    }

    @Override
    public void like(long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LibUser libUser = userService.findByUsername(username);
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            libUser.getLikes().add(book);
            userRepository.save(libUser);
        }
    }
}
