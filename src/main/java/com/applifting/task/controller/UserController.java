package com.applifting.task.controller;

import com.applifting.task.dto.UserDTO;
import com.applifting.task.exception.EmailIsNotValidException;
import com.applifting.task.exception.ParameterMissingException;
import com.applifting.task.exception.UserAlreadyExistsException;
import com.applifting.task.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    public final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/users/" + createdUser.getId()).toUri())
                    .body(createdUser);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        } catch (EmailIsNotValidException | ParameterMissingException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
