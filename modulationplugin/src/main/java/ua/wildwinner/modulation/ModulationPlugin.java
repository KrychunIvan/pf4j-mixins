package ua.wildwinner.modulation;

import org.pf4j.PluginWrapper;
import ua.wildwinner.boot.MixinPlugin;

public class ModulationPlugin extends MixinPlugin {
    public ModulationPlugin(PluginWrapper wrapper) {
        super(wrapper);
        createInitializer()
                .registerMixinClassNode("ua.wildwinner.modulation.DoSomeThingUsefulMixin")
                .registerConfig();
    }
}
