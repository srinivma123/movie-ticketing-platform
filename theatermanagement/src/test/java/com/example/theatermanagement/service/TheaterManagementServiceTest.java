package com.example.theatermanagement.service;

import com.example.theatermanagement.entity.Theatres;
import com.example.theatermanagement.exception.DatabaseException;
import com.example.theatermanagement.exception.ResourceNotFoundException;
import com.example.theatermanagement.repository.TheaterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TheaterManagementServiceTest {

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private TheaterManagementService theaterManagementService;

    @BeforeEach
    void setUp() {
        theaterRepository.deleteAll(); // Clean the database before each test
    }

    // Positive Test Cases

    @Test
    void testGetTheatreById_Success() {
        Theatres theatre = new Theatres();
        theatre.setName("Theatre 1");
        theatre.setCity("City 1");
        theatre.setAddress("Address 1");
        theatre.setCreatedAt(LocalDateTime.now());

        Theatres savedTheatre = theaterRepository.save(theatre);

        Theatres result = theaterManagementService.getTheatreById(savedTheatre.getId());
        assertNotNull(result);
        assertEquals(savedTheatre.getId(), result.getId());
    }

    @Test
    void testAddTheatre_Success() {
        Theatres theatre = new Theatres();
        theatre.setName("New Theatre");
        theatre.setCity("New City");
        theatre.setAddress("New Address");
        theatre.setCreatedAt(LocalDateTime.now());

        Theatres result = theaterManagementService.addTheatre(theatre);
        assertNotNull(result);
        assertEquals(theatre.getName(), result.getName());
    }

    @Test
    void testUpdateTheatre_Success() {
        Theatres theatre = new Theatres();
        theatre.setName("Theatre 1");
        theatre.setCity("City 1");
        theatre.setAddress("Address 1");
        theatre.setCreatedAt(LocalDateTime.now());

        Theatres savedTheatre = theaterRepository.save(theatre);

        Theatres updatedTheatre = new Theatres();
        updatedTheatre.setName("Updated Theatre");
        updatedTheatre.setCity("Updated City");
        updatedTheatre.setAddress("Updated Address");

        Theatres result = theaterManagementService.updateTheatre(savedTheatre.getId(), updatedTheatre);
        assertNotNull(result);
        assertEquals(updatedTheatre.getName(), result.getName());
    }

    @Test
    void testDeleteTheatre_Success() {
        Theatres theatre = new Theatres();
        theatre.setName("Theatre 1");
        theatre.setCity("City 1");
        theatre.setAddress("Address 1");
        theatre.setCreatedAt(LocalDateTime.now());

        Theatres savedTheatre = theaterRepository.save(theatre);

        assertDoesNotThrow(() -> theaterManagementService.deleteTheatre(savedTheatre.getId()));
    }

    // Negative Test Cases

    @Test
    void testGetTheatreById_ResourceNotFoundException() {
        Integer theatreId = 999; // Non-existent ID
        assertThrows(ResourceNotFoundException.class, () -> theaterManagementService.getTheatreById(theatreId));
    }



    @Test
    void testUpdateTheatre_DatabaseException() {
        Theatres theatre = new Theatres();
        theatre.setName("Theatre 1");
        theatre.setCity("City 1");
        theatre.setAddress("Address 1");
        theatre.setCreatedAt(LocalDateTime.now());

        Theatres savedTheatre = theaterRepository.save(theatre);

        Theatres updatedTheatre = new Theatres();
        updatedTheatre.setName("Updated Theatre");
        updatedTheatre.setCity("Updated City");
        updatedTheatre.setAddress("Updated Address");

        // Simulate a DB exception by causing a constraint violation
        savedTheatre.setId(null); // Set ID to null to simulate a DB error
        assertThrows(DatabaseException.class, () -> theaterManagementService.updateTheatre(savedTheatre.getId(), updatedTheatre));
    }

    @Test
    void testDeleteTheatre_DatabaseException() {
        Theatres theatre = new Theatres();
        theatre.setName("Theatre 1");
        theatre.setCity("City 1");
        theatre.setAddress("Address 1");
        theatre.setCreatedAt(LocalDateTime.now());

        Theatres savedTheatre = theaterRepository.save(theatre);

        // Simulate a DB exception by deleting a non-existent theatre
        savedTheatre.setId(999); // Set ID to a non-existent value
        assertThrows(ResourceNotFoundException.class, () -> theaterManagementService.deleteTheatre(savedTheatre.getId()));
    }
}
