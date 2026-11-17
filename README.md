# Steady Refill

Client-only Fabric mod for Minecraft **26.2**, Java 25+, Fabric Loader 0.19.3+.
Fabric API is not required. Servers need no installation.

## Install

Remove HotbarAutoFill and put `steady-refill-1.0.2.jar` from `build/libs/`
into your Minecraft instance's `mods` folder. The loader prevents running both
refill mods together because the old one would still release your Use key.

## Behavior

- Replaces a depleted selected main-hand or offhand stack with matching items.
- Leaves Minecraft's Use and Attack bindings untouched, including keyboard remaps.
- Keeps vanilla held-use timing: no new click is required after a refill.
- Searches the main inventory first, then other hotbar slots; never takes the
  item from your other hand. Matching includes item components.
- If a different pickup occupies the slot after the last stackable item is used,
  swaps the refill into the hand and preserves the pickup in the source slot.
  Normal use remainders (buckets, bowls, bottles) are left alone.
- Uses ordinary inventory swaps without opening a screen or touching the cursor.
- Stops handling refills in menus, while unfocused, and when no matching items remain.
- Only refills stacks emptied during use/mining. Does not refill intentional drops,
  replace nonempty bucket/bowl leftovers, protect tools, or display item counters.
- No remembered empty-slot targets or repeated retry packets. If a server rejects
  a swap, the mod does not force it through.

This is a separate implementation, not a modified copy of HotbarAutoFill.

## Tree farm acceptance check

1. Equip one bone meal in your selected slot and one sapling in your offhand.
   Put spare stacks of both in your main inventory.
2. Hold Use at the farm. Both depleted hands should refill and the next vanilla
   use should continue without releasing and pressing the button.
3. Bind Use to a keyboard key and repeat while holding the key.
4. Test both hands depleting in quick succession, and with spares only in other
   hotbar slots. Confirm your selected slot never changes.
5. Release Use immediately after refill; use must stop normally.
6. Open inventory/chat/pause while holding Use; no refill should run in a menu.
7. Remove spare stacks; depletion should leave the hand empty without looping.
8. Use the last bone meal while standing on dropped logs/sticks. If a pickup
   fills the depleted slot, verify bone meal returns and the pickup is preserved.
9. Swap hands manually while holding Use; verify the mod does not undo the swap.
10. Repeat on the actual multiplayer server to check its inventory synchronization.

Build and automated slot-selection regression checks pass locally. The tree-farm
checks above require a running game and have not been performed automatically.

## Build

Run `./gradle-local build` with Java 25. The `check` task includes regression
checks for inventory priority, selected-hand exclusion, offhand refill sources,
exhausted inventory, and inventory-to-menu slot mapping.
