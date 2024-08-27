package ua.wildwinner.boot;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import ua.wildwinner.MixinService;

public class MixinClassLoader extends URLClassLoader {

    private static final String[] PROHIBITED_PREFIXES = {
        "ua.wildwinner.boot.",
        "ua.wildwinner.MixinService",
        "org.objectweb.asm.",
        "org.json.",
        "org.stianloader.micromixin.transform.",
        "org.slf4j."
    };

    static {
        ClassLoader.registerAsParallelCapable();
    }

    private static URL[] getBootURLs() {
        ClassLoader parentCL = MixinClassLoader.class.getClassLoader();
        if (parentCL instanceof URLClassLoader) {
            // Java 8 compatible method
            return ((URLClassLoader) parentCL).getURLs();
        } else {
            // Java 9+ compatible method (just parse the classpath from the property)
            String classpath = System.getProperty("java.class.path");
            String[] parts = classpath.split(File.pathSeparator);
            URL[] urls = new URL[parts.length];
            for (int i = 0; i < urls.length; i++) {
                try {
                    String part = parts[i];
                    String protocol;
                    if (part.contains("!")) {
                        protocol = "jar://";
                    } else if (part.endsWith(".jar")) {
                        part = part + "!/";
                        protocol = "jar:file://";
                    } else {
                        part = part + "/";
                        protocol = "file://";
                    }
                    urls[i] = new URL(protocol + part);
                } catch (MalformedURLException e) {
                    throw new Error(e);
                }
            }

            return urls;
        }
    }
    private final ClassLoader asmClassloader = new URLClassLoader(new URL[0], this);

    private final MixinService mixinService;

    public MixinClassLoader(MixinService mixinService) {
        super(MixinClassLoader.getBootURLs(), MixinClassLoader.class.getClassLoader());
        this.mixinService = mixinService;
    }

    public MixinService getMixinService() {
        return this.mixinService;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (this.getClassLoadingLock(name)) {
            Class<?> alreadyLoaded = this.findLoadedClass(name);
            if (alreadyLoaded != null) {
                if (resolve) {
                    this.resolveClass(alreadyLoaded);
                }
                return alreadyLoaded;
            }

            for (String prefix : MixinClassLoader.PROHIBITED_PREFIXES) {
                if (name.startsWith(prefix)) {
                    return super.loadClass(name, resolve);
                }
            }

            URL url = this.findResource(name.replace('.', '/') + ".class");

            if (url == null) {
                return super.loadClass(name, resolve);
            }

            try (InputStream in = url.openStream()) {
                ClassReader reader = new ClassReader(in);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                this.mixinService.transform(node);

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected ClassLoader getClassLoader() {
                        return MixinClassLoader.this.asmClassloader;
                    }
                };
                node.accept(writer);
                byte[] data = writer.toByteArray();
                Class<?> defined = this.defineClass(name, data, 0, data.length);
                if (resolve) {
                    this.resolveClass(defined);
                }
                return defined;
            } catch (Exception e) {
                throw new ClassNotFoundException("Not found: " + name, e);
            }
        }
    }
}
