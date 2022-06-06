package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.objects.StringResponse;

public class MonacoRangeUtil {


    // Default is true. Finds conditions under which prev does not affect next and can be ignored
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

    public static boolean isRangeOverlap(MonacoRange prev, MonacoRange next) {
        System.out.printf("isSCWithinRange(next, prev): %s\nisSCWithinRange(prev, next): %s" +
                "\nisECWithinRange(next, prev): %s\nisECWithinRange(prev, next): %s\n",
                isSCWithinRange(next, prev),isSCWithinRange(prev, next),isECWithinRange(next, prev),isECWithinRange(prev, next));

        return isSCWithinRange(next, prev) || isSCWithinRange(prev, next)
                || isECWithinRange(next, prev) || isECWithinRange(prev, next);

    }

    // TODO cleanup
    public static boolean isSCWithinRange(MonacoRange n, MonacoRange p) {
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

    // TODO cleanup
    public static boolean isECWithinRange(MonacoRange n, MonacoRange p) {
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

    public static StringChangeRequest[] resolveConflictingRanges(StringChangeRequest prev, StringChangeRequest next) {


        /*
            N |-----|
            P |-----|

            make N simple insert after P.

            N       |
            P |-----|
             */
        if (isSCWithinRange(next.getRange(), prev.getRange()) && // TODO test this condition
                isECWithinRange(next.getRange(), prev.getRange())) {
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
        else if (isSCWithinRange(next.getRange(), prev.getRange())) {
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
        else if (isECWithinRange(next.getRange(), prev.getRange())) {
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
        else if (isECWithinRange(prev.getRange(), next.getRange())
                && isSCWithinRange(prev.getRange(), next.getRange())) {
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
