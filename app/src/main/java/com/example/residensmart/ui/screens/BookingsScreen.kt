package com.example.residensmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.residensmart.data.model.*
import com.example.residensmart.ui.components.CondoTopBar
import com.example.residensmart.ui.components.QrCodePreviewModal
import com.example.residensmart.ui.components.StatusBadge
import com.example.residensmart.ui.theme.*
import com.example.residensmart.ui.viewmodel.CondoViewModel

@Composable
fun BookingsScreen(
    viewModel: CondoViewModel,
    modifier: Modifier = Modifier
) {
    val resident by viewModel.resident.collectAsState()
    val amenities by viewModel.amenities.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Amenidades, 1: Mis Reservas
    var amenityToBook by remember { mutableStateOf<Amenity?>(null) }
    var activeQrBooking by remember { mutableStateOf<Booking?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EditorialBg),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            CondoTopBar(
                resident = resident,
                currentRole = currentRole,
                onRoleToggle = { viewModel.setRole(it) }
            )
        }

        // HEADER
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Reserva de Áreas Comunes",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = EditorialText
                )
                Text(
                    text = "Disfruta de las amenidades exclusivas de tu condominio reservando en tiempo real.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialMuted
                )
            }
        }

        // TABS
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardSurface,
                contentColor = BrandBlue,
                divider = { HorizontalDivider(color = EditorialBorder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, EditorialBorder)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "CATÁLOGO DE ÁREAS (${amenities.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_amenities_catalog")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "MIS RESERVAS (${bookings.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_my_bookings")
                )
            }
        }

        // TAB 0: AMENITIES CATALOG
        if (selectedTab == 0) {
            items(amenities) { amenity ->
                AmenityCatalogCard(
                    amenity = amenity,
                    onBookClick = { amenityToBook = amenity },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            // TAB 1: MY BOOKINGS
            if (bookings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes reservaciones registradas aún.",
                            fontSize = 14.sp,
                            color = EditorialMuted
                        )
                    }
                }
            } else {
                items(bookings) { booking ->
                    BookingDetailCard(
                        booking = booking,
                        onShowQr = { activeQrBooking = booking },
                        onCancel = { viewModel.cancelBooking(booking.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // MODAL: CREATE BOOKING
    amenityToBook?.let { amenity ->
        CreateBookingDialog(
            amenity = amenity,
            onDismiss = { amenityToBook = null },
            onConfirm = { date, timeSlot, duration, guests ->
                val newBooking = viewModel.createBooking(amenity, date, timeSlot, duration, guests)
                amenityToBook = null
                activeQrBooking = newBooking
                selectedTab = 1 // Switch to My Bookings
            }
        )
    }

    // QR MODAL
    activeQrBooking?.let { b ->
        QrCodePreviewModal(
            title = b.amenityName,
            code = b.qrCode,
            details = "${b.date} • Horario: ${b.timeSlot} • ${b.guestCount} invitados",
            onDismiss = { activeQrBooking = null }
        )
    }
}

@Composable
fun AmenityCatalogCard(
    amenity: Amenity,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column {
            // Photo Header
            AsyncImage(
                model = amenity.image,
                contentDescription = amenity.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = amenity.name,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = EditorialText
                        )
                        Text(
                            text = "Capacidad máxima: ${amenity.capacity} personas",
                            fontSize = 12.sp,
                            color = EditorialMuted
                        )
                    }

                    StatusBadge(
                        text = if (amenity.hourlyRate == 0.0) "GRATIS" else "$%,.0f / hr".format(amenity.hourlyRate),
                        containerColor = if (amenity.hourlyRate == 0.0) BrandTeal.copy(alpha = 0.12f) else BrandBlue.copy(alpha = 0.12f),
                        contentColor = if (amenity.hourlyRate == 0.0) BrandTeal else BrandBlue
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = amenity.description,
                    fontSize = 13.sp,
                    color = EditorialMuted,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = EditorialBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (amenity.requiresReview) "Requiere aprobación previa" else "Confirmación instantánea",
                        fontSize = 11.sp,
                        color = if (amenity.requiresReview) StatusPending else StatusPaid,
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.testTag("book_amenity_${amenity.id}")
                    ) {
                        Text(
                            text = "RESERVAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialBg
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingDetailCard(
    booking: Booking,
    onShowQr: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.amenityName,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EditorialText
                )
                StatusBadge(
                    text = booking.status.labelEs,
                    containerColor = when (booking.status) {
                        BookingStatus.CONFIRMED -> StatusPaid.copy(alpha = 0.1f)
                        BookingStatus.PENDING -> StatusPending.copy(alpha = 0.1f)
                        BookingStatus.CANCELLED -> StatusUrgent.copy(alpha = 0.1f)
                    },
                    contentColor = when (booking.status) {
                        BookingStatus.CONFIRMED -> StatusPaid
                        BookingStatus.PENDING -> StatusPending
                        BookingStatus.CANCELLED -> StatusUrgent
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(text = "FECHA", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                    Text(text = booking.date, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text(text = "HORARIO", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                    Text(text = booking.timeSlot, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text(text = "INVITADOS", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                    Text(text = "${booking.guestCount} pers.", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Total pagado/a pagar: $%,.2f MXN".format(booking.totalCost),
                fontSize = 13.sp,
                color = BrandTeal,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = EditorialBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (booking.status == BookingStatus.CONFIRMED) {
                    Button(
                        onClick = onShowQr,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.testTag("show_qr_${booking.id}")
                    ) {
                        Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PASE DIGITAL QR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
                    }
                } else {
                    Text(
                        text = if (booking.status == BookingStatus.PENDING) "En espera de validación administrativa" else "Reserva cancelada",
                        fontSize = 12.sp,
                        color = EditorialMuted
                    )
                }

                if (booking.status != BookingStatus.CANCELLED) {
                    TextButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.testTag("cancel_booking_${booking.id}")
                    ) {
                        Text("CANCELAR", fontSize = 11.sp, color = StatusUrgent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateBookingDialog(
    amenity: Amenity,
    onDismiss: () -> Unit,
    onConfirm: (date: String, timeSlot: String, duration: Int, guests: Int) -> Unit
) {
    var selectedDate by remember { mutableStateOf("2026-06-18") }
    val availableDates = listOf("2026-06-18", "2026-06-19", "2026-06-20", "2026-06-21", "2026-06-22")

    val timeSlots = listOf(
        "09:00 - 11:00" to 2,
        "11:00 - 13:00" to 2,
        "13:00 - 16:00" to 3,
        "16:00 - 19:00" to 3,
        "19:00 - 22:00" to 3
    )
    var selectedSlotIndex by remember { mutableIntStateOf(2) }
    var guestCount by remember { mutableIntStateOf(4.coerceAtMost(amenity.capacity)) }

    val duration = timeSlots[selectedSlotIndex].second
    val totalCost = amenity.hourlyRate * duration

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reservar ${amenity.name}",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "SELECCIONA FECHA", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableDates.take(3).forEach { d ->
                        FilterChip(
                            selected = selectedDate == d,
                            onClick = { selectedDate = d },
                            label = { Text(text = d.takeLast(5), fontSize = 11.sp) },
                            shape = RoundedCornerShape(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "SELECCIONA HORARIO", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                Spacer(modifier = Modifier.height(6.dp))

                timeSlots.forEachIndexed { idx, pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSlotIndex = idx }
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSlotIndex == idx,
                            onClick = { selectedSlotIndex = idx },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "${pair.first} (${pair.second} horas)", fontSize = 12.sp, color = EditorialText)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "NÚMERO DE INVITADOS (MÁX: ${amenity.capacity})", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (guestCount > 1) guestCount-- },
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        text = "$guestCount",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    OutlinedButton(
                        onClick = { if (guestCount < amenity.capacity) guestCount++ },
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EditorialHighlight)
                        .border(1.dp, EditorialBorder)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Costo Total:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EditorialText)
                        Text(
                            text = if (totalCost == 0.0) "GRATUITO" else "$%,.2f MXN".format(totalCost),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BrandTeal
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedDate, timeSlots[selectedSlotIndex].first, duration, guestCount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.testTag("confirm_booking_button")
            ) {
                Text("CONFIRMAR RESERVA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(2.dp)
            ) {
                Text("CANCELAR", fontSize = 11.sp, color = EditorialMuted)
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(4.dp)
    )
}
