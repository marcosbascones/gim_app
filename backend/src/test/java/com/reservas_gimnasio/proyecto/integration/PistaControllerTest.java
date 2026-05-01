package com.reservas_gimnasio.proyecto.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPistas() throws Exception {
        mockMvc.perform(get("/pistas"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPistaPorId() throws Exception {
        mockMvc.perform(get("/pistas/1"))
                .andExpect(status().isOk());
    }

    @Test
    void crearPista() throws Exception {
        mockMvc.perform(post("/pistas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Pista Test\",\"deporte\":\"TENIS\",\"activa\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarPista() throws Exception {
        mockMvc.perform(put("/pistas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Pista Test Actualizada\",\"deporte\":\"PADEL\",\"activa\":false}"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarPista() throws Exception {
        mockMvc.perform(delete("/pistas/1"))
                .andExpect(status().isOk());
    }
}