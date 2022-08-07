package com.applifting.task.service;

import com.applifting.task.dto.UserDTO;
import com.applifting.task.entity.User;
import com.applifting.task.exception.EmailIsNotValidException;
import com.applifting.task.exception.ParameterMissingException;
import com.applifting.task.exception.UserAlreadyExistsException;
import com.applifting.task.exception.UserDoesNotExistException;
import com.applifting.task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserRepository userRepository;
    private static final String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"; // regex by specification RFC5322

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO userDTO) throws UserAlreadyExistsException, EmailIsNotValidException, ParameterMissingException {
        checkUserParameters(userDTO);
        User user = new User(userDTO.getUsername(), userDTO.getEmail(), userDTO.getAccessToken());
        userRepository.save(user);
        return toDTO(user);
    }

    public void checkUserParameters(UserDTO userDTO) throws ParameterMissingException, UserAlreadyExistsException, EmailIsNotValidException {
        // Checking if necessary parameters have been entered
        if (userDTO.getUsername() == null) {
            throw new ParameterMissingException("Username was not entered.");
        }
        if (userDTO.getAccessToken() == null) {
            throw new ParameterMissingException("Access token was not entered.");
        }
        // Checking if entered parameters are valid
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(userDTO.getUsername()));
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User with nickname " + userDTO.getUsername() + " already exists.");
        }
        optionalUser = Optional.ofNullable(userRepository.findByAccessToken(userDTO.getAccessToken()));
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User with this access token already exists.");
        }
        if (!emailIsValid(userDTO.getEmail())){
            throw new EmailIsNotValidException("Users' email is not valid.");
        }
    }

    public boolean emailIsValid(String email) {
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public User getUserByAccessToken(String accessToken) throws UserDoesNotExistException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByAccessToken(accessToken));
        if (optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User with this access token does not exist.");
        }
        return optionalUser.get();
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAccessToken()
        );
    }
}
