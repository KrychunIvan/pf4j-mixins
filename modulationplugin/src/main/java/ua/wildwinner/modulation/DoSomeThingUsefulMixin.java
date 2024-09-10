package ua.wildwinner.modulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.wildwinner.DoSomeThingUseful;
import ua.wildwinner.modulation.internal.Outer;

@Mixin(value = DoSomeThingUseful.class)
public class DoSomeThingUsefulMixin extends DoSomeThingUseful {
    private static Logger log = LoggerFactory.getLogger(DoSomeThingUsefulMixin.class);

    @Inject(method = "doWork", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        log.info("Hi from DoSomeThingUsefulMixin");
        new Outer().outerCall();
        hiCall();
    }

    private static void hiCall() {
        new Inner().hi();
    }

    public static class Inner {
        public void hi() {
            log.info("Inner hi");
        }
    }
}
