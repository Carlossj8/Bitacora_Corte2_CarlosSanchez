package com.labrasaviva.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getPlatos_sinToken_esPublico_devuelve200() throws Exception {
        mockMvc.perform(get("/api/platos"))
                .andExpect(status().isOk());
    }

    @Test
    void crearPlato_sinToken_devuelve403() throws Exception {
        mockMvc.perform(post("/api/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bandeja Paisa\",\"precio\":28000,\"categoria\":\"Tipico\",\"disponible\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void crearPlato_conRolCliente_devuelve403() throws Exception {
        mockMvc.perform(post("/api/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bandeja Paisa\",\"precio\":28000,\"categoria\":\"Tipico\",\"disponible\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_conCredencialesIncorrectas_devuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@labrasaviva.com\",\"password\":\"clave_erronea\"}"))
                .andExpect(status().isUnauthorized());
    }
}
