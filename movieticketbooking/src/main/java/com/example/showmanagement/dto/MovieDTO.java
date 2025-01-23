package com.example.showmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    private Integer id;
    private String title;
    private String language;
    private String genre;
    private int duration;


    private List<LocalizationDTO> localizations;



    // Getters and Setters
}
