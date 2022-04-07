package tesseract.OTserver.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

@SpringBootTest
public class MonacoRangeUtilTests {
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
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertFalse_PrevLineAndColumnBeforeNext() {
        MonacoRange next = new MonacoRange(1,1,1,1);
        MonacoRange prev = new MonacoRange(2,2,2,2);
        assertEquals(false, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_IntersectingRangesPrevInFront() {
        MonacoRange prev = new MonacoRange(3,7,1,2);
        MonacoRange next = new MonacoRange(5,9,2,2);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_IntersectingRangesPrevInBack() {
        MonacoRange next = new MonacoRange(3,7,1,2);
        MonacoRange prev = new MonacoRange(5,9,2,2);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_SameRange() {
        MonacoRange prev = new MonacoRange(1,1,1,1);
        MonacoRange next = new MonacoRange(1,1,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertFalse_PrevECIgnoredBecauseItsRange() {
        MonacoRange prev = new MonacoRange(2,2,1,1);
        MonacoRange next = new MonacoRange(1,2,1,1);
        assertEquals(false, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_PrevECConsideredBecauseSimpleInsert() {
        MonacoRange prev = new MonacoRange(2,2,1,1);
        MonacoRange next = new MonacoRange(2,2,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_NextRangeBeforePrevRangeOverlapSelection() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(4,9,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_NextPrevSameSelection() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(2,6,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    /*------------------------------------------------------------------------------*/


    /*
    N |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_Same() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(2,6,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }


    /*
    N   |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_PrevBeforeNext1Line() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(1,4,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N  |-----|
    P   |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_NextBeforePrev1Line() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(5,9,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N  |-----|
    P        |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_NextBeforePrev1LineNoGap() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(6,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N  |-----|
    P         |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_NextBeforePrev1LineGap() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(7,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N       |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_PrevBeforeNext1LineNoGap() {
        MonacoRange prev = new MonacoRange(2,6,1,1);
        MonacoRange next = new MonacoRange(6,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N        |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_PrevBeforeNext1LineGap() {
        MonacoRange prev = new MonacoRange(2,6,1,1);
        MonacoRange next = new MonacoRange(7,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P   |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_PrevInsideNext1Line() {
        MonacoRange prev = new MonacoRange(2,9,1,1);
        MonacoRange next = new MonacoRange(4,6,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P   |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_NextInsidePrev1Line() {
        MonacoRange next = new MonacoRange(2,9,1,1);
        MonacoRange prev = new MonacoRange(4,6,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P   \n|-----|
     */
    @Test
    void isRangeOverlap_assertFalse_Both1LineButPrevBelowNext() {
        MonacoRange prev = new MonacoRange(2,9,2,2);
        MonacoRange next = new MonacoRange(4,6,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |-----\n
      ---| P |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_Next2LineStopsAtNextSC() {
        MonacoRange prev = new MonacoRange(4,9,2,2);
        MonacoRange next = new MonacoRange(1,4,1,2);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P  \n\n\n |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_WayDifferntLines() {
        MonacoRange prev = new MonacoRange(4,9,6,9);
        MonacoRange next = new MonacoRange(1,4,1,2);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }


}

