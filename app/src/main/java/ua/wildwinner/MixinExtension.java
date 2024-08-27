package ua.wildwinner;

import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinConfig;

import java.util.function.BiConsumer;

public interface MixinExtension {
    void registerClassNode(BiConsumer<String, ClassNode> addClassNode);

    MixinConfig configProvider();
}
