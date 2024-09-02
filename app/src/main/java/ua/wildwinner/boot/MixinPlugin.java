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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MixinPlugin extends Plugin {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private URLClassLoader classLoader;
    private MixinInitializer mixinInitializer;

    public MixinPlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.classLoader = (URLClassLoader) this.getClass().getClassLoader();
    }

    protected final MixinInitializer createInitializer() {
        if (mixinInitializer == null) {
            return mixinInitializer = new MixinInitializer();
        }
        throw new IllegalStateException("Double call createInitializer");
    }

    protected class MixinInitializer {
        private final List<Consumer<MixinService>> initMixins = new ArrayList<>();

        private MixinInitializer() {}

        public MixinInitializer registerSource(String name) {
            String className = name.replace('.', '/') + ".class";
            initMixins.add(mixinService -> mixinService.addSourceUrlProvider(className, () -> classLoader.findResource(className)));
            return this;
        }

        public MixinInitializer registerMixinClassNode(String name) {
            String className = name.replace('.', '/');
            try (InputStream inputStream = classLoader.getResourceAsStream(className + ".class")) {
                if (inputStream == null) {
                    throw new RuntimeException("Class not found: " + className);
                }
                ClassReader classReader = new ClassReader(inputStream);
                ClassNode classNode = new ClassNode(Opcodes.ASM9);
                classReader.accept(classNode, 0);
                initMixins.add(mixinService -> mixinService.addNode(className, classNode));
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void registerConfig() {
            try {
                MixinConfig mixinConfig = MixinPlugin.this.configureMixins();
                initMixins.add(mixinService -> mixinService.registerMixin(mixinConfig));
            } catch (IOException e) {
                log.error("Load mixin config error", e);
                throw new RuntimeException("Mixin config failed", e);
            } catch (MixinConfig.InvalidMixinConfigException e) {
                log.error("Parse mixin config error", e);
                throw new RuntimeException("Mixin config failed", e);
            }
        }

        private void run(MixinService mixinService) {
            initMixins.forEach(consumer -> consumer.accept(mixinService));
        }
    }

    protected MixinConfig configureMixins() throws IOException, MixinConfig.InvalidMixinConfigException {
        return MixinConfig.fromString(readJsonFromResource("mixins.json"));
    }

    protected String readJsonFromResource(String resourcePath) throws IOException {
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

    final void initMixins(MixinService mixinService) {
        if (mixinInitializer == null) {
            log.error("Use MixinPlugin only with MixinInitializer");
            throw new RuntimeException("MixinInitializer is null");
        }
        mixinInitializer.run(mixinService);
    }
}
