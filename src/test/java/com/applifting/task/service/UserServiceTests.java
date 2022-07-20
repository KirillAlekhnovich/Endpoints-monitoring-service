package com.applifting.task.service;

import com.applifting.task.dto.UserDTO;
import com.applifting.task.entity.User;
import com.applifting.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User("Batman", "batman@example.com", "dcb20f8a-5657-4f1b-9f7f-ce65739b359e");
        UserDTO userDTO = new UserDTO(1L, "Batman", "batman@example.com", "dcb20f8a-5657-4f1b-9f7f-ce65739b359e");
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO returnedDTO = userService.createUser(userDTO);
        assertEquals(userDTO.getUsername(), returnedDTO.getUsername());
        assertEquals(userDTO.getEmail(), returnedDTO.getEmail());
        assertEquals(userDTO.getAccessToken(), returnedDTO.getAccessToken());

        verify(userRepository, times(1)).save(any(User.class));
    }
}
