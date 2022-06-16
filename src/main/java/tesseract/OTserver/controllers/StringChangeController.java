package tesseract.OTserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.services.DocumentService;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class StringChangeController {

    @Autowired
    private DocumentService documentService;

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
        System.out.printf("\n%s\n", request.toString());
        return ResponseEntity.ok(this.documentService.submitChange(request));
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "identity"
    )
    @ResponseBody
    public String getIdentity(HttpServletRequest httpRequest) {
        System.out.printf("%s has connected.\n", httpRequest.getRemoteAddr());
        return httpRequest.getRemoteAddr();
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "model"
    )
    @ResponseBody
    public String getModel(HttpServletRequest httpRequest) {
        return documentService.getDocumentModel();
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "revId"
    )
    @ResponseBody
    public ResponseEntity<Integer> getDocumentRevID(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(documentService.getDocumentRevId());
    }

}