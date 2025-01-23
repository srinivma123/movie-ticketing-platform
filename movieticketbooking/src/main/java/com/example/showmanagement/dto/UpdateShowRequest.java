package com.example.showmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateShowRequest {
    private Integer showId;
    private Integer theatreId;
    private String showTime;
    private Integer availableSeats;
    private Integer totalSeats;
}