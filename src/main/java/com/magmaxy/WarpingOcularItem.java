package com.magmaxy;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

import static net.minecraft.world.item.Items.AMETHYST_SHARD;

public class WarpingOcularItem extends SpyglassItem {
    public WarpingOcularItem(Item.Properties properties) {

        super(properties
                .durability(64)
                .enchantable(14)
                .component(DataComponents
                        .REPAIRABLE, new Repairable(
                                HolderSet
                                        .direct(AMETHYST_SHARD
                                                .builtInRegistryHolder()
                                        )
                        )
                )

        );
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        user.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
        user.awardStat(Stats.ITEM_USED.get(this));
        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPYGLASS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("itemTooltip.warping-ocular.warping_ocular1").withStyle(ChatFormatting.LIGHT_PURPLE));
        textConsumer.accept(Component.translatable("itemTooltip.warping-ocular.warping_ocular2").withStyle(ChatFormatting.LIGHT_PURPLE));
    }

}