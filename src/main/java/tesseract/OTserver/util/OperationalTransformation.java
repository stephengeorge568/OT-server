package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationalTransformation {

    /**
     * Transforms the given request based on the history of committed requests
     * TODO: can generate more requests due to resolveConflictingRanges. This is not accounted for yet.
     * @param request the request to transform
     * @param history the history of committed requests
     * @return
     */
    public static ArrayList<StringChangeRequest> transform(StringChangeRequest request,
                                                         HashMap<Integer, ArrayList<StringChangeRequest>> history) {
        // The first request in transformedRequests will always be the original, left-most request
        ArrayList<StringChangeRequest> transformedRequests = new ArrayList<>(2);
        transformedRequests.add(request);

        // For all relevant requests... transform
        for (StringChangeRequest historicalRequest : getRelevantHistory(request.getRevID(), history)) {

            // If previous request and current request share same identity, ignore, transform otherwise
            if (!(request.getIdentity().equals(historicalRequest.getIdentity()))) { //  && request.getRevID() == historicalRequest.getRevID() why was this inside !()??
                System.out.println("Transforming...");
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

    /**
     * Get the historical requests that affect the current request based on its revID
     * @param revID the revID of the current request
     * @param history the history map
     * @return list of requests that affect the current request
     */
    private static ArrayList<StringChangeRequest> getRelevantHistory(Integer revID, HashMap<Integer, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> relevantRequests = new ArrayList<>();

        history.forEach(((id, list) -> {
            if (id >= revID) {
                relevantRequests.addAll(list); //TODO does this do what i think. add to beginning or end. i think end.
            }
        }));

        return relevantRequests;
    }

    /**
     * Returns the transformed version of next based on the prev historical request.
     * @param prev the previous request, which serves as the basis on which to transform next
     * @param next the current request to transform
     * @return the transformed version of next that was altered based on prev's range and text
     */
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

            int netPrevNewLineNumberChange = numberOfNewLinesInPrev
                    - (prev.getRange().getEndLineNumber() - prev.getRange().getStartLineNumber());

            boolean isPrevSimpleInsert = prev.getRange().getStartColumn() == prev.getRange().getEndColumn()
                    && prev.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber();

            // TODO numberOfNewLinesInPrev > 0 may be redundant. clean up
            // If simple insert, range behaves differently.
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
                        newSC = (newSC - prev.getRange().getEndColumn()) + prevTextLengthAfterLastNewLine + 1;
                    }
                    if (next.getRange().getEndLineNumber() == prev.getRange().getEndLineNumber()) {
                        newEC = (newEC - prev.getRange().getEndColumn()) + prevTextLengthAfterLastNewLine + 1;
                    }
                } else {
                    // prev is range and there is no new lines in prev text


                    // this all assumes next is on same line as prev
                    int numberOfCharsDeletedOnPrevLine = prev.getRange().getEndColumn()
                                - prev.getRange().getStartColumn();
                    if (next.getRange().getStartLineNumber() == prev.getRange().getEndLineNumber()) {
                        newSC = newSC - numberOfCharsDeletedOnPrevLine + prev.getText().length();
                    } else { // next start is on diff line than prev start but still within range
                        newSC = prev.getRange().getStartColumn() + prev.getText().length();
                    }
                    
                    if (next.getRange().getEndLineNumber() == prev.getRange().getEndLineNumber()) {
                        newEC = newEC - numberOfCharsDeletedOnPrevLine + prev.getText().length();
                    } else {
                        if (MonacoRangeUtil.isRangeSimpleInsert(next.getRange())) {
                            newEC = newSC;
                        } else {
                            // TODO
                            System.out.println("NOT IMPLEMENTED.");
                        }
                    }
                    // deal with new on diff line (but still inside prev range)
                }
            }

            // If next start column within range of prev, push next.SL to prev.SL + # of new lines in prev
            if (MonacoRangeUtil.isSCWithinRange(prev.getRange(), next.getRange())) {
                newSL = prev.getRange().getStartLineNumber() + numberOfNewLinesInPrev;
            } else {
                newSL += netPrevNewLineNumberChange;
            }

            if (MonacoRangeUtil.isECWithinRange(prev.getRange(), next.getRange())) {
                newEL = prev.getRange().getStartLineNumber() + numberOfNewLinesInPrev;
            } else {
                newEL += netPrevNewLineNumberChange;
            }
        }

        next.setRange(new MonacoRange(newSC, newEC, newSL, newEL));
        return next;
    }

}
