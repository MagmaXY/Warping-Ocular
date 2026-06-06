package com.magmaxy.client.mixin;

import com.magmaxy.WarpingOcular;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {

	@Unique
	private static final Identifier WARPING_SCOPE = Identifier.fromNamespaceAndPath(
			WarpingOcular.MOD_ID, "textures/misc/warping_ocular_scope.png"
	);

	@Unique
	private float warpingOcular$animationProgress = 0.0f;


	@Unique
	private static final float ANIMATION_SPEED = 0.2f;

	@Inject(
			method = "extractCameraOverlays",
			at = @At("HEAD"),
			cancellable = true
	)
	private void onExtractCameraOverlays(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;

		boolean isFirstPerson = client.options.getCameraType().isFirstPerson();

		boolean isUsingOcular = isFirstPerson && player != null && player.isUsingItem() && player.getUseItem().is(WarpingOcular.WARPING_OCULAR);

		float frameDelta = deltaTracker.getGameTimeDeltaTicks();
		if (isUsingOcular) {
			warpingOcular$animationProgress = Math.min(1.0f, warpingOcular$animationProgress + (ANIMATION_SPEED * frameDelta));
		} else {
			if (!isFirstPerson) {
				warpingOcular$animationProgress = 0.0f;
			} else {
				warpingOcular$animationProgress = Math.max(0.0f, warpingOcular$animationProgress - (ANIMATION_SPEED * frameDelta * 2));
			}
		}

		if (warpingOcular$animationProgress > 0.0f) {
			ci.cancel();

			int width = client.getWindow().getGuiScaledWidth();
			int height = client.getWindow().getGuiScaledHeight();
			int shortSide = Math.min(width, height);

			float currentScale = 0.50f + (warpingOcular$animationProgress * 0.625f);
			int animatedSide = (int) (shortSide * currentScale);

			int x = (width - animatedSide) / 2;
			int y = (height - animatedSide) / 2;

			if (x > 0) {
				graphics.fill(0, 0, x, height, 0xFF000000);
				graphics.fill(x + animatedSide, 0, width, height, 0xFF000000);
			}
			if (y > 0) {
				graphics.fill(0, 0, width, y, 0xFF000000);
				graphics.fill(0, y + animatedSide, width, height, 0xFF000000);
			}

			graphics.blit(
					RenderPipelines.GUI_TEXTURED,
					WARPING_SCOPE,
					x,
					y,
					0.0F,
					0.0F,
					animatedSide,
					animatedSide,
					256,
					256,
					256,
					256
			);
		}
	}
}