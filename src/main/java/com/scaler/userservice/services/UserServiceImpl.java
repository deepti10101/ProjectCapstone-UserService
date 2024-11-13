package com.scaler.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.userservice.dtos.SendEmailDto;
import com.scaler.userservice.exception.UserAlreadyExistException;
import com.scaler.userservice.models.Token;
import com.scaler.userservice.models.User;
import com.scaler.userservice.repositories.TokenRepository;
import com.scaler.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private TokenRepository tokenRepository;

    private KafkaTemplate<String , String> kafkaTempalte;

    private ObjectMapper objectMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           TokenRepository tokenRepository,
                           KafkaTemplate kafkaTemplate,
                           ObjectMapper objectMapper)
    {
        this.userRepository=userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.tokenRepository= tokenRepository;
        this.kafkaTempalte=kafkaTemplate;
        this.objectMapper=objectMapper;
    }

    @Override
    public User signUp(String name, String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            //throw new UserAlreadyExistException();
            return null;
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        SendEmailDto sendEmailDto= new SendEmailDto();
        sendEmailDto.setFromEmail("rajan.deepthi10@gmail.com");
        sendEmailDto.setToEmail(email);
        sendEmailDto.setBody("Welcome " + name + "! to Scaler");

        String sendEmailDtoString=null;
        try {
             sendEmailDtoString= objectMapper.writeValueAsString(sendEmailDto);
        } catch (JsonProcessingException e) {
            System.out.println("something went wrong in convering object to string ");
            throw new RuntimeException(e);
        }
        kafkaTempalte.send("sendEmail",sendEmailDtoString);

        return savedUser;

    }

    @Override
    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            // TODO: Throw an exception from here like userNotFoundException
            return null;
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password,user.getHashedPassword())){
            // TODO: throw an exception that password is wrong
            return null;
        }
            Token token= createToken(user);
        return tokenRepository.save(token);
    }

    @Override
    public User validate(String token) {
        Optional<Token> tokenOptional=tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(token,
                    false, new Date());
        if(tokenOptional.isEmpty()){
            //Throw new TokenInvalidException
            return null;
        }
        Token tokenval=tokenOptional.get();
        return tokenval.getUser();

    }

    @Override
    public void logout(String token) {
        Optional<Token> tokenOptional=tokenRepository.findByValueAndDeleted(token, false);
        if(tokenOptional.isEmpty()){
            //Throw new TokenInvalidException
            return ;
        }
        Token tokens=tokenOptional.get();
        tokens.setDeleted(true);
        tokenRepository.save(tokens);
    }

    public Token createToken(User user) {
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date dateAfter30Days = calendar.getTime();

        token.setExpiryAt(dateAfter30Days);
        token.setDeleted(false);
        return token;
    }
}
