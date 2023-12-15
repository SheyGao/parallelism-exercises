package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns an array with the lengths of the non-empty strings from arr (in order)
     * For example, if arr is ["", "", "cse", "332", "", "hw", "", "7", "rox"], then
     * main.java.filterEmpty(arr) == [3, 3, 2, 1, 3].
     *
     * A parallel algorithm to solve this problem in O(lg n) span and O(n) work is the following:
     * (1) Do a parallel map to produce a bit set
     * (2) Do a parallel prefix over the bit set
     * (3) Do a parallel map to produce the output
     *
     * In lecture, we wrote parallelPrefix together, and it is included in the gitlab repository.
     * Rather than reimplementing that piece yourself, you should just use it. For the other two
     * parts though, you should write them.
     *
     * Do not bother with a sequential cutoff for this exercise, just have a base case that processes a single element.
     */
    public static int[] filterEmpty(String[] arr) {
        int[] bits = mapToBitSet(arr);
        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bits);
        return mapToOutput(arr, bits, bitsum);
    }

    public static int[] mapToBitSet(String[] arr) {
        int[] bitMap = new int[arr.length];
        POOL.invoke(new mapTask(arr, bitMap, 0, bitMap.length));
        return bitMap;
    }

    public static int[] sequentialMask(String[] arr, int[] bitMap, int lo, int hi){
        for (int i = lo; i < hi; i++){
            bitMap[i] = arr[i].length() > 0? 1: 0;
        }
        return bitMap;
    }

    private static class mapTask extends RecursiveAction {
        String[] arr;
        int[] bitMap;
        int lo, hi;
        public mapTask(String[] arr, int[] bitMap, int lo, int hi){
            this.arr = arr;
            this.bitMap = bitMap;
            this.lo = lo;
            this.hi = hi;
        }
        protected void compute() {
             if (hi - lo <= 1){
                 sequentialMask(arr, bitMap, lo, hi);
             } else {
                 int mid = lo + (hi - lo) / 2;
                 mapTask left = new mapTask(arr, bitMap, lo , mid);
                 mapTask right = new mapTask(arr, bitMap, mid , hi);
                 left.fork();
                 right.compute();
                 left.join();
             }
        }
    }

    public static int[] mapToOutput(String[] input, int[] bits, int[] bitsum) {
        if (bitsum.length == 0) return new int[0];
        int[] output = new int[bitsum[bitsum.length - 1]];
        POOL.invoke(new outputTask(input, output, bits, bitsum, 0, input.length));
        return output;
    }

    public static int[] sequentialOutput(String[] input, int[] output, int[] bits, int[] bitsum, int lo, int hi){
        for (int i = lo; i < hi; i++){
            if (bits[i] == 1) {
                output[bitsum[i] - 1] = input[i].length();
            }
        }
        return output;
    }

    private static class outputTask extends RecursiveAction{
        String[] input;
        int[] output, bits, bitsum;
        int lo, hi;
        public outputTask(String[] input, int[] output, int[] bits, int[] bitsum, int lo, int hi){
            this.input = input;
            this.output = output;
            this.bits = bits;
            this.bitsum = bitsum;
            this.lo = lo;
            this.hi = hi;
        }
        protected void compute(){
            if (hi - lo <= 1){
                sequentialOutput(input, output, bits, bitsum, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                outputTask left = new outputTask(input, output, bits, bitsum, lo, mid);
                outputTask right = new outputTask(input, output, bits, bitsum, mid, hi);
                left.fork();
                right.compute();
                left.join();
            }
        }
    }

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}