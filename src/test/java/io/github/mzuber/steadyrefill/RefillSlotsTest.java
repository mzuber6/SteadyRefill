package io.github.mzuber.steadyrefill;

public final class RefillSlotsTest {
    public static void main(String[] args) {
        check(RefillSlots.findSource(0, 0, slot -> slot == 0) == -1, "selected hand excluded");
        check(RefillSlots.findSource(2, 40, slot -> slot == 2 || slot == 40) == -1,
                "offhand refill cannot steal main hand or use itself");
        check(RefillSlots.findSource(0, 0, slot -> slot == 1 || slot == 35) == 35,
                "storage preferred over hotbar");
        check(RefillSlots.findSource(0, 40, slot -> slot == 8) == 8, "hotbar fallback");
        check(RefillSlots.findSource(0, 0, slot -> false) == -1, "no replacement");
        check(RefillSlots.findSource(8, 40, slot -> slot == 9) == 9, "offhand storage refill");
        for (int slot = 0; slot < 36; slot++) {
            check(RefillSlots.menuSlot(slot) == (slot < 9 ? slot + 36 : slot), "slot mapping");
        }
        System.out.println("All refill slot regression checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
