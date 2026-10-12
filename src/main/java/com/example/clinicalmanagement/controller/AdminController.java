package com.example.clinicalmanagement.controller;

import com.example.clinicalmanagement.entity.Appointment;
import com.example.clinicalmanagement.entity.Department;
import com.example.clinicalmanagement.entity.Doctor;
import com.example.clinicalmanagement.entity.TestReport;
import com.example.clinicalmanagement.service.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final ConsultationService consultationService;
    private final DepartmentService departmentService;
    private final TestReportService testReportService;

    @Autowired
    public AdminController(DoctorService doctorService,
                           PatientService patientService,
                           AppointmentService appointmentService,
                           ConsultationService consultationService,
                           DepartmentService departmentService,
                           TestReportService testReportService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.consultationService = consultationService;
        this.departmentService = departmentService;
        this.testReportService = testReportService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        List<Appointment> allAppointments = appointmentService.getAllAppointments();
        List<TestReport> allTestReports = testReportService.getAllTestReports();
        LocalDate today = LocalDate.now();

        long pendingCount = allAppointments.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                .count();

        long todayCount = allAppointments.stream()
                .filter(a -> today.equals(a.getAppointmentDate()))
                .count();

        // Calculate collected revenue in INR from PAID appointments & PAID test reports
        BigDecimal apptRevenue = allAppointments.stream()
                .filter(a -> "PAID".equalsIgnoreCase(a.getPaymentStatus()))
                .map(a -> a.getFeeAmount() != null ? a.getFeeAmount() :
                        (a.getDoctor() != null && a.getDoctor().getConsultationFee() != null ?
                                a.getDoctor().getConsultationFee() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal testRevenue = allTestReports.stream()
                .filter(t -> "PAID".equalsIgnoreCase(t.getPaymentStatus()))
                .map(t -> t.getTestFee() != null ? t.getTestFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = apptRevenue.add(testRevenue);

        model.addAttribute("totalDoctors", doctorService.countDoctors());
        model.addAttribute("totalPatients", patientService.countPatients());
        model.addAttribute("totalAppointments", allAppointments.size());
        model.addAttribute("totalConsultations", consultationService.countConsultations());
        model.addAttribute("totalReportsCount", allTestReports.size());
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("totalRevenue", totalRevenue);

        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("testReports", allTestReports);

        return "admin";
    }

    @PostMapping("/doctor/add")
    public String addDoctor(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam("fullName") String fullName,
                            @RequestParam("email") String email,
                            @RequestParam("departmentId") Long departmentId,
                            @RequestParam("specialization") String specialization,
                            @RequestParam(value = "qualifications", defaultValue = "") String qualifications,
                            @RequestParam(value = "experienceYears", defaultValue = "0") Integer experienceYears,
                            @RequestParam(value = "phone", defaultValue = "") String phone,
                            @RequestParam(value = "consultationFee", defaultValue = "500.00") BigDecimal consultationFee,
                            RedirectAttributes redirectAttributes) {
        try {
            doctorService.createDoctor(
                    username, password, fullName, email, departmentId,
                    specialization, qualifications, experienceYears, phone, consultationFee
            );
            redirectAttributes.addFlashAttribute("successMessage", "Doctor " + fullName + " registered successfully into hospital staff!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error onboarding doctor: " + ex.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/department/add")
    public String addDepartment(@RequestParam("name") String name,
                                @RequestParam("description") String description,
                                RedirectAttributes redirectAttributes) {
        try {
            departmentService.saveDepartment(new Department(name, description));
            redirectAttributes.addFlashAttribute("successMessage", "Department '" + name + "' added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding department: " + ex.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/testreport/add")
    public String addTestReport(@RequestParam("patientId") Long patientId,
                                @RequestParam("doctorId") Long doctorId,
                                @RequestParam("testName") String testName,
                                @RequestParam("testCategory") String testCategory,
                                @RequestParam("testDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate testDate,
                                @RequestParam("testResult") String testResult,
                                @RequestParam(value = "normalRange", defaultValue = "") String normalRange,
                                @RequestParam(value = "status", defaultValue = "NORMAL") String status,
                                @RequestParam(value = "remarks", defaultValue = "") String remarks,
                                @RequestParam(value = "labReferenceNumber", defaultValue = "") String labReferenceNumber,
                                @RequestParam(value = "testFee", defaultValue = "500.00") BigDecimal testFee,
                                @RequestParam(value = "paymentStatus", defaultValue = "PAID") String paymentStatus,
                                RedirectAttributes redirectAttributes) {
        try {
            testReportService.createTestReport(
                    patientId, doctorId, testName, testCategory, testDate, testResult,
                    normalRange, status, remarks, labReferenceNumber, testFee, paymentStatus
            );
            redirectAttributes.addFlashAttribute("successMessage", "Diagnostic Test Report for '" + testName + "' generated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording lab report: " + ex.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/testreport/{id}/pay")
    public String payTestReport(@PathVariable("id") Long id,
                                @RequestParam(value = "paymentMethod", defaultValue = "UPI") String paymentMethod,
                                @RequestParam(value = "transactionReference", required = false) String transactionReference,
                                RedirectAttributes redirectAttributes) {
        try {
            testReportService.updatePaymentStatus(id, "PAID", paymentMethod, transactionReference);
            redirectAttributes.addFlashAttribute("successMessage", "Payment for Diagnostic Report #" + id + " marked as PAID via " + paymentMethod + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating payment: " + ex.getMessage());
        }
        return "redirect:/admin";
    }
}
