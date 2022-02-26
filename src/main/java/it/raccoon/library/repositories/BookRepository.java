package it.raccoon.library.repositories;

import it.raccoon.library.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
    Page<Book> findAll(Pageable paging);
    Page<Book> findByAuthor_NameContaining(String authorName, Pageable pageable);
    Page<Book> findByGenre_NameContaining(String genreName, Pageable pageable);
    Page<Book> findByNameContaining(String name, Pageable pageable);
}
