package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("steveLander")
                .password("12345")
                .email("steve.lander@gmail.com")
                .build();
    }

    @Test
    @DisplayName("Get All Users")
    void givenUsersList_whenGetAllUsers_thenReturnUsersList() {
        List<User> users = Collections.singletonList(user);
        given(userRepository.findAll()).willReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
    }

    @Test
    @DisplayName("Get User By ID - Success")
    void givenUserId_whenGetUserById_thenReturnUserObject() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User retrievedUser = userService.getUserById(1L);

        assertThat(retrievedUser).isEqualTo(user);
    }

    @Test
    @DisplayName("Get User By ID - Not Found")
    void givenNonExistingUserId_whenGetUserById_thenThrowException() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("Get User By Email - Success")
    void givenUserEmail_whenGetUserByEmail_thenReturnUserObject() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        User retrievedUser = userService.getUserByEmail(user.getEmail());

        assertThat(retrievedUser).isEqualTo(user);
    }

    @Test
    @DisplayName("Get User By Email - Not Found")
    void givenNonExistingUserEmail_whenGetUserByEmail_thenThrowException() {
        given(userRepository.findByEmail("not existing email")).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail("not existing email"));
    }

    @Test
    @DisplayName("Save User - Success")
    void givenUserObject_whenSaveUser_thenReturnUserObject() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(user)).willReturn(user);

        User savedUser = userService.saveUser(user);

        assertThat(savedUser).isNotNull();
        assertEquals("12345", savedUser.getPassword());
    }

    @Test
    @DisplayName("Save User - Duplicate Email")
    public void givenExistingUser_whenSaveUser_thenReturnNull() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.ofNullable(user));

        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user)); // Expecting exception

        verify(userRepository, never()).save(any(User.class)); // Verify save was not called
    }

    @Test
    @DisplayName("Update User - Success")
    void givenUserObject_whenUpdateUser_thenReturnUpdatedUserObject() {
        User user1 = user;
        user1.setEmail("ram@gmail.com");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user)); // User exists
        given(userRepository.save(any(User.class))).willReturn(user1); // Return updated user

        User updatedUser = userService.updateUser(user1);

        assertThat(updatedUser.getEmail()).isEqualTo("ram@gmail.com");
    }

    @Test
    @DisplayName("Update User - Not Found")
    void givenNonExistingUser_whenUpdateUser_thenThrowException() {
        User nonExistingUser = user;
        nonExistingUser.setId(99L);
        given(userRepository.findById(nonExistingUser.getId())).willReturn(Optional.empty()); //User does not exist

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(nonExistingUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Delete User - Success")
    void givenUserId_whenDeleteUser_thenVerifyDeletion() {
        willDoNothing().given(userRepository).deleteById(user.getId());

        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }
}