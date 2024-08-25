package ua.wildwinner;

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

    @Extension
    public static class MixinProvider implements MixinExtension, ExtensionPoint {

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
