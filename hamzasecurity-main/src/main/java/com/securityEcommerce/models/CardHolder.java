package com.securityEcommerce.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;


    @AllArgsConstructor
    @Getter
    @Setter
    @Entity
    public class CardHolder {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long CustomerId;


        private String HeaderRecord;
        private String BinNumber ;
        private String cardNumber;
        private String UpdateCode;
        private String ProductCardType;
        private String CardHoldersFullName;
        private String CardHoldersAddress1;
        private String CardHoldersAddress2;
        private String CardHoldersAddress3;
        private String ZipCode;
        private String BankAccountNumber1;
        private String BankAccountNumber2;
        private String BranchCode;
        private LocalDate CardBeginDate;
        private LocalDate CardExpiryDate;
        private String CardProcessIndicator;
        private String PinOffSet;
        private String FeesCode;
        private String territoryCode;
        private String JulianDate;
        private long BankIdentificationCode;
        private LocalDate CardHolderBirthDay;
        private String PassportNumber;
        private String CountryCode;
        private String CityCode;
        private String renewOption;
        private String cardholderSourceCode;
        private String CardHoldersCIN;
        private String CardHoldersPhoneNumber;
        private String CardHoldersEmailAddress;
        private boolean PkiIndicator;
        private String acsIndicator;
        private boolean Confirmation=false;
        private Timestamp createdAt;
        private Timestamp updatedAt;


        public CardHolder() {

        }

        @PrePersist
        protected void onCreate() {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
        @PreUpdate
        protected void onUpdate() {
            updatedAt = new Timestamp(System.currentTimeMillis());
        }
    }

