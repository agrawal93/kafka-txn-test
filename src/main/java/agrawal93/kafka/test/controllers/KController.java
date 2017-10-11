package agrawal93.kafka.test.controllers;

import agrawal93.kafka.test.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KController {
    
    @Autowired
    private Service service;
    
    @GetMapping("/")
    public String publish() {
        service.process();
        return "Successful";
    }
    
}
