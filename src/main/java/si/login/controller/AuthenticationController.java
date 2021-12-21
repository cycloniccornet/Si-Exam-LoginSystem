package si.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import si.login.model.User;


import org.springframework.security.crypto.bcrypt.BCrypt;
import si.login.service.LoginService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    LoginService loginService;

    @GetMapping("/login")
    public Object authenticateUser(@RequestBody User requestedUser) {
            return loginService.valdiateLogin(requestedUser);
    }

    @PostMapping("/register")
    public Object createNewUser(@RequestBody User requestedUser) {
       return loginService.registerUser(requestedUser);
    }
}