package ua.wildwinner.plugin1;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginWrapper;
import ua.wildwinner.boot.MixinPlugin;
import ua.wildwinner.extensions.SayHello;

public class PluginImpl extends MixinPlugin {
    public PluginImpl(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("Start plugin");
    }

    @Override
    public void stop() {
        log.info("Stop plugin");
    }

    @Extension
    public static class PluginHello implements SayHello, ExtensionPoint {

        @Override
        public String hello() {
            return "PluginHello";
        }
    }
}
