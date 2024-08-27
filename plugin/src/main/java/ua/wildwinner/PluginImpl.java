package ua.wildwinner;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stianloader.micromixin.transform.api.MixinConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class PluginImpl extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(PluginImpl.class);

    public PluginImpl(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("Start plugin");
    }

    @Override
    public void stop() {
        log.info("Stop plugin");
    }

    @Extension
    public static class PluginHello implements SayHello, ExtensionPoint {

        @Override
        public String hello() {
            return "PluginHello";
        }
    }

    private static String readJsonFromResource(String resourcePath) throws IOException {
        try (InputStream inputStream = PluginImpl.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (InputStreamReader isr = new InputStreamReader(inputStream);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }


    public static void registerClassNode(Class<?> clazz, BiConsumer<String, ClassNode> addClassNode)
            throws IOException {

        String className = clazz.getName().replace('.', '/');
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(className + ".class")) {
            if (inputStream == null) {
                throw new IOException("Class not found: " + className);
            }

            ClassReader classReader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            classReader.accept(classNode, 0);

            addClassNode.accept(className, classNode);
        }
    }

    @Extension
    public static class MixinProvider implements MixinExtension, ExtensionPoint {

        @Override
        public void registerClassNode(BiConsumer<String, ClassNode> addClassNode) {
            try {
                PluginImpl.registerClassNode(TargetMixin.class, addClassNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public MixinConfig configProvider() {
            try {
                return MixinConfig.fromString(readJsonFromResource("mixins.json"));
            } catch (IOException e) {
                log.error("Load mixin config error", e);
            } catch (MixinConfig.InvalidMixinConfigException e) {
                log.error("Parse mixin config error", e);
            }
            throw new RuntimeException("Mixin config failed");
        }
    }
}
