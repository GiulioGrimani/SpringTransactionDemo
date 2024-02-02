package com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode

@Entity
public class Salume {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salume_id")
	private Integer salumeId;

	private String name;

	public Salume(String name) {
		this.name = name;
	}

}
