package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// ID token (JWT) devolvido pelo Google Identity Services no front.
public record GoogleLoginRequest(String idToken) {}
