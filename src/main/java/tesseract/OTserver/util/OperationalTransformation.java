package tesseract.OTserver.util;

import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationalTransformation {

    // TODO just make this a bean or a service idk whcih is more epxected

    public static StringChangeRequest transformThisBitch(StringChangeRequest request,
                                                         HashMap<Long, ArrayList<StringChangeRequest>> history) {

        // gather all the previous requests that will affect this one
        // update request to account for relevant historical requests
        for (StringChangeRequest historicalRequest : getAffectingRequests(request.getRevID(), history)) {
            // May need to do request = transformOper... TODO
            transformOperation(historicalRequest, request);
        }

        // might need to update revID to something IDk think about this later

        // return the new request
        return request;
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

            if (MonacoRangeUtil.isRangeOverlap(prev.getRange(), next.getRange())) {
                // adjust next accordingly, i.e remove commonly replaced ranges
            }



            newSL += netNewLineNumberChange;
            newEL += netNewLineNumberChange;
        }

        next.getRange().setEndColumn(newEC);
        next.getRange().setStartColumn(newSC);
        next.getRange().setStartLineNumber(newSL);
        next.getRange().setEndLineNumber(newEL);
        return next;
    }









}
