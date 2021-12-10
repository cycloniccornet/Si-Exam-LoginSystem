package si.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import si.login.model.User;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import si.login.repository.UserRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody User requestedUser) throws Exception {
        try {
            List<User> allUsers = userRepository.findAll();
            //checks if fetched users does NOT contains an object with requestetUser.getUsername
            if (allUsers.stream().noneMatch(object -> object.getUsername().equals(requestedUser.getUsername())))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("HTTP 404: Resource not found");

            for (User current : allUsers) {
                if (current.getUsername().equals(requestedUser.getUsername())) {
                    if (BCrypt.checkpw(requestedUser.getPassword(), current.getPassword())) return ResponseEntity.status(HttpStatus.OK).body("HTTP 200 OK: The server successfully processed the request");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("HTTP 403: Cannot access the requested resource");
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("HTTP 400 Bad request: The request is incorrect or corrupt, and the server can't understand it.");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("HTTP 417 Expectation: The expectation given in the request's Expect header could not be met." + exception);
        }
    }



    @PutMapping("/updateUserById/{userId}")
    public ResponseEntity<String> updateExistingUser(@PathVariable int userId, @RequestBody User requestedUser) {
        try {
            User changedUser = userRepository.findById(userId).orElse(null);
            if (changedUser == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("HTTP 404: Resource not found");

            changedUser.setUsername(requestedUser.getUsername());
            changedUser.setPassword(BCrypt.hashpw(requestedUser.getPassword(), BCrypt.gensalt(10)));

            userRepository.save(changedUser);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("HTTP 204 No Content: The server successfully processed the request, but is not returning any content");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("HTTP 417 Expectation: The expectation given in the request's Expect header could not be met." + exception);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> createNewUser(@RequestBody User requestedUser) {
        try {
            requestedUser.setPassword(BCrypt.hashpw(requestedUser.getPassword(), BCrypt.gensalt(10)));
            User newUser = userRepository.save(requestedUser);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getUserId()).toUri();

            return ResponseEntity.status(HttpStatus.CREATED).body("HTTP 201: User is created " + location);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("HTTP 417 Expectation: The expectation given in the request's Expect header could not be met." + exception); //Todo: fix path
        }
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteExistingUser(@PathVariable int userId) {
        try {
            User fetchedUser = userRepository.findById(userId).orElse(null);
            if (fetchedUser == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("HTTP 404: Resource not found"); //Todo: User not found error

            userRepository.delete(fetchedUser);

            return ResponseEntity.status(HttpStatus.OK).body("HTTP 200 OK: Resource successfully deleted");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("HTTP 417 Expectation: The expectation given in the request's Expect header could not be met." + exception); //Todo: fix path
        }
    }

}




