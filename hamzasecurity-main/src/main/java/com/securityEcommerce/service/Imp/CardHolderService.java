package com.securityEcommerce.service.Imp;

import com.securityEcommerce.models.CardHolder;
import com.securityEcommerce.models.StoredCardHolder;
import com.securityEcommerce.repository.CardHolderRepository;
import com.securityEcommerce.repository.StoredCardHolderRepository;
import com.securityEcommerce.service.Interface.IcardHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CardHolderService implements IcardHolderService {
    @Autowired
    CardHolderRepository cardHolderRepository;
    @Autowired
    StoredCardHolderRepository storedCardHolderRepository;

    private static final String ENCRYPTION_KEY = "0123456789ABCDEF0123456789ABCDEF";
    @Override
    public CardHolder CreateCardDataINPut(CardHolder cardHolder) {
        //cardHolder.setBinNumber(cardHolder.getBinNumber());
        String cardNumber = generateUniqueCardNumber(cardHolder.getBinNumber());
        cardHolder.setCardNumber(encrypt(cardNumber));
        cardHolder.setBankAccountNumber1(encrypt(cardHolder.getBankAccountNumber1()));

        cardHolder.setBankAccountNumber2(encrypt(cardHolder.getBankAccountNumber2()));
        cardHolder.setPassportNumber(cardHolder.getPassportNumber());
        cardHolderRepository.save(cardHolder);
        return cardHolder;
    }



    @Override
    public CardHolder getCardHolderById(Long customerId) {
        CardHolder cardHolder = cardHolderRepository.findById(customerId).orElse(null);
        if (cardHolder != null) {
            // Decrypt the required fields
            cardHolder.setCardNumber(decrypt(cardHolder.getCardNumber()));
            cardHolder.setBankAccountNumber1(decrypt(cardHolder.getBankAccountNumber1()));
            cardHolder.setBankAccountNumber2(decrypt(cardHolder.getBankAccountNumber2()));
            cardHolder.setPassportNumber(decrypt(cardHolder.getPassportNumber()));
            // Decrypt other fields as needed

            return cardHolder;
        }
        return null;
    }
    @Override
    public List<CardHolder> getAllCardHolders() {
        // Assuming you have a repository for accessing the CardHolder entities
        return cardHolderRepository.findAll();
    }
    @Override
    public List<CardHolder> getCardHoldersByDay(LocalDate fixedDay) {
        // Assuming you have a method in the service layer to retrieve all cardholders
        List<CardHolder> allCardHolders = getAllCardHolders();

        // Filter the cardholders based on the fixed day
        List<CardHolder> cardHoldersForFixedDay = allCardHolders.stream()
                .filter(cardHolder -> cardHolder.getCreatedAt().toLocalDateTime().toLocalDate().equals(fixedDay))
                .collect(Collectors.toList());

        return cardHoldersForFixedDay;
    }

    @Override
    public String generateUniqueCardNumber(String bin) {
        String cardNumber;
        String Fullcardnumber;
        boolean isUnique = false;

        do {
            cardNumber = bin + generateCardNumber();
            Fullcardnumber= cardNumber + generateCheckDigitusingLuhn(cardNumber);
            boolean exists = cardHolderRepository.existsByCardNumber(Fullcardnumber);
            if (!exists) {
                isUnique = true;
            }
        } while (!isUnique);

        return Fullcardnumber;
    }

    @Override
    public String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumberBuilder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            int digit = random.nextInt(10);
            cardNumberBuilder.append(digit);
        }
        return cardNumberBuilder.toString();
    }


    public static int generateCheckDigitusingLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        int checkDigit = (sum * 9) % 10;
        return checkDigit;
    }

    // Method to encrypt data using AES
    private String encrypt(String data) {
        try {
            SecretKey key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private String decrypt(String encryptedData) {
        try {
            SecretKey key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

}
    private String decryptCardNumber(String encryptedCardNumber) {
        String decryptedCardNumber = decrypt(encryptedCardNumber);
        if (decryptedCardNumber != null) {
            return decryptedCardNumber.trim();
        }
        return null;
    }


    ///////////////////////Stored cardholder

    @Override
  // @Scheduled(cron = "0 0 0 * * *") // Run daily at 00:00:00
    @Scheduled(cron = "0 0 23 * * *") // Run daily at 00:00:00
    public void updateStoredCardHolders() {
        LocalDate currentDate = LocalDate.now();
        LocalDate previousDate = currentDate.minusDays(1);

        // Update for current date
        updateStoredCardHolders(currentDate);

        // Update for previous date
        updateStoredCardHolders(previousDate);
    }

    public void updateStoredCardHolders(LocalDate date) {
        List<CardHolder> cardHolders = getCardHoldersByDay(date);
        for (CardHolder cardHolder : cardHolders) {
            String cardnumber = cardHolder.getCardNumber();
            boolean exists = storedCardHolderRepository.existsByCardNumber(cardnumber);
            if (!exists && cardHolder.isConfirmation()) {
                StoredCardHolder storedCardHolder = new StoredCardHolder();
                storedCardHolder.setCardNumber(cardHolder.getCardNumber());
                storedCardHolder.setBankAccountNumber1(cardHolder.getBankAccountNumber1());
                storedCardHolder.setBankAccountNumber2(cardHolder.getBankAccountNumber2());
                storedCardHolder.setCardHoldersFullName(cardHolder.getCardHoldersFullName());
                storedCardHolder.setCardBeginDate(cardHolder.getCardBeginDate());
                storedCardHolder.setCardExpiryDate(cardHolder.getCardExpiryDate());
                storedCardHolder.setCardHoldersCIN(cardHolder.getCardHoldersCIN());
                storedCardHolder.setCardHoldersEmailAddress(cardHolder.getCardHoldersEmailAddress());
                storedCardHolder.setCardHoldersPhoneNumber(cardHolder.getCardHoldersPhoneNumber());
                storedCardHolder.setPassportNumber(cardHolder.getPassportNumber());
                storedCardHolder.setJulianDate(cardHolder.getJulianDate());

                // Set other attributes from cardHolder to storedCardHolder as needed
                storedCardHolderRepository.save(storedCardHolder);
                // }
            }

        }

    }

}

