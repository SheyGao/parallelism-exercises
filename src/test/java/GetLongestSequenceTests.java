import getLongestSequence.GetLongestSequence;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class GetLongestSequenceTests {
    public static final int FULLY_SEQUENTIAL = Integer.MAX_VALUE;
    public static final int REASONABLE_CUTOFF = 100;
    public static final int FULLY_PARALLEL = 1;
    public static final int NUM_SMALL_TESTS = 500;
    public static final int NUM_LARGE_TESTS = 5;
    public static final int SMALL_SIZE = 100;
    public static final int LARGE_SIZE = 100000;
    public static Random RANDOM = new Random(332134);

    @Test
    public void testSmallSequential() {
        for (int i = 0; i < NUM_SMALL_TESTS; i++) {
            testSmallBitArray(i, FULLY_SEQUENTIAL, 0);
            testSmallBitArray(i, FULLY_SEQUENTIAL, 1);
        }
    }

    @Test(timeout = 25000)
    public void testSmallParallel() {
        for (int i = 0; i < NUM_SMALL_TESTS; i++) {
            testSmallBitArray(i, FULLY_PARALLEL, 0);
            testSmallBitArray(i, FULLY_PARALLEL, 1);
        }
    }

    @Test(timeout = 25000)
    public void testLarge() {
        for (int i = 0; i < NUM_LARGE_TESTS; i++) {
            testRandomBitArray(LARGE_SIZE, REASONABLE_CUTOFF, 0);
            testRandomBitArray(LARGE_SIZE, REASONABLE_CUTOFF, 1);
        }
    }

    private void testSmallBitArray(int num, int cutoff, int match) {
        int best = 0;
        int conseq = 0;
        int[] bits = new int[SMALL_SIZE];
        for (int i = SMALL_SIZE - 1; i >= 0; i--) {
            bits[i] = (num >> i) & 1;
            if (bits[i] == match) {
                conseq++;
            } else {
                conseq = 0;
            }
            best = Math.max(best, conseq);
        }
        int actual = GetLongestSequence.getLongestSequence(match, bits, cutoff);
        assertEquals(best, actual);
    }

    private void testRandomBitArray(int size, int cutoff, int match) {
        int best = 0;
        int conseq = 0;
        int[] bits = new int[size];
        for (int i = 0; i < size; i++) {
            bits[i] = RANDOM.nextBoolean() ? 1 : 0;
            if (bits[i] == match) {
                conseq++;
            } else {
                conseq = 0;
            }
            best = Math.max(best, conseq);
        }
        int actual = GetLongestSequence.getLongestSequence(match, bits, cutoff);
        assertEquals(best, actual);
    }
}
