package com.example.springsecurityjwt.service;

import com.example.springsecurityjwt.model.Role;
import com.example.springsecurityjwt.model.User;
import com.example.springsecurityjwt.model.UserPrinciple;
import com.example.springsecurityjwt.model.dto.UserDTO;
import com.example.springsecurityjwt.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> findAll() {
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User u : iUserRepository.findAll()) {
            userDTOS.add(toDTO(u));
        }
        return userDTOS;
    }

    public UserDTO findById(Long id) {
        Optional<User> user = iUserRepository.findById(id);
        return user.map(this::toDTO).orElse(null);
    }

    public User findByUsername(String username) {
        return iUserRepository.findByUsername(username);
    }

    public boolean add(User user) {
        String passwordEncode = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordEncode);
        iUserRepository.save(user);
        return true;
    }

    public void delete(Long id) {
        iUserRepository.deleteById(id);
    }

    public UserDetails loadUserByUsername(String username) {
        User user = iUserRepository.findByUsername(username);
        if (user != null) {
            return UserPrinciple.build(user);
        }
        return null;
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getRoles());
    }
}
