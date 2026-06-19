package com.octral.SecureBox.service;

import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUser(Long id){
        return userRepository.findById(id).orElse(new User( -1L));
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }


}
