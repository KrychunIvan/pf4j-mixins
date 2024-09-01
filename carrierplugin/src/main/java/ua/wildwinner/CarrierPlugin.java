package ua.wildwinner;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import ua.wildwinner.extensions.MixinTargetExtension;
import ua.wildwinner.extensions.SayHello;

import java.net.URLClassLoader;

public class CarrierPlugin extends Plugin {
    public CarrierPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class P1 extends MixinTargetExtension implements ExtensionPoint {

        public P1() {
            super((URLClassLoader) CarrierPlugin.class.getClassLoader());
            registerSource(DoSomeThingUseful.class);
        }
    }

    @Extension
    public static class CarrierPluginHello implements SayHello, ExtensionPoint {

        @Override
        public String hello() {
            new DoSomeThingUseful().doWork();
            return "CarrierPlugin";
        }
    }
}
