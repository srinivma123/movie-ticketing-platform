package com.example.showmanagement.service;

import com.example.showmanagement.dto.*;
import com.example.showmanagement.entity.Shows;
import com.example.showmanagement.repository.ShowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowService {
    private final ShowRepository showRepository;
    private final WebClient.Builder webClientBuilder;


    private static final Logger logger = LoggerFactory.getLogger(ShowService.class);

    @Value("${movie.service.rest.url}")
    private String movieServiceRestUrl; // Base URL for MovieService REST API


    @Value("${theater.service.rest.url}")
    private String theaterManagementServiceUrl;
    public ShowService(ShowRepository showRepository, WebClient.Builder webClientBuilder) {
        this.showRepository = showRepository;
        this.webClientBuilder = webClientBuilder;

    }

    // Browse theatres with optional localizations
    public List<ShowsDTO> browseTheatres(String movieTitle, String city, String date, boolean includeLocalizationContent) {
        try {

            logger.info("Inside Browsing theatres method for movie: {}, city: {}, date: {}, includeLocalizationContent: {}", movieTitle, city, date, includeLocalizationContent);
            // Fetch movie details by title from MovieService REST API
            MovieDTO movie = getMovieByTitle(movieTitle, includeLocalizationContent);

            LocalDateTime start = LocalDate.parse(date).atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            // Fetch shows for the movie within the specified date range
            List<Shows> shows = showRepository.findByMovieAndShowTimeBetween(movie.getId(), start, end);
            logger.info("Successfully fetched  shows for movie: {}",  movieTitle);

            // Fetch theatre details for all shows from TheaterManagementService
            List<Integer> theatreIds = shows.stream()
                    .map(Shows::getTheaterId)
                    .distinct()
                    .collect(Collectors.toList());

            List<TheatreDTO> theatres = getTheatresByIds(theatreIds);
            // Filter shows by city and map to DTOs
            // Filter shows by city and map to DTOs
            return shows.stream()
                    .filter(show -> {
                        TheatreDTO theatre = findTheatreById(theatres, show.getTheaterId());
                        return theatre != null && theatre.getCity().equalsIgnoreCase(city);
                    })
                    .map(show -> {
                        TheatreDTO theatre = findTheatreById(theatres, show.getTheaterId());
                        return convertToDTO(show, movie, theatre);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while browsing theatres: {}", e.getMessage());
            throw new RuntimeException("Error while fetching shows: " + e.getMessage(), e);
        }
    }





    public ShowsDTO createShow(CreateShowRequest createShowRequest) {
        logger.info("Creating a new show for theatre ID: {}, movie ID: {}", createShowRequest.getTheatreId(), createShowRequest.getMovieId());
        try {
            // Fetch theatre details from TheaterManagementService
            TheatreDTO theatre = getTheatreById(createShowRequest.getTheatreId());

            // Fetch movie details by ID from MovieService REST API
            MovieDTO movie = getMovieById(createShowRequest.getMovieId());

            // Validate and parse showTime
            LocalDateTime showTime = parseShowTime(createShowRequest.getShowTime());

            // Check if a show with the same theatre, movie, and showTime already exists
            boolean exists = showRepository.existsByTheatreIdAndMovieIdAndShowTime(
                    createShowRequest.getTheatreId(), createShowRequest.getMovieId(), showTime);
            if (exists) {
                logger.warn("Show already exists for theatre ID: {}, movie ID: {}, showTime: {}", createShowRequest.getTheatreId(), createShowRequest.getMovieId(), showTime);
                throw new RuntimeException("A show with the same theatre, movie, and show time already exists.");
            }

            // Create and save the new show
            Shows newShow = Shows.builder()
                    .theaterId(theatre.getId()) // Use theaterId instead of a Theatre entity
                    .movieId(createShowRequest.getMovieId())
                    .showTime(showTime)
                    .availableSeats(createShowRequest.getAvailableSeats())
                    .totalSeats(createShowRequest.getTotalSeats())
                    .build();

            Shows savedShow = showRepository.save(newShow);
            logger.info("Successfully created show with ID: {}", savedShow.getId());

            // Convert to DTO
            return convertToDTO(savedShow,movie, theatre);
        } catch (Exception e) {
            logger.error("Error while creating show: {}", e.getMessage());
            throw new RuntimeException("Error while creating show: " + e.getMessage(), e);
        }
    }

    // Update an existing show
    public ShowsDTO updateShow(UpdateShowRequest updateShowRequest) {
        logger.info("Updating show with ID: {}", updateShowRequest.getShowId());
        try {
            // Parse and validate showTime
            LocalDateTime showTime = parseShowTime(updateShowRequest.getShowTime());

            // Fetch the show to update
            Shows show = showRepository.findById(updateShowRequest.getShowId())
                    .orElseThrow(() -> new RuntimeException("Show not found with id: " + updateShowRequest.getShowId()));

            // Check if the theatreId of the show matches the theatreId in the request
            if (!show.getTheaterId().equals(updateShowRequest.getTheatreId())) {
                throw new RuntimeException("Theatre ID mismatch. The show does not belong to the specified theatre.");
            }

            // Update the show details
            show.updateDetails(showTime, updateShowRequest.getAvailableSeats(), updateShowRequest.getTotalSeats());

            // Save the updated show
            Shows updatedShow = showRepository.save(show);
            logger.info("Successfully created show with ID: {}", updatedShow.getId());

            // Convert to DTO
            return convertToDTO(updatedShow, getMovieById(show.getMovieId()), getTheatreById(show.getTheaterId()));
        } catch (Exception e) {
            logger.error("Error while creating show: {}", e.getMessage());
            throw new RuntimeException("Error while updating show: " + e.getMessage(), e);
        }
    }

    // Delete a show
    public void deleteShow(DeleteShowRequest deleteShowRequest) {
        logger.info("Deleting show with ID: {}", deleteShowRequest.getShowId());
        try {
            // Fetch the show by its ID
            Shows show = showRepository.findById(deleteShowRequest.getShowId())
                    .orElseThrow(() -> new RuntimeException("Show not found with id: " + deleteShowRequest.getShowId()));

            // Check if the show belongs to the specified theatre
            if (!show.getTheaterId().equals(deleteShowRequest.getTheatreId())) {
                throw new RuntimeException("Theatre ID mismatch. The show does not belong to the specified theatre.");
            }

            // Delete the show
            showRepository.delete(show);
            logger.info("Successfully deleted show with ID: {}", deleteShowRequest.getShowId());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting show: " + e.getMessage(), e);
        }
    }

    private LocalDateTime parseShowTime(String showTime) {
        try {
            // Parse the showTime string into a LocalDateTime object
            return LocalDateTime.parse(showTime);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid showTime format. Expected format: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    // Fetch movie by title using REST API
    private MovieDTO getMovieByTitle(String title, boolean includeLocalizationContent) {
        String url = movieServiceRestUrl + "/title/" + title + "?includeLocalizationContent=" + includeLocalizationContent;

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(MovieDTO.class)
                .block();
    }

    // Fetch movie by ID using REST API
    private MovieDTO getMovieById(Integer movieId) {
        String url = movieServiceRestUrl + "/" + movieId;

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(MovieDTO.class)
                .block();
    }

    private List<TheatreDTO> getTheatresByIds(List<Integer> theatreIds) {
        String url = theaterManagementServiceUrl + "/api/theatres/list";

        try {
            return webClientBuilder.build()
                    .post()
                    .uri(url)
                    .bodyValue(theatreIds) // Send theatre IDs as the request body
                    .retrieve()
                    .bodyToFlux(TheatreDTO.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            logger.error("Error while fetching theatre details: {}", e.getMessage());
            throw new RuntimeException("Error while fetching theatre details: " + e.getMessage(), e);
        }
    }

    private TheatreDTO findTheatreById(List<TheatreDTO> theatres, Integer theatreId) {
        return theatres.stream()
                .filter(theatre -> theatre.getId().equals(theatreId))
                .findFirst()
                .orElse(null);
    }

    private TheatreDTO getTheatreById(Integer theatreId) {
        String url = theaterManagementServiceUrl + "/api/theatres/" + theatreId;

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(TheatreDTO.class)
                .block();
    }

    // Convert Shows entity to ShowsDTO
    private ShowsDTO convertToDTO(Shows show, MovieDTO movie,TheatreDTO theatreDTO) {


        return ShowsDTO.builder()
                .id(show.getId())
                .showTime(show.getShowTime())
                .availableSeats(show.getAvailableSeats())
                .totalSeats(show.getTotalSeats())
                .movie(movie)
                .theatre(theatreDTO)
                .build();
    }

    // Convert Theatres entity to TheatreDTO

}