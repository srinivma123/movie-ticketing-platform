package com.example.showmanagement.dto;

import lombok.Data;

@Data
public class DeleteShowRequest {
    private Integer showId;
    private Integer theatreId;
}