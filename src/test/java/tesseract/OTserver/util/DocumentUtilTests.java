package tesseract.OTserver.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tesseract.OTserver.objects.MonacoRange;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DocumentUtilTests {

    @Test
    void contextLoads() {
    }

    @Test
    void getIndex_startOfModel() {
        assertEquals(1, DocumentUtil.getIndex("hello\nthere\\n\nokay\n\n\nthree \\n above this", 1, 1));
    }

    @Test
    void getIndex_lines() {
        assertEquals(31, DocumentUtil.getIndex("hello\nthere\\n\nokay\n\n\nthree \\n above this", 6, 3));
    }
}
