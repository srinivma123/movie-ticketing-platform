package com.example.showmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationDTO {

    private Integer id;


    private MovieDTO movie;

    private String language;
    private String region;
    private String title;
    private String description;
}
