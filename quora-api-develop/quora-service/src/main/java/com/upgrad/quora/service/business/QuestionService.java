package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.QuestionNotFoundException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.type.ActionType;
import com.upgrad.quora.service.type.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;




@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;


   // creates question instance in database.

    @Transactional(propagation = Propagation.REQUIRED)
    public Question createQuestion(Question question) {
        return questionDao.createQuestion(question);
    }


    // gets questions for a authorized user

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Question> getQuestionsForUser(final String uuId) throws QuestionNotFoundException, UserNotFoundException {
        UserEntity user = userDao.getUser(uuId);
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        List<Question> questionList = questionDao.getAllQuestionsForUser(user.getUuid());
        if (questionList == null) {
            throw new QuestionNotFoundException("QUER-001", "No questions found for user");
        } else {
            return questionList;
        }
    }
        // gets all questions for any user

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Question> getAllQuestions() throws QuestionNotFoundException {
        List<Question> questionList = questionDao.getAllQuestions();
        if (questionList == null) {
            throw new QuestionNotFoundException("QUER-002", "No questions found for any user");
        } else {
            return questionList;
        }
    }


   // checks if the Question is asked by the owner

    @Transactional(propagation = Propagation.REQUIRED)
    public Question isUserQuestionOwner(String questionUuId, UserAuthEntity authorizedUser, ActionType actionType) throws AuthorizationFailedException, InvalidQuestionException {
        Question question = questionDao.getQuestion(questionUuId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else if (!question.getUser().getUuid().equals(authorizedUser.getUser().getUuid())) {
            if (actionType.equals(ActionType.DELETE_QUESTION)) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
            }
        } else if (!authorizedUser.getUser().getRole().equals(RoleType.admin)
                && !question.getUser().getUuid().equals(authorizedUser.getUser().getUuid())
                && actionType.equals(ActionType.DELETE_QUESTION)) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        } else {
            return question;
        }
    }

    //to edit the Question

    @Transactional(propagation = Propagation.REQUIRED)
    public void editQuestion(Question question) {
        questionDao.editQuestion(question);
    }


     // deleting the question

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(Question question) {
        questionDao.deleteQuestion(question);
    }


     // getting object for the specific question uuid and if the Question is not found, throws InvalidQuestionException.

    public Question getQuestionForUuId(String questionUuId) throws InvalidQuestionException,AuthorizationFailedException {
        Question question = questionDao.getQuestion(questionUuId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        } else {
            return question;
        }
    }
}
