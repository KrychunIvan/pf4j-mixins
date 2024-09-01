package ua.wildwinner.boot;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginLoader;
import org.pf4j.PluginWrapper;
import ua.wildwinner.extensions.MixinExtension;
import ua.wildwinner.extensions.MixinTargetExtension;

public class CustomPluginManager extends DefaultPluginManager {
    private static MixinClassLoader mixinClassLoader;

    public static void setMixinClassLoader(MixinClassLoader mixinClassLoader) {
        CustomPluginManager.mixinClassLoader = mixinClassLoader;
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new JarPluginLoaderEx(this, mixinClassLoader);
    }

    public void commitMixins() {
        getExtensions(MixinTargetExtension.class);
        for (PluginWrapper pluginWrapper : getPlugins()) {
            getExtensions(MixinExtension.class, pluginWrapper.getPluginId()).forEach(MixinExtension::registerConfig);
        }
        unloadPlugins();
        loadPlugins();
        startPlugins();
    }
}
