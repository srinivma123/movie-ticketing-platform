package com.example.movieservice.controller;

import com.example.movieservice.entity.Movies;
import com.example.movieservice.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Get movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movies> getMovieById(@PathVariable Integer id) {
        Movies movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    // Get movie by title with optional localization content
    @GetMapping("/title/{title}")
    public ResponseEntity<Movies> getMovieByTitle(
            @PathVariable String title,
            @RequestParam(defaultValue = "false") boolean includeLocalizationContent) {
        Movies movie = movieService.getMovieByTitle(title, includeLocalizationContent);
        return ResponseEntity.ok(movie);
    }

    // Add a new movie
    @PostMapping
    public ResponseEntity<Movies> addMovie(@RequestBody Movies movie) {
        Movies savedMovie = movieService.addMovie(movie);
        return ResponseEntity.ok(savedMovie);
    }
}