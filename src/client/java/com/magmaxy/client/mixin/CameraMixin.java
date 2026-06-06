package com.magmaxy.client.mixin;

import com.magmaxy.WarpingOcular;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Unique
    private float warpingOcular$fovProgress = 0.0f;

    @Unique
    private static final float ANIMATION_SPEED = 0.04f;

    @Inject(
            method = "calculateFov",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyFovForWarpingOcular(float partialTicks, CallbackInfoReturnable<Float> cir) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        boolean isFirstPerson = client.options.getCameraType().isFirstPerson();

        boolean isUsingOcular = isFirstPerson && player != null && player.isUsingItem()
                && player.getUseItem().getItem() == WarpingOcular.WARPING_OCULAR;

        if (isUsingOcular) {
            warpingOcular$fovProgress = Math.min(1.0f, warpingOcular$fovProgress + (ANIMATION_SPEED * partialTicks));
        } else {
            if (!isFirstPerson) {
                warpingOcular$fovProgress = 0.0f;
            } else {
                warpingOcular$fovProgress = Math.max(0.0f, warpingOcular$fovProgress - (ANIMATION_SPEED * partialTicks * 2));
            }
        }

        if (warpingOcular$fovProgress > 0.0f) {
            float originalFov = cir.getReturnValue();
            float targetFov = originalFov / 10.0f;

            float animatedFov = originalFov + (targetFov - originalFov) * warpingOcular$fovProgress;

            cir.setReturnValue(animatedFov);
        }
    }
}