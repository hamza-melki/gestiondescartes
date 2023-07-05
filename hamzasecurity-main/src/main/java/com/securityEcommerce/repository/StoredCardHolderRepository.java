package com.securityEcommerce.repository;

import com.securityEcommerce.models.CardHolder;
import com.securityEcommerce.models.StoredCardHolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredCardHolderRepository extends JpaRepository<StoredCardHolder, Long> {

    boolean existsByCardNumber(String cardNumber);


}
