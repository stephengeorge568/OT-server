package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

public class MonacoRangeUtil {


    // Default is true. Finds conditions under which prev does not affect next and can be ignored

    /**
     * Determines whether or not a historical request affects the current request.
     * @param prev previous, historical request
     * @param next current request
     * @return true when prev range is at same spot or before next range
     * - i.e prev will affect next's range when transformed, false otherwise
     */
    public static boolean isPreviousRequestRelevent(MonacoRange prev, MonacoRange next) {

        boolean isNextSimpleInsert = next.getStartLineNumber() - next.getEndLineNumber() == 0
                && next.getStartColumn() - next.getEndColumn() == 0;
        boolean isPrevStartLineAfterNextEndLine = prev.getStartLineNumber() > next.getEndLineNumber();
        boolean isSameLine = prev.getStartLineNumber() == next.getEndLineNumber();

        if (isPrevStartLineAfterNextEndLine) return false; // if prev is on bigger line # than next - ignore
        if (isSameLine) { // if prev starts on same line that next ends
            // if next is simple insert, then next.ec cannot be prev.sc
            if(isNextSimpleInsert) {
                // next.ec must be less than prev.sc
                if (next.getEndColumn() < prev.getStartColumn()) return false;
            } else {
                // next.ec must be less or equal
                if (next.getEndColumn() <= prev.getStartColumn()) return false;
            }
        }
        return true;
    }

    /**
     * Determines whether or not there is range overlap in any direction between prev and next
     * @param prev previous, historical request
     * @param next current request
     * @return true when there is any overlap in the MonacoRanges of prev and next. False otherwise.
     */
    public static boolean isRangeOverlap(MonacoRange prev, MonacoRange next) {
        return isSCWithinRange(next, prev) || isSCWithinRange(prev, next)
                || isECWithinRange(next, prev) || isECWithinRange(prev, next);

    }

    // TODO cleanup
    /**
     * Determines whether or not p's start column is within n's range
     * @param n request n
     * @param p request p
     * @return true when p's start column is within n's range. false otherwise
     */
    public static boolean isSCWithinRange(MonacoRange p, MonacoRange n) {
        if (n.getStartLineNumber() > p.getStartLineNumber()
                && n.getStartLineNumber() < p.getEndLineNumber()) return true;

        if (n.getStartLineNumber() == p.getStartLineNumber()) {
            if (n.getStartLineNumber() == p.getEndLineNumber()) {
                if (!(n.getStartColumn() < p.getEndColumn())) return false;
            } if (n.getStartColumn() >= p.getStartColumn()) return true;
        }

        if (n.getStartLineNumber() == p.getEndLineNumber() && n.getStartLineNumber() != p.getStartLineNumber()) {
            if (n.getStartColumn() < p.getEndColumn()) return true;
        }

        return false;
    }

    /**
     * Determines whether or not p's end column is within n's range
     * @param n request n
     * @param p request p
     * @return true when p's end column is within n's range. false otherwise
     */
    public static boolean isECWithinRange(MonacoRange p, MonacoRange n) {
        if (n.getEndLineNumber() < p.getEndLineNumber()
                && n.getEndLineNumber() > p.getStartLineNumber()) return true;

        if (n.getEndLineNumber() == p.getEndLineNumber()) {
            if (n.getEndLineNumber() == p.getStartLineNumber()) {
                if (!(n.getEndColumn() > p.getStartColumn())) return false;
            } if (n.getEndColumn() <= p.getEndColumn()) return true;
        }

        if (n.getEndLineNumber() == p.getStartLineNumber() && n.getEndLineNumber() != p.getEndLineNumber()) {
            if (n.getEndColumn() > p.getStartColumn()) return true;
        }

        return false;
    }

    /**
     * Shifts the range of next to remove selection that both prev and next delete/replace
     * @param prev previous, historical request
     * @param next current request that can be altered
     * @return List of at most 2 requests. The first element is the original request that may have be altered.
     * the second element is either null or a second request generated from splitting two ranges
     */
    public static StringChangeRequest[] resolveConflictingRanges(StringChangeRequest prev, StringChangeRequest next) {
        /*
            N |-----|
            P |-----|

            make N simple insert after P.

            N       |
            P |-----|
             */
        if (isSCWithinRange(prev.getRange(), next.getRange()) && // TODO test this condition
                isECWithinRange(prev.getRange(), next.getRange())) {
            next.getRange().setStartLineNumber(prev.getRange().getEndLineNumber());
            next.getRange().setEndLineNumber(prev.getRange().getEndLineNumber());
            next.getRange().setStartColumn(prev.getRange().getEndColumn());
            next.getRange().setEndColumn(prev.getRange().getEndColumn());
        }

        /*
            N    |-----|
            P |-----|

            so push n.sc to p.ec, including *l ...

            N       |--|
            P |-----|
        */
        else if (isSCWithinRange(prev.getRange(), next.getRange())) {
            next.getRange().setStartLineNumber(prev.getRange().getEndLineNumber());
            next.getRange().setStartColumn(prev.getRange().getEndColumn());
        }

        /*
            N |-----|
            P    |-----|

            so push n.ec to p.sc, including *l ...

            N |--|
            P    |-----|

            NOTE: next is no longer relevant
        */
        else if (isECWithinRange(prev.getRange(), next.getRange())) {
            next.getRange().setEndLineNumber(prev.getRange().getStartLineNumber());
            next.getRange().setEndColumn(prev.getRange().getStartColumn());
        }

        /*
            N |-------------|
            P     |-----|

            N must be split up into two SCR... next & otherNext

            N |---|     |----|
            P     |-----|

            otherNext has no text. next keeps same text.

            NOTE: next is no longer relevant. otherNext must still be transformed
        */
        else if (isECWithinRange(next.getRange(), prev.getRange())
                && isSCWithinRange(next.getRange(), prev.getRange())) {
            //create deep copy for otherNext
            StringChangeRequest otherNext = new StringChangeRequest(next);
            otherNext.setText("");

            //shift end of next to start of prev
            next.getRange().setEndColumn(prev.getRange().getStartColumn());
            next.getRange().setEndLineNumber(prev.getRange().getStartLineNumber());

            //shift start of otherNext to end of prev
            otherNext.getRange().setStartColumn(prev.getRange().getEndColumn());
            otherNext.getRange().setStartLineNumber(prev.getRange().getEndLineNumber());

            return new StringChangeRequest[]{next, otherNext};
        }
        return new StringChangeRequest[]{next, null};
    }

}
