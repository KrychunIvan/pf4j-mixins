package ua.wildwinner.plugin2;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginWrapper;
import ua.wildwinner.boot.MixinPlugin;
import ua.wildwinner.extensions.SayHello;

public class PluginImpl extends MixinPlugin {
    public PluginImpl(PluginWrapper wrapper) {
        super(wrapper);
        createInitializer()
                .registerMixinClassNode("ua.wildwinner.plugin2.TargetPlugin2Mixin")
                .registerConfig();
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
}
