package com.learning.mvc.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.learning.mvc.model.Book;

@Repository
public class BookRepository {
    private final Map<Integer, Book> books;
    private int currentId;

    public BookRepository() {
        books = new HashMap<>();
        var book1 = new Book(++currentId, "Java for Dummies", "James", 39.99);
        var book2 = new Book(++currentId, "How to be not Batman", "Bruce Wayne", 29.99);
        var book3 = new Book(++currentId, "What's in a Name?", "John Doe", 24.99);
        var book4 = new Book(++currentId, "Hold my Hand", "Handless Hanny", 14.99);
        books.put(book1.getId(), book1);
        books.put(book2.getId(), book2);
        books.put(book3.getId(), book3);
        books.put(book4.getId(), book4);
    }

    public List<Book> findAll() {
        return new ArrayList<Book>(books.values());
    }

    public Book findById(Integer id) {
        return books.get(id);
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(++currentId);
        }
        books.put(book.getId(), book);
        return book;
    }

    public void deleteById(Integer id) {
        books.remove(id);
    }
}
