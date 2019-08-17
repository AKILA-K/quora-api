package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


@Repository
public class AdminDao {

    @PersistenceContext
    private EntityManager entityManager;


    public String deleteUser(final String uuid) throws UserNotFoundException{
        UserEntity deletedUserEntity = entityManager.createNamedQuery("userByUuid", UserEntity.class)
                .setParameter("uuid", uuid).getSingleResult();

        String deletedUserUuid = uuid;
        if(deletedUserEntity!=null) {
            Integer deletedUserId = deletedUserEntity.getId();
            UserAuthTokenEntity deletedUserAuthToken = entityManager.createNamedQuery("userAuthTokenByUserId", UserAuthTokenEntity.class)
                    .setParameter("user", deletedUserEntity).getSingleResult();
            entityManager.remove(deletedUserAuthToken);
            entityManager.remove(deletedUserEntity);

            return deletedUserUuid;
        } else {
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }


    }

}
