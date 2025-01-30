package com.learning.moviecatalog.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.learning.moviecatalog.models.Movie;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/movie")
public class MovieResource {

    @RequestMapping("/{id}")
    public Movie GetMovie(@PathParam("id") String id) {
        return new Movie(id, "Batman", "The is a movie about Batman. Not Bruce Wayne.");
    }
}
