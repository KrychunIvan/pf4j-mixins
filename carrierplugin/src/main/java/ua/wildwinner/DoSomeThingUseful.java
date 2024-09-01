package ua.wildwinner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoSomeThingUseful {
    private static Logger log;

    public DoSomeThingUseful() {
        log = LoggerFactory.getLogger(DoSomeThingUseful.class);
    }

    public void doWork() {
        log.info("Call doWork");
    }
}
