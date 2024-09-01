package ua.wildwinner.boot;

import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wildwinner.MixinService;
import ua.wildwinner.extensions.MixinExtension;
import ua.wildwinner.extensions.MixinTargetExtension;
import ua.wildwinner.extensions.SayHello;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

public class ApplicationBootstrap {
    private static final Logger log = LoggerFactory.getLogger(ApplicationBootstrap.class);

    public static void main(String[] args) {
        MixinService mixinService = new MixinService();
        try (MixinClassLoader mixinClassLoader = new MixinClassLoader(mixinService)) {
            MixinExtension.setMixinService(mixinService);
            MixinTargetExtension.setMixinService(mixinService);
            CustomPluginManager.setMixinClassLoader(mixinClassLoader);
            CustomPluginManager pluginManager = new CustomPluginManager();
            pluginManager.loadPlugins();
            pluginManager.startPlugins();
            List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
            pluginManager.commitMixins();
            for (PluginWrapper plugin : startedPlugins) {
                String pluginId = plugin.getDescriptor().getPluginId();
                log.info(String.format("Extensions added by plugin '%s':", pluginId));
                List<SayHello> extensions = pluginManager.getExtensions(SayHello.class, pluginId);
                extensions.forEach(extension -> log.info("Plugin extension say - {}", extension.hello()));
            }

            Class<?> applicationEntrypoint = mixinClassLoader.loadClass("ua.wildwinner.Application");
            MethodHandle handle = MethodHandles.lookup().findStatic(applicationEntrypoint, "main", MethodType.methodType(void.class, String[].class));
            handle.invoke((Object) args);
            pluginManager.stopPlugins();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
