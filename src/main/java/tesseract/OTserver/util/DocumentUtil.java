package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

import java.util.Arrays;

public class DocumentUtil {

    /*
    Monaco API docs : https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.ITextModel.html#pushEditOperations
    This method must mimic Monaco API's pushEditOperations functionality (or whatever process actually does the editing)
     */
    public static String updateModel(String model, StringChangeRequest req) {
        // Delete stuff

        // Insert stuff
        return "";
    }

    public static int getIndex(String model, int col, int line) {
        int index = 0;
        String[] lines = model.split("\n");
        Arrays.stream(lines).forEach(l -> {
            System.out.println(l);
        });
        System.out.println(lines.length);
        for (int i = 1; i < line; i++) {
            index += lines[i].length() + 1; // plus 1 because \n is removed via model.split
        } index += col;

        return index;
    }

}
