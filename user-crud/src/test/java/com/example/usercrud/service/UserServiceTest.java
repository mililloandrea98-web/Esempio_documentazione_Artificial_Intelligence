package com.example.usercrud.service;

import com.example.usercrud.exception.UserNotFoundException;
import com.example.usercrud.model.User;
import com.example.usercrud.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("Mario Rossi", "mario.rossi@example.com");
        existingUser.setId(1L);
    }

    @Test
    void findAll_restituisceTuttiGliUtenti() {
        User secondUser = new User("Luigi Verdi", "luigi.verdi@example.com");
        secondUser.setId(2L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser, secondUser));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(2).containsExactly(existingUser, secondUser);
    }

    @Test
    void findById_restituisceUtente_quandoEsiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        User result = userService.findById(1L);

        assertThat(result).isEqualTo(existingUser);
    }

    @Test
    void findById_lanciaEccezione_quandoNonEsiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_salvaENuovoUtente() {
        User toCreate = new User("Anna Bianchi", "anna.bianchi@example.com");
        when(userRepository.save(toCreate)).thenReturn(existingUser);

        User result = userService.create(toCreate);

        assertThat(result).isEqualTo(existingUser);
        verify(userRepository, times(1)).save(toCreate);
    }

    @Test
    void update_aggiornaCampiEsalva_quandoUtenteEsiste() {
        User payload = new User("Nome Aggiornato", "nuova.email@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.update(1L, payload);

        assertThat(result.getName()).isEqualTo("Nome Aggiornato");
        assertThat(result.getEmail()).isEqualTo("nuova.email@example.com");
        verify(userRepository).save(existingUser);
    }

    @Test
    void update_lanciaEccezione_quandoUtenteNonEsiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, new User("X", "x@example.com")))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_rimuoveUtente_quandoEsiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.delete(1L);

        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    void delete_lanciaEccezione_quandoUtenteNonEsiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).delete(any(User.class));
    }
}
