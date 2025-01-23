package com.example.showmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TheatreDTO {
    private Integer id;
    private String name;
    private String city;
    private String address;

}