package com.magmaxy;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WarpingOcularTeleportPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WarpingOcularTeleportPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(WarpingOcular.MOD_ID, "teleport"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpingOcularTeleportPayload> CODEC =
            StreamCodec.unit(new WarpingOcularTeleportPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}