package ua.wildwinner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Target instance = new Target();
        String result = instance.hi();
        log.info("Transformed call {}", result);
    }
}
