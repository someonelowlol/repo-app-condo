package com.example.residensmart.data.model

enum class UserRole {
    RESIDENT,
    ADMIN
}

data class Resident(
    val id: String = "res-402",
    val name: String = "Luis Martínez",
    val apartment: String = "Apto. 402",
    val tower: String = "Torre B",
    val email: String = "luis.martinez@condofeliz.com",
    val phone: String = "+52 55 4321 0987",
    val avatar: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=256&auto=format&fit=crop",
    val balance: Double = 2150.00
)

enum class PaymentStatus(val labelEs: String) {
    PENDING("Pendiente"),
    UNDER_REVIEW("En Revisión"),
    PAID("Pagado")
}

enum class PaymentCategory(val labelEs: String) {
    MAINTENANCE("Mantenimiento"),
    AMENITY("Amenidad"),
    FINE("Multa"),
    OTHER("Otros")
}

enum class PaymentMethod(val labelEs: String) {
    CREDIT_CARD("Tarjeta de Crédito/Débito"),
    BANK_TRANSFER("Transferencia SPEI"),
    CASH("Pago en Efectivo / Administración")
}

data class Payment(
    val id: String,
    val title: String,
    val description: String,
    val amount: Double,
    val dueDate: String,
    val status: PaymentStatus,
    val category: PaymentCategory,
    val reference: String = "",
    val paidAt: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val proofFile: String? = null
)

data class Amenity(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val hourlyRate: Double,
    val capacity: Int,
    val rules: List<String>,
    val image: String,
    val requiresReview: Boolean
)

enum class BookingStatus(val labelEs: String) {
    CONFIRMED("Confirmada"),
    PENDING("En Revisión"),
    CANCELLED("Cancelada")
}

data class Booking(
    val id: String,
    val amenityId: String,
    val amenityName: String,
    val date: String,
    val timeSlot: String,
    val durationHours: Int,
    val totalCost: Double,
    val status: BookingStatus,
    val residentId: String,
    val guestCount: Int,
    val createdAt: String,
    val qrCode: String = ""
)

enum class IncidentCategory(val labelEs: String) {
    PLUMBING("Plomería"),
    ELECTRICITY("Electricidad"),
    ELEVATOR("Elevadores"),
    SECURITY("Seguridad"),
    COMMON_AREA("Área Común"),
    OTHER("Otros")
}

enum class IncidentPriority(val labelEs: String) {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta")
}

enum class IncidentStatus(val labelEs: String) {
    REPORTED("Reportado"),
    ASSIGNED("Asignado"),
    IN_PROGRESS("En Progreso"),
    RESOLVED("Resuelto")
}

data class IncidentComment(
    val id: String,
    val authorName: String,
    val authorRole: String, // "resident", "admin", "technician"
    val content: String,
    val createdAt: String
)

data class Incident(
    val id: String,
    val title: String,
    val category: IncidentCategory,
    val priority: IncidentPriority,
    val location: String,
    val description: String,
    val status: IncidentStatus,
    val createdAt: String,
    val updatedAt: String,
    val images: List<String> = emptyList(),
    val comments: List<IncidentComment> = emptyList(),
    val technicianName: String? = null
)

enum class AnnouncementCategory(val labelEs: String) {
    INFO("Informativo"),
    MAINTENANCE("Mantenimiento"),
    URGENTE("Urgente"),
    EVENT("Evento")
}

data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val category: AnnouncementCategory,
    val author: String,
    val isRead: Boolean = false
)

data class StaffContact(
    val id: String,
    val role: String,
    val name: String,
    val phone: String,
    val email: String,
    val hours: String
)
