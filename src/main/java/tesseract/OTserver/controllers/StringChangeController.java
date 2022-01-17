package tesseract.OTserver.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.objects.StringChangeRequest;
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class StringChangeController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            path = "change"
    )
    @ResponseBody
    public void stringChange(@RequestBody StringChangeRequest request) {
        // send request to web socket subscribers
        System.out.println(request.getText() + " " + request.getIndex());
        //this.simpMessagingTemplate.convertAndSend(request);
    }


}