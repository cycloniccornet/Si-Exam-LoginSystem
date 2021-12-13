package si.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import si.login.model.User;
import si.login.repository.UserRepository;
import si.login.service.KafkaService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaService kafkaService;

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
            kafkaService.sendUpdateUserTopic(changedUser);
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
            kafkaService.sendDeleteUserTopic(fetchedUser);
            return new ResponseEntity<>("Resource successfully deleted", HttpStatus.valueOf(200));
        } catch (Exception exception) {
            if (exception.toString().contains("could not extract ResultSet")) return new ResponseEntity<>("Internal Server Error: \n" + exception, HttpStatus.valueOf(500));
            return new ResponseEntity<>("The expectation given in the request's Expect header could not be met" + exception, HttpStatus.valueOf(417));
        }
    }
}
