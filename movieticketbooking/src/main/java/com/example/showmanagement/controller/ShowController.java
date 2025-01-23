package com.example.showmanagement.controller;


import com.example.showmanagement.dto.CreateShowRequest;
import com.example.showmanagement.dto.DeleteShowRequest;
import com.example.showmanagement.dto.ShowsDTO;
import com.example.showmanagement.dto.UpdateShowRequest;
import com.example.showmanagement.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @GetMapping("/browse")
    public List<ShowsDTO> browseTheatres(
            @RequestParam String movieTitle,
            @RequestParam String city,
            @RequestParam String date,
            @RequestParam boolean includeLocalizationContent) {
        return showService.browseTheatres(movieTitle, city, date, includeLocalizationContent);
    }

    @PostMapping
    public ShowsDTO createShow(
            @RequestBody CreateShowRequest createShowRequest) {
        return showService.createShow(createShowRequest);
    }

    @PutMapping("/{showId}")
    public ShowsDTO updateShow(
           @RequestBody UpdateShowRequest updateShowRequest) {
        return showService.updateShow(updateShowRequest);
    }

    @DeleteMapping("/{showId}")
    public void deleteShow(@RequestBody DeleteShowRequest deleteShowRequest) {
        showService.deleteShow(deleteShowRequest);
    }
}



