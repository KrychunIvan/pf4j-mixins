package ua.wildwinner.boot;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stianloader.micromixin.transform.api.MixinConfig;
import ua.wildwinner.MixinService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.stream.Collectors;

public class MixinPlugin extends Plugin {
    private final Logger log = LoggerFactory.getLogger(MixinPlugin.class);
    private static MixinService mixinService;
    private URLClassLoader classLoader;

    public static boolean initMixin = true;

    public static void setMixinService(MixinService mixinService) {
        MixinPlugin.mixinService = mixinService;
    }

    public MixinPlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.classLoader = (URLClassLoader) this.getClass().getClassLoader();
    }

    protected void registerSource(String name) {
        if (!initMixin) {
            String className = name.replace('.', '/') + ".class";
            mixinService.addSourceUrlProvider(className, () -> classLoader.findResource(className));
        }
    }

    public void registerConfig() {
        registerConfig(classLoader, "mixins.json");
    }

    public void registerConfig(ClassLoader classLoader, String mixinConfigPath) {
        if (initMixin) {
            try {
                mixinService.registerMixin(MixinConfig.fromString(readJsonFromResource(classLoader, mixinConfigPath)));
                return;
            } catch (IOException e) {
                log.error("Load mixin config error", e);
            } catch (MixinConfig.InvalidMixinConfigException e) {
                log.error("Parse mixin config error", e);
            }
            throw new RuntimeException("Mixin config failed");
        }
    }

    protected void registerMixinClassNode(String name) {
        if (initMixin) {
            String className = name.replace('.', '/');
            try (InputStream inputStream = classLoader.getResourceAsStream(className + ".class")) {
                if (inputStream == null) {
                    throw new RuntimeException("Class not found: " + className);
                }

                ClassReader classReader = new ClassReader(inputStream);
                ClassNode classNode = new ClassNode(Opcodes.ASM9);
                classReader.accept(classNode, 0);

                mixinService.addNode(className, classNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private String readJsonFromResource(ClassLoader classLoader, String resourcePath) throws IOException {
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (InputStreamReader isr = new InputStreamReader(inputStream);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
