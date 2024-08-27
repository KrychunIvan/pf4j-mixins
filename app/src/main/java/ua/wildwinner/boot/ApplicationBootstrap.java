package ua.wildwinner.boot;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import ua.wildwinner.MixinService;

public class ApplicationBootstrap {
    public static void main(String[] args) {
        try (MixinClassLoader classloader = new MixinClassLoader(new MixinService())) {
            Class<?> applicationEntrypoint = classloader.loadClass("ua.wildwinner.Application");
            MethodHandle handle = MethodHandles.lookup().findStatic(applicationEntrypoint, "main", MethodType.methodType(void.class, String[].class));
            handle.invoke((Object) args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
