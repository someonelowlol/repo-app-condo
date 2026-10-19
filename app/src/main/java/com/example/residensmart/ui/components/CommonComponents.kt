package com.example.residensmart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.residensmart.data.model.*
import com.example.residensmart.ui.theme.*

@Composable
fun CondoTopBar(
    resident: Resident,
    currentRole: UserRole,
    onRoleToggle: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardSurface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, EditorialBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(BrandBlue, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = "Logo",
                        tint = EditorialBg,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "RESIDENTSMART",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandBlue,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = if (currentRole == UserRole.ADMIN) "Consola Administrador" else "${resident.apartment} • ${resident.tower}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Role toggle chip
            FilterChip(
                selected = currentRole == UserRole.ADMIN,
                onClick = {
                    val nextRole = if (currentRole == UserRole.RESIDENT) UserRole.ADMIN else UserRole.RESIDENT
                    onRoleToggle(nextRole)
                },
                label = {
                    Text(
                        text = if (currentRole == UserRole.ADMIN) "MODO ADMIN" else "RESIDENTE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (currentRole == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        contentDescription = "Role Icon",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandBlue,
                    selectedLabelColor = EditorialBg,
                    selectedLeadingIconColor = EditorialBg,
                    containerColor = EditorialHighlight,
                    labelColor = EditorialText
                ),
                shape = RoundedCornerShape(2.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = currentRole == UserRole.ADMIN,
                    borderColor = EditorialBorder,
                    selectedBorderColor = BrandBlue
                ),
                modifier = Modifier.testTag("role_toggle_chip")
            )
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(2.dp))
            .border(1.dp, contentColor.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = contentColor
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = EditorialText
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialMuted
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.testTag("section_action_${title.lowercase().replace(" ", "_")}")
            ) {
                Text(
                    text = actionText.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandBlue
                )
            }
        }
    }
}

@Composable
fun QrCodePreviewModal(
    title: String,
    code: String,
    details: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pase de Acceso QR",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = EditorialText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = details,
                    fontSize = 13.sp,
                    color = EditorialMuted
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Simulated QR Block
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(EditorialHighlight, RoundedCornerShape(4.dp))
                        .border(2.dp, EditorialBorder, RoundedCornerShape(4.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "Código QR",
                        modifier = Modifier.size(140.dp),
                        tint = BrandBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTeal
                )
                Text(
                    text = "Presenta este código en la caseta de vigilancia o torniquete inteligente.",
                    fontSize = 11.sp,
                    color = EditorialMuted,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.testTag("qr_dialog_close")
            ) {
                Text("Listo", color = EditorialBg)
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(4.dp)
    )
}
