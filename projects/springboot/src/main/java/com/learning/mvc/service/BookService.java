package com.learning.mvc.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.learning.mvc.exception.BookNotFoundException;
import com.learning.mvc.model.Book;
import com.learning.mvc.repository.BookRepository;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id) {
        Book book = bookRepository.findById(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        return book;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Integer id) {
        if (bookRepository.findById(id) == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        bookRepository.deleteById(id);
    }
}
