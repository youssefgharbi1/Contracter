package com.project.contracter.service.serviceInterface;


import com.project.contracter.dtos.user.UserCreateDTO;
import com.project.contracter.dtos.user.UserDTO;
import com.project.contracter.exception.BadRequestException;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.model.User;

import java.util.Optional;

public interface UserServiceI {
    UserDTO createUser(UserCreateDTO dto) throws ConflictException, BadRequestException;
    UserDTO getById(Long id) throws ResourceNotFoundException;
    Optional<User> findEntityById(Long id);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
}

