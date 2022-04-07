package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;

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

    // TODO test
    public static boolean isRangeOverlap(MonacoRange prev, MonacoRange next) {

        System.out.printf("isSCWithinRange(next, prev): %s\nisSCWithinRange(prev, next): %s" +
                "\nisECWithinRange(next, prev): %s\nisECWithinRange(prev, next): %s\n",
                isSCWithinRange(next, prev),isSCWithinRange(prev, next),isECWithinRange(next, prev),isECWithinRange(prev, next));

        return isSCWithinRange(next, prev) || isSCWithinRange(prev, next)
                || isECWithinRange(next, prev) || isECWithinRange(prev, next);

    }

    // TODO test and cleanup
    private static boolean isSCWithinRange(MonacoRange n, MonacoRange p) {
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

    // TODO test and cleanup
    private static boolean isECWithinRange(MonacoRange n, MonacoRange p) {
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

}
