# EasyComm - Processador de Certificados

[![Linguagem](https://img.shields.io/badge/language-Java%2021-blue)](https://www.oracle.com/java/)
[![Framework](https://img.shields.io/badge/framework-Spring%20Boot%203.5-green)](https://spring.io/projects/spring-boot)

O EasyComm é um sistema back-end construído em Java com Spring Boot, projetado para gerenciar usuários e processar docuemntos de forma assíncrona. A aplicação oferece uma API RESTful segura para autenticação, upload e validação de documentos.

## ✨ Funcionalidades Atuais

* **Autenticação e Autorização:**
    * Sistema completo de registro e login de usuários.
    * Autenticação segura baseada em **JSON Web Tokens (JWT)**.
    * Controle de acesso baseado em papéis (Roles): `ADMIN` e `USER`.
    * As senhas são armazenadas de forma segura usando o algoritmo **BCrypt**.

* **Gestão de Documentos:**
    * Endpoint para criação de registros de certificados.
    * Estrutura de dados flexível para certificados, incluindo categorias, status, e informações de validação.
    * Lógica de negócio para prevenir a criação de certificados duplicados para o mesmo usuário, título e categoria.

* **Arquitetura:**
    * Utiliza **MongoDB** como banco de dados único para toda a aplicação (usuários e certificados).
    * Design baseado em DTOs (Data Transfer Objects) para desacoplar a camada de API da lógica de negócio.
    * Código estruturado em camadas (Controllers, Services, Repositories).
    * Previsão para integração com um sistema de mensageria (Kafka/RabbitMQ) para o processamento assíncrono de arquivos.

## 🛠️ Tecnologias Utilizadas

* **Back-end:**
    * Java 21
    * Spring Boot 3.5.0
    * Maven
* **Banco de Dados:**
    * MongoDB
    * Spring Data MongoDB
* **Segurança:**
    * Spring Security
    * JWT (via `auth0/java-jwt`)
* **Outras Bibliotecas:**
    * Lombok
* **Ambiente de Desenvolvimento:**
    * Docker & Docker Compose
