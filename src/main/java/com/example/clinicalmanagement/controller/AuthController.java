package com.example.clinicalmanagement.controller;

import com.example.clinicalmanagement.service.Services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AuthController {

    private final PatientService patientService;

    @Autowired
    public AuthController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin";
                } else if (role.equals("ROLE_DOCTOR")) {
                    return "redirect:/doctor";
                } else if (role.equals("ROLE_PATIENT")) {
                    return "redirect:/patient";
                }
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            @RequestParam(value = "denied", required = false) String denied,
                            @RequestParam(value = "portal", required = false, defaultValue = "admin") String portal,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please verify your credentials and try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Patient account created successfully! Sign in below to access your portal.");
            portal = "patient";
        }
        if (denied != null) {
            model.addAttribute("errorMessage", "Access Denied: The requested page requires Administrator privileges. Please sign in with an Administrator account.");
            portal = "admin";
        }

        model.addAttribute("selectedPortal", portal);
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerPatient(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("fullName") String fullName,
                                  @RequestParam("email") String email,
                                  @RequestParam(value = "dateOfBirth", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
                                  @RequestParam(value = "gender", required = false) String gender,
                                  @RequestParam(value = "bloodGroup", required = false) String bloodGroup,
                                  @RequestParam(value = "contactNumber", required = false) String contactNumber,
                                  @RequestParam(value = "address", required = false) String address,
                                  @RequestParam(value = "emergencyContact", required = false) String emergencyContact,
                                  @RequestParam(value = "medicalHistory", required = false) String medicalHistory,
                                  @RequestParam(value = "allergies", required = false) String allergies,
                                  Model model) {
        try {
            patientService.registerPatient(
                    username, password, fullName, email, dateOfBirth,
                    gender, bloodGroup, contactNumber, address,
                    emergencyContact, medicalHistory, allergies
            );
            return "redirect:/login?registered=true&portal=patient";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("gender", gender);
            model.addAttribute("bloodGroup", bloodGroup);
            model.addAttribute("contactNumber", contactNumber);
            model.addAttribute("address", address);
            return "register";
        }
    }
}
