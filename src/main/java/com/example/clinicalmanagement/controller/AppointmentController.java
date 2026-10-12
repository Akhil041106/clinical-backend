package com.example.clinicalmanagement.controller;

import com.example.clinicalmanagement.entity.Patient;
import com.example.clinicalmanagement.service.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService,
                                 PatientService patientService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam("doctorId") Long doctorId,
                                  @RequestParam("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
                                  @RequestParam("appointmentTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime appointmentTime,
                                  @RequestParam("reason") String reason,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Patient patient = patientService.getPatientByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Patient profile not found for user: " + username));

            if (appointmentDate.isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment date cannot be in the past.");
                return "redirect:/patient";
            }

            appointmentService.bookAppointment(patient.getId(), doctorId, appointmentDate, appointmentTime, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully! Consultation fee pending confirmation.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to book appointment: " + ex.getMessage());
        }
        return "redirect:/patient";
    }

    @PostMapping("/{id}/pay")
    public String payAppointment(@PathVariable("id") Long appointmentId,
                                 @RequestParam(value = "paymentMethod", defaultValue = "UPI") String paymentMethod,
                                 @RequestParam(value = "transactionReference", required = false) String transactionReference,
                                 @RequestHeader(value = "Referer", required = false) String referer,
                                 RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updatePaymentStatus(appointmentId, "PAID", paymentMethod, transactionReference);
            redirectAttributes.addFlashAttribute("successMessage", "Payment of consultation fee for Appointment #" + appointmentId + " marked as PAID via " + paymentMethod + " successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to record payment: " + ex.getMessage());
        }
        return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/";
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") Long appointmentId,
                                    @RequestHeader(value = "Referer", required = false) String referer,
                                    RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateStatus(appointmentId, "CANCELLED");
            redirectAttributes.addFlashAttribute("successMessage", "Appointment #" + appointmentId + " cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel appointment: " + ex.getMessage());
        }
        return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/";
    }

    @PostMapping("/{id}/approve")
    public String approveAppointment(@PathVariable("id") Long appointmentId,
                                     @RequestHeader(value = "Referer", required = false) String referer,
                                     RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateStatus(appointmentId, "APPROVED");
            redirectAttributes.addFlashAttribute("successMessage", "Appointment #" + appointmentId + " approved.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve appointment: " + ex.getMessage());
        }
        return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/";
    }
}
