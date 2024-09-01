package ua.wildwinner.plugin2;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wildwinner.extensions.MixinExtension;
import ua.wildwinner.extensions.SayHello;

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
    public static class Plugin2Hello implements SayHello, ExtensionPoint {

        @Override
        public String hello() {
            return "PluginHello plugin2";
        }
    }

    @Extension
    public static class MixinProviderPlugin2 extends MixinExtension implements ExtensionPoint {

        public MixinProviderPlugin2() {
            super(PluginImpl.class.getClassLoader());
            registerMixinClassNode(TargetMixin.class);
        }
    }
}
