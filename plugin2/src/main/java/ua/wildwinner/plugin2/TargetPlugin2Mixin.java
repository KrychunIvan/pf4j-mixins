package ua.wildwinner.plugin2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.wildwinner.Target;

@Mixin(value = Target.class)
public class TargetPlugin2Mixin extends Target {
    private static Logger log = LoggerFactory.getLogger(TargetPlugin2Mixin.class);
    @Shadow
    private String hi;

    @Inject(method = "hi", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        hi += "+shadow hi from plugin2";
        if (log == null) {
            throw new RuntimeException("log is null");
        }
        log.info("Hi from Mixin plugin2");
    }
}
