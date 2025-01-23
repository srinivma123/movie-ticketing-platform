package com.example.showmanagement.service;

import com.example.showmanagement.dto.*;
import com.example.showmanagement.entity.Shows;
import com.example.showmanagement.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShowServiceTest {

    @Autowired
    private ShowRepository showRepository;

    @InjectMocks
    private ShowService showService;

    @Mock
    private WebClient.Builder webClientBuilder;

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        webClient = mock(WebClient.class);
        requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    }

    // Positive Test Cases

    @Test
    void testBrowseTheatres_Success() {
        String movieTitle = "Test Movie";
        String city = "Test City";
        String date = "2023-05-01";
        boolean includeLocalizationContent = true;

        MovieDTO movieDTO = MovieDTO.builder()
                .id(1)
                .title(movieTitle)
                .language("English")
                .genre("Action")
                .duration(120)
                .build();

        TheatreDTO theatreDTO = TheatreDTO.builder()
                .id(1)
                .city(city)
                .name("Test Theatre")
                .address("123 Test Street")
                .build();

        Shows show = Shows.builder()
                .theaterId(theatreDTO.getId())
                .movieId(movieDTO.getId())
                .showTime(LocalDateTime.parse(date + "T10:00:00"))
                .availableSeats(100)
                .totalSeats(150)
                .build();

        showRepository.save(show);

        when(responseSpecMock.bodyToMono(MovieDTO.class)).thenReturn(Mono.just(movieDTO));
        when(responseSpecMock.bodyToFlux(TheatreDTO.class)).thenReturn(Flux.fromIterable(Arrays.asList(theatreDTO)));

        List<ShowsDTO> result = showService.browseTheatres(movieTitle, city, date, includeLocalizationContent);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(movieTitle, result.get(0).getMovie().getTitle());
        assertEquals(city, result.get(0).getTheatre().getCity());
    }

    @Test
    void testCreateShow_Success() {
        CreateShowRequest createShowRequest = new CreateShowRequest();
        createShowRequest.setTheatreId(1);
        createShowRequest.setMovieId(1);
        createShowRequest.setShowTime("2023-05-01T10:00:00");
        createShowRequest.setAvailableSeats(100);
        createShowRequest.setTotalSeats(150);

        TheatreDTO theatreDTO = TheatreDTO.builder()
                .id(1)
                .name("Test Theatre")
                .city("Test City")
                .address("123 Test Street")
                .build();

        MovieDTO movieDTO = MovieDTO.builder()
                .id(1)
                .title("Test Movie")
                .language("English")
                .genre("Action")
                .duration(120)
                .build();

        when(responseSpecMock.bodyToMono(TheatreDTO.class)).thenReturn(Mono.just(theatreDTO));
        when(responseSpecMock.bodyToMono(MovieDTO.class)).thenReturn(Mono.just(movieDTO));

        ShowsDTO result = showService.createShow(createShowRequest);

        assertNotNull(result);
        assertEquals(createShowRequest.getTheatreId(), result.getTheatre().getId());
        assertEquals(createShowRequest.getMovieId(), result.getMovie().getId());
    }

    // Negative Test Cases

    @Test
    void testBrowseTheatres_Exception() {
        String movieTitle = "Test Movie";
        String city = "Test City";
        String date = "2023-05-01";
        boolean includeLocalizationContent = true;

        when(responseSpecMock.bodyToMono(MovieDTO.class)).thenThrow(new WebClientResponseException(404, "Not Found", null, null, null, null));

        assertThrows(RuntimeException.class,
                () -> showService.browseTheatres(movieTitle, city, date, includeLocalizationContent));
    }

    @Test
    void testCreateShow_AlreadyExists() {
        CreateShowRequest createShowRequest = new CreateShowRequest();
        createShowRequest.setTheatreId(1);
        createShowRequest.setMovieId(1);
        createShowRequest.setShowTime("2023-05-01T10:00:00");
        createShowRequest.setAvailableSeats(100);
        createShowRequest.setTotalSeats(150);

        Shows show = Shows.builder()
                .theaterId(createShowRequest.getTheatreId())
                .movieId(createShowRequest.getMovieId())
                .showTime(LocalDateTime.parse(createShowRequest.getShowTime()))
                .availableSeats(createShowRequest.getAvailableSeats())
                .totalSeats(createShowRequest.getTotalSeats())
                .build();

        showRepository.save(show);



        assertThrows(RuntimeException.class, () -> showService.createShow(createShowRequest));
    }
}
