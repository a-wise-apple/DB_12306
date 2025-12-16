package com.example.railway.service;

import com.example.railway.domain.UserAccount;
import com.example.railway.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public Optional<UserAccount> findByUsername(String username) {
        // Assuming 'name' is the username for now, or we can use idNumber/phone
        return userAccountRepository.findByName(username);
    }
    
    public Optional<UserAccount> findByIdNumber(String idNumber) {
        return userAccountRepository.findByIdNumber(idNumber);
    }

    public Optional<UserAccount> findById(Integer id) {
        return userAccountRepository.findById(id);
    }

    public UserAccount register(UserAccount user) {
        // In a real app, we would hash the password here
        return userAccountRepository.save(user);
    }
}
