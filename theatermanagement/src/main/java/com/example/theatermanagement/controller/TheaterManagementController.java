package com.example.theatermanagement.controller;

import com.example.theatermanagement.entity.Theatres;
import com.example.theatermanagement.service.TheaterManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/theatres")
public class TheaterManagementController {

    private final TheaterManagementService theaterManagementService;

    public TheaterManagementController(TheaterManagementService theaterManagementService) {
        this.theaterManagementService = theaterManagementService;
    }

    @GetMapping("/{theatreId}")
    public ResponseEntity<Theatres> getTheatreById(@PathVariable Integer theatreId) {
        return ResponseEntity.ok(theaterManagementService.getTheatreById(theatreId));
    }

    @PostMapping
    public ResponseEntity<Theatres> addTheatre(@RequestBody Theatres theatre) {
        return ResponseEntity.ok(theaterManagementService.addTheatre(theatre));
    }

    @PutMapping("/{theatreId}")
    public ResponseEntity<Theatres> updateTheatre(@PathVariable Integer theatreId, @RequestBody Theatres updatedTheatre) {
        return ResponseEntity.ok(theaterManagementService.updateTheatre(theatreId, updatedTheatre));
    }
    @PostMapping("/list")
    public ResponseEntity<List<Theatres>> getTheatresByIds(@RequestBody List<Integer> theatreIds) {
        List<Theatres> theatres = theatreIds.stream()
                .map(theaterManagementService::getTheatreById)

                .collect(Collectors.toList());
        return ResponseEntity.ok(theatres);
    }

    @DeleteMapping("/{theatreId}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable Integer theatreId) {
        theaterManagementService.deleteTheatre(theatreId);
        return ResponseEntity.noContent().build();
    }
}