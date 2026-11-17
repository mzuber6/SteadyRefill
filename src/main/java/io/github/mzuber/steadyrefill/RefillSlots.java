package io.github.mzuber.steadyrefill;

import java.util.function.IntPredicate;

public final class RefillSlots {
    private RefillSlots() {}

    public static int findSource(int selected, int target, IntPredicate matches) {
        for (int index = 9; index < 45; index++) {
            int source = index < 36 ? index : index - 36;
            if (source != selected && source != target && matches.test(source)) {
                return source;
            }
        }
        return -1;
    }

    public static int menuSlot(int inventorySlot) {
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }
}
