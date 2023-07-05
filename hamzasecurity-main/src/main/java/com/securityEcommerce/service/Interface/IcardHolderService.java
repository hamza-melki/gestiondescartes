package com.securityEcommerce.service.Interface;

import com.securityEcommerce.models.CardHolder;

import java.time.LocalDate;
import java.util.List;

public interface IcardHolderService {
    CardHolder CreateCardDataINPut(CardHolder cardHolder);

    List<CardHolder>getAllCardHolders();
    CardHolder getCardHolderById(Long customerId);


    List<CardHolder> getCardHoldersByDay(LocalDate fixedDay);

    String generateUniqueCardNumber(String bin);

    String generateCardNumber();

    void updateStoredCardHolders();
}
