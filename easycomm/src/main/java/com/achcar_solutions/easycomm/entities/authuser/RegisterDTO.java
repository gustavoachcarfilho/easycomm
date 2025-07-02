package com.achcar_solutions.easycomm.entities.authuser;

public record RegisterDTO(String email, String password, String cpf, String name, UserRole role) {
}
