package ua.wildwinner;

import java.util.List;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.wildwinner.boot.MixinClassLoader;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static MixinService mixinService = ((MixinClassLoader) Application.class.getClassLoader()).getMixinService();

    public static void main(String[] args) {
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            log.info(String.format("Extensions added by plugin '%s':", pluginId));
            List<SayHello> extensions = pluginManager.getExtensions(SayHello.class);
            extensions.forEach(extension -> log.info("Plugin extension say - {}", extension.hello()));
            List<MixinExtension> mixinProviders = pluginManager.getExtensions(MixinExtension.class);
            mixinProviders.forEach(mixinProvider -> {
                mixinProvider.registerClassNode(mixinService::addNode);
                mixinService.registerMixin(mixinProvider.configProvider());
            });
        }

        try {
            Target instance = new Target();
            String result = instance.hi();
            log.info("Transformed call {}", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pluginManager.stopPlugins();
    }
}
