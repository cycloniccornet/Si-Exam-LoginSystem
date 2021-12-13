package si.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import si.login.model.User;

@Service
public class KafkaService {

    @Autowired
    KafkaTemplate<String, String> template;

    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public void sendCreateUserTopic(User user) {
        String topic = "user-created";
        template.send(topic, user.toString());
        logger.info("Sent Info to Kafka - " + user);
        template.flush();
    }

    public void sendUpdateUserTopic(User user) {
        String topic = "user-updated";
        template.send(topic, user.toString());
        logger.info("Sent Info to Kafka - " + user);
        template.flush();
    }

    public void sendDeleteUserTopic(User user) {
        String topic = "user-deleted";
        template.send(topic, user.toString());
        logger.info("Sent Info to Kafka - " + user);
        template.flush();
    }

    public void sendUserLoginTopic(User user) {
        String topic = "user-login";
        template.send(topic, user.toString());
        logger.info("Sent Info to Kafka - " + user);
        template.flush();
    }
}
