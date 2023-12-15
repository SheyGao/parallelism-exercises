package getLongestSequence;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLongestSequence {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the length of the longest consecutive sequence of val in arr.
     * For example, if arr is [2, 17, 17, 8, 17, 17, 17, 0, 17, 1], then
     * getLongestSequence(17, arr) == 3 and getLongestSequence(35, arr) == 0.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument. We have provided you with an extra class SequenceRange. We recommend you use this class as
     * your return value, but this is not required.
     */
    private static int CUTOFF;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static int val;
    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        GetLongestSequence.CUTOFF = sequentialCutoff;
        GetLongestSequence.val = val;
        SequenceRange res = POOL.invoke(new getLongestTask(arr, 0, arr.length));
        return res.longestRange;
    }

    // [2, 17, 17, 8, 17, 17, 17, 0, 17, 1]
    public static SequenceRange sequentialTask(int[] arr, int lo, int hi, int val){
        // count left
        int left = 0;
        for(int i = lo; i < hi; i ++) {
            if(arr[i] == val) {
                left ++;
            } else {
                break;
            }
        }

        // count right
        int right = 0;
        for(int j = hi - 1; j >= lo; j--) {
            if(arr[j] == val){
                right ++;
            } else {
                break;
            }
        }
        // longestRange
        int count = 0;
        int longest = 0;
        for (int j = lo; j < hi; j++){
            if (arr[j] == val){
                count ++;
            } else {
                longest = Math.max(count, longest);
                count = 0;
            }
            longest = Math.max(count, longest);
        }
        return new SequenceRange(left, right, longest);
    }

    private static class getLongestTask extends RecursiveTask<SequenceRange> {
        private int lo, hi;
        private int[] arr;

        public getLongestTask(int[] arr, int lo, int hi){
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        protected SequenceRange compute() {
            if (hi - lo <= CUTOFF){
                return sequentialTask(arr, lo, hi, val);
            } else {
                int totalL;
                int totalR;
                int mid = lo + (hi - lo) / 2;
                getLongestTask leftTask = new getLongestTask(arr, lo, mid);
                getLongestTask rightTask = new getLongestTask(arr, mid, hi);
                leftTask.fork();
                SequenceRange rightResult = rightTask.compute();
                SequenceRange leftResult = leftTask.join();
                int ll = leftResult.matchingOnLeft;
                totalL = ll;
                int rr = rightResult.matchingOnRight;
                totalR = rr;
                int lr = leftResult.matchingOnRight;
                int rl = rightResult.matchingOnLeft;
                if (ll == mid - lo)
                    totalL = ll + rl;
                if (rr == hi - mid )
                    totalR = rr + lr;
                int longest = Math.max(Math.max(rightResult.longestRange, leftResult.longestRange),
                        lr + rl);
                return new SequenceRange(totalL, totalR, longest);
            }
        }
    }

    private static void usage() {
        System.err.println("USAGE: GetLongestSequence <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}