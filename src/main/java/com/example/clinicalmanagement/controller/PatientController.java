package com.example.clinicalmanagement.controller;

import com.example.clinicalmanagement.entity.Appointment;
import com.example.clinicalmanagement.entity.Consultation;
import com.example.clinicalmanagement.entity.Patient;
import com.example.clinicalmanagement.entity.TestReport;
import com.example.clinicalmanagement.service.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final AppointmentService appointmentService;
    private final ConsultationService consultationService;
    private final TestReportService testReportService;

    @Autowired
    public PatientController(PatientService patientService,
                             DoctorService doctorService,
                             DepartmentService departmentService,
                             AppointmentService appointmentService,
                             ConsultationService consultationService,
                             TestReportService testReportService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.departmentService = departmentService;
        this.appointmentService = appointmentService;
        this.consultationService = consultationService;
        this.testReportService = testReportService;
    }

    @GetMapping
    public String patientDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Patient patient = patientService.getPatientByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Patient profile not found for user: " + username));

        List<Appointment> allAppointments = appointmentService.getAppointmentsByPatient(patient.getId());
        List<Consultation> consultations = consultationService.getConsultationsByPatient(patient.getId());
        List<TestReport> testReports = testReportService.getTestReportsByPatient(patient.getId());
        LocalDate today = LocalDate.now();

        // Separate upcoming vs past/completed
        List<Appointment> upcomingAppointments = allAppointments.stream()
                .filter(a -> ("PENDING".equalsIgnoreCase(a.getStatus()) || "APPROVED".equalsIgnoreCase(a.getStatus()))
                        && !a.getAppointmentDate().isBefore(today))
                .collect(Collectors.toList());

        List<Appointment> pastAppointments = allAppointments.stream()
                .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())
                        || "CANCELLED".equalsIgnoreCase(a.getStatus())
                        || a.getAppointmentDate().isBefore(today))
                .collect(Collectors.toList());

        // Calculate pending bills amount in ₹
        BigDecimal pendingApptBills = allAppointments.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getPaymentStatus()))
                .map(a -> a.getFeeAmount() != null ? a.getFeeAmount() :
                        (a.getDoctor() != null && a.getDoctor().getConsultationFee() != null ?
                                a.getDoctor().getConsultationFee() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingTestBills = testReports.stream()
                .filter(t -> "PENDING".equalsIgnoreCase(t.getPaymentStatus()))
                .map(t -> t.getTestFee() != null ? t.getTestFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPendingDues = pendingApptBills.add(pendingTestBills);

        // Calculate age
        Integer age = null;
        if (patient.getDateOfBirth() != null) {
            age = Period.between(patient.getDateOfBirth(), today).getYears();
        }

        Appointment nextAppointment = upcomingAppointments.isEmpty() ? null : upcomingAppointments.get(0);

        model.addAttribute("patient", patient);
        model.addAttribute("patientAge", age);
        model.addAttribute("nextAppointment", nextAppointment);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("pastAppointments", pastAppointments);
        model.addAttribute("allAppointments", allAppointments);
        model.addAttribute("consultations", consultations);
        model.addAttribute("testReports", testReports);
        model.addAttribute("totalPendingDues", totalPendingDues);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("departments", departmentService.getAllDepartments());

        return "patient";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("contactNumber") String contactNumber,
                                @RequestParam("address") String address,
                                @RequestParam("emergencyContact") String emergencyContact,
                                @RequestParam(value = "medicalHistory", defaultValue = "") String medicalHistory,
                                @RequestParam(value = "allergies", defaultValue = "") String allergies,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Patient patient = patientService.getPatientByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Patient not found: " + username));

            patient.setContactNumber(contactNumber);
            patient.setAddress(address);
            patient.setEmergencyContact(emergencyContact);
            patient.setMedicalHistory(medicalHistory);
            patient.setAllergies(allergies);

            patientService.savePatient(patient);
            redirectAttributes.addFlashAttribute("successMessage", "Your medical health profile was updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + ex.getMessage());
        }
        return "redirect:/patient";
    }

    @PostMapping("/testreport/{id}/pay")
    public String payTestReport(@PathVariable("id") Long reportId,
                                @RequestParam(value = "paymentMethod", defaultValue = "UPI") String paymentMethod,
                                @RequestParam(value = "transactionReference", required = false) String transactionReference,
                                RedirectAttributes redirectAttributes) {
        try {
            testReportService.updatePaymentStatus(reportId, "PAID", paymentMethod, transactionReference);
            redirectAttributes.addFlashAttribute("successMessage", "Diagnostic test fee for Report #" + reportId + " marked as PAID via " + paymentMethod + " successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to process payment: " + ex.getMessage());
        }
        return "redirect:/patient";
    }
}
