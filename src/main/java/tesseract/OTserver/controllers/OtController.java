package tesseract.OTserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.services.OtService;
import javax.servlet.http.HttpServletRequest;

@RestController
public class OtController {

    @Autowired
    private OtService otService;

    /**
     * Incoming string change requests from clients
     * @param httpRequest
     * @param request The request from the client
     * @return Response entity containing the id the client should update its revID to
     */
    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            path = "change"
    )
    @ResponseBody
    public ResponseEntity<Integer> stringChange(HttpServletRequest httpRequest, @RequestBody StringChangeRequest request) {
        System.out.printf("%s\n", request.toString());
        return ResponseEntity.ok(this.otService.submitChange(request));
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "identity"
    )
    @ResponseBody
    public ResponseEntity<Integer> getIdentity(HttpServletRequest httpRequest) {
        System.out.printf("%s has connected.\n", httpRequest.getRemoteAddr());
        otService.incrementClientIdentityCounter();
        return ResponseEntity.ok(otService.getClientIdentityCounter());
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "model"
    )
    @ResponseBody
    public String getModel(HttpServletRequest httpRequest) {
        return otService.getDocumentModel();
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "revId"
    )
    @ResponseBody
    public ResponseEntity<Integer> getDocumentRevID(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(otService.getDocumentRevId());
    }

}