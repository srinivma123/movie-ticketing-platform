package com.example.showmanagement.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShowRequest {
    private Integer theatreId;
    private Integer movieId;
    private String showTime;
    private int availableSeats;
    private int totalSeats;
}
