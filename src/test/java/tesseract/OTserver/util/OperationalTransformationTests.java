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

}

