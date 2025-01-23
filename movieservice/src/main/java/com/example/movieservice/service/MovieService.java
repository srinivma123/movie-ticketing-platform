package com.example.movieservice.service;

import com.example.movieservice.entity.Localization;
import com.example.movieservice.entity.Movies;
import com.example.movieservice.exception.ResourceNotFoundException;
import com.example.movieservice.exception.DatabaseException;
import com.example.movieservice.repository.LocalizationRepository;
import com.example.movieservice.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final LocalizationRepository localizationRepository;

    public MovieService(MovieRepository movieRepository, LocalizationRepository localizationRepository) {
        this.movieRepository = movieRepository;
        this.localizationRepository = localizationRepository;
    }

    // Get movie by title with optional localization content
    public Movies getMovieByTitle(String title, boolean includeLocalizationContent) {
        logger.info("Fetching movie by title: {} with includeLocalizationContent: {}", title, includeLocalizationContent);
        try {
            Optional<Movies> movieOptional = movieRepository.findByTitle(title);
            if (movieOptional.isEmpty()) {
                logger.warn("Movie with title '{}' not found", title);
                throw new ResourceNotFoundException("Movie with title '" + title + "' not found.");
            }

            Movies movie = movieOptional.get();

            if (includeLocalizationContent) {
                logger.debug("Fetching localizations for movie: {}", title);
                movie.getLocalizations().size(); // Trigger lazy-loading of localizations
            } else {
                logger.debug("Excluding localizations for movie: {}", title);
                movie.setLocalizations(null);
            }

            logger.info("Successfully fetched movie: {}", title);
            return movie;
        } catch (DataAccessException ex) {
            logger.error("Database error while fetching movie by title '{}': {}", title, ex.getMessage());
            throw new DatabaseException("Database error occurred while fetching movie by title: " + ex.getMessage());
        }
    }

    public Movies getMovieById(Integer id) {
        logger.info("Fetching movie by ID: {}", id);
        try {
            Optional<Movies> movie = movieRepository.findById(id);
            if (movie.isEmpty()) {
                logger.warn("Movie with ID '{}' not found", id);
                throw new ResourceNotFoundException("Movie with ID '" + id + "' not found.");
            }
            logger.info("Successfully fetched movie with ID: {}", id);
            return movie.get();
        } catch (DataAccessException ex) {
            logger.error("Database error while fetching movie by ID '{}': {}", id, ex.getMessage());
            throw new DatabaseException("Database error occurred while fetching movie by ID: " + ex.getMessage());
        }
    }

    public Movies addMovie(Movies movie) {
        logger.info("Adding a new movie: {}", movie.getTitle());
        try {
            Movies savedMovie = movieRepository.save(movie);
            logger.info("Successfully added movie: {}", movie.getTitle());
            return savedMovie;
        } catch (DataAccessException ex) {
            logger.error("Database error while adding movie '{}': {}", movie.getTitle(), ex.getMessage());
            throw new DatabaseException("Database error occurred while adding a new movie: " + ex.getMessage());
        }
    }

    public Localization addLocalization(Localization localization) {
        logger.info("Adding localization for movie ID: {}", localization.getMovie().getId());
        try {
            Localization savedLocalization = localizationRepository.save(localization);
            logger.info("Successfully added localization for movie ID: {}", localization.getMovie().getId());
            return savedLocalization;
        } catch (DataIntegrityViolationException ex) {
            logger.error("Localization already exists for movie ID: {}", localization.getMovie().getId());
            throw new RuntimeException("Localization already exists for this movie, language, and region.");
        }
    }
}