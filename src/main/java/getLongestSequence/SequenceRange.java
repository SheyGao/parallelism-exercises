package getLongestSequence;

/**
 * A major part of the challenge here is to figure out what to do with this class.
 * We heavily recommended not to edit this (but you can).
 */
public class SequenceRange {
    public int matchingOnLeft, matchingOnRight;
    public int longestRange;

    public SequenceRange(int matchingOnLeft, int matchingOnRight, int longestRange) {
        this.matchingOnLeft = matchingOnLeft;
        this.matchingOnRight = matchingOnRight;
        this.longestRange = longestRange;
    }
}
