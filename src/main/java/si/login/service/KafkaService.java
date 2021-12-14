package si.login.service;

import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    KafkaTemplate<String, String> template;

    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public void sendUserTopic(JSONObject object) {
        String topic = "users";
        template.send(topic, object.toString());
        logger.info("Sent Info to Kafka - " + object);
        template.flush();
    }
}
