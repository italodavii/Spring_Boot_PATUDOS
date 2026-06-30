package com.example.estudo_patudos_api_spring_jpa.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

// Valida o ID token vindo do front (Google Identity Services): confere assinatura (chaves
// públicas do Google), audiência (nosso GOOGLE_CLIENT_ID) e expiração. Retorna o payload ou null.
@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public GoogleIdToken.Payload verificar(String idTokenString) {
        if (idTokenString == null || idTokenString.isBlank()) return null;
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken == null ? null : idToken.getPayload();
        } catch (Exception e) {
            return null; // token inválido/expirado/assinatura errada
        }
    }
}
