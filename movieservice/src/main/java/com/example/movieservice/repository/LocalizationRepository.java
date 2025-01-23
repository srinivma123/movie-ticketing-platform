package com.example.movieservice.repository;

import com.example.movieservice.entity.Localization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalizationRepository extends JpaRepository<Localization, Integer> {
    List<Localization> findByMovieId(Integer movieId);
}
