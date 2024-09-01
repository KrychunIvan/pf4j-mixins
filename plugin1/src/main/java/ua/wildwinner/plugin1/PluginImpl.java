package ua.wildwinner.plugin1;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wildwinner.boot.MixinPlugin;
import ua.wildwinner.extensions.SayHello;

public class PluginImpl extends MixinPlugin {
    private static final Logger log = LoggerFactory.getLogger(PluginImpl.class);

    public PluginImpl(PluginWrapper wrapper) {
        super(wrapper);
        registerMixinClassNode("ua.wildwinner.plugin1.TargetMixin");
        registerConfig();
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
