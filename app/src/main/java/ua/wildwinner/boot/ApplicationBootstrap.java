package ua.wildwinner.boot;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wildwinner.MixinExtension;
import ua.wildwinner.MixinService;
import ua.wildwinner.SayHello;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

public class ApplicationBootstrap {
    private static final Logger log = LoggerFactory.getLogger(ApplicationBootstrap.class);

    public static void main(String[] args) {
        MixinService mixinService = new MixinService();
        MixinExtension.setMixinService(mixinService);
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            log.info(String.format("Extensions added by plugin '%s':", pluginId));
            List<SayHello> extensions = pluginManager.getExtensions(SayHello.class, pluginId);
            extensions.forEach(extension -> log.info("Plugin extension say - {}", extension.hello()));
        }
        pluginManager.getExtensions(MixinExtension.class).forEach(MixinExtension::registerConfig);

        try (MixinClassLoader classloader = new MixinClassLoader(mixinService)) {
            Class<?> applicationEntrypoint = classloader.loadClass("ua.wildwinner.Application");
            MethodHandle handle = MethodHandles.lookup().findStatic(applicationEntrypoint, "main", MethodType.methodType(void.class, String[].class));
            handle.invoke((Object) args);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        pluginManager.stopPlugins();
    }
}
