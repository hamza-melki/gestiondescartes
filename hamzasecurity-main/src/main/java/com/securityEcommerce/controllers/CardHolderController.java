package com.securityEcommerce.controllers;

import com.securityEcommerce.models.CardHolder;
import com.securityEcommerce.models.User;
import com.securityEcommerce.repository.UserRepository;
import com.securityEcommerce.security.jwt.JwtUtils;
import com.securityEcommerce.security.services.RefreshTokenService;
import com.securityEcommerce.security.services.UserDetailsImpl;
import com.securityEcommerce.service.Interface.IcardHolderService;
import com.securityEcommerce.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class CardHolderController {
    @Autowired
    IcardHolderService icardHolderService;
    @Autowired
    UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/CardHolder")
    public ResponseEntity<?> CreateCardDataINPut( @RequestBody CardHolder cardHolder, @RequestHeader String RefrechTokenV) {
       String usernam = userService.getCurrentUser().getUsername();
       Long userid= userService.getCurrentUser().getId();
        Optional<User> optionalUser = userRepository.findByUsername(usernam);
        if (optionalUser.isPresent()) {
            if (userService.isUserIdMatchingToken(userid,RefrechTokenV)){
                CardHolder addedCarddatainPut = icardHolderService.CreateCardDataINPut(cardHolder);
                return new ResponseEntity<>(addedCarddatainPut, HttpStatus.CREATED);
            } else {
                // Token is invalid or expired, return an error response
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("open an other session refrechtoken invalid ");
            }
         }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid Token");
            }

    }


    @GetMapping("/CardHolder/{CustomerId}")
    public ResponseEntity<Object> getCardHolderById(@PathVariable Long CustomerId){
        CardHolder cardHolder = icardHolderService.getCardHolderById(CustomerId);
        if (cardHolder != null) {
            return new ResponseEntity<>(cardHolder, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Card holder not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/CardHolder")
    public ResponseEntity<List<CardHolder>> getAllCardHolders() {
        List<CardHolder> cardHolders = icardHolderService.getAllCardHolders();
        return new ResponseEntity<>(cardHolders, HttpStatus.OK);
    }

    @GetMapping("/CardHolder/day/{fixedDay}")
    public ResponseEntity<?> getCardHoldersByDay(@PathVariable LocalDate fixedDay) {
        List<CardHolder> cardHolders = icardHolderService.getCardHoldersByDay(fixedDay);
        if (cardHolders.isEmpty()) {
            String message = "No card holders found for the specified date.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return new ResponseEntity<>(cardHolders, HttpStatus.OK);
    }


    @PostMapping("/update-stored")
    public ResponseEntity<String> updateStoredCardHolders() {
        icardHolderService.updateStoredCardHolders();
        return ResponseEntity.ok("Stored card holders updated successfully.");
    }
}

