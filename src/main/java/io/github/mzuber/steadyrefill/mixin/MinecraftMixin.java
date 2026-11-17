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
        // Tick snapshots also cover eating completion and tools breaking during mining.
        boolean acting = client.options.keyUse.isDown() || client.options.keyAttack.isDown()
                || (client.player != null && client.player.isUsingItem());
        steadyrefill$tick = acting ? HandRefill.capture(client) : null;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void steadyrefill$afterTick(CallbackInfo ci) {
        if (steadyrefill$tick != null) {
            steadyrefill$tick.refill((Minecraft) (Object) this);
            steadyrefill$tick = null;
        }
    }
}
