package tesseract.OTserver.objects;

import java.util.*;

public class Document {

    private DocumentLanguage language;
    private String model;
    private Long revID;
    private HashMap<Long, ArrayList<StringChangeRequest>> changeHistory;
    private Queue<StringChangeRequest> pendingChangesQueue;

    // Will need new constructor when its ready to pull data from previous session
    // For new documents
    public Document() {
        this.language = DocumentLanguage.JAVA;
        this.model = "";
        this.revID = 1L;
        this.changeHistory = new HashMap<>();
        this.pendingChangesQueue = new LinkedList<>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getRevID() {
        return revID;
    }

    public void setRevID(Long revID) {
        this.revID = revID;
    }

    public HashMap<Long, ArrayList<StringChangeRequest>> getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(HashMap<Long, ArrayList<StringChangeRequest>> changeHistory) {
        this.changeHistory = changeHistory;
    }

    public Queue<StringChangeRequest> getPendingChangesQueue() {
        return pendingChangesQueue;
    }

    public void setPendingChangesQueue(PriorityQueue<StringChangeRequest> pendingChangesQueue) {
        this.pendingChangesQueue = pendingChangesQueue;
    }
}
