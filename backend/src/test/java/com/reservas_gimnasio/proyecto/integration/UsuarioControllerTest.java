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
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUsuariosId() throws Exception {
        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void crearUsuario() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Test\",\"email\":\"test@test.com\",\"password\":\"1234\",\"rol\":\"USER\"}"))
                .andExpect(status().isOk());

    }

    @Test
    void actualizarUsuario() throws Exception {
        mockMvc.perform(put("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Test\",\"email\":\"test@test.com\",\"password\":\"1234\",\"rol\":\"USER\"}"))
                .andExpect(status().isOk());

    }

    @Test
    void eliminarUsuario() throws Exception {
        delete("/usuarios/1");
    }

}
