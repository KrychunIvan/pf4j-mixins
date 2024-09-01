package ua.wildwinner;

import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import ua.wildwinner.extensions.MixinExtension;

public class ModulationPlugin extends Plugin {
    public ModulationPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class MixinProvider extends MixinExtension implements ExtensionPoint {

        public MixinProvider() {
            super(ModulationPlugin.class.getClassLoader());
            registerMixinClassNode(DoSomeThingUsefulMixin.class);
        }
    }
}
