package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Persisting user entity. Done when the user signs up
    public UserEntity createUser (UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }

    }

    //Get user details by user's username
    //Returns UserEntity
    public UserEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class)
                    .setParameter("username", username).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }

    }

    //Get user details by user UUID
    //Returns UserEntity
    public UserEntity getUserById(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (Exception exc) {
            return null;
        }

    }

    //Creating access token entry when the user signs in
    //Returns UserAuthTokenEntity
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    //Signout function
    //Returns UUID of the signed out user
    public String signOut(final String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        final ZonedDateTime now = ZonedDateTime.now();

        Integer userId = userAuthTokenEntity.getUser().getId();
        userAuthTokenEntity.setLogoutAt(now);
        entityManager.merge(userAuthTokenEntity);
        UserEntity userEntity = entityManager.createNamedQuery("userById", UserEntity.class)
                    .setParameter("id", userId).getSingleResult();
        return userEntity.getUuid();
     }

    //To check if the user with the access token is signed in / access token exists in the table
    //Returns boolean based on whether the access token is present in the table
    public boolean hasUserSignedIn(final String accessToken) {
        UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        if(userAuthTokenEntity!=null){
            return true;
        } else {
            return false;
        }
    }

    //To check if the user has a valid acces token / access token exists and is valid
    //Returns boolean based on 2 factors: The expires_at time is greater than current time and LogoutAt is null
    public boolean isUserAccessTokenValid(final String accessToken) {
        UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        final ZonedDateTime now = ZonedDateTime.now();
        if(userAuthTokenEntity.getLogoutAt()==null && userAuthTokenEntity.getExpiresAt().compareTo(now)>=0){
            return true;
        } else {
            return false;
        }
    }

    //To check if the user corresponding to this access token is has admin role
    //Return boolean based on the value in the "role" field
    public boolean isRoleAdmin(final String accessToken) {
        UserAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("userByAccessToken", UserAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if(userEntity.getRole().equalsIgnoreCase("admin")){
            return true;
        } else {
            return false;
        }
    }

    //Update user details
    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }



    public UserAuthTokenEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch(NoResultException exc){
            return null;
        }

    }
}
