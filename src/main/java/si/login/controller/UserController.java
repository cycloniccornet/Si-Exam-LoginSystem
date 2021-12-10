package si.login.controller;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import si.login.model.User;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
@RequestMapping("/user")
public class UserController {

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
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {
        try {
            Optional<User> fetchedUser = userRepository.findById(userId);

            if (fetchedUser.isEmpty()) return new ResponseEntity<>("Resource not found" + fetchedUser, HttpStatus.valueOf(404));

            EntityModel<User> resource = EntityModel.of(fetchedUser.get());
            WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAllUsers());
            resource.add(linkTo.withRel("all-users"));

            Link selfLink = linkTo(methodOn(this.getClass()).getUserById(userId)).withSelfRel();
            resource.add(selfLink);

            return new ResponseEntity<>("Resource successfully fetched" + resource, HttpStatus.valueOf(200));

        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>("Internal Server Error: \n" + exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>("The expectation given in the request's Expect header could not be met: \n" + exception, HttpStatus.valueOf(417));
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
