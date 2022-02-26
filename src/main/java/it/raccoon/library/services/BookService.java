package it.raccoon.library.services;

import it.raccoon.library.domain.Book;
import it.raccoon.library.domain.Filter;

import java.util.List;

public interface BookService {

    List<Book> getAll(int page, int size);

    Book getByID(long id);

    Book save(Book book);

    Book update(Book book, long id);

    void deleteById(long id);

    List<Book> filter(Filter filter, String q, int page, int size);

    void like(long id);

}
