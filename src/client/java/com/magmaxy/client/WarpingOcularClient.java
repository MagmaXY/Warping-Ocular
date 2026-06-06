package com.magmaxy.client;

import com.magmaxy.WarpingOcular;
import com.magmaxy.WarpingOcularTeleportPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class WarpingOcularClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.options.keyAttack.isDown()) {
				if (client.player.isUsingItem() && client.player.getUseItem().getItem() == WarpingOcular.WARPING_OCULAR) {
					ClientPlayNetworking.send(new WarpingOcularTeleportPayload());
					while (client.options.keyAttack.consumeClick()) {}
				}
			}
		});
	}
}