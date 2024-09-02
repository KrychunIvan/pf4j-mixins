package ua.wildwinner;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinConfig;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;

public class MixinService {
    private final Map<String, ClassNode> nodes = new HashMap<>();
    private MixinTransformer<Void> transformer;
    private Map<String, Supplier<URL>> sourceUrls = new HashMap<>();

    public MixinService() {
        MapBytecodeProvider<Void> bytecodeProvider = new MapBytecodeProvider<>(nodes);
        ClassWrapperPool pool = new ClassWrapperPool().addProvider(bytecodeProvider);
        transformer = new MixinTransformer<>(bytecodeProvider, pool);
    }

    public void addNode(String name, ClassNode classNode) {
        nodes.put(name, classNode);
    }

    public void registerMixin(MixinConfig mixinConfig) {
        transformer.addMixin(null, mixinConfig);
    }

    public void transform(ClassNode classNode) {
        transformer.transform(classNode);
    }

    public void addSourceUrlProvider(String className, Supplier<URL> urlSupplier) {
        sourceUrls.put(className, urlSupplier);
    }

    public URL getUrl(String className) {
        Supplier<URL> urlSupplier = sourceUrls.get(className);
        return urlSupplier == null ? null : urlSupplier.get();
    }
}
