package com.example.residensmart.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.residensmart.data.model.*
import com.example.residensmart.ui.components.CondoTopBar
import com.example.residensmart.ui.components.QrCodePreviewModal
import com.example.residensmart.ui.components.SectionHeader
import com.example.residensmart.ui.components.StatusBadge
import com.example.residensmart.ui.theme.*
import com.example.residensmart.ui.viewmodel.CondoViewModel

@Composable
fun HomeScreen(
    viewModel: CondoViewModel,
    onNavigateTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val resident by viewModel.resident.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val incidents by viewModel.incidents.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var activeQrBooking by remember { mutableStateOf<Booking?>(null) }

    val pendingAmount = payments.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EditorialBg),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // TOP APP BAR
        item {
            CondoTopBar(
                resident = resident,
                currentRole = currentRole,
                onRoleToggle = { viewModel.setRole(it) }
            )
        }

        // EDITORIAL WELCOME BANNER & BALANCE HERO
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(2.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ESTADO DE CUENTA",
                                style = MaterialTheme.typography.labelSmall,
                                color = EditorialMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$%,.2f MXN".format(resident.balance),
                                fontFamily = FontFamily.Serif,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (resident.balance > 0) StatusUrgent else StatusPaid
                            )
                            Text(
                                text = if (resident.balance > 0) "Saldo pendiente de cuotas" else "Tu cuenta está al corriente",
                                fontSize = 12.sp,
                                color = EditorialMuted
                            )
                        }

                        Button(
                            onClick = { onNavigateTab(1) }, // Payments
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.testTag("home_pay_button")
                        ) {
                            Text(
                                text = "PAGAR CUOTA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialBg
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = EditorialBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Residente: ${resident.name}",
                            fontSize = 12.sp,
                            color = EditorialText
                        )
                        Text(
                            text = "Unidad: ${resident.apartment}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandTeal
                        )
                    }
                }
            }
        }

        // QUICK ACTION TILES
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "ACCIONES RÁPIDAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = EditorialMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.ReceiptLong,
                        label = "Pagos",
                        badge = "${payments.count { it.status == PaymentStatus.PENDING }}",
                        onClick = { onNavigateTab(1) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_payments")
                    )
                    QuickActionTile(
                        icon = Icons.Default.EventSeat,
                        label = "Reservar",
                        badge = null,
                        onClick = { onNavigateTab(2) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_bookings")
                    )
                    QuickActionTile(
                        icon = Icons.Default.ReportProblem,
                        label = "Incidentes",
                        badge = "${incidents.count { it.status != IncidentStatus.RESOLVED }}",
                        onClick = { onNavigateTab(3) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_incidents")
                    )
                    QuickActionTile(
                        icon = Icons.Default.ContactPhone,
                        label = "Directorio",
                        badge = null,
                        onClick = { onNavigateTab(4) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_directory")
                    )
                }
            }
        }

        // ANNOUNCEMENTS CAROUSEL / FEED
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
                SectionHeader(
                    title = "Circulares y Avisos Oficiales",
                    subtitle = "Comunicaciones de la administración del condominio"
                )
            }
        }

        items(announcements) { announcement ->
            AnnouncementCard(
                announcement = announcement,
                onMarkRead = { viewModel.markAnnouncementRead(announcement.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // UPCOMING BOOKINGS SECTION
        item {
            val upcoming = bookings.filter { it.status == BookingStatus.CONFIRMED || it.status == BookingStatus.PENDING }
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
                SectionHeader(
                    title = "Tus Reservas Próximas",
                    subtitle = "Acceso a áreas comunes",
                    actionText = "Ver Todas",
                    onActionClick = { onNavigateTab(2) }
                )

                if (upcoming.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardSurface)
                            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes reservas activas en este momento.",
                            fontSize = 13.sp,
                            color = EditorialMuted
                        )
                    }
                } else {
                    upcoming.take(2).forEach { booking ->
                        BookingItemSummary(
                            booking = booking,
                            onShowQr = { activeQrBooking = booking },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }

        // ACTIVE INCIDENTS PREVIEW
        item {
            val activeTickets = incidents.filter { it.status != IncidentStatus.RESOLVED }
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
                SectionHeader(
                    title = "Tickets de Mantenimiento",
                    subtitle = "Seguimiento a tus reportes",
                    actionText = "Reportar Falla",
                    onActionClick = { onNavigateTab(3) }
                )

                if (activeTickets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardSurface)
                            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay fallas reportadas activas en tu unidad.",
                            fontSize = 13.sp,
                            color = EditorialMuted
                        )
                    }
                } else {
                    activeTickets.take(2).forEach { incident ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
                                .clickable { onNavigateTab(3) },
                            colors = CardDefaults.cardColors(containerColor = CardSurface),
                            shape = RoundedCornerShape(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = incident.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = EditorialText,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusBadge(
                                        text = incident.status.labelEs,
                                        containerColor = EditorialHighlight,
                                        contentColor = BrandBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = incident.location,
                                    fontSize = 12.sp,
                                    color = EditorialMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // QR DIALOG
    activeQrBooking?.let { b ->
        QrCodePreviewModal(
            title = b.amenityName,
            code = b.qrCode,
            details = "${b.date} • Horario: ${b.timeSlot}",
            onDismiss = { activeQrBooking = null }
        )
    }
}

@Composable
fun QuickActionTile(
    icon: ImageVector,
    label: String,
    badge: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = BrandBlue,
                    modifier = Modifier.size(24.dp)
                )
                if (badge != null && badge != "0") {
                    Box(
                        modifier = Modifier
                            .offset(x = 12.dp, y = (-4).dp)
                            .background(StatusUrgent, RoundedCornerShape(10.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = EditorialText
            )
        }
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onMarkRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val categoryColor = when (announcement.category) {
        AnnouncementCategory.URGENTE -> StatusUrgent
        AnnouncementCategory.MAINTENANCE -> StatusPending
        AnnouncementCategory.EVENT -> BrandTeal
        AnnouncementCategory.INFO -> BrandBlue
    }

    Card(
        modifier = modifier
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
            .clickable {
                expanded = !expanded
                if (!announcement.isRead) onMarkRead()
            },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = announcement.category.labelEs,
                    containerColor = categoryColor.copy(alpha = 0.12f),
                    contentColor = categoryColor
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = announcement.date,
                        fontSize = 11.sp,
                        color = EditorialMuted
                    )
                    if (!announcement.isRead) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(StatusUrgent, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EditorialText
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = announcement.content,
                fontSize = 13.sp,
                color = EditorialMuted,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emitido por: ${announcement.author}",
                    fontSize = 11.sp,
                    color = EditorialMuted
                )
                Text(
                    text = if (expanded) "Mostrar menos" else "Leer completo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
            }
        }
    }
}

@Composable
fun BookingItemSummary(
    booking: Booking,
    onShowQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.amenityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EditorialText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${booking.date} • ${booking.timeSlot}",
                    fontSize = 12.sp,
                    color = EditorialMuted
                )
                Text(
                    text = "${booking.guestCount} invitados • Total: $%,.2f MXN".format(booking.totalCost),
                    fontSize = 11.sp,
                    color = BrandTeal,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onShowQr,
                shape = RoundedCornerShape(2.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                modifier = Modifier.testTag("booking_show_qr_${booking.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "QR",
                    modifier = Modifier.size(16.dp),
                    tint = BrandBlue
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("QR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
            }
        }
    }
}
