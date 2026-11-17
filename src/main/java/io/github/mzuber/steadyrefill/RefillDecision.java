package io.github.mzuber.steadyrefill;

/** Decides whether a recent action left an empty hand or a pickup in its place. */
public final class RefillDecision {
    private RefillDecision() {}

    public static boolean shouldRefill(int previousCount, int maxStackSize,
            boolean hasUseRemainder, boolean currentEmpty, boolean currentMatches) {
        if (previousCount == 0 || currentMatches) return false;
        if (currentEmpty) return true;
        // A different item can have been picked up after the final item was used.
        // Restrict occupied-slot replacement to the last item of stackable supplies;
        // never replace a bucket/bowl/bottle produced by normal item use.
        return previousCount == 1 && maxStackSize > 1 && !hasUseRemainder;
    }
}
