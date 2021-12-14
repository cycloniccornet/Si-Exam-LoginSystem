package si.login.service;

import net.minidev.json.JSONObject;
import si.login.model.User;

public class JsonConverter {

    public JSONObject userToJson(User user, String type) {
        JSONObject object = new JSONObject();
        object.put("userId", user.getUserId());
        object.put("mail", user.getMail());
        object.put("name", user.getName());
        object.put("username", user.getUsername());
        object.put("password", user.getPassword());
        object.put("type", type);
        return object;
    }
}
