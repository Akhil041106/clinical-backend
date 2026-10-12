package com.example.clinicalmanagement.controller;

import com.example.clinicalmanagement.entity.Doctor;
import com.example.clinicalmanagement.service.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final ConsultationService consultationService;
    private final TestReportService testReportService;

    @Autowired
    public DoctorController(DoctorService doctorService,
                            AppointmentService appointmentService,
                            ConsultationService consultationService,
                            TestReportService testReportService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.consultationService = consultationService;
        this.testReportService = testReportService;
    }

    @GetMapping
    public String doctorDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Doctor doctor = doctorService.getDoctorByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found for user: " + username));

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointmentService.getAppointmentsByDoctor(doctor.getId()));
        model.addAttribute("consultations", consultationService.getConsultationsByDoctor(doctor.getId()));
        model.addAttribute("testReports", testReportService.getTestReportsByDoctor(doctor.getId()));

        return "doctor";
    }

    @PostMapping("/appointment/{id}/status")
    public String updateAppointmentStatus(@PathVariable("id") Long appointmentId,
                                          @RequestParam("status") String status,
                                          RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateStatus(appointmentId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment #" + appointmentId + " marked as " + status + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update appointment: " + ex.getMessage());
        }
        return "redirect:/doctor";
    }

    @PostMapping("/consultation/save")
    public String saveConsultation(@RequestParam("appointmentId") Long appointmentId,
                                   @RequestParam(value = "symptoms", defaultValue = "") String symptoms,
                                   @RequestParam(value = "vitalSigns", defaultValue = "") String vitalSigns,
                                   @RequestParam("diagnosis") String diagnosis,
                                   @RequestParam("prescription") String prescription,
                                   @RequestParam(value = "instructions", defaultValue = "") String instructions,
                                   @RequestParam(value = "followUpDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate followUpDate,
                                   RedirectAttributes redirectAttributes) {
        try {
            consultationService.createConsultation(
                    appointmentId, symptoms, vitalSigns, diagnosis, prescription, instructions, followUpDate
            );
            redirectAttributes.addFlashAttribute("successMessage", "Consultation and prescription recorded successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to record consultation: " + ex.getMessage());
        }
        return "redirect:/doctor";
    }
}
