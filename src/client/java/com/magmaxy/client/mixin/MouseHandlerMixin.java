package com.magmaxy.client.mixin;

import com.magmaxy.WarpingOcular;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Inject(
            method = "turnPlayer",
            at = @At("HEAD")
    )
    private void reduceSensitivityForWarpingOcular(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;

        if (player != null && player.isUsingItem()
                && player.getUseItem().getItem() == WarpingOcular.WARPING_OCULAR
                &&  client.options.getCameraType().isFirstPerson()) {

            this.accumulatedDX *= 0.1;
            this.accumulatedDY *= 0.1;
        }
    }
}