package ua.wildwinner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Target.class)
public class TargetMixin extends Target {
    private static Logger log;
    @Shadow
    private String hi;

    public TargetMixin() {
        log = LoggerFactory.getLogger(TargetMixin.class);
    }

    @Inject(method = "hi", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        hi = "shadow hi";
        log.info("Hi from Mixin");
    }
}
