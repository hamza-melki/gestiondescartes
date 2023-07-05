package com.securityEcommerce.service.Interface;



import com.securityEcommerce.models.User;

import java.util.List;

public interface UserService {
    User CreateUser(User user);
    User UpdateUser(User user);

    List<User>AllUser();
    User GetbyId (Long id);
    void DeleteUser(Long id);

    User getCurrentUser();

    boolean isUserIdMatchingToken(Long userId, String token);
}
