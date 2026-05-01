package com.example.estudo_patudos_api_spring_jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarTodosOsPets() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk());
    }

    @Test
    void deveFiltrarPetsPorEspecie() throws Exception {
        mockMvc.perform(get("/api/pets")
                        .param("especie", "Cão")
                        .param("genero", "Fêmea"))
                .andExpect(status().isOk());
    }
}