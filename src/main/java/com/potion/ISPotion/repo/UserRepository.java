package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    Set<User> findAllByUsername(String username);

}
