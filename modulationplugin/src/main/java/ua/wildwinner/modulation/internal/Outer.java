package ua.wildwinner.modulation.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Outer {
    private static Logger log = LoggerFactory.getLogger(Outer.class);

    public void outerCall() {
        log.info("Outer hi");
    }
}
