package si.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import si.login.model.User;
import si.login.repository.UserRepository;
import si.login.service.KafkaService;
import si.login.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public ResponseEntity<Object> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {
       return userService.getUserById(userId);
    }

    @PutMapping("/updateUserById/{userId}")
    public ResponseEntity<String> updateExistingUser(@PathVariable int userId, @RequestBody User requestedUser) {
        return userService.updateExistingUser(userId, requestedUser);
    }


    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteExistingUser(@PathVariable int userId) {
        return userService.deleteExistingUser(userId);
    }
}
