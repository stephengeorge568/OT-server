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

