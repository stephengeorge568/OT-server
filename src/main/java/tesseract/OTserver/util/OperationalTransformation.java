package tesseract.OTserver.util;

import tesseract.OTserver.objects.StringChangeRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationalTransformation {

    public static StringChangeRequest transformThisBitch(StringChangeRequest request,
                                                         HashMap<Long, ArrayList<StringChangeRequest>> history) {
        // transform the shit outta that request

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
    private static void transformOperation(StringChangeRequest prev, StringChangeRequest next) {
        // just build a list of rules that we can confirm. shits complicated asf



        // no matter what, if prev is exclusively after next in terms of range
        // next does not change

        // in regards to insert after insert with \n's in prev. This only affects column # if
        // they are on the same line. then, consider how many characters after last \n
        // and before next.text. Thats how many chars to shift column #s

        Integer newSC;
        Integer newEC;
        Integer newSL;
        Integer newEL;
        // INSERT after INSERT considering \n
//        if (prev.getRange().getStartLineNumber() <= next.getRange().getStartLineNumber() &&
//            prev.getRange().getEndLineNumber() <= next.getRange().getStartLineNumber() &&)

        int countOfNewLines = 0;
        Matcher m = Pattern.compile("(\\r\\n)|(\\n)|(\\r)").matcher(prev.getText());
        while (m.find())
            countOfNewLines ++; // sl & el will have this added to them
        newSL = next.getRange().getStartLineNumber() + countOfNewLines + 1;
        newEL = next.getRange().getEndLineNumber() + countOfNewLines + 1;

        // if this > 0, sc ec will be diff bc of it also
        if (countOfNewLines > 0) {

        }
    }


}
