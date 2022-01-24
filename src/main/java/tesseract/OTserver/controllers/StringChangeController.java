package tesseract.OTserver.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.objects.StringResponse;
import tesseract.OTserver.services.DocumentService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StringChangeController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            path = "change"
    )
    @ResponseBody
    public void stringChange(HttpServletRequest httpRequest, @RequestBody StringChangeRequest request) {

        this.documentService.submitChange(request);

        this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", request);
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "identity"
    )
    @ResponseBody
    public StringResponse getIdentity(HttpServletRequest httpRequest) {
        System.out.println(httpRequest.getRemoteAddr() + " has connected via websocket.");
        return new StringResponse(httpRequest.getRemoteAddr());
    }


}