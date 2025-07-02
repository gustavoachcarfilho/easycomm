package com.achcar_solutions.easycomm.controllers;

import com.achcar_solutions.easycomm.entities.certificate.Certificate;
import com.achcar_solutions.easycomm.entities.certificate.CertificateCreationRequest;
import com.achcar_solutions.easycomm.entities.certificate.CertificateCreationResponse;
import com.achcar_solutions.easycomm.entities.certificate.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping
    public ResponseEntity<Certificate> createCertificate(CertificateCreationRequest request) {
        return new ResponseEntity<Certificate>(certificateService.createCertificate(request), HttpStatus.CREATED);
    }
}
