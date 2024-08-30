package ua.wildwinner;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stianloader.micromixin.transform.api.MixinConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class MixinExtension {
    private static MixinService mixinService;
    private final Logger log = LoggerFactory.getLogger(MixinExtension.class);
    private ClassLoader classLoader;

    public MixinExtension(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static void setMixinService(MixinService mixinService) {
        MixinExtension.mixinService = mixinService;
    }

    public void registerConfig() {
        registerConfig(classLoader, "mixins.json");
    }

    public void registerConfig(ClassLoader classLoader, String mixinConfigPath) {
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

    protected void registerClassNode(Class<?> clazz) {
        String className = clazz.getName().replace('.', '/');
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(className + ".class")) {
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
