package com.example.clinicalmanagement.repository;

import com.example.clinicalmanagement.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface Repositories {

    @Repository
    interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);
        Optional<User> findByEmail(String email);
        boolean existsByUsername(String username);
        boolean existsByEmail(String email);
        List<User> findByRole(String role);
    }

    @Repository
    interface DepartmentRepository extends JpaRepository<Department, Long> {
        Optional<Department> findByName(String name);
    }

    @Repository
    interface DoctorRepository extends JpaRepository<Doctor, Long> {
        Optional<Doctor> findByUserId(Long userId);
        Optional<Doctor> findByUserUsername(String username);
        List<Doctor> findByDepartmentId(Long departmentId);
    }

    @Repository
    interface PatientRepository extends JpaRepository<Patient, Long> {
        Optional<Patient> findByUserId(Long userId);
        Optional<Patient> findByUserUsername(String username);
        @Query("SELECT p FROM Patient p WHERE LOWER(p.user.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
        List<Patient> searchByName(@Param("name") String name);
    }

    @Repository
    interface AppointmentRepository extends JpaRepository<Appointment, Long> {
        List<Appointment> findByPatientId(Long patientId);
        List<Appointment> findByDoctorId(Long doctorId);
        List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
        List<Appointment> findByStatus(String status);
        List<Appointment> findAllByOrderByAppointmentDateDescAppointmentTimeDesc();
        List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);
        List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(Long doctorId);
    }

    @Repository
    interface ConsultationRepository extends JpaRepository<Consultation, Long> {
        List<Consultation> findByPatientId(Long patientId);
        List<Consultation> findByDoctorId(Long doctorId);
        Optional<Consultation> findByAppointmentId(Long appointmentId);
        List<Consultation> findByPatientIdOrderByConsultationDateDesc(Long patientId);
        List<Consultation> findByDoctorIdOrderByConsultationDateDesc(Long doctorId);
    }

    @Repository
    interface TestReportRepository extends JpaRepository<TestReport, Long> {
        List<TestReport> findByPatientIdOrderByTestDateDesc(Long patientId);
        List<TestReport> findByDoctorIdOrderByTestDateDesc(Long doctorId);
        List<TestReport> findAllByOrderByTestDateDesc();
    }
}
