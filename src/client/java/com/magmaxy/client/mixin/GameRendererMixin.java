package com.magmaxy.client.mixin;

import com.magmaxy.WarpingOcular;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    private boolean wasUsingOcular = false;

    @Unique
    private static final Identifier INVERT_SHADER = Identifier.withDefaultNamespace("invert");

    @Inject(method = "render", at = @At("HEAD"))
    private void applyWarpingShader(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) {
            return;
        }

        var player = client.player;

        boolean isUsingOcular = player.isUsingItem()
                && player.getUseItem().getItem() == WarpingOcular.WARPING_OCULAR;

        if (isUsingOcular && !wasUsingOcular) {
            ((GameRendererInvoker) client.gameRenderer).invokeSetPostEffect(INVERT_SHADER);
            wasUsingOcular = true;
        } else if (!isUsingOcular && wasUsingOcular) {
            client.gameRenderer.clearPostEffect();
            player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
            wasUsingOcular = false;
        }
    }
}