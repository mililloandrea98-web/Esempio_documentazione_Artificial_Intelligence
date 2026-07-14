package com.example.usercrud.controller;

import com.example.usercrud.exception.GlobalExceptionHandler;
import com.example.usercrud.exception.UserNotFoundException;
import com.example.usercrud.model.User;
import com.example.usercrud.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_restituisce200EListaUtenti() throws Exception {
        User user = new User("Mario Rossi", "mario.rossi@example.com");
        user.setId(1L);
        when(userService.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Mario Rossi"));
    }

    @Test
    void getById_restituisce200EUtente_quandoEsiste() throws Exception {
        User user = new User("Mario Rossi", "mario.rossi@example.com");
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mario.rossi@example.com"));
    }

    @Test
    void getById_restituisce404_quandoUtenteNonEsiste() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Utente non trovato con id: 99"));
    }

    @Test
    void create_restituisce201EUtenteCreato_quandoDatiValidi() throws Exception {
        User payload = new User("Anna Bianchi", "anna.bianchi@example.com");
        User created = new User("Anna Bianchi", "anna.bianchi@example.com");
        created.setId(5L);
        when(userService.create(any(User.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void create_restituisce400_quandoNomeMancante() throws Exception {
        User payload = new User("", "anna.bianchi@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_restituisce400_quandoEmailNonValida() throws Exception {
        User payload = new User("Anna Bianchi", "email-non-valida");

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_restituisce200EUtenteAggiornato() throws Exception {
        User payload = new User("Nome Nuovo", "nuovo@example.com");
        User updated = new User("Nome Nuovo", "nuovo@example.com");
        updated.setId(1L);
        when(userService.update(eq(1L), any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Nuovo"));
    }

    @Test
    void update_restituisce404_quandoUtenteNonEsiste() throws Exception {
        User payload = new User("Nome Nuovo", "nuovo@example.com");
        when(userService.update(eq(99L), any(User.class))).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(put("/api/users/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_restituisce204_quandoUtenteEsiste() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    void delete_restituisce404_quandoUtenteNonEsiste() throws Exception {
        org.mockito.Mockito.doThrow(new UserNotFoundException(99L)).when(userService).delete(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
