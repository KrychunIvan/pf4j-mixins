package ua.wildwinner.extensions;

import ua.wildwinner.MixinService;

import java.net.URLClassLoader;

public abstract class MixinTargetExtension {
    private static MixinService mixinService;
    private URLClassLoader classLoader;

    public MixinTargetExtension(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static void setMixinService(MixinService mixinService) {
        MixinTargetExtension.mixinService = mixinService;
    }

    protected void registerSource(Class<?> clazz) {
        String className = clazz.getName().replace('.', '/') + ".class";
        mixinService.addSourceUrlProvider(className, () -> classLoader.findResource(className));
    }
}
