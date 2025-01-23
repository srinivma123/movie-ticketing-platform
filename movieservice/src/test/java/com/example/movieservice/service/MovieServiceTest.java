package com.example.movieservice.service;

import com.example.movieservice.entity.Movies;
import com.example.movieservice.exception.DatabaseException;
import com.example.movieservice.exception.ResourceNotFoundException;
import com.example.movieservice.repository.LocalizationRepository;
import com.example.movieservice.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private LocalizationRepository localizationRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMovieByTitle_Positive() {
        // Arrange
        String title = "Inception";
        Movies movie = new Movies();
        movie.setTitle(title);
        when(movieRepository.findByTitle(title)).thenReturn(Optional.of(movie));

        // Act
        Movies result = movieService.getMovieByTitle(title, false);

        // Assert
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(movieRepository, times(1)).findByTitle(title);
    }

    @Test
    void testGetMovieByTitle_Negative_NotFound() {
        // Arrange
        String title = "NonExistentMovie";
        when(movieRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieByTitle(title, false));
        verify(movieRepository, times(1)).findByTitle(title);
    }

    @Test
    void testGetMovieById_Positive() {
        // Arrange
        Integer id = 1;
        Movies movie = new Movies();
        movie.setId(id);
        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

        // Act
        Movies result = movieService.getMovieById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(movieRepository, times(1)).findById(id);
    }

    @Test
    void testGetMovieById_Negative_NotFound() {
        // Arrange
        Integer id = 999;
        when(movieRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(id));
        verify(movieRepository, times(1)).findById(id);
    }

    @Test
    void testAddMovie_Positive() {
        // Arrange
        Movies movie = new Movies();
        movie.setTitle("New Movie");
        when(movieRepository.save(movie)).thenReturn(movie);

        // Act
        Movies result = movieService.addMovie(movie);

        // Assert
        assertNotNull(result);
        assertEquals("New Movie", result.getTitle());
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void testAddMovie_Negative_DatabaseError() {
        // Arrange
        Movies movie = new Movies();
        movie.setTitle("New Movie");
        when(movieRepository.save(movie)).thenThrow(new DatabaseException("Database error"));

        // Act & Assert
        assertThrows(DatabaseException.class, () -> movieService.addMovie(movie));
        verify(movieRepository, times(1)).save(movie);
    }
}