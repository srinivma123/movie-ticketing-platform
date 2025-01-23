package com.example.movieservice.repository;

import com.example.movieservice.entity.Movies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movies, Integer> {
    // Fetch movie by title (localizations will be lazy-loaded by default)
    Optional<Movies> findByTitle(String title);

    // Fetch movie by ID (localizations will be lazy-loaded by default)
    Optional<Movies> findById(Integer id);
}