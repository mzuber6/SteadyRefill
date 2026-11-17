package io.github.mzuber.steadyrefill.mixin;

import io.github.mzuber.steadyrefill.HandRefill;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Unique private HandRefill steadyrefill$use;
    @Unique private HandRefill steadyrefill$tick;
    @Unique private int steadyrefill$recentActionTicks;

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void steadyrefill$beforeUse(CallbackInfo ci) {
        steadyrefill$use = HandRefill.capture((Minecraft) (Object) this);
    }

    @Inject(method = "startUseItem", at = @At("RETURN"))
    private void steadyrefill$afterUse(CallbackInfo ci) {
        if (steadyrefill$use != null) {
            steadyrefill$use.refill((Minecraft) (Object) this);
            steadyrefill$use = null;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void steadyrefill$beforeTick(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        // Server-authoritative items (including bone meal) can become empty
        // between ticks. Compare against the previous tick BEFORE taking a new copy.
        boolean acting = client.options.keyUse.isDown() || client.options.keyAttack.isDown()
                || (client.player != null && client.player.isUsingItem());
        HandRefill current = HandRefill.capture(client);
        if (current == null || client.options.keyDrop.isDown()) {
            steadyrefill$tick = null;
            steadyrefill$recentActionTicks = 0;
            return;
        }
        if (acting) {
            steadyrefill$recentActionTicks = 4;
        } else if (steadyrefill$recentActionTicks > 0) {
            steadyrefill$recentActionTicks--;
        }
        if (steadyrefill$tick != null && steadyrefill$recentActionTicks > 0) {
            steadyrefill$tick.refill(client);
        }
        steadyrefill$tick = HandRefill.capture(client);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void steadyrefill$afterTick(CallbackInfo ci) {
        if (steadyrefill$tick != null) {
            Minecraft client = (Minecraft) (Object) this;
            if (steadyrefill$recentActionTicks > 0) {
                steadyrefill$tick.refill(client);
            }
            steadyrefill$tick = HandRefill.capture(client);
        }
    }
}
