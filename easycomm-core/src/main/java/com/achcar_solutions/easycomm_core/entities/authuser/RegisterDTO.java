package com.achcar_solutions.easycomm_core.entities.authuser;

public record RegisterDTO(String email, String password, String cpf, String name, UserRole role) {
}
