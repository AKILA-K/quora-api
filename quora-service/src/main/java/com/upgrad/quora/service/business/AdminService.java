package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AdminDao adminDao;

    /*This method propogates the transaction of deleting the user in the database if the signed in user is an admin
     and the user that has to be deleted has valid accesstoken*/

    /*The annotation @Transactional esatblishs the connection, performs the operation and commits the transaction*/

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userUuid, final String authorization) throws AuthorizationFailedException, UserNotFoundException {


        if(userDao.hasUserSignedIn(authorization)){
            if(userDao.isUserAccessTokenValid(authorization)){
                if (userDao.isRoleAdmin(authorization)) {
                    UserEntity userEntity = userDao.getUserById(userUuid);
                    if(userEntity== null){
                        throw new UserNotFoundException("USR-001","User with the entered Uuid to be deleted does not exist");
                    } else {
                        return adminDao.deleteUser(userUuid);
                    }
                } else {
                    throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out");
            }
        } else{
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }
}
