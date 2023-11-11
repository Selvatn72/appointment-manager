package com.aptmgt.customer.services;


import com.aptmgt.commons.model.UserEntity;

public interface UserService {

    UserEntity getUserByUsername(String username);

    UserEntity saveUser(UserEntity user);

    UserEntity getUserByEmail(String username);
}
