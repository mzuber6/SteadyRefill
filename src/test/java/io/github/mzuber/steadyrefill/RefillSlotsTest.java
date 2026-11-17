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
        check(RefillDecision.shouldRefill(1, 64, false, false, false),
                "pickup in depleted slot must allow refill");
        check(RefillDecision.shouldRefill(1, 64, false, true, false), "empty final stack");
        check(!RefillDecision.shouldRefill(0, 64, false, false, false), "no previous item");
        check(!RefillDecision.shouldRefill(1, 64, false, false, true),
                "matching pickup already refilled hand");
        check(!RefillDecision.shouldRefill(2, 64, false, false, false),
                "different item without final-item depletion must be left alone");
        check(!RefillDecision.shouldRefill(1, 1, false, false, false),
                "unstackable transformation must be left alone");
        check(!RefillDecision.shouldRefill(1, 16, true, false, false),
                "bottles and other use remainders must be left alone");
        System.out.println("All refill slot regression checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
