package ua.wildwinner;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.BytecodeProvider;
import org.stianloader.micromixin.transform.api.supertypes.ASMClassWrapperProvider;

import java.util.Map;

public class MapBytecodeProvider<M> extends ASMClassWrapperProvider implements BytecodeProvider<M> {

    private final Map<String, ClassNode> nodes;

    public MapBytecodeProvider(Map<String, ClassNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public ClassNode getClassNode(M modularityAttachment, @NotNull String internalName) throws ClassNotFoundException {
        ClassNode node = nodes.get(internalName);
        if (node == null) {
            throw new ClassNotFoundException("Class '" + internalName + "' could not be located for attachment '" + modularityAttachment + "'.");
        }
        return node;
    }

    @Override
    public ClassNode getNode(@NotNull String name) {
        return nodes.get(name);
    }
}
