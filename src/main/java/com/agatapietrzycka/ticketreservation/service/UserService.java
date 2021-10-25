package com.agatapietrzycka.ticketreservation.service;

import com.agatapietrzycka.ticketreservation.controller.dto.CreateUserDto;
import com.agatapietrzycka.ticketreservation.controller.dto.ResponseDto;
import com.agatapietrzycka.ticketreservation.model.Role;
import com.agatapietrzycka.ticketreservation.model.User;
import com.agatapietrzycka.ticketreservation.model.enums.RoleType;
import com.agatapietrzycka.ticketreservation.repository.RoleRepository;
import com.agatapietrzycka.ticketreservation.repository.UserRepository;
import com.agatapietrzycka.ticketreservation.util.exception.CustomUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public ResponseDto createUser(CreateUserDto createUserDto, Set<RoleType> roles ){
      if(isEmailAlreadyTaken(createUserDto.getEmail())){
          throw new CustomUserException(String.format("Email: %s is already in use!", createUserDto.getEmail()));
      }
        Set<Role> mappedRoles = mapRoles(roles);
        User mappedUser = mapToEntity(createUserDto, mappedRoles);
        List<String> errorMessages = getErrorMessages(mappedUser);
        ResponseDto response = new ResponseDto(null, errorMessages);
        if(errorMessages.isEmpty()){
            roleRepository.saveAll(mappedRoles);
            User user = userRepository.save(mappedUser);
            response.setId(user.getUserId());

        }
        return response;
    }

    private boolean isEmailAlreadyTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    private Set<Role> mapRoles(Set<RoleType> roles){
        return roles.stream()
                .map(this::mapRole)
                .collect(Collectors.toSet());
    }

    private Role mapRole(RoleType roleType){
        Role role = new Role();
        role.setRole(roleType);
        return role;
    }

    private User mapToEntity(CreateUserDto createUserDto, Set<Role> roles) {
        User user = new User();
        user.setActive(true);
        user.setSurname(createUserDto.getSurname());
        user.setName(createUserDto.getName());
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setEmail(createUserDto.getEmail());
        user.setCreatedDate(Instant.now());
        user.setRoles(roles);
        return user;
    }

    private List<String> getErrorMessages(User user) {
        List<String> errorMessages = new ArrayList<>();
        Validator validator = validatorFactory.getValidator();
        validator.validate(user).forEach(err -> errorMessages.add(err.getMessage()));
        return errorMessages;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return user
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Email: %s not found", email)));
    }
}
