package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int id;
	private int employeeId;
	private String name;
	private String email;
	private String password;
	private String roles;
	
}
