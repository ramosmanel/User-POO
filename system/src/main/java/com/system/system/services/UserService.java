package com.system.system.services;

import com.system.system.entities.DTO.LoginDTO;
import com.system.system.entities.User;
import com.system.system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void registerUser(User user){
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) throw new IllegalArgumentException("Email já registrado no banco.");

        //Criptografando a senha do usuário
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<User> loginUser(LoginDTO loginDTO){
        Optional<User> existingUser = userRepository.findByEmail(loginDTO.email());
        if (existingUser.isEmpty()) throw new IllegalArgumentException("Email não registrado no banco.");
        User user = existingUser.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(!encoder.matches(loginDTO.password(), user.getPassword()))
            throw new IllegalArgumentException("A senha digitada está incorreta.");

        return userRepository.findAll();
    }

    public void updateUser(User user){
        User existingUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (existingUser.getPassword().equals("null") || existingUser.getPassword().isEmpty()){
            user.setPassword(existingUser.getPassword());
        }

        user.setPassword(encoder.encode(existingUser.getPassword()));
        user.setId(existingUser.getId());
        userRepository.save(user);
    }

    public void deleteUser(String email){
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        userRepository.delete(existingUser);
    }
}
