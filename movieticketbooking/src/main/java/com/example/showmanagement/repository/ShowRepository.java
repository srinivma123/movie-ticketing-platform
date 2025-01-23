package com.example.showmanagement.repository;

import com.example.showmanagement.entity.Shows;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowRepository extends JpaRepository<Shows, Integer> {

    @Query("SELECT s FROM Shows s WHERE s.movieId = :movieId AND s.showTime BETWEEN :start AND :end")
    List<Shows> findByMovieAndShowTimeBetween(
            @Param("movieId") Integer movieId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Shows s " +
            "WHERE s.theaterId = :theatreId AND s.movieId = :movieId AND s.showTime = :showTime")
    boolean existsByTheatreIdAndMovieIdAndShowTime(@Param("theatreId") Integer theatreId,
                                                   @Param("movieId") Integer movieId,
                                                   @Param("showTime") LocalDateTime showTime);
}
