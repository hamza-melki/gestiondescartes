package com.securityEcommerce.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stored_card_holder")
public class StoredCardHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String HeaderRecord;
    private String cardNumber;
    private String CardHoldersFullName;
    private String BankAccountNumber1;
    private String BankAccountNumber2;
    private LocalDate CardBeginDate;
    private LocalDate CardExpiryDate;
    private String JulianDate;
    private long BankIdentificationCode;
    private String PassportNumber;
    private String CardHoldersCIN;
    private String CardHoldersPhoneNumber;
    private String CardHoldersEmailAddress;
}
