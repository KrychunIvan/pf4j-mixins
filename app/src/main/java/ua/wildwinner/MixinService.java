package ua.wildwinner;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinConfig;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;

public class MixinService {
    private final Map<String, ClassNode> nodes = new HashMap<>();
    private MixinTransformer<Void> transformer;

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
}
