package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationalTransformation {

    // TODO just make this a bean or a service idk whcih is more epxected

    public static ArrayList<StringChangeRequest> transform(StringChangeRequest request,
                                                         HashMap<Long, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> transformedRequests = new ArrayList<>(2);
        // gather all the previous requests that will affect this one
        // update request to account for relevant historical requests
        for (StringChangeRequest historicalRequest : getAffectingRequests(request.getRevID(), history)) {
            // May need to do request = transformOper... TODO

            StringChangeRequest pair[] = MonacoRangeUtil.resolveConflictingRanges(historicalRequest, request);

            transformedRequests.add(transformOperation(historicalRequest, pair[0]));
            if (pair[1] != null) transformedRequests.add(transformOperation(historicalRequest, pair[1]));



        }
        if (transformedRequests.isEmpty()) {
            transformedRequests.add(request);
        }
        // might need to update revID to something IDk think about this later

        // return the new request
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

    /*
        If prev is exclusively after next than next wont change
        no new line, no intersect, on same line
            next sc = next.sc - (prev.ec - prev.sc) - prev.text.length // (same for next ec)

       when does stuff chagne:
            TODO prev is before next
            TODO theres new lines on prior lines
            TODO theres new line on same line as next
            replacing on same line alters columns
            TODO adjust for deletion of preceeding stuff to include newlines
            TODO conflicting ranges, might be diff for insert/remove/combinations etc

            MIGHT need to return list of SCRs
     */
    public static StringChangeRequest transformOperation(StringChangeRequest prev, StringChangeRequest next) {

        int newSC = next.getRange().getStartColumn();
        int newEC = next.getRange().getEndColumn();
        int newSL = next.getRange().getStartLineNumber();
        int newEL = next.getRange().getEndLineNumber();
        int numberOfNewLinesInPrev = (int) prev.getText().lines().count() - 1;

        if (MonacoRangeUtil.isPreviousRequestRelevent(prev.getRange(), next.getRange())) {

            // # of new lines removed
            int netNewLineNumberChange = numberOfNewLinesInPrev
                    - (prev.getRange().getEndLineNumber() - prev.getRange().getStartLineNumber());




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
