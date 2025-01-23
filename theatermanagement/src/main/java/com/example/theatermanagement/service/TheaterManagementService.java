package com.example.theatermanagement.service;

import com.example.theatermanagement.entity.Theatres;
import com.example.theatermanagement.exception.ResourceNotFoundException;
import com.example.theatermanagement.exception.DatabaseException;
import com.example.theatermanagement.repository.TheaterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class TheaterManagementService {
    private static final Logger logger = LoggerFactory.getLogger(TheaterManagementService.class);

    private final TheaterRepository theaterRepository;

    public TheaterManagementService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    // Fetch theatre by ID
    public Theatres getTheatreById(Integer theatreId) {
        logger.info("Fetching theatre with ID: {}", theatreId);
        try {
            return theaterRepository.findById(theatreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with ID: " + theatreId));
        } catch (DataAccessException ex) {
            logger.error("Database error while fetching theatre with ID: {}", theatreId, ex);
            throw new DatabaseException("Database error occurred while fetching theatre with ID: " + theatreId);
        }
    }

    // Add a new theatre
    public Theatres addTheatre(Theatres theatre) {
        logger.info("Adding a new theatre: {}", theatre.getName());
        try {
            return theaterRepository.save(theatre);
        } catch (DataAccessException ex) {
            logger.error("Database error while adding theatre: {}", theatre.getName(), ex);
            throw new DatabaseException("Database error occurred while adding a new theatre: " + ex.getMessage());
        }
    }

    // Update an existing theatre
    public Theatres updateTheatre(Integer theatreId, Theatres updatedTheatre) {
        logger.info("Updating theatre with ID: {}", theatreId);
        try {
            Theatres existingTheatre = getTheatreById(theatreId);
            existingTheatre.setName(updatedTheatre.getName());
            existingTheatre.setCity(updatedTheatre.getCity());
            existingTheatre.setAddress(updatedTheatre.getAddress());
            return theaterRepository.save(existingTheatre);
        } catch (DataAccessException ex) {
            logger.error("Database error while updating theatre with ID: {}", theatreId, ex);
            throw new DatabaseException("Database error occurred while updating theatre with ID: " + theatreId);
        }
    }

    // Delete a theatre
    public void deleteTheatre(Integer theatreId) {
        logger.info("Deleting theatre with ID: {}", theatreId);
        try {
            Theatres theatre = getTheatreById(theatreId);
            theaterRepository.delete(theatre);
        } catch (DataAccessException ex) {
            logger.error("Database error while deleting theatre with ID: {}", theatreId, ex);
            throw new DatabaseException("Database error occurred while deleting theatre with ID: " + theatreId);
        }
    }
}