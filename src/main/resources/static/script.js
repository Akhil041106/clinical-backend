// CarePulse Clinical Management System - Frontend Interactivity

document.addEventListener('DOMContentLoaded', () => {
    // Auto-dismiss feedback alert banners after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Dynamic doctor filtering by department in appointment booking
    const deptSelect = document.getElementById('departmentSelect');
    const doctorSelect = document.getElementById('doctorSelect');

    if (deptSelect && doctorSelect) {
        deptSelect.addEventListener('change', () => {
            const selectedDeptId = deptSelect.value;
            const options = doctorSelect.querySelectorAll('option');

            options.forEach(opt => {
                if (!opt.value) {
                    opt.hidden = false;
                    return;
                }
                const optDept = opt.getAttribute('data-department');
                if (!selectedDeptId || optDept === selectedDeptId) {
                    opt.hidden = false;
                } else {
                    opt.hidden = true;
                }
            });

            doctorSelect.value = '';
        });
    }

    // Initialize Tab Navigation
    initTabs();
});

// Tab Switching System
function initTabs() {
    const tabLinks = document.querySelectorAll('.tab-link');
    tabLinks.forEach(link => {
        link.addEventListener('click', () => {
            const targetTabId = link.getAttribute('data-tab');
            switchTab(targetTabId);
        });
    });
}

function switchTab(tabId) {
    // Deactivate all tab links
    document.querySelectorAll('.tab-link').forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('data-tab') === tabId) {
            link.classList.add('active');
        }
    });

    // Hide all tab panes
    document.querySelectorAll('.tab-pane').forEach(pane => {
        pane.classList.remove('active');
    });

    // Show target tab pane
    const targetPane = document.getElementById(tabId);
    if (targetPane) {
        targetPane.classList.add('active');
    }
}

// Live Table Search Filter
function filterTable(inputId, tableId) {
    const input = document.getElementById(inputId);
    const filter = input.value.toLowerCase();
    const table = document.getElementById(tableId);
    if (!table) return;

    const trs = table.getElementsByTagName('tr');
    for (let i = 1; i < trs.length; i++) {
        const text = trs[i].textContent || trs[i].innerText;
        if (text.toLowerCase().indexOf(filter) > -1) {
            trs[i].style.display = '';
        } else {
            trs[i].style.display = 'none';
        }
    }
}

// Appointment Status Filter
function filterAppointmentsByStatus(status, pillElement) {
    const pills = document.querySelectorAll('.filter-pill');
    pills.forEach(p => p.classList.remove('active'));
    if (pillElement) {
        pillElement.classList.add('active');
    }

    const rows = document.querySelectorAll('.appointment-row');
    rows.forEach(row => {
        const rowStatus = row.getAttribute('data-status');
        if (status === 'ALL' || rowStatus === status) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Modal Management
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Open Consultation Modal for Doctor
function openConsultationModal(appointmentId, patientName, reason) {
    const apptIdInput = document.getElementById('consultationAppointmentId');
    const patientNameSpan = document.getElementById('consultationPatientName');
    const reasonSpan = document.getElementById('consultationReason');

    if (apptIdInput) apptIdInput.value = appointmentId;
    if (patientNameSpan) patientNameSpan.textContent = patientName;
    if (reasonSpan) reasonSpan.textContent = reason;

    openModal('consultationModal');
}

// Print Prescription Action
function printPrescription() {
    window.print();
}

// Action Confirmation
function confirmAction(message) {
    return confirm(message || 'Are you sure you want to proceed?');
}

// Payment Gateway Modal Controller
function openPaymentModal(itemType, id, title, amount, postActionUrl) {
    const modal = document.getElementById('paymentModal');
    if (!modal) return;

    // Set form target action
    const form = document.getElementById('paymentGatewayForm');
    if (form) form.action = postActionUrl;

    // Update bill summary
    const titleSpan = document.getElementById('paymentBillTitle');
    const amountSpan = document.getElementById('paymentBillAmount');
    const btnAmountSpan = document.getElementById('paymentBtnAmount');
    const qrImg = document.getElementById('paymentUpiQr');

    if (titleSpan) titleSpan.textContent = title || 'Medical Service Fee';
    if (amountSpan) amountSpan.textContent = '₹' + amount;
    if (btnAmountSpan) btnAmountSpan.textContent = '₹' + amount;

    // Generate dynamic QR Code for UPI
    if (qrImg) {
        const upiUri = `upi://pay?pa=carepulse.billing@okhdfcbank&pn=CarePulse%20Hospital&am=${encodeURIComponent(amount)}&cu=INR&tn=Invoice-${id}`;
        qrImg.src = `https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent(upiUri)}`;
    }

    // Default to UPI tab
    selectPaymentChannel('upi');

    openModal('paymentModal');
}

function selectPaymentChannel(channel) {
    // Buttons
    document.querySelectorAll('.payment-tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-channel') === channel);
    });

    // Panes
    document.querySelectorAll('.payment-channel-pane').forEach(pane => {
        pane.classList.toggle('active', pane.getAttribute('id') === `channel-${channel}`);
    });

    // Hidden input
    const methodInput = document.getElementById('paymentMethodInput');
    if (methodInput) {
        methodInput.value = channel.toUpperCase();
    }
}

function copyText(text, btnElement) {
    navigator.clipboard.writeText(text).then(() => {
        const originalText = btnElement.textContent;
        btnElement.textContent = '✓ Copied!';
        setTimeout(() => {
            btnElement.textContent = originalText;
        }, 1500);
    }).catch(() => {
        alert('Copied to clipboard: ' + text);
    });
}

