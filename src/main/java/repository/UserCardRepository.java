package com.card.calculator.repository;

import com.card.calculator.domain.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    Optional<UserCard> findByUserNum(String userNum);
}