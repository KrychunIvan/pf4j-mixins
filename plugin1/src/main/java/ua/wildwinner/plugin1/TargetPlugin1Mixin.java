package ua.wildwinner.plugin1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.wildwinner.Target;

@Mixin(value = Target.class)
public class TargetPlugin1Mixin extends Target {
    private static Logger log = LoggerFactory.getLogger(TargetPlugin1Mixin.class);
    @Shadow
    private String hi;

    @Inject(method = "hi", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        hi += "+shadow hi plugin";
        if (log == null) {
            throw new RuntimeException("log is null");
        }
        log.info("Hi from Mixin");
    }
}
