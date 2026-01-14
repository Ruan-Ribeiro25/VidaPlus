package com.vidaplus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TelemedicinaController {

    @GetMapping("/telemedicina")
    public String salaEspera() {
        return "pages/telemedicina";
    }
}