package com.example.residensmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.residensmart.data.model.StaffContact
import com.example.residensmart.data.model.UserRole
import com.example.residensmart.ui.components.CondoTopBar
import com.example.residensmart.ui.theme.*
import com.example.residensmart.ui.viewmodel.CondoViewModel

@Composable
fun DirectoryScreen(
    viewModel: CondoViewModel,
    modifier: Modifier = Modifier
) {
    val resident by viewModel.resident.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val staffContacts = viewModel.staffContacts

    var simulatedNotice by remember { mutableStateOf<String?>(null) }

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
                    text = "Directorio y Contactos Clave",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = EditorialText
                )
                Text(
                    text = "Líneas de comunicación directa con administración, caseta de vigilancia y servicios de emergencia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialMuted
                )
            }
        }

        // EMERGENCY CALL-OUT
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, StatusUrgent.copy(alpha = 0.4f), RoundedCornerShape(2.dp)),
                colors = CardDefaults.cardColors(containerColor = StatusUrgent.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = StatusUrgent,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "CENTRAL DE EMERGENCIAS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = StatusUrgent
                            )
                            Text(
                                text = "Línea Nacional 911 • Bomberos 068",
                                fontSize = 12.sp,
                                color = EditorialText
                            )
                        }
                    }

                    Button(
                        onClick = { simulatedNotice = "Llamando a la Central de Emergencias 911..." },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusUrgent),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.testTag("call_911_btn")
                    ) {
                        Text("LLAMAR 911", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
                    }
                }
            }
        }

        // STAFF CONTACTS LIST
        items(staffContacts) { contact ->
            StaffContactCard(
                contact = contact,
                onCall = { simulatedNotice = "Iniciando llamada con ${contact.name} (${contact.phone})" },
                onEmail = { simulatedNotice = "Abriendo correo para ${contact.email}" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // REGULATIONS & CONDO RULES ACCORDION
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
                Text(
                    text = "Reglamento Interno Esencial",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = EditorialText
                )
                Spacer(modifier = Modifier.height(8.dp))

                val rules = listOf(
                    "Horario de Silencio: De domingo a jueves a partir de las 22:00 hrs. Viernes y sábados a partir de las 00:00 hrs.",
                    "Mascotas: Uso obligatorio de correa en todas las áreas comunes. Recoger heces inmediatamente.",
                    "Estacionamiento: Respetar exclusivamente el cajón asignado a tu unidad privativa.",
                    "Mudanzas y Trabajos Ruidosos: Únicamente permitidos de lunes a viernes de 09:00 a 18:00 hrs y sábados de 09:00 a 14:00 hrs con previo aviso a caseta."
                )

                rules.forEach { rule ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = BrandTeal,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = rule, fontSize = 12.sp, color = EditorialText, lineHeight = 17.sp)
                        }
                    }
                }
            }
        }
    }

    // NOTICE SNACKBAR / TOAST SIMULATION
    simulatedNotice?.let { msg ->
        AlertDialog(
            onDismissRequest = { simulatedNotice = null },
            title = {
                Text(
                    text = "Acción de Teléfono / Mensajería",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
            },
            text = { Text(text = msg, fontSize = 13.sp, color = EditorialText) },
            confirmButton = {
                Button(
                    onClick = { simulatedNotice = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text("Entendido", color = EditorialBg)
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(4.dp)
        )
    }
}

@Composable
fun StaffContactCard(
    contact: StaffContact,
    onCall: () -> Unit,
    onEmail: () -> Unit,
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.role.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contact.name,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EditorialText
                    )
                    Text(
                        text = "Horario: ${contact.hours}",
                        fontSize = 11.sp,
                        color = EditorialMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = EditorialBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_${contact.id}")
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = contact.phone, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                }

                OutlinedButton(
                    onClick = onEmail,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("email_${contact.id}")
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = EditorialMuted)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Correo", fontSize = 11.sp, color = EditorialText)
                }
            }
        }
    }
}
