package tesseract.OTserver.objects;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Document {

    private DocumentLanguage language;
    private String model;
    private Long revID;
    private Stack<StringChangeRequest> changeHistory;

    /*
        Notes: not thread safe? may be issue idk
        head of queue is LEAST. THIS must be accounted for TODO
     */
    private PriorityQueue<StringChangeRequest> pendingChangesQueue;

    // Will need new constructor when its ready to pull data from previous session
    // For new documents
    public Document() {
        this.language = DocumentLanguage.JAVA;
        this.model = "";
        this.revID = 1L;
        this.changeHistory = new Stack<>();
        this.pendingChangesQueue = new PriorityQueue<>();
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

    public Stack<StringChangeRequest> getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(Stack<StringChangeRequest> changeHistory) {
        this.changeHistory = changeHistory;
    }

    public PriorityQueue<StringChangeRequest> getPendingChangesQueue() {
        return pendingChangesQueue;
    }

    public void setPendingChangesQueue(PriorityQueue<StringChangeRequest> pendingChangesQueue) {
        this.pendingChangesQueue = pendingChangesQueue;
    }
}
