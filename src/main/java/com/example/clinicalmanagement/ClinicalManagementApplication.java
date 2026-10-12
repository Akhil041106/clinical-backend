package com.example.clinicalmanagement;

import com.example.clinicalmanagement.entity.*;
import com.example.clinicalmanagement.repository.Repositories.*;
import com.example.clinicalmanagement.service.Services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
@EnableJpaRepositories(considerNestedRepositories = true)
public class ClinicalManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                         DepartmentRepository departmentRepository,
                                         DoctorRepository doctorRepository,
                                         PatientRepository patientRepository,
                                         AppointmentRepository appointmentRepository,
                                         ConsultationRepository consultationRepository,
                                         TestReportRepository testReportRepository,
                                         UserService userService) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            System.out.println("Initializing CarePulse Clinical Management System with Indian Healthcare Data...");

            // 1. Seed Hospital Administrator
            User admin = userService.registerUser(
                    "admin",
                    "admin123",
                    "Vikram Malhotra",
                    "admin@carepulse.clinic",
                    "ADMIN"
            );

            // 2. Seed Medical Departments
            Department cardio = departmentRepository.save(new Department("Cardiology", "Cardiovascular care, ECG, echocardiography, hypertension and preventative heart health."));
            Department general = departmentRepository.save(new Department("General Medicine", "Comprehensive adult primary care, fever, seasonal infections, and chronic disease management."));
            Department pediatrics = departmentRepository.save(new Department("Pediatrics", "Infant and child wellness, vaccinations, growth monitoring and pediatric care."));
            Department ortho = departmentRepository.save(new Department("Orthopedics", "Bone, joint, spine care, arthritis management, and sports injury recovery."));
            Department neuro = departmentRepository.save(new Department("Neurology", "Brain, spinal cord, migraine care, neurological diagnostics, and stroke rehabilitation."));

            // 3. Seed Doctors with Indian Names & Rupee Consultation Fees
            User docUser1 = userService.registerUser("dr.sharma", "doctor123", "Dr. Rajesh Sharma, MD", "dr.sharma@carepulse.clinic", "DOCTOR");
            Doctor drSharma = doctorRepository.save(new Doctor(
                    docUser1, cardio, "Interventional Cardiology", "MD, DM (Cardiology) - AIIMS New Delhi", 14, "+91 98112 34567", new BigDecimal("800.00")
            ));

            User docUser2 = userService.registerUser("dr.patel", "doctor123", "Dr. Priya Patel, MD", "dr.patel@carepulse.clinic", "DOCTOR");
            Doctor drPatel = doctorRepository.save(new Doctor(
                    docUser2, general, "Internal Medicine", "MBBS, MD (Medicine) - KEM Hospital Mumbai", 10, "+91 98223 45678", new BigDecimal("500.00")
            ));

            User docUser3 = userService.registerUser("dr.verma", "doctor123", "Dr. Amit Verma, MD", "dr.verma@carepulse.clinic", "DOCTOR");
            Doctor drVerma = doctorRepository.save(new Doctor(
                    docUser3, pediatrics, "Pediatric Care", "MD (Pediatrics), DCH - PGIMER Chandigarh", 8, "+91 98334 56789", new BigDecimal("600.00")
            ));

            User docUser4 = userService.registerUser("dr.rao", "doctor123", "Dr. Sunita Rao, MS", "dr.rao@carepulse.clinic", "DOCTOR");
            Doctor drRao = doctorRepository.save(new Doctor(
                    docUser4, ortho, "Orthopedic Surgery", "MS (Ortho), Fellowship in Joint Replacement - Apollo Chennai", 12, "+91 98445 67890", new BigDecimal("750.00")
            ));

            // 4. Seed Patients with Indian Names
            User patUser1 = userService.registerUser("rahul.s", "patient123", "Rahul Sharma", "rahul.sharma@example.in", "PATIENT");
            Patient patientRahul = patientRepository.save(new Patient(
                    patUser1, LocalDate.of(1986, 7, 14), "Male", "B+", "+91 98765 43210",
                    "Flat 402, Green Glen Layout, Bellandur, Bangalore", "Pooja Sharma (+91 98765 43211)",
                    "Mild primary hypertension (controlled)", "Penicillin"
            ));

            User patUser2 = userService.registerUser("ananya.i", "patient123", "Ananya Iyer", "ananya.iyer@example.in", "PATIENT");
            Patient patientAnanya = patientRepository.save(new Patient(
                    patUser2, LocalDate.of(1993, 11, 28), "Female", "O+", "+91 97654 32109",
                    "B-12, Mayur Vihar Phase 1, New Delhi", "Suresh Iyer (+91 97654 32110)",
                    "Childhood allergic rhinitis, seasonal bronchitis", "Sulfa drugs, Dust mites"
            ));

            User patUser3 = userService.registerUser("vikram.s", "patient123", "Vikram Singh", "vikram.singh@example.in", "PATIENT");
            Patient patientVikram = patientRepository.save(new Patient(
                    patUser3, LocalDate.of(1979, 3, 21), "Male", "AB+", "+91 96543 21098",
                    "704, Sea Breeze Towers, Bandra West, Mumbai", "Neha Singh (+91 96543 21099)",
                    "Type-2 Diabetes mellitus (Metformin 500mg OD)", "None"
            ));

            // 5. Seed Appointments with Indian Doctor-Patient context, Fees, and Payment Status
            Appointment appt1 = appointmentRepository.save(new Appointment(
                    patientRahul, drSharma, LocalDate.now().plusDays(1), LocalTime.of(10, 30),
                    "Cardiovascular evaluation and routine blood pressure monitoring", "APPROVED",
                    "PAID", new BigDecimal("800.00")
            ));

            Appointment appt2 = appointmentRepository.save(new Appointment(
                    patientAnanya, drPatel, LocalDate.now(), LocalTime.of(11, 30),
                    "Persistent dry cough, mild sore throat and seasonal malaise", "COMPLETED",
                    "PAID", new BigDecimal("500.00")
            ));

            Appointment appt3 = appointmentRepository.save(new Appointment(
                    patientVikram, drRao, LocalDate.now().plusDays(2), LocalTime.of(15, 0),
                    "Right knee joint stiffness and discomfort after morning walks", "PENDING",
                    "PENDING", new BigDecimal("750.00")
            ));

            Appointment appt4 = appointmentRepository.save(new Appointment(
                    patientRahul, drPatel, LocalDate.now().plusDays(4), LocalTime.of(12, 0),
                    "Annual general health check-up and lipid review", "PENDING",
                    "PENDING", new BigDecimal("500.00")
            ));

            // 6. Seed Consultation & Digital Prescription
            Consultation consultation = new Consultation(
                    appt2,
                    drPatel,
                    patientAnanya,
                    "Patient reports dry cough for 4 days, throat irritation, no fever today, mild nasal congestion.",
                    "BP: 116/74 mmHg | Pulse: 74 bpm | Temp: 98.4 °F | SpO2: 99% | Weight: 58 kg",
                    "Acute Upper Respiratory Tract Irritation / Pharyngitis (Resolving)",
                    "1. Tab. Montelukast + Levocetirizine (Montair-LC) - 1 tablet orally once daily at bedtime for 5 days\n2. Syp. Ascoril-D - 10ml orally thrice daily after food for 4 days\n3. Salt water gargles thrice daily and steam inhalation before sleep",
                    "Stay well hydrated with warm fluids. Avoid cold beverages and air-conditioner exposure. Review if fever or breathlessness develops.",
                    LocalDate.now().plusWeeks(2)
            );
            consultationRepository.save(consultation);

            // 7. Seed Diagnostic Lab Test Reports
            testReportRepository.save(new TestReport(
                    patientRahul, drSharma, "Complete Blood Count (CBC)", "Pathology",
                    LocalDate.now().minusDays(3),
                    "Hemoglobin: 14.8 g/dL (Normal: 13.0 - 17.0)\nRBC Count: 4.95 mill/cumm (Normal: 4.5 - 5.5)\nTotal WBC Count: 6,800 cells/cumm (Normal: 4,000 - 11,000)\nPlatelet Count: 2.65 Lakhs/cumm (Normal: 1.5 - 4.5)\nNeutrophils: 62% | Lymphocytes: 30% | Eosinophils: 4% | Monocytes: 4%",
                    "All parameters within biological reference intervals",
                    "NORMAL",
                    "Erythrocyte morphology normocytic normochromic. No hemoparasites seen.",
                    "LAB-BLR-2026-8812", new BigDecimal("450.00"), "PAID"
            ));

            testReportRepository.save(new TestReport(
                    patientRahul, drSharma, "Comprehensive Lipid Profile", "Biochemistry",
                    LocalDate.now().minusDays(3),
                    "Total Cholesterol: 182 mg/dL (Desirable: < 200)\nHDL Cholesterol (Good): 48 mg/dL (Normal: > 40)\nLDL Cholesterol: 104 mg/dL (Optimal: < 100, Near optimal: 100-129)\nTriglycerides: 145 mg/dL (Normal: < 150)\nVLDL Cholesterol: 29 mg/dL (Normal: 5 - 40)\nTC / HDL Ratio: 3.79 (Normal: < 4.5)",
                    "Total cholesterol and triglycerides desirable",
                    "NORMAL",
                    "Mild border-line LDL; lifestyle modification with regular brisk walk and dietary fibre recommended.",
                    "LAB-BLR-2026-8815", new BigDecimal("750.00"), "PAID"
            ));

            testReportRepository.save(new TestReport(
                    patientAnanya, drPatel, "Digital Chest X-Ray (PA View)", "Radiology",
                    LocalDate.now().minusDays(1),
                    "Lung fields are clear bilaterally. Bronchovascular markings appear normal. Hilar structures normal. Cardiothoracic ratio is within normal limits. Both costophrenic angles and domes of diaphragm are clear. Bony cage and soft tissue structures intact.",
                    "Normal pulmonary parenchymal architecture",
                    "NORMAL",
                    "No active focal lung parenchymal lesion or pneumonic consolidation detected.",
                    "RAD-DEL-2026-4409", new BigDecimal("600.00"), "PAID"
            ));

            testReportRepository.save(new TestReport(
                    patientVikram, drPatel, "HbA1c (Glycated Hemoglobin)", "Pathology",
                    LocalDate.now().minusDays(5),
                    "HbA1c: 6.8% (Non-Diabetic: < 5.7% | Prediabetes: 5.7 - 6.4% | Fair Diabetic Control: 6.5 - 7.0%)\nEstimated Average Glucose (eAG): 148 mg/dL",
                    "Target for adults with diabetes: < 7.0%",
                    "NORMAL",
                    "Fair glycemic control observed. Continue current dosage of Metformin 500mg OD.",
                    "LAB-MUM-2026-1193", new BigDecimal("550.00"), "PAID"
            ));

            System.out.println("CarePulse Clinical Management System initialized successfully.");
            System.out.println("Administrator Account: admin / admin123 (Vikram Malhotra)");
            System.out.println("Staff Doctor: dr.sharma / doctor123 (Dr. Rajesh Sharma, MD)");
            System.out.println("Registered Patient: rahul.s / patient123 (Rahul Sharma)");
        };
    }
}
