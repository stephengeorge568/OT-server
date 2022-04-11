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

    private Document currentDocument;

    // https://stackoverflow.com/questions/51822642/spring-service-class-with-constructor-with-autowiredrequired-false-parame
    // For when depen inj is needed for service constructors
    public DocumentService() {
        this.currentDocument = new Document();
    }
    // TODO reformat so easier to test
    // Thread.sleep is a temporary solution. This will need to be redesigned more than likely TODO
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
                this.currentDocument.setModel(DocumentUtil.updateModel(this.currentDocument.getModel(), changedRequest));
                System.out.println("\n-------------------------------------------------------------------------\n"
                        + this.currentDocument.getModel()
                        + "\n-------------------------------------------------------------------------");
                System.out.printf("DEBUG: propogating:\n\tfrom: %s\n\tstr:\n\trevId:%d\n\n",
                        changedRequest.getIdentity(),
                        changedRequest.getText(),
                        changedRequest.getRevID());
                // TODO do i need to update changedRequest revID before propogation?
                this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", changedRequest);
            }
        }

        this.currentDocument.getPendingChangesQueue().remove();

        return this.currentDocument.getRevID();
    }



    public void printStuff() {
        System.out.println("------------------------------ Start here");
        currentDocument.getPendingChangesQueue().stream().forEach(r -> {
            System.out.println(r.getText());
        });

    }


}
