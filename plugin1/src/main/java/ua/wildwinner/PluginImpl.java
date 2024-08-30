package ua.wildwinner;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginImpl extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(PluginImpl.class);

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

    @Extension
    public static class MixinProvider extends MixinExtension implements ExtensionPoint {

        public MixinProvider() {
            super(PluginImpl.class.getClassLoader());
            registerClassNode(TargetMixin.class);
        }
    }
}
