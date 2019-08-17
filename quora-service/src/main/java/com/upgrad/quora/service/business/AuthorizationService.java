package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private UserDao userDao;

    //To check if the user with the access token is signed in / access token exists in the table
    //Returns boolean based on whether the access token is present in the table
    public boolean hasUserSignedIn (final String authorization) {
        return userDao.hasUserSignedIn(authorization);
    }

    //To check if the user has a valid acces token / access token exists and is valid
    //Returns boolean based on 2 factors: The expires_at time is greater than current time and LogoutAt is null
    public boolean isUserAccessTokenValid (final String authorization)  {
        return userDao.isUserAccessTokenValid(authorization);
    }

    public UserAuthTokenEntity isValidActiveAuthToken(final String authorization) throws AuthorizationFailedException{
        return userDao.isValidActiveAuthToken(authorization);

    }


    public UserAuthTokenEntity fetchAuthTokenEntity(final String authorization) throws AuthorizationFailedException {
        final UserAuthTokenEntity fetchedUserAuthTokenEntity = userDao.getUserAuthToken(authorization);
        return fetchedUserAuthTokenEntity;
    }



}
