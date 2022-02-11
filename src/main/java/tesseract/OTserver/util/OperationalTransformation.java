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
    private static StringChangeRequest transformOperation(StringChangeRequest prev, StringChangeRequest next) {

        if (isPreviousRequestRelevent(prev.getRange(), next.getRange())) {

            // return a new StringChangeRequest
        }

        return next;
    }

    // Default is true. Finds conditions under which prev does not affect next and can be ignored
    public static boolean isPreviousRequestRelevent(MonacoRange prev, MonacoRange next) {

        boolean isNextSimpleInsert = next.getStartLineNumber() - next.getEndLineNumber() == 0
                && next.getStartColumn() - next.getEndColumn() == 0;
        boolean isPrevStartLineAfterNextEndLine = prev.getStartLineNumber() > next.getEndLineNumber();
        boolean isSameLine = prev.getStartLineNumber() == next.getEndLineNumber();

        if (isPrevStartLineAfterNextEndLine) return false; // if prev is on bigger line # than next - ignore
        if (isSameLine) { // if prev starts on same line that next ends
            // if next is simple insert, then next.ec cannot be prev.sc
            if(isNextSimpleInsert) {
                // next.ec must be less than prev.sc
                if (next.getEndColumn() < prev.getStartColumn()) return false;
            } else {
                // next.ec must be less or equal
                if (next.getEndColumn() <= prev.getStartColumn()) return false;
            }
        }
        return true;
    }

}
