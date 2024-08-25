package ua.wildwinner;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static MixinService mixinService = new MixinService();

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
            mixinProviders.forEach(mixinProvider -> mixinService.registerMixin(mixinProvider.configProvider()));
            mixinService.transform(Target.class);
            new Target().hi();
        }
        pluginManager.stopPlugins();
    }
}
