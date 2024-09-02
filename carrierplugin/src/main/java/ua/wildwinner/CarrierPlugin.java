package ua.wildwinner;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginWrapper;
import ua.wildwinner.boot.MixinPlugin;
import ua.wildwinner.extensions.SayHello;

public class CarrierPlugin extends MixinPlugin {
    public CarrierPlugin(PluginWrapper wrapper) {
        super(wrapper);
        createInitializer()
                .registerSource("ua.wildwinner.DoSomeThingUseful");
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
