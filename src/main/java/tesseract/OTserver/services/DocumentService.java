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
    private SimpMessagingTemplate simpMessagingTemplate;

    // have it create new document when client connects TODO
    private Document currentDocument;

    public DocumentService() {
        this.currentDocument = new Document();
    }

    // Thread.sleep is a temporary solution. This will need to be redesigned more than likely
    public Integer submitChange(StringChangeRequest request) {
        // put change in pending changes queue
        currentDocument.getPendingChangesQueue().add(request);
        while (!(this.currentDocument.getPendingChangesQueue().peek().getTimestamp().equals(request.getTimestamp())
                && this.currentDocument.getPendingChangesQueue().peek().getIdentity().equals(request.getIdentity()))) {
            try {
                Thread.currentThread().sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ArrayList<StringChangeRequest> newChangeRequests = OperationalTransformation.transform(request, this.currentDocument.getChangeHistory());

        this.currentDocument.setRevID(this.currentDocument.getRevID() + 1);
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

        this.currentDocument.getPendingChangesQueue().remove();

        return this.currentDocument.getRevID();
    }

    private void updateModel(StringChangeRequest changedRequest) {
        this.currentDocument.setModel(DocumentUtil.updateModel(this.currentDocument.getModel(), changedRequest));
    }

    private void propogateToClients(StringChangeRequest changedRequest) {
        changedRequest.setSetID(this.currentDocument.getRevID());
        this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", changedRequest);
    }

}
