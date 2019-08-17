package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminService;
import com.upgrad.quora.service.business.SignoutService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SignoutService signoutService;


    @RequestMapping(method = RequestMethod.POST, path = "/admin/user/{userId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") final String uuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
//        System.out.println("TEST");
        if(signoutService.hasUserSignedIn(authorization)) {
            if(signoutService.isUserAccessTokenValid(authorization)) {
                String UUID = adminService.deleteUser(uuid, authorization);
                //   System.out.println(userEntity.getUuid());
                final UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(UUID).status("USER SUCCESSFULLY DELETED");
                return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

    }
}
