package ua.wildwinner;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinConfig;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MixinService {
    private MixinTransformer<Void> transformer;

    public MixinService() {
        Map<String, ClassNode> nodes = new HashMap<>();
        MapBytecodeProvider<Void> bytecodeProvider = new MapBytecodeProvider<>(nodes);
        ClassWrapperPool pool = new ClassWrapperPool().addProvider(bytecodeProvider);
        transformer = new MixinTransformer<>(bytecodeProvider, pool);
    }

    public void registerMixin(MixinConfig mixinConfig) {
        transformer.addMixin(null, mixinConfig);
    }

    public void transform(Class<?> tClass) {
        try {
            transformer.transform(getClassNode(tClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClassNode getClassNode(Class<?> targetClass) throws IOException {
        ClassReader classReader = new ClassReader(targetClass.getName());
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        return classNode;
    }
}
