package tesseract.OTserver.objects;

import java.util.*;

public class Document {

    private DocumentLanguage language;
    private String model;
    private Integer revID;
    private HashMap<Integer, ArrayList<StringChangeRequest>> changeHistory;
    private Queue<StringChangeRequest> pendingChangesQueue;

    // Will need new constructor when its ready to pull data from previous session
    // For new documents
    public Document() {
        this.language = DocumentLanguage.JAVA;
        this.model = "";
        this.revID = 1;
        this.changeHistory = new HashMap<>();
        this.pendingChangesQueue = new LinkedList<>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getRevID() {
        return revID;
    }

    public void setRevID(Integer revID) {
        this.revID = revID;
    }

    public HashMap<Integer, ArrayList<StringChangeRequest>> getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(HashMap<Integer, ArrayList<StringChangeRequest>> changeHistory) {
        this.changeHistory = changeHistory;
    }

    public Queue<StringChangeRequest> getPendingChangesQueue() {
        return pendingChangesQueue;
    }

    public void setPendingChangesQueue(PriorityQueue<StringChangeRequest> pendingChangesQueue) {
        this.pendingChangesQueue = pendingChangesQueue;
    }
}
