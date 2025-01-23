package com.example.theatermanagement.repository;

import com.example.theatermanagement.entity.Theatres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theatres, Integer> {
}
