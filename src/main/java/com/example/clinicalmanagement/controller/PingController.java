package com.example.clinicalmanagement.controller; // match your own package

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

@Controller
public class PingController {

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "ok";
    }
}
