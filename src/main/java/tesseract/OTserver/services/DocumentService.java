package tesseract.OTserver.services;

import org.springframework.stereotype.Service;
import tesseract.OTserver.objects.Document;
import tesseract.OTserver.objects.StringChangeRequest;

@Service
public class DocumentService {

    private Document currentDocument;

    // https://stackoverflow.com/questions/51822642/spring-service-class-with-constructor-with-autowiredrequired-false-parame
    // For when depen inj is needed for service constructors
    public DocumentService() {
        this.currentDocument = new Document();
    }

    public void submitChange(StringChangeRequest request) {
        // put change in pending changes queue
        currentDocument.getPendingChangesQueue().add(request);






        // once operation is transformed and committed, add new change to change history

        // once propogated to other clients, return new revID
    }



    public void printStuff() {
        System.out.println("------------------------------ Start here");
        currentDocument.getPendingChangesQueue().stream().forEach(r -> {
            System.out.println(r.getText());
        });

    }


}
