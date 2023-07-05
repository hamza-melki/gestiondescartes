package com.securityEcommerce.repository;

import com.securityEcommerce.models.CardHolder;

import com.securityEcommerce.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CardHolderRepository extends JpaRepository<CardHolder, Long> {


    Optional<CardHolder> findById( Long CustomerId);

    boolean existsByCardNumber(String cardNumber);
}