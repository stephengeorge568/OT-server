package tesseract.OTserver.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest
public class OperationalTransformationTests {
    // https://www.baeldung.com/junit-5
    @Test
    void contextLoads() {
    }

    @Test
    void transformOperation_LinesChangeWhenPrevAboveNext() {
        StringChangeRequest prev = new StringChangeRequest("a\nb\nc", new MonacoRange(4,8,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,6, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,6, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(3,6,4,4));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_TwoSimpleInsertsPrevBefore() {
        StringChangeRequest prev = new StringChangeRequest("a", new MonacoRange(3,3,1,1 ));
        StringChangeRequest next = new StringChangeRequest("b", new MonacoRange(6,6, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("b", new MonacoRange(6,6, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("b", new MonacoRange(7,7,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_LinesChangeWhenPrevSameNext() {
        StringChangeRequest prev = new StringChangeRequest("a\nb\nc", new MonacoRange(1,1,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(4,4,3,3));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_ShiftRightNoNewLines() {
        StringChangeRequest prev = new StringChangeRequest("abc", new MonacoRange(1,1,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(6,6,1,1));

        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_ShiftRightNewLines() {
        StringChangeRequest prev = new StringChangeRequest("abc\ndefgh", new MonacoRange(1,1,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(8,8,2,2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    /*
    N           |-----|
    P |-----|               p.text=""
        turns into
    N    |-----|
    P
     */
    @Test
    void transformOperation_SelectionDelete() {
        StringChangeRequest prev = new StringChangeRequest("", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(7,9, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(4,9, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(4,6,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }


    @Test
    void transformOperation_ShiftRightNewLines2() {
        StringChangeRequest prev = new StringChangeRequest("\n", new MonacoRange(2,2,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6,6, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6,6, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(5,5,2,2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    /*
    N           |-----|
    P |-----|               p.text="abc"
        turns into
    N           |-----|
    P |-----|               abc same length as being deleted. stays same
     */
    @Test
    void transformOperation_SelectionReplaceSameNoNewLine() {
        StringChangeRequest prev = new StringChangeRequest("abc", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(9,12,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    /*
    N           |-----|
    P |-----|               p.text="abcdef"
        turns into
    N              |-----|
    P |-----|
     */
    @Test
    void transformOperation_SelectionReplaceNoNewLine() {
        StringChangeRequest prev = new StringChangeRequest("abcdef", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(12,15,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_SelectionReplaceNewLines() {
        StringChangeRequest prev = new StringChangeRequest("abc\n", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(5,8,2,2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_SelectionReplaceNewLinesTextAfterNewLine() {
        StringChangeRequest prev = new StringChangeRequest("abc\ndefg", new MonacoRange(1, 4, 1, 1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(7, 13, 2, 2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_SelectionDiffLinesNoNewLineRemoved() {
        StringChangeRequest prev = new StringChangeRequest("abc\ndefg", new MonacoRange(1, 4, 1, 1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(6, 12, 3, 3));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_DeletionDiffLinesNewLineRemoved() {
        StringChangeRequest prev = new StringChangeRequest("", new MonacoRange(3, 4, 1, 2 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(5, 11, 1, 1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_DeletionDiffLinesNewLineRemovedTextAdded() {
        StringChangeRequest prev = new StringChangeRequest("qtf", new MonacoRange(3, 4, 1, 2 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(8, 14, 1, 1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transform_OnlyFirstHistoryRelevant() {
        StringChangeRequest request = new StringChangeRequest("a", new MonacoRange(5, 5, 1, 1 ), 1);
        StringChangeRequest history1 = new StringChangeRequest("c", new MonacoRange(1, 1, 1, 1), 1);
        StringChangeRequest history2 = new StringChangeRequest("q", new MonacoRange(3, 3, 1, 1), 1);

        ArrayList<StringChangeRequest> historyList = new ArrayList<>();
        historyList.add(history1);
        historyList.add(history2);

        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();
        history.put(1, historyList);
        StringChangeRequest expe1 = new StringChangeRequest("a", new MonacoRange(7, 7, 1, 1), 1);
        //StringChangeRequest expe2 = new StringChangeRequest(null);
        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);

        System.out.println("Transformed:\n" + trans.get(0).toString());
        System.out.println("Expected:\n" + expe1.toString());

        assertEquals(true, trans.get(0).isEqual(expe1));
        //assertEquals(true, trans.get(1) == null);
    }



//    @Test
//    void transformOperation_PrevInsideNextReplaceWithText() {
//        StringChangeRequest prev = new StringChangeRequest("123", new MonacoRange(6, 2, 1, 2 ));
//        StringChangeRequest next = new StringChangeRequest("abcdef", new MonacoRange(4, 4, 1, 2));
//        StringChangeRequest nextCopy = new StringChangeRequest("abcdef", new MonacoRange(4, 4, 1, 2));
//        StringChangeRequest expe = new StringChangeRequest("abcdef", new MonacoRange(4, 11, 1, 1));
//        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);
//
//        printTransOpTest(prev, nextCopy, tran, expe);
//        assertEquals(true, expe.isEqual(tran));
//    }

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

    @Test
    void test() {
        System.out.println("ok\nfm".lastIndexOf("\n"));
        System.out.println("ok\nfm".length() - "ok\nfm".lastIndexOf("\n"));
    }




}

