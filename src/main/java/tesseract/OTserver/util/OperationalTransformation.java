package tesseract.OTserver.util;

import tesseract.OTserver.objects.StringChangeRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class OperationalTransformation {

    public static StringChangeRequest transformThisBitch(StringChangeRequest request,
                                                         HashMap<Long, ArrayList<StringChangeRequest>> history) {
        // transform the shit outta that request

        // gather all the previous requests that will affect this one
        // update request to account for relevant historical requests
        for (StringChangeRequest historicalRequest : getAffectingRequests(request.getRevID(), history)) {

        }

        // return the new request
        return null;
    }

    // returns list of changes with revIDs after given revID. List is ordered by revID in ascending order...
    // i.e oldest changes are at head of list
    private static ArrayList<StringChangeRequest> getAffectingRequests(Long revID, HashMap<Long, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> relevantRequests = new ArrayList<>();
        for (Long i = history.size() - revID + 1; i < history.size(); i++) {
            relevantRequests.addAll(history.get(i));
        } return relevantRequests;
    }

    private static void transformOperation(StringChangeRequest current, StringChangeRequest previous) {

    }


}
