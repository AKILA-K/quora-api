package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.business.UserProfileService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


// Controller class for question-operations.

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    SignupBusinessService signupBusinessService;

    @Autowired
    QuestionService questionService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserProfileService userProfileService;



    // Rest Endpoint method implementation used for creating question for authorized user.Only logged-in user is allowed to create a question.

    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
            public ResponseEntity<?> createQuestion(final QuestionRequest questionRequest,
            @RequestHeader final String authorization)
            throws AuthorizationFailedException {
        if (authorizationService.hasUserSignedIn(authorization)) {
            if (authorizationService.isUserAccessTokenValid(authorization)) {
                UserAuthTokenEntity userAuthTokenEntity = authorizationService.fetchAuthTokenEntity(authorization);
                UserEntity user = userAuthTokenEntity.getUser();
                Question question = new Question();
                question.setUser(userAuthTokenEntity.getUser());
                question.setUuid(UUID.randomUUID().toString());
                question.setContent(questionRequest.getContent());
                final ZonedDateTime now = ZonedDateTime.now();
                question.setDate(now);
                Question createdQuestion = questionService.createQuestion(question);
                QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
                return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }
        // Rest Endpoint method implementation used for getting all questions for authorized user.Only logged in user is allowed to get the details.

        @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<?> getAllQuestions ( @RequestHeader final String authorization) throws
        AuthorizationFailedException {
            UserAuthTokenEntity userAuthTokenEntity = authorizationService.isValidActiveAuthToken(authorization);
                    List<Question> questionList = questionService.getAllQuestions();
                    StringBuilder builder = new StringBuilder();
                    getContentsString(questionList, builder);
                    StringBuilder uuIdBuilder = new StringBuilder();
                    getUuIdString(questionList, uuIdBuilder);
                    QuestionDetailsResponse questionResponse = new QuestionDetailsResponse()
                    .id(uuIdBuilder.toString())
                    .content(builder.toString());
            return new ResponseEntity<QuestionDetailsResponse>(questionResponse, HttpStatus.OK);
        }

        //Rest Endpoint method implementation used for getting all questions for any user.Only logged-in user and the owner of the question is allowed to use this endpoint.

        @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<?> editQuestionContent (QuestionEditRequest questionEditRequest,
        @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws
        AuthorizationFailedException, InvalidQuestionException {
            UserAuthTokenEntity userAuthTokenEntity = authorizationService.isValidActiveAuthToken(authorization);
            Question question = questionService.isUserQuestionOwner(questionId, userAuthTokenEntity, ActionType.EDIT_QUESTION);
            question.setContent(questionEditRequest.getContent());
            questionService.editQuestion(question);
            QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(question.getUuid()).status("QUESTION EDITED");
            return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
        }

        //   Rest Endpoint method implementation used for deleting question by question id.Only logged-in user who is owner of the question or admin is allowed to delete a question

        @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<?> userDelete ( @PathVariable("questionId") final String questionUuId,
        @RequestHeader("authorization") final String authorization) throws
        AuthorizationFailedException, InvalidQuestionException {
            UserAuthTokenEntity userAuthTokenEntity = authorizationService.isValidActiveAuthToken(authorization);
            Question question = questionService.isUserQuestionOwner(questionUuId, userAuthTokenEntity, ActionType.DELETE_QUESTION);
            questionService.deleteQuestion(question);
            QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse()
                    .id(question.getUuid())
                    .status("QUESTION DELETED");
            return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<?> getAllQuestionsByUser ( @PathVariable("userId") final String uuId,
        @RequestHeader("authorization") final String authorization) throws
        AuthorizationFailedException, UserNotFoundException {
            UserAuthTokenEntity userAuthTokenEntity = authorizationService.isValidActiveAuthToken(authorization);
            List<Question> questionList = questionService.getQuestionsForUser(uuId);
            StringBuilder contentBuilder = new StringBuilder();
            StringBuilder uuIdBuilder = new StringBuilder();
            getContentsString(questionList, contentBuilder);
            getUuIdString(questionList, uuIdBuilder);
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse()
                    .id(uuIdBuilder.toString())
                    .content(contentBuilder.toString());
            //return new ResponseEntity<>(questionResponse, HttpStatus.OK);
            return new ResponseEntity<QuestionDetailsResponse>(questionResponse, HttpStatus.OK);
        }

        // private utility method for appending the uuid of questions.

        public static final StringBuilder getUuIdString (List < Question > questionList, StringBuilder uuIdBuilder){

            for (Question questionObject : questionList) {
                uuIdBuilder.append(questionObject.getUuid()).append(",");
            }
            return uuIdBuilder;
        }


        // private utility method for providing contents string in appended format

        public static final StringBuilder getContentsString (List < Question > questionList, StringBuilder builder){
            for (Question questionObject : questionList) {
                builder.append(questionObject.getContent()).append(",");
            }
            return builder;
        }

    }



