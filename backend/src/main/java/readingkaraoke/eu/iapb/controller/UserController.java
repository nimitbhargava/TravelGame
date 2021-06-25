package readingkaraoke.eu.iapb.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import readingkaraoke.eu.iapb.entity.User;
import readingkaraoke.eu.iapb.entity.response.SessionResponse;
import readingkaraoke.eu.iapb.exception.RegistrationException;
import readingkaraoke.eu.iapb.exception.UserException;
import readingkaraoke.eu.iapb.service.SessionService;
import readingkaraoke.eu.iapb.service.UserService;

import java.io.IOException;


@CrossOrigin(origins = {"https://readingkaraoke.xyz", "http://localhost:8081"})
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    SessionService sessionService;


    @PostMapping("/login")
    public ResponseEntity<SessionResponse> userLogin(@RequestBody User user) throws IOException {
        try {
            user = userService.getUser(user);
            SessionResponse session = sessionService.startNewSession(user);
            return new ResponseEntity<>(session, HttpStatus.OK);
        } catch (UserException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> userRegister(@RequestBody User newUser) throws IOException {
        try {
            newUser = userService.registerNewUser(newUser);
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } catch(RegistrationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

}
