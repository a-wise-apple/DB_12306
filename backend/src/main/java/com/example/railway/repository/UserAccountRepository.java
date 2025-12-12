package com.example.railway.repository;

import com.example.railway.domain.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

    Optional<UserAccount> findByPhone(String phone);

    Optional<UserAccount> findByIdNumber(String idNumber);
}
