package tesseract.OTserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.objects.StringResponse;
import tesseract.OTserver.services.DocumentService;
import javax.servlet.http.HttpServletRequest;

@RestController
public class StringChangeController {

    @Autowired
    private DocumentService documentService;

    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            path = "change"
    )
    @ResponseBody
    public ResponseEntity<Integer> stringChange(HttpServletRequest httpRequest, @RequestBody StringChangeRequest request) {
        System.out.printf("DEBUG: received:\n\tfrom: %s\n\tstr:\n\trevId:%d\n\n", request.getIdentity(), request.getText(), request.getRevID());
        return ResponseEntity.ok(this.documentService.submitChange(request));
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "identity"
    )
    @ResponseBody // TODO this can be ResponseEntity<String>
    public StringResponse getIdentity(HttpServletRequest httpRequest) {
        // this is a lie because geting GET request doesnt mean Websockets connected TODO
        System.out.println(httpRequest.getRemoteAddr() + " has connected via websocket.");
        return new StringResponse(httpRequest.getRemoteAddr());
    }

}