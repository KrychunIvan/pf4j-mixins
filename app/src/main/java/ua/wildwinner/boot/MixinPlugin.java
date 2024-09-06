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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MixinPlugin extends Plugin {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private MixinInitializer mixinInitializer;

    public MixinPlugin(PluginWrapper wrapper) {
        super(wrapper);
        mixinInitializer = new MixinInitializer((URLClassLoader) this.getClass().getClassLoader());
        mixinInitializer.registerConfig();
    }

    private class MixinInitializer {
        private final List<Consumer<MixinService>> initMixins = new ArrayList<>();
        private URLClassLoader classLoader;

        private MixinInitializer(URLClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        void registerSource(String name) {
            String className = name.replace('.', '/') + ".class";
            initMixins.add(mixinService -> mixinService.addSourceUrlProvider(className,
                    () -> classLoader.getResource(className)));
        }

        void registerMixinClassNode(String name) {
            String className = name.replace('.', '/');
            try (InputStream inputStream = classLoader.getResourceAsStream(className + ".class")) {
                if (inputStream == null) {
                    throw new RuntimeException("Class not found: " + className);
                }
                ClassReader classReader = new ClassReader(inputStream);
                ClassNode classNode = new ClassNode(Opcodes.ASM9);
                classReader.accept(classNode, 0);
                initMixins.add(mixinService -> mixinService.addNode(className, classNode));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        void registerConfig() {
            List<MixinConfig> mixinsConfigs = new ArrayList<>();
            mixinsConfigs.add(parseConfig(classLoader, "mixins.json"));
            mixinsConfigs.add(parseConfig(classLoader, "mixins-targets.json"));
            mixinsConfigs.removeIf(Objects::isNull);

            if (mixinsConfigs.size() == 2) {
                if (mixinsConfigs.get(0).setSourceFile == mixinsConfigs.get(1).setSourceFile) {
                    throw new IllegalStateException("Both Mixin configurations have the same type. One should be a Mixin, and the other a target.");
                }
            }

            for (MixinConfig mixinConfig : mixinsConfigs) {
                mixinConfig.mixins.stream()
                        .map(className -> mixinConfig.mixinPackage + "." + className)
                        .forEach(fullClassName -> {
                            if (mixinConfig.setSourceFile) {
                                registerSource(fullClassName);
                            } else {
                                registerMixinClassNode(fullClassName);
                            }
                        });
                if (!mixinConfig.setSourceFile) {
                    initMixins.add(mixinService -> mixinService.registerMixin(mixinConfig));
                }
            }
        }

        private void run(MixinService mixinService) {
            initMixins.forEach(consumer -> consumer.accept(mixinService));
        }
    }

    private MixinConfig parseConfig(URLClassLoader classLoader, String fileName) {
        try {
            String readMixinsJson = readJsonFromResource(classLoader, fileName);
            if (readMixinsJson != null) {
                log.debug("Found mixins config `{}`", fileName);
                return MixinConfig.fromString(readMixinsJson);
            }
        } catch (MixinConfig.InvalidMixinConfigException | IOException e) {
            log.error("Parse mixin config error", e);
            throw new RuntimeException("Mixin config failed", e);
        }
        return null;
    }

    protected String readJsonFromResource(ClassLoader classLoader, String resourcePath) throws IOException {
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return null;
            }
            try (InputStreamReader isr = new InputStreamReader(inputStream);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    final void initMixins(MixinService mixinService) {
        if (mixinInitializer == null) {
            log.error("Use MixinPlugin only with MixinInitializer");
            throw new RuntimeException("MixinInitializer is null");
        }
        mixinInitializer.run(mixinService);
    }
}
