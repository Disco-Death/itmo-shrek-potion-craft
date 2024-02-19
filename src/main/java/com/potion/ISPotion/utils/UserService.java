package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Iterable<User> findAllExecutors() {
        List<User> users = (List<User>) userRepository.findAll();

        return users.stream()
                .filter(user -> user.getRoles().contains(Role.EMPLOYEE))
                .collect(Collectors.toList());
    }
}
