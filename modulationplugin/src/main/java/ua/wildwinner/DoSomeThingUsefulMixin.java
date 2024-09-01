package ua.wildwinner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DoSomeThingUseful.class)
public class DoSomeThingUsefulMixin extends DoSomeThingUseful {
    private static Logger log;

    public DoSomeThingUsefulMixin() {
        log = LoggerFactory.getLogger(DoSomeThingUsefulMixin.class);
    }

    @Inject(method = "doWork", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        log.info("Hi from DoSomeThingUsefulMixin");
    }
}
