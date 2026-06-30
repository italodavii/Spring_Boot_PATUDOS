package com.example.estudo_patudos_api_spring_jpa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Devolve a mensagem (em PT) das exceções intencionais no corpo: { "message": "..." }.
 * Assim o front mostra o texto certo (ex.: "Já existe uma conta com este e-mail.") em vez do
 * nome do status em inglês ("Conflict"). Só trata ResponseStatusException — erros inesperados
 * (500) seguem o fluxo padrão do Spring, SEM expor a mensagem interna.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        String mensagem = ex.getReason() != null ? ex.getReason() : "Não foi possível concluir a ação.";
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("status", ex.getStatusCode().value(), "message", mensagem));
    }
}
