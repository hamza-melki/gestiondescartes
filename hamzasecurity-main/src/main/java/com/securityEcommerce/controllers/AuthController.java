package com.securityEcommerce.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.securityEcommerce.models.*;
import com.securityEcommerce.payload.request.LoginRequest;
import com.securityEcommerce.payload.request.SignupRequest;
import com.securityEcommerce.payload.request.TokenRefreshRequest;
import com.securityEcommerce.payload.response.JwtResponse;
import com.securityEcommerce.payload.response.MessageResponse;
import com.securityEcommerce.payload.response.TokenRefreshResponse;
import com.securityEcommerce.repository.RoleRepository;
import com.securityEcommerce.repository.UserRepository;
import com.securityEcommerce.security.jwt.JwtUtils;
import com.securityEcommerce.security.services.RefreshTokenService;
import com.securityEcommerce.security.services.UserDetailsImpl;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.securityEcommerce.exception.TokenRefreshException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;



  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    Optional<User> u=userRepository.findByUsername(loginRequest.getUsername()) ;
    //if(u.get().getConfirme()==true) {


      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

      String jwt = jwtUtils.generateJwtToken(userDetails);

      List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
              .collect(Collectors.toList());

      RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

      return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
              userDetails.getUsername(), userDetails.getEmail(), roles));
  //  }
   // else {
     // throw new RuntimeException("user not confirmed");
    //}
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_User)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "PROVIDER":
          Role adminRole = roleRepository.findByName(ERole.ROLE_Admin)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_User)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        })
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
            "Refresh token is not in database!"));
  }
  
  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = userDetails.getId();
    refreshTokenService.deleteByUserId(userId);
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }

 /* @GetMapping("/confirm")
  public ResponseEntity<?>confirmuser(@RequestParam String email){
    User user =userRepository.findByEmail(email);
    if(user!=null){
     user.setConfirme(true);
     userRepository.save(user);
      return ResponseEntity.ok(new MessageResponse("User confirmed"));

    }
    else{
      return ResponseEntity.ok(new MessageResponse("Error"));
    }
  }*/
 /* @PostMapping("/forgetpassword")
  public HashMap<String,String> resetPassword(String email) {
    HashMap message = new HashMap();
    User userexisting = userRepository.findByEmail(email);
    if (userexisting == null) {
      message.put("user", "user not found");
      return message;
    }
    UUID token = UUID.randomUUID();
    userexisting.setPasswordResetToken(token.toString());
    userexisting.setId(userexisting.getId());
    Email mail = new Email();
    mail.setContent("votre nouveau token est : " +userexisting.getPasswordResetToken());
    mail.setFrom("itgate@gmail.com");
    mail.setTo(userexisting.getEmail());
    mail.setSubject("Reset password");
    emailService.sendSimpleMessage(mail);
    userRepository.saveAndFlush(userexisting);
    message.put("user", "user found , check your email");

    return message;

  }

  @PostMapping("/savePassword/{passwordResetToken}")
  public HashMap<String,String> savePassword(@PathVariable String passwordResetToken, String newPassword) {

    User userexisting = userRepository.findByPasswordResetToken(passwordResetToken);
    HashMap message = new HashMap();

    if (userexisting != null) {
      userexisting.setId(userexisting.getId());
      userexisting.setPassword(new BCryptPasswordEncoder().encode(newPassword));
      userexisting.setPasswordResetToken(null);
      userRepository.save(userexisting);
      message.put("resetpassword", "proccesed");
      return message;

    } else {
      message.put("resetpassword", "failed");
      return message;

    }
  }*/

}
