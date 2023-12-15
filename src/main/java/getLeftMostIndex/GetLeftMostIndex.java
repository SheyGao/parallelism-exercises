package getLeftMostIndex;
import hasOver.HasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the index of the left-most occurrence of needle in haystack (think of needle and haystack as
     * Strings) or -1 if there is no such occurrence.
     *
     * For example, main.java.getLeftMostIndex("cse332", "Dudecse4ocse332momcse332Rox") == 9 and
     * main.java.getLeftMostIndex("sucks", "Dudecse4ocse332momcse332Rox") == -1.
     * cse332, cse331cse332
     * Your code must actually use the sequentialCutoff argument. You may assume that needle.length is much
     * smaller than haystack.length. A solution that peeks across subproblem boundaries to decide partial matches
     * will be significantly cleaner and simpler than one that does not.
     */
    private static int CUTOFF;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new GetLeftMostIndex.indexTask(needle, haystack, 0, haystack.length));
    }

    public static int sequentialIndex(char[] needle, char[] haystack, int lo, int hi) {
        while( lo < hi && lo + needle.length <= haystack.length) {
            int i = 0;
            while (needle[i] == haystack[lo + i]){
                if (i == needle.length - 1){
                    return lo;
                } else {
                    i++;
                }
            }
            lo++;
        }
        return -1;
    }

    private static class indexTask extends RecursiveTask<Integer> {
        private final char[] needle, haystack;
        private final int lo, hi;
        public indexTask(char[] needle, char[]haystack, int lo, int hi){
            this.needle = needle;
            this.haystack = haystack;
            this.lo = lo;
            this.hi = hi;
        }

        protected Integer compute(){
            if (hi - lo <= CUTOFF){
                return sequentialIndex(needle, haystack, lo, hi);
            }
            int mid = lo + (hi - lo) / 2;
            indexTask left = new indexTask(needle, haystack, lo, mid);
            indexTask right = new indexTask(needle, haystack, mid, hi);
            left.fork();
            int rightResult = right.compute();
            int leftResult = left.join();
            if (leftResult != -1){
                return leftResult;
            } else {
                return rightResult;
            }

        }



    }


    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
