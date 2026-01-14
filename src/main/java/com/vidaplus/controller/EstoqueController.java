package com.vidaplus.controller;

import com.vidaplus.entity.Produto;
import com.vidaplus.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/estoque")
public class EstoqueController {

    @Autowired private ProdutoRepository produtoRepository;

    @GetMapping
    public String listarEstoque(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("produto", new Produto()); // Para o modal de cadastro
        return "admin/estoque"; // Vamos criar esse HTML depois
    }

    @PostMapping("/salvar")
    public String salvarProduto(Produto produto) {
        produtoRepository.save(produto);
        return "redirect:/admin/estoque?msg=salvo";
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable Long id) {
        produtoRepository.deleteById(id);
        return "redirect:/admin/estoque?msg=excluido";
    }
}