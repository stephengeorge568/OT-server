[1mdiff --git a/src/main/java/tesseract/OTserver/controllers/StringChangeController.java b/src/main/java/tesseract/OTserver/controllers/StringChangeController.java[m
[1mindex 9c89a1f..d87ea00 100644[m
[1m--- a/src/main/java/tesseract/OTserver/controllers/StringChangeController.java[m
[1m+++ b/src/main/java/tesseract/OTserver/controllers/StringChangeController.java[m
[36m@@ -23,7 +23,7 @@[m [mpublic class StringChangeController {[m
     )[m
     @ResponseBody[m
     public ResponseEntity<Integer> stringChange(HttpServletRequest httpRequest, @RequestBody StringChangeRequest request) {[m
[31m-        System.out.printf("DEBUG: received:\n\tfrom: %s\n\tstr:\n\trevId:%d\n\n", request.getIdentity(), request.getText(), request.getRevID());[m
[32m+[m[32m        System.out.printf("DEBUG: received:\n\tfrom: %s\n\tstr:%s\n\trevId:%d\n", request.getIdentity(), request.getText(), request.getRevID());[m
         return ResponseEntity.ok(this.documentService.submitChange(request));[m
     }[m
 [m
[1mdiff --git a/src/main/java/tesseract/OTserver/objects/Document.java b/src/main/java/tesseract/OTserver/objects/Document.java[m
[1mindex a9b58ad..85c2b50 100644[m
[1m--- a/src/main/java/tesseract/OTserver/objects/Document.java[m
[1m+++ b/src/main/java/tesseract/OTserver/objects/Document.java[m
[36m@@ -7,7 +7,7 @@[m [mpublic class Document {[m
     private DocumentLanguage language;[m
     private String model;[m
     private Integer revID;[m
[31m-    private HashMap<Long, ArrayList<StringChangeRequest>> changeHistory;[m
[32m+[m[32m    private HashMap<Integer, ArrayList<StringChangeRequest>> changeHistory;[m
     private Queue<StringChangeRequest> pendingChangesQueue;[m
 [m
     // Will need new constructor when its ready to pull data from previous session[m
[36m@@ -36,11 +36,11 @@[m [mpublic class Document {[m
         this.revID = revID;[m
     }[m
 [m
[31m-    public HashMap<Long, ArrayList<StringChangeRequest>> getChangeHistory() {[m
[32m+[m[32m    public HashMap<Integer, ArrayList<StringChangeRequest>> getChangeHistory() {[m
         return changeHistory;[m
     }[m
 [m
[31m-    public void setChangeHistory(HashMap<Long, ArrayList<StringChangeRequest>> changeHistory) {[m
[32m+[m[32m    public void setChangeHistory(HashMap<Integer, ArrayList<StringChangeRequest>> changeHistory) {[m
         this.changeHistory = changeHistory;[m
     }[m
 [m
[1mdiff --git a/src/main/java/tesseract/OTserver/objects/StringChangeRequest.java b/src/main/java/tesseract/OTserver/objects/StringChangeRequest.java[m
[1mindex 03e109d..d90a0da 100644[m
[1m--- a/src/main/java/tesseract/OTserver/objects/StringChangeRequest.java[m
[1m+++ b/src/main/java/tesseract/OTserver/objects/StringChangeRequest.java[m
[36m@@ -1,16 +1,23 @@[m
 package tesseract.OTserver.objects;[m
[31m-import java.time.Instant;[m
 [m
 public class StringChangeRequest {[m
     private String timestamp;[m
     private String text;[m
     private String identity; // currently just client ip. this will change[m
     private MonacoRange range;[m
[31m-    private Long revID;[m
[32m+[m[32m    private Integer revID;[m
[32m+[m[32m    private Integer setID = -1; //this used to convey to clients the new revID to set for document[m
 [m
     public StringChangeRequest(String text, MonacoRange range) {[m
         this.text = text;[m
         this.range = range;[m
[32m+[m[32m        this.identity = range.toString() + text;[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    public StringChangeRequest(String text, MonacoRange range, Integer revID) {[m
[32m+[m[32m        this.text = text;[m
[32m+[m[32m        this.range = range;[m
[32m+[m[32m        this.revID = revID;[m
     }[m
 [m
     public StringChangeRequest() {}[m
[36m@@ -28,11 +35,11 @@[m [mpublic class StringChangeRequest {[m
         this.revID = other.getRevID();[m
     }[m
 [m
[31m-    public Long getRevID() {[m
[32m+[m[32m    public Integer getRevID() {[m
         return revID;[m
     }[m
 [m
[31m-    public void setRevID(Long revID) {[m
[32m+[m[32m    public void setRevID(Integer revID) {[m
         this.revID = revID;[m
     }[m
 [m
[36m@@ -74,6 +81,13 @@[m [mpublic class StringChangeRequest {[m
         this.range = range;[m
     }[m
 [m
[32m+[m[32m    public Integer getSetID() {[m
[32m+[m[32m        return setID;[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    public void setSetID(Integer setID) {[m
[32m+[m[32m        this.setID = setID;[m
[32m+[m[32m    }[m
 [m
     public boolean isEqual(StringChangeRequest req) {[m
         return this.getRange().isEqual(req.getRange())[m
[36m@@ -86,7 +100,10 @@[m [mpublic class StringChangeRequest {[m
                 "timestamp='" + timestamp + '\'' +[m
                 ", text='" + text + '\'' +[m
                 ", identity='" + identity + '\'' +[m
[31m-                ", rangeSC=" + range.getStartColumn() +[m
[32m+[m[32m                ", rangeSC=" + range.getStartColumn() + '\'' +[m
[32m+[m[32m                ", rangeEC=" + range.getEndColumn() + '\'' +[m
[32m+[m[32m                ", rangeSL=" + range.getStartLineNumber() + '\'' +[m
[32m+[m[32m                ", rangeEL=" + range.getEndLineNumber() + '\'' +[m
                 ", revID=" + revID +[m
                 '}';[m
     }[m
[1mdiff --git a/src/main/java/tesseract/OTserver/services/DocumentService.java b/src/main/java/tesseract/OTserver/services/DocumentService.java[m
[1mindex abf5e33..8c7728e 100644[m
[1m--- a/src/main/java/tesseract/OTserver/services/DocumentService.java[m
[1m+++ b/src/main/java/tesseract/OTserver/services/DocumentService.java[m
[36m@@ -6,6 +6,7 @@[m [mimport org.springframework.stereotype.Service;[m
 import tesseract.OTserver.objects.Document;[m
 import tesseract.OTserver.objects.StringChangeRequest;[m
 import tesseract.OTserver.util.DocumentUtil;[m
[32m+[m[32mimport tesseract.OTserver.util.MonacoRangeUtil;[m
 import tesseract.OTserver.util.OperationalTransformation;[m
 [m
 import java.util.ArrayList;[m
[36m@@ -27,7 +28,7 @@[m [mpublic class DocumentService {[m
     // TODO reformat so easier to test[m
     // Thread.sleep is a temporary solution. This will need to be redesigned more than likely TODO[m
     public Integer submitChange(StringChangeRequest request) {[m
[31m-        // put change in pending changes queue[m
[32m+[m
         currentDocument.getPendingChangesQueue().add(request);[m
         while (!(this.currentDocument.getPendingChangesQueue().peek().getTimestamp().equals(request.getTimestamp())[m
                 && this.currentDocument.getPendingChangesQueue().peek().getIdentity().equals(request.getIdentity()))) {[m
[36m@@ -38,26 +39,26 @@[m [mpublic class DocumentService {[m
             }[m
         }[m
 [m
[32m+[m[32m        // resolve conflicting ranges, produce list of scr to transform[m
[32m+[m
[32m+[m
         ArrayList<StringChangeRequest> newChangeRequests = OperationalTransformation.transform(request, this.currentDocument.getChangeHistory());[m
 [m
         this.currentDocument.setRevID(this.currentDocument.getRevID() + 1);[m
 [m
         for (StringChangeRequest changedRequest : newChangeRequests) {[m
[32m+[m
[32m+[m[32m            changedRequest.setSetID(this.currentDocument.getRevID());[m
[32m+[m
             if (changedRequest != null) {[m
                 if (this.currentDocument.getChangeHistory().get(changedRequest.getRevID()) != null)[m
                     this.currentDocument.getChangeHistory().get(changedRequest.getRevID()).add(changedRequest);[m
                 else[m
                     this.currentDocument.getChangeHistory().put(changedRequest.getRevID(), new ArrayList<>(Arrays.asList(changedRequest)));[m
[31m-                this.currentDocument.setModel(DocumentUtil.updateModel(this.currentDocument.getModel(), changedRequest));[m
[31m-                System.out.println("\n-------------------------------------------------------------------------\n"[m
[31m-                        + this.currentDocument.getModel()[m
[31m-                        + "\n-------------------------------------------------------------------------");[m
[31m-                System.out.printf("DEBUG: propogating:\n\tfrom: %s\n\tstr:\n\trevId:%d\n\n",[m
[31m-                        changedRequest.getIdentity(),[m
[31m-                        changedRequest.getText(),[m
[31m-                        changedRequest.getRevID());[m
[31m-                // TODO do i need to update changedRequest revID before propogation?[m
[31m-                this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", changedRequest);[m
[32m+[m
[32m+[m[32m                updateModel(changedRequest);[m
[32m+[m
[32m+[m[32m                propogateToClients(changedRequest);[m
             }[m
         }[m
 [m
[36m@@ -66,6 +67,21 @@[m [mpublic class DocumentService {[m
         return this.currentDocument.getRevID();[m
     }[m
 [m
[32m+[m[32m    private void updateModel(StringChangeRequest changedRequest) {[m
[32m+[m[32m        this.currentDocument.setModel(DocumentUtil.updateModel(this.currentDocument.getModel(), changedRequest));[m
[32m+[m[32m        System.out.println("-------------------------------------------------------------------------\n"[m
[32m+[m[32m                + this.currentDocument.getModel()[m
[32m+[m[32m                + "\n-------------------------------------------------------------------------");[m
[32m+[m[32m        System.out.println("Okay");[m
[32m+[m[32m        System.out.printf("DEBUG: propogating:\n\tfrom: %s\n\tstr:%s\n\trevId:%d\n\n",[m
[32m+[m[32m                changedRequest.getIdentity(),[m
[32m+[m[32m                changedRequest.getText(),[m
[32m+[m[32m                changedRequest.getRevID());[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    private void propogateToClients(StringChangeRequest changedRequest) {[m
[32m+[m[32m        this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", changedRequest);[m
[32m+[m[32m    }[m
 [m
 [m
     public void printStuff() {[m
[1mdiff --git a/src/main/java/tesseract/OTserver/util/OperationalTransformation.java b/src/main/java/tesseract/OTserver/util/OperationalTransformation.java[m
[1mindex 8bb22df..de67d2d 100644[m
[1m--- a/src/main/java/tesseract/OTserver/util/OperationalTransformation.java[m
[1m+++ b/src/main/java/tesseract/OTserver/util/OperationalTransformation.java[m
[36m@@ -8,11 +8,14 @@[m [mpublic class OperationalTransformation {[m
 [m
     // TODO just make this a bean or a service idk whcih is more epxected[m
     public static ArrayList<StringChangeRequest> transform(StringChangeRequest request,[m
[31m-                                                         HashMap<Long, ArrayList<StringChangeRequest>> history) {[m
[31m-        ArrayList<StringChangeRequest> transformedRequests = new ArrayList<>(2);[m
[32m+[m[32m                                                         HashMap<Integer, ArrayList<StringChangeRequest>> history) {[m
[32m+[m[32m        ArrayList<StringChangeRequest> transformedRequests = new ArrayList<>();[m
[32m+[m[32m        ensureHistoryIsPopulated(request.getRevID(), history);[m
[32m+[m[32m        // todo given big enough history, it can spawn more than 2 scr.[m
 [m
[31m-        for (StringChangeRequest historicalRequest : getAffectingRequests(request.getRevID(), history)) {[m
[31m-            StringChangeRequest pair[] = MonacoRangeUtil.resolveConflictingRanges(historicalRequest, request);[m
[32m+[m[32m        for (StringChangeRequest historicalRequest : getRelevantHistory(request.getRevID(), history)) {[m
[32m+[m[32m            // todo may need to do this before transform is called. then call transform on all that it recursively creates[m
[32m+[m[32m            StringChangeRequest pair[] = MonacoRangeUtil.resolveConflictingRanges(request, historicalRequest);[m
 [m
             transformedRequests.add(transformOperation(historicalRequest, pair[0]));[m
             if (pair[1] != null) transformedRequests.add(transformOperation(historicalRequest, pair[1]));[m
[36m@@ -23,24 +26,55 @@[m [mpublic class OperationalTransformation {[m
         return transformedRequests;[m
     }[m
 [m
[32m+[m[32m    private static void getGeneratedRequestsFromConflictingRanges(StringChangeRequest request,[m
[32m+[m[32m                                                                  HashMap<Integer, ArrayList<StringChangeRequest>> history,[m
[32m+[m[32m                                                                  int index) {[m
[32m+[m[32m        ArrayList<StringChangeRequest> relevantRequests = getRelevantHistory(request.getRevID(), history);[m
[32m+[m
[32m+[m[32m        // resolve conflicts[m
[32m+[m[32m        for (StringChangeRequest historicalRequest : getRelevantHistory(request.getRevID(), history)) {[m
[32m+[m[32m            StringChangeRequest pair[] = MonacoRangeUtil.resolveConflictingRanges(request, historicalRequest);[m
[32m+[m
[32m+[m[32m            //transformedRequests.add(transformOperation(historicalRequest, pair[0]));[m
[32m+[m[32m            //if (pair[1] != null) transformedRequests.add(transformOperation(historicalRequest, pair[1]));[m
[32m+[m[32m        }[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m
[32m+[m
[32m+[m[32m    private static void ensureHistoryIsPopulated(Integer revID ,HashMap<Integer, ArrayList<StringChangeRequest>> history) {[m
[32m+[m[32m        for (int i = 1; i <= revID; i++) {[m
[32m+[m[32m            if (!history.containsKey(i)) history.put(i, new ArrayList<>());[m
[32m+[m[32m        }[m
[32m+[m[32m    }[m
[32m+[m
     // returns list of changes with revIDs after given revID. List is ordered by revID in ascending order...[m
     // i.e oldest changes are at head of list[m
[31m-    private static ArrayList<StringChangeRequest> getAffectingRequests(Long revID, HashMap<Long, ArrayList<StringChangeRequest>> history) {[m
[32m+[m[32m    private static ArrayList<StringChangeRequest> getRelevantHistory(Integer revID, HashMap<Integer, ArrayList<StringChangeRequest>> history) {[m
         ArrayList<StringChangeRequest> relevantRequests = new ArrayList<>();[m
[31m-        for (Long i = history.size() - revID + 1; i < history.size(); i++) {[m
[31m-            relevantRequests.addAll(history.get(i));[m
[31m-        } return relevantRequests;[m
[32m+[m[32m        // todo ignore scr of revID same if from same identity?[m
[32m+[m
[32m+[m[32m        history.forEach(((id, list) -> {[m
[32m+[m[32m            if (id >= revID) {[m
[32m+[m[32m                relevantRequests.addAll(list);[m
[32m+[m[32m            }[m
[32m+[m[32m        }));[m
[32m+[m[32m//        for (Integer i = revID; history.containsKey(i) && history.size() < i; i++) {[m
[32m+[m[32m//            relevantRequests.addAll(history.get(i));[m
[32m+[m[32m//        }[m
[32m+[m[32m        return relevantRequests;[m
     }[m
 [m
     public static StringChangeRequest transformOperation(StringChangeRequest prev, StringChangeRequest next) {[m
 [m
[32m+[m[32m        if (prev.getIdentity().equals(next.getIdentity())) return next;[m
[32m+[m
         int newSC = next.getRange().getStartColumn();[m
         int newEC = next.getRange().getEndColumn();[m
         int newSL = next.getRange().getStartLineNumber();[m
         int newEL = next.getRange().getEndLineNumber();[m
         int numberOfNewLinesInPrev = (int) prev.getText().chars().filter(x -> x == '\n').count();[m
 [m
[31m-        // TODO ensure \t and \n etc behave as 1 length char[m
         int prevTextLengthAfterLastNewLine = prev.getText().length();[m
 [m
         if (numberOfNewLinesInPrev > 0) {[m
[1mdiff --git a/src/test/java/tesseract/OTserver/util/OperationalTransformationTests.java b/src/test/java/tesseract/OTserver/util/OperationalTransformationTests.java[m
[1mindex 79832b4..b795914 100644[m
[1m--- a/src/test/java/tesseract/OTserver/util/OperationalTransformationTests.java[m
[1m+++ b/src/test/java/tesseract/OTserver/util/OperationalTransformationTests.java[m
[36m@@ -6,6 +6,9 @@[m [mimport org.springframework.boot.test.context.SpringBootTest;[m
 import tesseract.OTserver.objects.MonacoRange;[m
 import tesseract.OTserver.objects.StringChangeRequest;[m
 [m
[32m+[m[32mimport java.util.ArrayList;[m
[32m+[m[32mimport java.util.HashMap;[m
[32m+[m
 @SpringBootTest[m
 public class OperationalTransformationTests {[m
     // https://www.baeldung.com/junit-5[m
[36m@@ -183,6 +186,27 @@[m [mpublic class OperationalTransformationTests {[m
         assertEquals(true, expe.isEqual(tran));[m
     }[m
 [m
[32m+[m[32m    @Test[m
[32m+[m[32m    void transform_OnlyFirstHistoryRelevant() {[m
[32m+[m[32m        StringChangeRequest request = new StringChangeRequest("qtf", new MonacoRange(5, 7, 1, 1 ), 1);[m
[32m+[m[32m        StringChangeRequest history1 = new StringChangeRequest("", new MonacoRange(1, 4, 1, 1), 1);[m
[32m+[m[32m        StringChangeRequest history2 = new StringChangeRequest("abc", new MonacoRange(5, 5, 1, 1), 1);[m
[32m+[m[32m        ArrayList<StringChangeRequest> historyList = new ArrayList<>();[m
[32m+[m[32m        historyList.add(new StringChangeRequest("", new MonacoRange(1, 4, 1, 1), 1));[m
[32m+[m[32m        historyList.add(new StringChangeRequest("abc", new MonacoRange(5, 5, 1, 1), 1));[m
[32m+[m[32m        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();[m
[32m+[m[32m        history.put(1, historyList);[m
[32m+[m[32m        StringChangeRequest expe1 = new StringChangeRequest("qtf", new MonacoRange(2, 4, 1, 1), 1);[m
[32m+[m[32m        //StringChangeRequest expe2 = new StringChangeRequest(null);[m
[32m+[m[32m        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);[m
[32m+[m
[32m+[m[32m        System.out.println(trans.get(0).toString());[m
[32m+[m[32m        System.out.println(expe1.toString());[m
[32m+[m
[32m+[m[32m        assertEquals(true, trans.get(0).isEqual(expe1));[m
[32m+[m[32m        assertEquals(true, trans.get(1) == null);[m
[32m+[m[32m    }[m
[32m+[m
     @Test[m
     void transformOperation_DeletionDiffLinesNewLineRemovedTextAdded() {[m
         StringChangeRequest prev = new StringChangeRequest("qtf", new MonacoRange(3, 4, 1, 2 ));[m
[36m@@ -195,6 +219,18 @@[m [mpublic class OperationalTransformationTests {[m
         assertEquals(true, expe.isEqual(tran));[m
     }[m
 [m
[32m+[m[32m    @Test[m
[32m+[m[32m    void transformOperation_PrevInsideNextReplaceWithText() {[m
[32m+[m[32m        StringChangeRequest prev = new StringChangeRequest("123", new MonacoRange(6, 2, 1, 2 ));[m
[32m+[m[32m        StringChangeRequest next = new StringChangeRequest("abcdef", new MonacoRange(4, 4, 1, 2));[m
[32m+[m[32m        StringChangeRequest nextCopy = new StringChangeRequest("abcdef", new MonacoRange(4, 4, 1, 2));[m
[32m+[m[32m        StringChangeRequest expe = new StringChangeRequest("abcdef", new MonacoRange(4, 11, 1, 1));[m
[32m+[m[32m        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);[m
[32m+[m
[32m+[m[32m        printTransOpTest(prev, nextCopy, tran, expe);[m
[32m+[m[32m        assertEquals(true, expe.isEqual(tran));[m
[32m+[m[32m    }[m
[32m+[m
     private void printTransOpTest(StringChangeRequest prev, StringChangeRequest next,[m
                                   StringChangeRequest transformed,StringChangeRequest expe) {[m
 [m
[1mdiff --git a/target/classes/ot-notes.txt b/target/classes/ot-notes.txt[m
[1mindex 0a8eabc..6994100 100644[m
[1m--- a/target/classes/ot-notes.txt[m
[1m+++ b/target/classes/ot-notes.txt[m
[36m@@ -1,144 +1,2 @@[m
[31m-If prev is exclusively after next than next wont change[m
[31m-        no new line, no intersect, on same line[m
[31m-            next sc = next.sc - (prev.ec - prev.sc) - prev.text.length // (same for next ec)[m
[31m-[m
[31m-       when does stuff chagne:[m
[31m-            TODO prev is before next[m
[31m-            theres new lines on prior lines[m
[31m-                - next.*l += # of new lines in prev[m
[31m-            theres new line on same line as next[m
[31m-                - next.*l += prev.*l + # of new lines[m
[31m-                - next.*c = next.*c - # of chars after lastNL on same line[m
[31m-            replacing on same line alters columns[m
[31m-            TODO adjust for deletion of preceeding stuff to include newlines[m
[31m-            TODO conflicting ranges, might be diff for insert/remove/combinations etc[m
[31m-[m
[31m-       when is prev considered "after" next:[m
[31m-            when prev start line is after next end line[m
[31m-            if prev start line same as next end line:[m
[31m-                if prev sc > next ec[m
[31m-[m
[31m-            issue is prev start column, and next end column. when is next ec inclusive, and when is it exclusive[m
[31m-            when prev starts after next end on same line:[m
[31m-            if next is a simple insert, then next.ec is illegal and cannot be prev.sc[m
[31m-            if next is not simple insert and instead a replace, next.ec can be used as prev.sc[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-[m
[31m-new lines:[m
[31m-    abcdef[m
[31m-[m
[31m-    prev:[m
[31m-    ins \n @ 4[m
[31m-    sc 4[m
[31m-    ec 4[m
[31m-    sl 1[m
[31m-    el 1[m
[31m-[m
[31m-    next:[m
[31m-    ins x @ 7       ->      ins x[m
[31m-    sc 7                    sc 4[m
[31m-    ec 7                    ec 4    *c = next.*c - # of characters after last new line on same line as next[m
[31m-    sl 1                    sl 2    *l = prev.*l + # of new lines[m
[31m-    el 1                    el 2[m
[31m-[m
[31m-conflicting ranges:[m
[31m-    abc\ndef\nghi[m
[31m-    abc[m
[31m-    def[m
[31m-    hgi[m
[31m-[m
[31m-[m
[31m-    prev:[m
[31m-    ins xyz (replace cde)[m
[31m-    sc 3[m
[31m-    ec 3[m
[31m-    sl 1[m
[31m-    el 2[m
[31m-[m
[31m-    prev:[m
[31m-    ins mno (replace efg)[m
[31m-    sc 2[m
[31m-    ec 2[m
[31m-    sl 2[m
[31m-    el 3[m
[31m-[m
[31m-    expected output:[m
[31m-    abxyzmnohi[m
[31m-[m
[31m-    (1-4] (3-6][m
[31m-    Consider 3 is already covere[m
[31m-[m
[31m-[m
[31m-[m
[31m-when prev starts after next end on same line:[m
[31m-if next is a simple insert, then next.ec is illegal and cannot be prev.sc[m
[31m-if next is not simple insert and instead a replace, next.ec can be used as prev.sc[m
[31m-[m
[31m-[m
[31m-OT RULES -------------------------------------------------------------------[m
[31m-        // just build a list of rules that we can confirm.[m
[31m-[m
[31m-        // in regards to insert after insert with \n's in prev. This only affects column # if[m
[31m-        // they are on the same line. then, consider how many characters after last \n[m
[31m-        // and before next.text. Thats how many chars to shift column #s[m
[31m-[m
[31m-        Integer newSC;[m
[31m-        Integer newEC;[m
[31m-        Integer newSL;[m
[31m-        Integer newEL;[m
[31m-        // INSERT after INSERT considering \n[m
[31m-//        if (prev.getRange().getStartLineNumber() <= next.getRange().getStartLineNumber() &&[m
[31m-//            prev.getRange().getEndLineNumber() <= next.getRange().getStartLineNumber() &&)[m
[31m-[m
[31m-        int countOfNewLines = 0;[m
[31m-        Matcher m = Pattern.compile("(\\r\\n)|(\\n)|(\\r)").matcher(prev.getText());[m
[31m-        while (m.find())[m
[31m-            countOfNewLines ++; // sl & el will have this added to them[m
[31m-        newSL = next.getRange().getStartLineNumber() + countOfNewLines + 1;[m
[31m-        newEL = next.getRange().getEndLineNumber() + countOfNewLines + 1;[m
[31m-[m
[31m-        // if this > 0, sc ec will be diff bc of it also[m
[31m-        if (countOfNewLines > 0) {[m
[31m-[m
[31m-        }[m
[31m-[m
[31m-[m
[31m-;[m
[31m-[m
[31m-[m
[32m+[m[32mrevID notes:[m
[32m+[m[32m    at end of each change, inc revID and propograte to clients[m
\ No newline at end of file[m
[1mdiff --git a/target/maven-status/maven-compiler-plugin/compile/default-compile/createdFiles.lst b/target/maven-status/maven-compiler-plugin/compile/default-compile/createdFiles.lst[m
[1mindex f5e2bbf..5cf0268 100644[m
[1m--- a/target/maven-status/maven-compiler-plugin/compile/default-compile/createdFiles.lst[m
[1m+++ b/target/maven-status/maven-compiler-plugin/compile/default-compile/createdFiles.lst[m
[36m@@ -1,4 +1,5 @@[m
 tesseract\OTserver\objects\Document.class[m
[32m+[m[32mtesseract\OTserver\util\MonacoRangeUtil.class[m
 tesseract\OTserver\config\WebSocketConfig.class[m
 tesseract\OTserver\controllers\WebSocketController.class[m
 tesseract\OTserver\controllers\StringChangeController.class[m
[36m@@ -10,3 +11,4 @@[m [mtesseract\OTserver\objects\DocumentLanguage.class[m
 tesseract\OTserver\config\WebConfig.class[m
 tesseract\OTserver\objects\MonacoRange.class[m
 tesseract\OTserver\util\OperationalTransformation.class[m
[32m+[m[32mtesseract\OTserver\util\DocumentUtil.class[m
[1mdiff --git a/target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst b/target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst[m
[1mindex 3257be2..57333a5 100644[m
[1m--- a/target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst[m
[1m+++ b/target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst[m
[36m@@ -1,12 +1,14 @@[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\config\WebSocketConfig.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\services\DocumentService.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\config\WebConfig.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\DocumentLanguage.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\controllers\WebSocketController.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\StringResponse.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\Document.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\StringChangeRequest.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\OtServerApplication.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\MonacoRange.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\util\OperationalTransformation.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\main\java\tesseract\OTserver\controllers\StringChangeController.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\MonacoRange.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\Document.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\DocumentLanguage.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\StringChangeRequest.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\config\WebConfig.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\config\WebSocketConfig.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\controllers\StringChangeController.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\OtServerApplication.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\services\DocumentService.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\util\DocumentUtil.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\util\MonacoRangeUtil.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\controllers\WebSocketController.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\objects\StringResponse.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\main\java\tesseract\OTserver\util\OperationalTransformation.java[m
[1mdiff --git a/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/createdFiles.lst b/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/createdFiles.lst[m
[1mindex 20caa40..e4dad6a 100644[m
[1m--- a/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/createdFiles.lst[m
[1m+++ b/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/createdFiles.lst[m
[36m@@ -1,2 +1,4 @@[m
 tesseract\OTserver\OtServerApplicationTests.class[m
[32m+[m[32mtesseract\OTserver\util\DocumentUtilTests.class[m
[32m+[m[32mtesseract\OTserver\util\MonacoRangeUtilTests.class[m
 tesseract\OTserver\util\OperationalTransformationTests.class[m
[1mdiff --git a/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/inputFiles.lst b/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/inputFiles.lst[m
[1mindex 40492d8..aa031e5 100644[m
[1m--- a/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/inputFiles.lst[m
[1m+++ b/target/maven-status/maven-compiler-plugin/testCompile/default-testCompile/inputFiles.lst[m
[36m@@ -1,2 +1,4 @@[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\test\java\tesseract\OTserver\util\OperationalTransformationTests.java[m
[31m-C:\Users\steph\OneDrive\Desktop\OT-server\src\test\java\tesseract\OTserver\OtServerApplicationTests.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\test\java\tesseract\OTserver\util\DocumentUtilTests.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\test\java\tesseract\OTserver\util\MonacoRangeUtilTests.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\test\java\tesseract\OTserver\OtServerApplicationTests.java[m
[32m+[m[32mC:\Users\Stephen\Desktop\OT-server\src\test\java\tesseract\OTserver\util\OperationalTransformationTests.java[m
