package com.example.showmanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ShowsDTO {
    private Integer id;
    private LocalDateTime showTime;
    private int availableSeats;
    private int totalSeats;
    private MovieDTO movie; // Movie details fetched dynamically
    private TheatreDTO theatre; // Theatre details

}
