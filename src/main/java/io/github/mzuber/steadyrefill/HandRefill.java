package io.github.mzuber.steadyrefill;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

/** A one-action snapshot: no remembered slot survives a menu or player change. */
public record HandRefill(LocalPlayer player, int selected, ItemStack main, ItemStack off) {
    public static HandRefill capture(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.gui.screen() != null
                || !client.isWindowActive() || client.player.isSpectator()
                || client.player.containerMenu != client.player.inventoryMenu
                || !client.player.containerMenu.getCarried().isEmpty()) {
            return null;
        }
        var player = client.player;
        return new HandRefill(player, player.getInventory().getSelectedSlot(),
                player.getMainHandItem().copy(), player.getOffhandItem().copy());
    }

    public void refill(Minecraft client) {
        if (client.player != player || client.gameMode == null || client.gui.screen() != null
                || !client.isWindowActive() || !player.isAlive()
                || player.containerMenu != player.inventoryMenu
                || !player.containerMenu.getCarried().isEmpty()
                || player.getInventory().getSelectedSlot() != selected) {
            return;
        }
        refillHand(client, main, selected);
        refillHand(client, off, Inventory.SLOT_OFFHAND);
    }

    private void refillHand(Minecraft client, ItemStack previous, int target) {
        var inventory = player.getInventory();
        if (previous.isEmpty() || !inventory.getItem(target).isEmpty()) {
            return;
        }
        // Main inventory first, then other hotbar slots. Never steal the other hand.
        int source = RefillSlots.findSource(selected, target,
                slot -> ItemStack.isSameItemSameComponents(previous, inventory.getItem(slot)));
        if (source >= 0) {
            // Vanilla number-key/offhand swap: cursor stays empty and the server
            // receives an ordinary inventory action. No screen or input changes.
            client.gameMode.handleContainerInput(player.inventoryMenu.containerId,
                    RefillSlots.menuSlot(source), target, ContainerInput.SWAP, player);
        }
    }
}
