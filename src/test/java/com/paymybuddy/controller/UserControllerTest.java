package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .build();
    }

    @Test
    @DisplayName("Get All Users - Success")
    void givenUsers_whenGetAllUsers_thenReturnUserList() {
        given(userService.getAllUsers()).willReturn(List.of(user));

        ResponseEntity<List<User>> result = userController.getAllUsers();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(List.of(user));
    }

    @Test
    @DisplayName("Get User By Id - Success")
    void givenUserId_whenGetUserById_thenReturnUser() {
        given(userService.getUserById(user.getId())).willReturn(user); // User exists

        ResponseEntity<User> result = userController.getUserById(user.getId());

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(user);
    }

    @Test
    @DisplayName("Get User By Id - Not Found")
    void givenNonExistingUserId_whenGetUserById_thenReturnNotFound() {
        given(userService.getUserById(any(Long.class))).willThrow(new EntityNotFoundException("User not found"));

        ResponseEntity<User> result = userController.getUserById(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(404); //Assert status code 404 not found
    }

    @Test
    @DisplayName("Update User - Success")
    void givenUser_whenUpdateUser_thenReturnUpdatedUser() {
        User user1 = user;
        user1.setEmail("updated@test.com");
        given(userService.updateUser(user1)).willReturn(user1);

        ResponseEntity<User> updatedUser = userController.updateUser(1L, user1);


        assertThat(updatedUser.getStatusCode().value()).isEqualTo(200);
        assertThat(updatedUser.getBody()).isEqualTo(user1);
    }

    @Test
    @DisplayName("Update User - Not Found")
    void givenNonExistingUser_whenUpdateUser_thenReturnNotFound() {
        given(userService.updateUser(user)).willThrow(new EntityNotFoundException("not found"));

        ResponseEntity<User> result = userController.updateUser(99L, user); // Non-existing ID

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @DisplayName("Delete User - Success")
    void givenUserId_whenDeleteUser_thenReturnOk() {
        willDoNothing().given(userService).deleteUser(user.getId());

        ResponseEntity<String> result = userController.deleteUser(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("User deleted successfully");
    }

    @Test
    @DisplayName("Delete User - Not Found")
    void givenNonExistingUserId_whenDeleteUser_thenReturnBadRequest() {
        willThrow(new IllegalArgumentException("User not found")).given(userService).deleteUser(99L);

        ResponseEntity<String> result = userController.deleteUser(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("User not found");
    }

    /*@Test
    @DisplayName("Add Connection - Success")
    void givenUserIdAndConnectionId_whenAddConnection_thenReturnOk() {

        ResponseEntity<String> response = userController.addConnection(1L, connectionRequestDto);

        assertThat(response.getStatusCode().value()).isEqualTo(200); //Assert response is OK
        assertThat(response.getBody()).isEqualTo("Connection added successfully");
    }

    @Test
    @DisplayName("Add Connection - User Not Found")
    void givenNonExistingUser_whenAddConnection_thenReturnBadRequest() {
        when(userService.addConnection(99L, 2L))
                .thenThrow(new EntityNotFoundException("User not found with id: 99"));//Simulate exception

        ResponseEntity<String> response = userController.addConnection(99L, connectionRequestDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(400); //Bad request
        assertThat(response.getBody()).isEqualTo("User not found with id: 99");

    }

    @Test
    @DisplayName("Add Connection - Already Exists")
    void givenExistingConnection_whenAddConnection_thenReturnBadRequest() {
        when(userService.addConnection(1L, 2L))
                .thenThrow(new IllegalArgumentException("Connection already exists."));

        ResponseEntity<String> response = userController.addConnection(1L, connectionRequestDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(400); //Bad request
        assertThat(response.getBody()).isEqualTo("Connection already exists.");
    }*/

}