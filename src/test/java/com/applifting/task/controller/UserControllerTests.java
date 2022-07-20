package com.applifting.task.controller;

import com.applifting.task.dto.UserDTO;
import com.applifting.task.exception.EmailIsNotValidException;
import com.applifting.task.exception.ParameterMissingException;
import com.applifting.task.exception.UserAlreadyExistsException;
import com.applifting.task.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateUser() throws Exception {
        // Testing if we create user correctly
        UserDTO userDTO = new UserDTO(1L, "Batman", "batman@example.com", "dcb20f8a-5657-4f1b-9f7f-ce65739b359e");
        Mockito.when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Batman\",\"email\":\"batman@example.com\",\"accessToken\":\"dcb20f8a-5657-4f1b-9f7f-ce65739b359e\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", Matchers.is("Batman")))
                .andExpect(jsonPath("$.email", Matchers.is("batman@example.com")))
                .andExpect(jsonPath("$.accessToken", Matchers.is("dcb20f8a-5657-4f1b-9f7f-ce65739b359e")));

        // Testing if we're creating user with existing username or access token
        Mockito.when(userService.createUser(any(UserDTO.class))).thenThrow(UserAlreadyExistsException.class);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Batman\",\"email\":\"batman@example.com\",\"accessToken\":\"dcb20f8a-5657-4f1b-9f7f-ce65739b359e\"}"))
                .andExpect(status().isConflict());

        // Testing if email is valid
        Mockito.when(userService.createUser(any(UserDTO.class))).thenThrow(EmailIsNotValidException.class);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Batman\",\"email\":\"batman@example.com\",\"accessToken\":\"dcb20f8a-5657-4f1b-9f7f-ce65739b359e\"}"))
                .andExpect(status().isNotAcceptable());

        // Testing if no parameters are missing
        Mockito.when(userService.createUser(any(UserDTO.class))).thenThrow(ParameterMissingException.class);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Batman\",\"email\":\"batman@example.com\",\"accessToken\":\"dcb20f8a-5657-4f1b-9f7f-ce65739b359e\"}"))
                .andExpect(status().isNotAcceptable());
    }
}
