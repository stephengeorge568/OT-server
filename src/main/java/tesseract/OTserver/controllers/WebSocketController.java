package tesseract.OTserver.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tesseract.OTserver.objects.StringChangeRequest;

@Controller
@RequestMapping("/")
public class WebSocketController {

    @MessageMapping("/string")
    @SendTo("/broker/string-change-requests")
    public StringChangeRequest send(StringChangeRequest stringChangeRequest) throws Exception {
        System.out.println("Request text: " + stringChangeRequest.getText());
        return stringChangeRequest;
    }


}
