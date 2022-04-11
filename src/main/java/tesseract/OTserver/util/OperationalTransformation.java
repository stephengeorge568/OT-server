package tesseract.OTserver.util;

import tesseract.OTserver.objects.StringChangeRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationalTransformation {

    // TODO just make this a bean or a service idk whcih is more epxected
    public static ArrayList<StringChangeRequest> transform(StringChangeRequest request,
                                                         HashMap<Long, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> transformedRequests = new ArrayList<>(2);

        for (StringChangeRequest historicalRequest : getAffectingRequests(request.getRevID(), history)) {
            StringChangeRequest pair[] = MonacoRangeUtil.resolveConflictingRanges(historicalRequest, request);

            transformedRequests.add(transformOperation(historicalRequest, pair[0]));
            if (pair[1] != null) transformedRequests.add(transformOperation(historicalRequest, pair[1]));
        }
        if (transformedRequests.isEmpty()) {
            transformedRequests.add(request);
        }
        return transformedRequests;
    }

    // returns list of changes with revIDs after given revID. List is ordered by revID in ascending order...
    // i.e oldest changes are at head of list
    private static ArrayList<StringChangeRequest> getAffectingRequests(Long revID, HashMap<Long, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> relevantRequests = new ArrayList<>();
        for (Long i = history.size() - revID + 1; i < history.size(); i++) {
            relevantRequests.addAll(history.get(i));
        } return relevantRequests;
    }

    public static StringChangeRequest transformOperation(StringChangeRequest prev, StringChangeRequest next) {

        int newSC = next.getRange().getStartColumn();
        int newEC = next.getRange().getEndColumn();
        int newSL = next.getRange().getStartLineNumber();
        int newEL = next.getRange().getEndLineNumber();
        int numberOfNewLinesInPrev = (int) prev.getText().chars().filter(x -> x == '\n').count();

        // TODO ensure \t and \n etc behave as 1 length char
        int prevTextLengthAfterLastNewLine = prev.getText().length();

        if (numberOfNewLinesInPrev > 0) {
            prevTextLengthAfterLastNewLine = prev.getText().length() - prev.getText().lastIndexOf("\n") - 1;
        }

        if (MonacoRangeUtil.isPreviousRequestRelevent(prev.getRange(), next.getRange())) {

            int netNewLineNumberChange = numberOfNewLinesInPrev
                    - (prev.getRange().getEndLineNumber() - prev.getRange().getStartLineNumber());

            boolean isPrevSimpleInsert = prev.getRange().getStartColumn() == prev.getRange().getEndColumn()
                    && prev.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber();

            if (isPrevSimpleInsert) {
                if (next.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber()) {
                    newSC = newSC - prev.getRange().getEndColumn() + prevTextLengthAfterLastNewLine + 1;
                } if (next.getRange().getEndLineNumber() == prev.getRange().getEndLineNumber()) {
                    newEC = newEC - prev.getRange().getEndColumn() + prevTextLengthAfterLastNewLine + 1;
                }
            } else {
                if (numberOfNewLinesInPrev > 0) {
                    if (next.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber()) {
                        newSC = (newSC - prev.getRange().getEndColumn()) + prevTextLengthAfterLastNewLine + 1; // do i need +1?
                    }
                    if (next.getRange().getEndLineNumber() == prev.getRange().getEndLineNumber()) {
                        newEC = (newEC - prev.getRange().getEndColumn()) + prevTextLengthAfterLastNewLine + 1;
                    }
                } else {
                    int numberOfCharsDeletedOnPrevLine = prev.getRange().getEndColumn()
                                - prev.getRange().getStartColumn();
                    if (next.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber()) {
                        newSC = newSC - numberOfCharsDeletedOnPrevLine + prev.getText().length();
                    }
                    if (next.getRange().getEndLineNumber() == prev.getRange().getEndLineNumber()) {
                        newEC = newEC - numberOfCharsDeletedOnPrevLine + prev.getText().length();
                    }

                }
            }

            newSL += netNewLineNumberChange;
            newEL += netNewLineNumberChange;
        }

        // next.setRange() probably more appropriate. Might need to be tested though
        next.getRange().setEndColumn(newEC);
        next.getRange().setStartColumn(newSC);
        next.getRange().setStartLineNumber(newSL);
        next.getRange().setEndLineNumber(newEL);
        return next;
    }

}
