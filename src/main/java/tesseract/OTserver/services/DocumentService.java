package tesseract.OTserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tesseract.OTserver.objects.Document;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.util.DocumentUtil;
import tesseract.OTserver.util.OperationalTransformation;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class DocumentService {


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate; // for websocket messaging

    // have it create new document when client connects TODO
    private Document currentDocument;

    public DocumentService() {
        this.currentDocument = new Document();
    }

    /**
     * Submit string change request to document. Transforms request and commits it to change history.
     * @param request the request to transform and commit to change history
     * @return the new document revID
     */
    public Integer submitChange(StringChangeRequest request) {
        // put change in pending changes queue
        currentDocument.getPendingChangesQueue().add(request);

        // wait for this requests turn to transform
        waitForTurn(request);

        // when this request's turn is next, transform
        ArrayList<StringChangeRequest> newChangeRequests = OperationalTransformation.transform(request, this.currentDocument.getChangeHistory());

        // increment document id
        this.currentDocument.setRevID(this.currentDocument.getRevID() + 1);

        // transform can return multiple string change requests, so iterate over each
        for (StringChangeRequest changedRequest : newChangeRequests) {
            if (changedRequest != null) {
                if (this.currentDocument.getChangeHistory().get(changedRequest.getRevID()) != null)
                    this.currentDocument.getChangeHistory().get(changedRequest.getRevID()).add(changedRequest);
                else
                    this.currentDocument.getChangeHistory().put(changedRequest.getRevID(), new ArrayList<>(Arrays.asList(changedRequest)));

                updateModel(changedRequest);
                propogateToClients(changedRequest);
            }
        }

        // remove this request from pending queue, since it is completed
        this.currentDocument.getPendingChangesQueue().remove();

        // return revID so client can update its document id
        return this.currentDocument.getRevID();
    }

    /**
     * Makes THIS request's thread wait for other, preceding threads to finish transforming
     * @param request the request this thread is responsible for
     */
    private void waitForTurn(StringChangeRequest request) {
        // while the next request in queue is not THIS one, wait 10 ms
        while (!(this.currentDocument.getPendingChangesQueue().peek().getTimestamp().equals(request.getTimestamp())
                && this.currentDocument.getPendingChangesQueue().peek().getIdentity().equals(request.getIdentity()))) {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the server side document string model
     * @param changedRequest the string change request
     */
    private void updateModel(StringChangeRequest changedRequest) {
        this.currentDocument.setModel(DocumentUtil.updateModel(this.currentDocument.getModel(), changedRequest));
    }

    /**
     * Send request to clients. Sets the set ID to the document's revID
     * @param changedRequest the request to propogate to clients
     */
    private void propogateToClients(StringChangeRequest changedRequest) {
        changedRequest.setSetID(this.currentDocument.getRevID());
        this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", changedRequest);
    }

}
