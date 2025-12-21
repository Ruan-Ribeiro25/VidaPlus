package com.speedmedical.controller;

import com.speedmedical.entity.Profissional;
import com.speedmedical.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("profissionais", profissionalService.findAll());
        return "profissional/lista";
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("profissional", new Profissional());
        return "profissional/cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Profissional profissional) {
        profissionalService.save(profissional);
        return "redirect:/profissionais/lista";
    }
}
