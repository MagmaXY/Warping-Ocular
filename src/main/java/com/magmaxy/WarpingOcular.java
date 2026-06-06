package com.magmaxy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Collections;

public class WarpingOcular implements ModInitializer {

	public static final String MOD_ID = "warping-ocular";

	public static final ResourceKey<Item> WARPING_OCULAR_KEY = ResourceKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath(MOD_ID, "warping_ocular")
	);

	public static final Item WARPING_OCULAR = Registry.register(
			BuiltInRegistries.ITEM,
			WARPING_OCULAR_KEY,
			new WarpingOcularItem(new Item.Properties().setId(WARPING_OCULAR_KEY).stacksTo(1))
	);

	public static final ResourceKey<net.minecraft.world.item.enchantment.Enchantment> WARPING_RANGE_KEY =
			ResourceKey.create(
					Registries.ENCHANTMENT,
					Identifier.fromNamespaceAndPath(MOD_ID, "warping_range")
			);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.serverboundPlay().register(WarpingOcularTeleportPayload.TYPE, WarpingOcularTeleportPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(WarpingOcularTeleportPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			context.server().execute(() -> {

				boolean hasPearl = player.isCreative();
				if (!hasPearl) {
					for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
						if (player.getInventory().getItem(i).is(Items.ENDER_PEARL)) {
							hasPearl = true;
							break;
						}
					}
				}

				if (hasPearl) {
					if (player.isUsingItem() && player.getUseItem().getItem() == WARPING_OCULAR) {
						int pearlSlot = -1;
						if (!player.isCreative()) {
							for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
								if (player.getInventory().getItem(i).is(Items.ENDER_PEARL)) {
									pearlSlot = i;
									break;
								}
							}
							if (pearlSlot == -1) return;
						}

						int enchantLevel = context.server()
								.registryAccess()
								.lookupOrThrow(Registries.ENCHANTMENT)
								.get(WARPING_RANGE_KEY)
								.map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, player.getUseItem()))
								.orElse(0);

						double range = 64.0 + 32.0 * enchantLevel;
						HitResult hit = player.pick(range, 1.0F, false);

						if (hit.getType() != HitResult.Type.MISS) {
							Vec3 pos = hit.getLocation();
							ServerLevel serverLevel = (ServerLevel) player.level();

							serverLevel.sendParticles(
									net.minecraft.core.particles.ParticleTypes.PORTAL,
									player.getX(), player.getY() + 1.0, player.getZ(),
									32,
									0.5, 0.5, 0.5,
									0.5
							);

							InteractionHand hand = player.getUsedItemHand();
							ItemStack stack = player.getItemInHand(hand);
							stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

							player.teleportTo(serverLevel, pos.x, pos.y, pos.z, Collections.emptySet(), player.getYRot(), player.getXRot(), false);

							player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
							player.level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

							if (!player.isCreative()) {
								player.getInventory().removeItem(pearlSlot, 1);
							}
							player.getCooldowns().addCooldown(WARPING_OCULAR.getDefaultInstance(), 200);
							player.stopUsingItem();

							serverLevel.sendParticles(
									net.minecraft.core.particles.ParticleTypes.PORTAL,
									pos.x(), pos.y() + 1.0, pos.z(),
									32,
									0.5, 0.5, 0.5,
									0.5
							);

							serverLevel.sendParticles(
									player,
									net.minecraft.core.particles.ParticleTypes.PORTAL,
									true,
									false,
									pos.x(), pos.y() + 1.0, pos.z(),
									32,
									0.5, 0.5, 0.5,
									0.5
							);

						}
					}
				}
			});
		});
	}
}