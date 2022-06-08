package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationalTransformation {

    /*
    TODO
    conflicting range produces extra SCR's. this is not accounted for!
     */
    public static ArrayList<StringChangeRequest> transform(StringChangeRequest request,
                                                         HashMap<Integer, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> transformedRequests = new ArrayList<>(2);
        transformedRequests.add(request);

        for (StringChangeRequest historicalRequest : getRelevantHistory(request.getRevID(), history)) {
            // two request back to back from same client. the second one should not transform based on the first.
            // it already accounted for that
            if (!(request.getIdentity().equals(historicalRequest.getIdentity()) && request.getRevID() == historicalRequest.getRevID())) {
                StringChangeRequest pair[] = MonacoRangeUtil.resolveConflictingRanges(historicalRequest, transformedRequests.get(0));
                StringChangeRequest temp = transformOperation(historicalRequest, pair[0]);
                transformedRequests.set(0, temp);
            }
            //if (pair[1] != null) transformedRequests.add(transformOperation(historicalRequest, pair[1]));
        }
        if (transformedRequests.isEmpty()) {
            transformedRequests.add(request);
        }
        return transformedRequests;
    }

    // returns list of changes with revIDs after given revID. List is ordered by revID in ascending order...
    // i.e oldest changes are at head of list
    private static ArrayList<StringChangeRequest> getRelevantHistory(Integer revID, HashMap<Integer, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> relevantRequests = new ArrayList<>();

        history.forEach(((id, list) -> {
            if (id >= revID) {
                relevantRequests.addAll(list);
            }
        }));

        return relevantRequests;
    }

    public static StringChangeRequest transformOperation(StringChangeRequest prev, StringChangeRequest next) {

        int newSC = next.getRange().getStartColumn();
        int newEC = next.getRange().getEndColumn();
        int newSL = next.getRange().getStartLineNumber();
        int newEL = next.getRange().getEndLineNumber();
        int numberOfNewLinesInPrev = (int) prev.getText().chars().filter(x -> x == '\n').count();

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
                if (numberOfNewLinesInPrev > 0) {
                    if (next.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber()) {
                        newSC = newSC - prev.getRange().getEndColumn() + prevTextLengthAfterLastNewLine + 1;
                    } if (next.getRange().getEndLineNumber() == prev.getRange().getEndLineNumber()) {
                        newEC = newEC - prev.getRange().getEndColumn() + prevTextLengthAfterLastNewLine + 1;
                    }
                } else {
                    if (next.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber()) {
                        newSC = newSC + prevTextLengthAfterLastNewLine;
                    } if (next.getRange().getEndLineNumber() == prev.getRange().getEndLineNumber()) {
                        newEC = newEC + prevTextLengthAfterLastNewLine;
                    }
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

        next.setRange(new MonacoRange(newSC, newEC, newSL, newEL));
        return next;
    }

}
