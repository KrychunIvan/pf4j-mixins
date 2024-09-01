package ua.wildwinner.boot;

import org.pf4j.JarPluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

import java.io.File;
import java.nio.file.Path;

public class JarPluginLoaderEx extends JarPluginLoader {
    private final MixinClassLoader mixinClassLoader;

    public JarPluginLoaderEx(PluginManager pluginManager, MixinClassLoader mixinClassLoader) {
        super(pluginManager);
        this.mixinClassLoader = mixinClassLoader;
    }

    @Override
    public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
        PluginClassLoader pluginClassLoader = new PluginClassLoader(this.pluginManager, pluginDescriptor, getClass().getClassLoader()) {

            @Override
            public void addFile(File file) {
                mixinClassLoader.addFile(file);
                super.addFile(file);
            }

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
}
