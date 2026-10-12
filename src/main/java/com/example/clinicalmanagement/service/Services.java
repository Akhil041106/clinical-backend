package com.example.clinicalmanagement.service;

import com.example.clinicalmanagement.entity.*;
import com.example.clinicalmanagement.repository.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Services {

    @Service
    @Transactional
    public static class UserService implements UserDetailsService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Autowired
        public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );
        }

        public User registerUser(String username, String rawPassword, String fullName, String email, String role) {
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username '" + username + "' is already taken.");
            }
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email '" + email + "' is already registered.");
            }

            User user = new User(
                    username,
                    passwordEncoder.encode(rawPassword),
                    fullName,
                    email,
                    role
            );
            return userRepository.save(user);
        }

        public Optional<User> findByUsername(String username) {
            return userRepository.findByUsername(username);
        }

        public Optional<User> findById(Long id) {
            return userRepository.findById(id);
        }

        public List<User> findAll() {
            return userRepository.findAll();
        }

        public long countUsers() {
            return userRepository.count();
        }
    }

    @Service
    @Transactional
    public static class DepartmentService {

        private final DepartmentRepository departmentRepository;

        @Autowired
        public DepartmentService(DepartmentRepository departmentRepository) {
            this.departmentRepository = departmentRepository;
        }

        public List<Department> getAllDepartments() {
            return departmentRepository.findAll();
        }

        public Optional<Department> getDepartmentById(Long id) {
            return departmentRepository.findById(id);
        }

        public Department saveDepartment(Department department) {
            return departmentRepository.save(department);
        }
    }

    @Service
    @Transactional
    public static class DoctorService {

        private final DoctorRepository doctorRepository;
        private final DepartmentRepository departmentRepository;
        private final UserService userService;

        @Autowired
        public DoctorService(DoctorRepository doctorRepository,
                             DepartmentRepository departmentRepository,
                             UserService userService) {
            this.doctorRepository = doctorRepository;
            this.departmentRepository = departmentRepository;
            this.userService = userService;
        }

        public List<Doctor> getAllDoctors() {
            return doctorRepository.findAll();
        }

        public Optional<Doctor> getDoctorById(Long id) {
            return doctorRepository.findById(id);
        }

        public Optional<Doctor> getDoctorByUsername(String username) {
            return doctorRepository.findByUserUsername(username);
        }

        public List<Doctor> getDoctorsByDepartment(Long departmentId) {
            return doctorRepository.findByDepartmentId(departmentId);
        }

        public Doctor createDoctor(String username, String password, String fullName, String email,
                                   Long departmentId, String specialization, String qualifications,
                                   Integer experienceYears, String phone, BigDecimal consultationFee) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID: " + departmentId));

            User user = userService.registerUser(username, password, fullName, email, "DOCTOR");

            Doctor doctor = new Doctor(
                    user, department, specialization, qualifications, experienceYears, phone, consultationFee
            );
            return doctorRepository.save(doctor);
        }

        public long countDoctors() {
            return doctorRepository.count();
        }
    }

    @Service
    @Transactional
    public static class PatientService {

        private final PatientRepository patientRepository;
        private final UserService userService;

        @Autowired
        public PatientService(PatientRepository patientRepository, UserService userService) {
            this.patientRepository = patientRepository;
            this.userService = userService;
        }

        public List<Patient> getAllPatients() {
            return patientRepository.findAll();
        }

        public Optional<Patient> getPatientById(Long id) {
            return patientRepository.findById(id);
        }

        public Optional<Patient> getPatientByUsername(String username) {
            return patientRepository.findByUserUsername(username);
        }

        public List<Patient> searchPatients(String query) {
            if (query == null || query.trim().isEmpty()) {
                return patientRepository.findAll();
            }
            return patientRepository.searchByName(query.trim());
        }

        public Patient registerPatient(String username, String password, String fullName, String email,
                                       LocalDate dob, String gender, String bloodGroup,
                                       String phone, String address, String emergencyContact,
                                       String medicalHistory, String allergies) {
            User user = userService.registerUser(username, password, fullName, email, "PATIENT");

            Patient patient = new Patient(
                    user, dob, gender, bloodGroup, phone, address, emergencyContact, medicalHistory, allergies
            );
            return patientRepository.save(patient);
        }

        public Patient savePatient(Patient patient) {
            return patientRepository.save(patient);
        }

        public long countPatients() {
            return patientRepository.count();
        }
    }

    @Service
    @Transactional
    public static class AppointmentService {

        private final AppointmentRepository appointmentRepository;
        private final PatientRepository patientRepository;
        private final DoctorRepository doctorRepository;

        @Autowired
        public AppointmentService(AppointmentRepository appointmentRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository) {
            this.appointmentRepository = appointmentRepository;
            this.patientRepository = patientRepository;
            this.doctorRepository = doctorRepository;
        }

        public List<Appointment> getAllAppointments() {
            return appointmentRepository.findAllByOrderByAppointmentDateDescAppointmentTimeDesc();
        }

        public Optional<Appointment> getAppointmentById(Long id) {
            return appointmentRepository.findById(id);
        }

        public List<Appointment> getAppointmentsByPatient(Long patientId) {
            return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId);
        }

        public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
            return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId);
        }

        public Appointment bookAppointment(Long patientId, Long doctorId, LocalDate date, LocalTime time, String reason) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid patient ID: " + patientId));
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + doctorId));

            Appointment appointment = new Appointment(
                    patient, doctor, date, time, reason, "PENDING", "PENDING", doctor.getConsultationFee()
            );
            return appointmentRepository.save(appointment);
        }

        public Appointment updateStatus(Long appointmentId, String newStatus) {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
            appointment.setStatus(newStatus.toUpperCase());
            return appointmentRepository.save(appointment);
        }

        public Appointment updatePaymentStatus(Long appointmentId, String paymentStatus) {
            return updatePaymentStatus(appointmentId, paymentStatus, null, null);
        }

        public Appointment updatePaymentStatus(Long appointmentId, String paymentStatus, String paymentMethod, String transactionReference) {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
            appointment.setPaymentStatus(paymentStatus.toUpperCase());
            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                appointment.setPaymentMethod(paymentMethod.toUpperCase());
            }
            if (transactionReference != null && !transactionReference.trim().isEmpty()) {
                appointment.setTransactionReference(transactionReference.trim());
            }
            return appointmentRepository.save(appointment);
        }

        public long countAppointments() {
            return appointmentRepository.count();
        }

        public long countPendingAppointments() {
            return appointmentRepository.findByStatus("PENDING").size();
        }
    }

    @Service
    @Transactional
    public static class ConsultationService {

        private final ConsultationRepository consultationRepository;
        private final AppointmentRepository appointmentRepository;

        @Autowired
        public ConsultationService(ConsultationRepository consultationRepository,
                                   AppointmentRepository appointmentRepository) {
            this.consultationRepository = consultationRepository;
            this.appointmentRepository = appointmentRepository;
        }

        public Optional<Consultation> getConsultationByAppointment(Long appointmentId) {
            return consultationRepository.findByAppointmentId(appointmentId);
        }

        public List<Consultation> getConsultationsByPatient(Long patientId) {
            return consultationRepository.findByPatientIdOrderByConsultationDateDesc(patientId);
        }

        public List<Consultation> getConsultationsByDoctor(Long doctorId) {
            return consultationRepository.findByDoctorIdOrderByConsultationDateDesc(doctorId);
        }

        public Consultation createConsultation(Long appointmentId, String symptoms, String vitals,
                                               String diagnosis, String prescription, String instructions,
                                               LocalDate followUpDate) {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

            appointment.setStatus("COMPLETED");
            appointmentRepository.save(appointment);

            Consultation consultation = new Consultation(
                    appointment,
                    appointment.getDoctor(),
                    appointment.getPatient(),
                    symptoms,
                    vitals,
                    diagnosis,
                    prescription,
                    instructions,
                    followUpDate
            );
            return consultationRepository.save(consultation);
        }

        public long countConsultations() {
            return consultationRepository.count();
        }
    }

    @Service
    @Transactional
    public static class TestReportService {

        private final TestReportRepository testReportRepository;
        private final PatientRepository patientRepository;
        private final DoctorRepository doctorRepository;

        @Autowired
        public TestReportService(TestReportRepository testReportRepository,
                                 PatientRepository patientRepository,
                                 DoctorRepository doctorRepository) {
            this.testReportRepository = testReportRepository;
            this.patientRepository = patientRepository;
            this.doctorRepository = doctorRepository;
        }

        public List<TestReport> getAllTestReports() {
            return testReportRepository.findAllByOrderByTestDateDesc();
        }

        public Optional<TestReport> getTestReportById(Long id) {
            return testReportRepository.findById(id);
        }

        public List<TestReport> getTestReportsByPatient(Long patientId) {
            return testReportRepository.findByPatientIdOrderByTestDateDesc(patientId);
        }

        public List<TestReport> getTestReportsByDoctor(Long doctorId) {
            return testReportRepository.findByDoctorIdOrderByTestDateDesc(doctorId);
        }

        public TestReport createTestReport(Long patientId, Long doctorId, String testName, String testCategory,
                                           LocalDate testDate, String testResult, String normalRange, String status,
                                           String remarks, String labRef, BigDecimal fee, String paymentStatus) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

            TestReport report = new TestReport(
                    patient, doctor, testName, testCategory, testDate, testResult,
                    normalRange, status, remarks, labRef, fee, paymentStatus
            );
            return testReportRepository.save(report);
        }

        public TestReport updatePaymentStatus(Long id, String paymentStatus) {
            return updatePaymentStatus(id, paymentStatus, null, null);
        }

        public TestReport updatePaymentStatus(Long id, String paymentStatus, String paymentMethod, String transactionReference) {
            TestReport report = testReportRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Test report not found: " + id));
            report.setPaymentStatus(paymentStatus.toUpperCase());
            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                report.setPaymentMethod(paymentMethod.toUpperCase());
            }
            if (transactionReference != null && !transactionReference.trim().isEmpty()) {
                report.setTransactionReference(transactionReference.trim());
            }
            return testReportRepository.save(report);
        }

        public long countTestReports() {
            return testReportRepository.count();
        }
    }
}
