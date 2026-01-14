package com.vidaplus.controller;

import com.vidaplus.entity.Profissional;
import com.vidaplus.entity.Usuario;
import com.vidaplus.repository.ProfissionalRepository;
import com.vidaplus.repository.UsuarioRepository;
import com.vidaplus.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Random;

@Controller
public class AuthController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProfissionalRepository profissionalRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService; 

    // =================================================================================
    // 1. LOGIN E REDIRECIONAMENTOS
    // =================================================================================

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/login-professional")
    public String loginProfessional() { 
        return "redirect:/acesso-profissional"; 
    }

    // *** ATENÇÃO: AS ROTAS "/register" FORAM REMOVIDAS DAQUI ***
    // Elas agora estão no RegisterController.java para suportar a criação automática de Polos.

    // =================================================================================
    // 2. CADASTRO PROFISSIONAL (UPGRADE DE CONTA EXISTENTE)
    // =================================================================================
    // Este método continua aqui pois exige que o usuário JÁ esteja logado/existente
    
    @GetMapping("/register-professional")
    public String registerProfessionalForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; 
        }

        Usuario usuarioLogado = usuarioRepository.findByUsernameOrCpf(principal.getName());
        
        if (usuarioLogado == null) {
             return "redirect:/login?error=user_not_found";
        }

        model.addAttribute("usuario", usuarioLogado);
        
        return "register-professional"; 
    }

    @PostMapping("/register-professional")
    public String registerProfessionalAction(Principal principal,
                                             @RequestParam String tipoProfissional, 
                                             @RequestParam String registroConselho, 
                                             @RequestParam String matricula,
                                             @RequestParam LocalDate dataMatricula,
                                             @RequestParam String especialidade,
                                             Model model) {
        
        if (principal == null) return "redirect:/login";

        Usuario usuarioExistente = usuarioRepository.findByUsernameOrCpf(principal.getName());

        // Atualiza o perfil no usuário base
        usuarioExistente.setPerfil(tipoProfissional);
        usuarioRepository.save(usuarioExistente);

        // Cria o registro profissional vinculado
        Profissional prof = new Profissional();
        prof.setUsuario(usuarioExistente);
        prof.setTipoProfissional(tipoProfissional);
        prof.setMatricula(matricula);
        prof.setDataMatricula(dataMatricula);
        prof.setEspecialidade(especialidade);
        prof.setStatusAprovacao("PENDENTE"); // Define status padrão

        // Define se é CRM ou COREN baseado no tipo
        if (tipoProfissional != null && tipoProfissional.toUpperCase().contains("MEDICO")) {
            prof.setCrm(registroConselho);
        } else {
            prof.setCoren(registroConselho);
        }

        profissionalRepository.save(prof);

        return "redirect:/acesso-profissional?upgrade=success";
    }

    // =================================================================================
    // 3. RECUPERAÇÃO DE SENHA E VERIFICAÇÃO (MANTIDOS ORIGINALMENTE)
    // =================================================================================

    @GetMapping("/verificar-conta")
    public String verificarConta() { return "verificar-conta"; }

    @PostMapping("/verificar-conta")
    public String processarVerificacao(@RequestParam("codigo") String codigo, Model model) {
        Usuario usuario = usuarioRepository.findByCodigoVerificacao(codigo);
        if (usuario != null) {
            usuario.setAtivo(true);
            usuario.setCodigoVerificacao(null);
            usuarioRepository.save(usuario);
            return "redirect:/login?ativado=true";
        }
        return "redirect:/verificar-conta?error=true";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPassword() { return "forgot-password"; }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String cpf, Model model) {
        Usuario usuario = usuarioRepository.findByCpf(cpf);
        if (usuario != null) {
            String token = String.format("%06d", new Random().nextInt(999999));
            usuario.setTokenReset(token);
            usuarioRepository.save(usuario);
            emailService.enviarEmail(usuario.getEmail(), "Reset Senha", "Token: " + token);
            return "redirect:/enter-code";
        }
        model.addAttribute("error", "CPF não encontrado.");
        return "forgot-password";
    }
    
    @GetMapping("/enter-code")
    public String enterCode() { return "enter-code"; }
    
    @PostMapping("/verify-reset-code")
    public String verifyResetCode(@RequestParam String token, Model model) {
        Usuario usuario = usuarioRepository.findByTokenReset(token);
        return (usuario != null) ? "redirect:/update-password?token=" + token : "redirect:/enter-code?error=invalid";
    }
    
    @GetMapping("/update-password")
    public String updatePasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("tokenValidado", token);
        return "update-password";
    }
    
    @PostMapping("/update-password")
    public String updatePasswordAction(@RequestParam String token, @RequestParam String senha, @RequestParam String confirmarSenha, Model model) {
        Usuario usuario = usuarioRepository.findByTokenReset(token);
        if (usuario != null && senha.equals(confirmarSenha)) {
            usuario.setSenha(passwordEncoder.encode(senha));
            usuario.setTokenReset(null);
            usuarioRepository.save(usuario);
            return "redirect:/login?reset=success";
        }
        return "redirect:/update-password?error=true";
    }
}