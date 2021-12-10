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

    @GetMapping("/")
    public ResponseEntity<Object> getAllUsers() {
        try {
            List<User> allUsers = userRepository.findAll();
            if (allUsers.isEmpty()) return new ResponseEntity<>("Resource not found" + allUsers, HttpStatus.valueOf(404));
            return new ResponseEntity<>(allUsers, HttpStatus.valueOf(200));
        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>(exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>(exception, HttpStatus.valueOf(403));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> retrieveUser(@PathVariable int userId) {
        try {
            Optional<User> fetchedUser = userRepository.findById(userId);

            if (fetchedUser.isEmpty()) return new ResponseEntity<>("Resource not found" + fetchedUser, HttpStatus.valueOf(404));

            EntityModel<User> resource = EntityModel.of(fetchedUser.get());
            WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAllUsers());
            resource.add(linkTo.withRel("all-users"));

            Link selfLink = linkTo(methodOn(this.getClass()).retrieveUser(userId)).withSelfRel();
            resource.add(selfLink);

            return new ResponseEntity<>("Resource successfully fetched" + resource, HttpStatus.valueOf(200));

        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>("Internal Server Error: \n" + exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>("The expectation given in the request's Expect header could not be met: \n" + exception, HttpStatus.valueOf(417));
        }

    }

    @GetMapping("/login")
    public ResponseEntity<Object> authenticateUser(@RequestBody User requestedUser) throws Exception {
        try {
            List<User> allUsers = userRepository.findAll();
            //checks if fetched users does NOT contains an object with requestedUser.getUsername
            if (allUsers.stream().noneMatch(object -> object.getUsername().equals(requestedUser.getUsername())))
                return new ResponseEntity<>("Resource not found", HttpStatus.valueOf(404));

            for (User current : allUsers) {
                if (current.getUsername().equals(requestedUser.getUsername())) {
                    if (BCrypt.checkpw(requestedUser.getPassword(), current.getPassword())) return new ResponseEntity<>("Login validated", HttpStatus.valueOf(200));
                    return new ResponseEntity<>("Cannot access the requested resource", HttpStatus.valueOf(403));
                }
            }

            return new ResponseEntity<>("The request is incorrect or corrupt, and the server can't understand it.", HttpStatus.valueOf(400));
        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>("Internal Server Error: \n" + exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>("The expectation given in the request's Expect header could not be met" + exception, HttpStatus.valueOf(417));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Object> createNewUser(@RequestBody User requestedUser) {
        try {
            requestedUser.setPassword(BCrypt.hashpw(requestedUser.getPassword(), BCrypt.gensalt(10)));
            User newUser = userRepository.save(requestedUser);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getUserId()).toUri();

            return new ResponseEntity<>("The user is created" + location, HttpStatus.valueOf(201));
        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>("Internal Server Error: \n" + exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>("The expectation given in the request's Expect header could not be met" + exception, HttpStatus.valueOf(417));
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
            return new ResponseEntity<>("The server successfully processed the request, but is not returning any content", HttpStatus.valueOf(204));
        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>("Internal Server Error: \n" + exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>("The expectation given in the request's Expect header could not be met" + exception, HttpStatus.valueOf(417));
        }
    }


    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteExistingUser(@PathVariable int userId) {
        try {
            User fetchedUser = userRepository.findById(userId).orElse(null);
            if (fetchedUser == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("HTTP 404: Resource not found");

            userRepository.delete(fetchedUser);

            return new ResponseEntity<>("Resource successfully deleted", HttpStatus.valueOf(200));
        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>("Internal Server Error: \n" + exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>("The expectation given in the request's Expect header could not be met" + exception, HttpStatus.valueOf(417));
        }
    }
}





