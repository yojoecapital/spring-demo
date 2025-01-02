# Spring Boot MVC

Spring Boot MVC is a framework that simplifies building web applications using a model view controller (MVC) design pattern. It integrates with Spring Boot's features for easier configuration and deployment.

## Controller Layer
 ```java
 @Controller
 public class HomeController {
     @RequestMapping
     public String index() {
         return "forward:/home.html";
     }
 }
 ```

- the `@Controller` annotation lets Spring detect and manage the class as part of the web layer
- the `@RequestMapping("/")` annotation maps the root URL `/` to the `index()` method
- returning `"forward:/home.html"` forwards the request to the `home.html` file in the static directory without changing the browser's URL
  - the static directory can be found at `src/main/resources/static`
  - you can also use `"redirect:/home.html"` but this will change the URL to `/home.html`
- you can also use a view package like Thymeleaf to return template files at `src/main/resources/template`

```java
@GetMapping("/about")
public String about(Model model) {
    model.addAttribute("message", "This is being rendered with Thymeleaf!");
    return "about";
}
// inside the template we can have this:
// <h1 th:text="${message}"></h1>
```

### Rest Controllers

```java
@GetMapping("/{id}")
public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
    try {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    } catch (BookNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}

@PostMapping
public Book addBook(@RequestBody Book book) {
    return bookService.addBook(book);
}
```

- you can use `@RestController` so that Spring doesn't try to return a static (or template) file and instead a REST (Representational State Transfer) response
- you can use `@GetMapping` for `GET` requests and `@PostMapping` for `POST`
- the `@PathVariable` annotation denotes that a parameter is on the path
- the `@RequestBody` annotation denotes that a parameter will be the request body
  - JSON is used for serialization and serialization by default by Spring Boot 

## Service Layer

- notice that our `BookRepository` dependency is auto wired by default into the constructor
- the `@Service` annotation isn't much different (actually I don't think it's different at all) from `@Component` but it gives a good visual queue on what this class if fore (i.e. busies logic)

```java
@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book getBookById(Integer id) {
        Book book = bookRepository.findById(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        return book;
    }
}
```

## Repository Layer

- Spring's exception translation mechanism is applied automatically to classes annotated with `@Repository`
- I haven't tested this but it is supposed to make catching exceptions easier with `DataAccessException`

## Model Layer
- models represent data structures for the application
- models typically are not beans but just POJOs 

 ```java
 public class Book {
     private int id;
     private String title;
     private String author;
     private double price;

     // Getters and setters
 }
 ```
