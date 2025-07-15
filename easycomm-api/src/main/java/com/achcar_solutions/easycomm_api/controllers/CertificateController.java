package com.achcar_solutions.easycomm_api.controllers;

import com.achcar_solutions.easycomm_core.entities.certificate.Certificate;
import com.achcar_solutions.easycomm_core.entities.certificate.CertificateCreationRequest;
import com.achcar_solutions.easycomm_api.services.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("certificate")
@Tag(name = "Certificados", description = "Endpoints para gerenciamento de certificados.")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Operation(summary = "Cria um novo certificado",
            description = "Faz o upload de um arquivo e cria um registro de certificado, que é então enviado para uma fila de processamento assíncrono.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Certificado criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Certificate.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou certificado duplicado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Token JWT inválido ou ausente")
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Certificate> createCertificate(@RequestPart("request") CertificateCreationRequest request,
                                                         @RequestPart("file") MultipartFile file) {
        return new ResponseEntity<Certificate>(certificateService.createCertificate(request, file), HttpStatus.CREATED);
    }

    @Operation(summary = "Busca um certificado por ID", description = "Retorna os detalhes de um certificado específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificado encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Certificate.class))),
            @ApiResponse(responseCode = "404", description = "Certificado não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable String id) {
        return new ResponseEntity<>(certificateService.getCertificateById(id), HttpStatus.OK);
    }

    @Operation(summary = "Lista todos os certificados", description = "Retorna uma lista com todos os certificados cadastrados no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de certificados retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        return new ResponseEntity<List<Certificate>>(certificateService.getAllCertificates(), HttpStatus.OK);
    }

    @Operation(summary = "Deleta um certificado por ID", description = "Remove um certificado e o arquivo associado do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Certificado deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Certificado não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable String id) {
        certificateService.deleteCertificate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
