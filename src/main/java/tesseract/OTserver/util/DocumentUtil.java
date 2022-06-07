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
        System.out.println("Length: " + model.length());
        System.out.println("Model: " + model);
        System.out.println("Req: " + req.getText());
        System.out.println("ins: " + getIndex(model, req.getRange().getStartColumn(), req.getRange().getStartLineNumber()));
        System.out.println("col: " + req.getRange().getStartColumn());
        StringBuilder modelBuilder = new StringBuilder(model);
        boolean isSimpleInsert = req.getRange().getStartLineNumber() == req.getRange().getEndLineNumber()
                && req.getRange().getStartColumn() == req.getRange().getEndColumn();

        if (!isSimpleInsert)
            modelBuilder.replace(
                    getIndex(model, req.getRange().getStartColumn(), req.getRange().getStartLineNumber()),
                    getIndex(model, req.getRange().getEndColumn(), req.getRange().getEndLineNumber()),
                    req.getText());
        else modelBuilder.insert(
                getIndex(model, req.getRange().getStartColumn(), req.getRange().getStartLineNumber()),
                req.getText());
        return modelBuilder.toString();
    }

    public static int getIndex(String model, int col, int line) {
        int index = 0;
        String[] lines = model.split("\n");

        for (int i = 0; i < line - 1; i++) {
            index += lines[i].length(); // plus 1 because \n is removed via model.split
        } index += col + line - 1;

        return index - 1; // -1 because string first index is 0, whereas MonacoRange first index is 1
    }
}
