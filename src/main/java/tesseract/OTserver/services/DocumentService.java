package tesseract.OTserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tesseract.OTserver.objects.Document;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.util.OperationalTransformation;

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
        // do operations
        // this will return a new StringChangeRequest. this will change below.

        StringChangeRequest newChangeRequest = OperationalTransformation.transformThisBitch(request, this.currentDocument.getChangeHistory());

        // put request in history. remove request from queue.
        // THIS REV ID MIGHT BE SOMETHING ELSE IDK THINK ABOUT THIS LATER
        this.currentDocument.getChangeHistory().get(newChangeRequest.getRevID()).add(newChangeRequest);
        this.currentDocument.getPendingChangesQueue().remove();

        // send to change to other clients
        this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", request);

        // once propogated to other clients, return new revID
        // This is a placeholder
        int temp = 0;
        return temp;
    }



    public void printStuff() {
        System.out.println("------------------------------ Start here");
        currentDocument.getPendingChangesQueue().stream().forEach(r -> {
            System.out.println(r.getText());
        });

    }


}
