package ua.wildwinner.boot;

import org.pf4j.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MixinPluginManager extends DefaultPluginManager {
    private MixinClassLoader mixinClassLoader;

    private final List<String> initializedMixinPluginIds = new ArrayList<>();

    public MixinPluginManager(MixinClassLoader mixinClassLoader) {
        this.mixinClassLoader = mixinClassLoader;
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new JarPluginLoader(this) {

            @Override
            public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
                PluginClassLoader pluginClassLoader =
                        new PluginClassLoader(this.pluginManager, pluginDescriptor, getClass().getClassLoader()) {

                            @Override
                            protected Class<?> findClass(String name) throws ClassNotFoundException {
                                try {
                                    return mixinClassLoader.loadClass(name);
                                } catch (ClassNotFoundException e) {
                                    return super.findClass(name);
                                }
                            }
                        };
                pluginClassLoader.addFile(pluginPath.toFile());

                return pluginClassLoader;
            }
        };
    }

    protected PluginFactory createPluginFactory() {
        return new DefaultPluginFactory() {
            @Override
            public Plugin create(PluginWrapper pluginWrapper) {
                if (initializedMixinPluginIds.contains(pluginWrapper.getPluginId())) {
                    return super.create(pluginWrapper);
                }
                String pluginClassName = pluginWrapper.getDescriptor().getPluginClass();
                Class<?> pluginClass;
                try {
                    pluginClass = pluginWrapper.getPluginClassLoader().loadClass(pluginClassName);
                } catch (ClassNotFoundException e) {
                    return null;
                }
                if (pluginClass.getSuperclass().equals(MixinPlugin.class)) {
                    Plugin plugin = super.create(pluginWrapper);
                    ((MixinPlugin) plugin).initMixins(mixinClassLoader.getMixinService());
                    initializedMixinPluginIds.add(pluginWrapper.getPluginId());
                    return plugin;
                }
                return super.create(pluginWrapper);
            }
        };
    }
}
