package si.login.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String mail;
    private String name;
    private String username;
    private String password;

    @Override
    public String toString() {
        return "{\"userId\": \""+userId+"\"," +
                " \"mail\": \""+mail+"\"," +
                " \"name\": \""+name+"\"," +
                " \"username\": \""+username+"\"," +
                " \"password\": \""+password+"\"}";
    }
}
