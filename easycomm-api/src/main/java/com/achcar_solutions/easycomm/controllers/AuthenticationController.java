package com.achcar_solutions.easycomm.controllers;

import com.achcar_solutions.easycomm.entities.authuser.AuthUser;
import com.achcar_solutions.easycomm.entities.authuser.AuthenticationDTO;
import com.achcar_solutions.easycomm.entities.authuser.LoginDTO;
import com.achcar_solutions.easycomm.entities.authuser.RegisterDTO;
import com.achcar_solutions.easycomm.security.TokenService;
import com.achcar_solutions.easycomm.repositories.AuthUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@RequestBody @Valid AuthenticationDTO authenticationDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.password());
        var  authentication = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((AuthUser) authentication.getPrincipal());
        return ResponseEntity.ok(new LoginDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterDTO> register(@RequestBody @Valid RegisterDTO registerDTO) {
        var isUserRegistered = authUserRepository.existsByEmail(registerDTO.email()) || authUserRepository.existsByCpf(registerDTO.cpf());
        if (isUserRegistered) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());
        AuthUser authUserToBeRegistered = AuthUser.builder()
                .name(registerDTO.name())
                .email(registerDTO.email())
                .cpf(registerDTO.cpf())
                .role(registerDTO.role())
                .password(encryptedPassword)
                .build();
        authUserRepository.save(authUserToBeRegistered);
        return ResponseEntity.ok().build();
    }
}
