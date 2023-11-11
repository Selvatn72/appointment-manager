package com.aptmgt.admin.services;


import com.aptmgt.commons.model.UserEntity;

public interface UserService {

    UserEntity getUserByUsername(String username);

    UserEntity saveUser(UserEntity user);

    UserEntity getUserByEmail(String username);
}
