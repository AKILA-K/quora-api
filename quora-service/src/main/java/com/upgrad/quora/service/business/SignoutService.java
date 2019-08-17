package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignoutService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public String signOut (final String authorization) throws SignOutRestrictedException {

        //Check if the user has an access token and it is valid
        if(userDao.hasUserSignedIn(authorization) && userDao.isUserAccessTokenValid(authorization)){
            return userDao.signOut(authorization);
        } else {
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
    }

}
