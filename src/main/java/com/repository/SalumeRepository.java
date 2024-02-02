package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.Salume;

public interface SalumeRepository extends JpaRepository<Salume, Integer> {

	Salume findByName(String name);

}
