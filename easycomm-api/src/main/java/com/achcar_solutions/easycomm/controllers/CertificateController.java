package com.achcar_solutions.easycomm.controllers;

import com.achcar_solutions.easycomm.entities.certificate.Certificate;
import com.achcar_solutions.easycomm.entities.certificate.CertificateCreationRequest;
import com.achcar_solutions.easycomm.services.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Certificate> createCertificate(@RequestPart("request") CertificateCreationRequest request,
                                                         @RequestPart("file") MultipartFile file) {
        return new ResponseEntity<Certificate>(certificateService.createCertificate(request, file), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable String id) {
        return new ResponseEntity<>(certificateService.getCertificateById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        return new ResponseEntity<List<Certificate>>(certificateService.getAllCertificates(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable String id) {
        certificateService.deleteCertificate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
