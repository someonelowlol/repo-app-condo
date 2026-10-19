package com.example.residensmart.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.residensmart.data.model.*
import com.example.residensmart.data.repository.CondoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class CondoViewModel(
    private val repository: CondoRepository = CondoRepository()
) : ViewModel() {

    private val _currentRole = MutableStateFlow(UserRole.RESIDENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    val resident: StateFlow<Resident> = repository.resident
    val payments: StateFlow<List<Payment>> = repository.payments
    val amenities: StateFlow<List<Amenity>> = repository.amenities
    val bookings: StateFlow<List<Booking>> = repository.bookings
    val incidents: StateFlow<List<Incident>> = repository.incidents
    val announcements: StateFlow<List<Announcement>> = repository.announcements
    val staffContacts: List<StaffContact> = repository.staffContacts

    fun setRole(role: UserRole) {
        _currentRole.value = role
        if (role == UserRole.ADMIN && _currentTab.value == 0) {
            _currentTab.value = 5 // switch to admin tab
        } else if (role == UserRole.RESIDENT && _currentTab.value == 5) {
            _currentTab.value = 0
        }
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun payPayment(paymentId: String, method: PaymentMethod, proofFile: String? = null) {
        repository.payPayment(paymentId, method, proofFile)
    }

    fun createBooking(amenity: Amenity, date: String, timeSlot: String, durationHours: Int, guestCount: Int): Booking {
        return repository.createBooking(amenity, date, timeSlot, durationHours, guestCount)
    }

    fun cancelBooking(bookingId: String) {
        repository.cancelBooking(bookingId)
    }

    fun reportIncident(title: String, category: IncidentCategory, priority: IncidentPriority, location: String, description: String, photoUrl: String?) {
        repository.reportIncident(title, category, priority, location, description, photoUrl)
    }

    fun addIncidentComment(incidentId: String, content: String, authorName: String, authorRole: String) {
        repository.addIncidentComment(incidentId, content, authorName, authorRole)
    }

    fun markAnnouncementRead(announcementId: String) {
        repository.markAnnouncementRead(announcementId)
    }

    fun updateIncidentStatus(incidentId: String, status: IncidentStatus, technician: String?) {
        repository.updateIncidentStatus(incidentId, status, technician)
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        repository.updateBookingStatus(bookingId, status)
    }

    fun publishAnnouncement(title: String, content: String, category: AnnouncementCategory, author: String) {
        repository.publishAnnouncement(title, content, category, author)
    }
}
