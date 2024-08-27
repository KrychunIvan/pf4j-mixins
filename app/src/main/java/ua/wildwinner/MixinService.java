package ua.wildwinner;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinConfig;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MixinService {
    private final CustomClassLoader customClassLoader = new CustomClassLoader(getClass().getClassLoader());
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

    public Class<?> transform(Class<?> clazz) throws IOException {
        ClassNode classNode = getClassNode(clazz);
        transformer.transform(classNode);
        byte[] classBytesFromClassNode = getClassBytesFromClassNode(classNode);
        return customClassLoader.defineClass(clazz.getName(), classBytesFromClassNode);
    }

    private ClassNode getClassNode(Class<?> targetClass) throws IOException {
        ClassReader classReader = new ClassReader(targetClass.getName());
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        return classNode;
    }

    private byte[] getClassBytesFromClassNode(ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
