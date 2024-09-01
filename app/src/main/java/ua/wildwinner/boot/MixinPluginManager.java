package ua.wildwinner.boot;

import org.pf4j.*;

import java.nio.file.Path;

public class MixinPluginManager extends DefaultPluginManager {
    private static MixinClassLoader mixinClassLoader;

    public static void setMixinClassLoader(MixinClassLoader mixinClassLoader) {
        MixinPluginManager.mixinClassLoader = mixinClassLoader;
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new JarPluginLoader(this){

            @Override
            public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
                PluginClassLoader pluginClassLoader = new PluginClassLoader(this.pluginManager, pluginDescriptor, getClass().getClassLoader()) {

                    @Override
                    protected Class<?> findClass(String name) throws ClassNotFoundException {
                        try {
                            return mixinClassLoader.loadClass(name);
                        } catch (ClassNotFoundException e) {
                            return super.findClass(name);
                        }
                    }

                    @Deprecated
                    @Override
                    public String toString() {
                        return "PluginClassLoader " + pluginDescriptor.getPluginId();
                    }
                };
                pluginClassLoader.addFile(pluginPath.toFile());

                return pluginClassLoader;
            }
        };
    }
}
