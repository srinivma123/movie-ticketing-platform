package com.example.showmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shows {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer theaterId;

    private Integer movieId;

    private LocalDateTime showTime;
    private int availableSeats;
    private int totalSeats;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
         // Optional but ensures consistency
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(LocalDateTime showTime, Integer availableSeats, Integer totalSeats) {
        if (showTime != null) {
            this.showTime = showTime;
        }
        if (availableSeats != null) {
            this.availableSeats = availableSeats;
        }
        if (totalSeats != null) {
            this.totalSeats = totalSeats;
        }
    }


    // Getters and Setters
}
