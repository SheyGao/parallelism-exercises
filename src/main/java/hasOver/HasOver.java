package hasOver;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class HasOver {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns true if arr has any elements strictly larger than val.
     * For example, if arr is [21, 17, 35, 8, 17, 1], then
     * main.java.hasOver(21, arr) == true and main.java.hasOver(35, arr) == false.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument.
     */
    //private static int val;
    private static int CUTOFF;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {
        CUTOFF = sequentialCutoff;
        //HasOver.val = val;
        return POOL.invoke(new hasOverTask(arr, 0, arr.length, val));
    }
    public static boolean sequentialHasOver(int[] arr, int lo, int hi, int val){

        for (int i = lo; i < hi; i++){
            if (arr[i] > val){
                return true;
            }
        }
        return false;
    }

    private static class hasOverTask extends RecursiveTask<Boolean> {
        private final int[] arr;
        private final int lo, hi, val;
        public hasOverTask(int[] arr, int lo, int hi, int val){
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.val = val;
        }

        protected Boolean compute(){
            if (hi - lo <= CUTOFF){
                return sequentialHasOver(arr, lo, hi, val);
            } else {
                int mid = lo + (hi - lo) / 2;

                hasOverTask left = new hasOverTask(arr, lo, mid, val);
                hasOverTask right = new hasOverTask(arr, mid, hi, val);

                left.fork();
                boolean rightResult = right.compute();
                boolean leftResult = left.join();
                return (leftResult || rightResult);
            }

        }

    }

    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
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
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }

    }
}
