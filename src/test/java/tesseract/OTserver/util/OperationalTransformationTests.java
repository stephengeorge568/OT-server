package tesseract.OTserver.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

@SpringBootTest
public class OperationalTransformationTests {
    // https://www.baeldung.com/junit-5
    @Test
    void contextLoads() {
    }

    // play with naming this feels weird
    // startColumn, endColumn, startLineNumber, endLineNumber
    @Test
    void isPreviousRequestRelevent_assertTrue_PrevLineAndColumnAfterNext() {
        MonacoRange prev = new MonacoRange(1,1,1,1);
        MonacoRange next = new MonacoRange(2,2,2,2);
        assertEquals(true, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertFalse_PrevLineAndColumnBeforeNext() {
        MonacoRange next = new MonacoRange(1,1,1,1);
        MonacoRange prev = new MonacoRange(2,2,2,2);
        assertEquals(false, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_IntersectingRangesPrevInFront() {
        MonacoRange prev = new MonacoRange(3,7,1,2);
        MonacoRange next = new MonacoRange(5,9,2,2);
        assertEquals(true, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_IntersectingRangesPrevInBack() {
        MonacoRange next = new MonacoRange(3,7,1,2);
        MonacoRange prev = new MonacoRange(5,9,2,2);
        assertEquals(true, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_SameRange() {
        MonacoRange prev = new MonacoRange(1,1,1,1);
        MonacoRange next = new MonacoRange(1,1,1,1);
        assertEquals(true, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertFalse_PrevECIgnoredBecauseItsRange() {
        MonacoRange prev = new MonacoRange(2,2,1,1);
        MonacoRange next = new MonacoRange(1,2,1,1);
        assertEquals(false, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_PrevECConsideredBecauseSimpleInsert() {
        MonacoRange prev = new MonacoRange(2,2,1,1);
        MonacoRange next = new MonacoRange(2,2,1,1);
        assertEquals(true, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_NextRangeBeforePrevRangeOverlapSelection() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(4,9,1,1);
        assertEquals(true, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_NextPrevSameSelection() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(2,6,1,1);
        assertEquals(true, OperationalTransformation.isPreviousRequestRelevent(prev, next));
    }

    /* -------------------------------------------------------------------------------------------- */

    @Test
    void transformOperation_assertTrue_LinesChangeWhenPrevAboveNext() {
        StringChangeRequest prev = new StringChangeRequest("a\nb\nc", new MonacoRange(4,8,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,6, 2, 2));

        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,6, 2, 2));

        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(3,6,4,4));

        printTransOpTest(prev, nextCopy, OperationalTransformation.transformOperation(prev,next), expe);
        assertEquals(true, expe.isEqual(OperationalTransformation.transformOperation(prev, next)));
    }

    @Test
    void transformOperation_assertTrue_LinesChangeWhenPrevSameNext() {
        StringChangeRequest prev = new StringChangeRequest("a\nb\nc", new MonacoRange(1,1,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(3,3,3,3));

        printTransOpTest(prev, nextCopy, OperationalTransformation.transformOperation(prev,next), expe);
        assertEquals(true, expe.isEqual(OperationalTransformation.transformOperation(prev, next)));
    }

    private void printTransOpTest(StringChangeRequest prev, StringChangeRequest next,
                                  StringChangeRequest transformed,StringChangeRequest expe) {

        // I should make this cleaner ... but I won't
        String output = String.format("  \tPV\tNX\tTF\tEX\t\n" +
                      "SC\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "EC\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "SL\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "EL\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "%s\t%s\t%s\t%s", prev.getRange().getStartColumn(), next.getRange().getStartColumn(), transformed.getRange().getStartColumn(), expe.getRange().getStartColumn(),
                prev.getRange().getEndColumn(), next.getRange().getEndColumn(), transformed.getRange().getEndColumn(), expe.getRange().getEndColumn(),
                prev.getRange().getStartLineNumber(), next.getRange().getStartLineNumber(), transformed.getRange().getStartLineNumber(), expe.getRange().getStartLineNumber(),
                prev.getRange().getEndLineNumber(), next.getRange().getEndLineNumber(), transformed.getRange().getEndLineNumber(), expe.getRange().getEndLineNumber(),
                prev.getText(), next.getText(), transformed.getText(), expe.getText());
        System.out.println(output);
    }
}

