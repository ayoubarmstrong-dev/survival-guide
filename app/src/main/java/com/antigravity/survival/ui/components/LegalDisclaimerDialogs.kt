package com.antigravity.survival.ui.components

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.antigravity.survival.ui.theme.*

const val PRIVACY_POLICY_URL = "https://ayoubarmstrong-dev.github.io/survival-guide/privacy-policy.html"
private const val PREFS_NAME = "aegis_survival_prefs"
private const val KEY_DISCLAIMER_ACCEPTED = "disclaimer_accepted"

/**
 * Checks if user has accepted the first-launch emergency disclaimer.
 */
fun hasAcceptedDisclaimer(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_DISCLAIMER_ACCEPTED, false)
}

/**
 * Sets disclaimer acceptance state.
 */
fun setDisclaimerAccepted(context: Context, accepted: Boolean = true) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_DISCLAIMER_ACCEPTED, accepted).apply()
}

/**
 * First-Launch Non-Dismissible Emergency & Medical Disclaimer Dialog.
 * Complies strictly with Google Play Medical & Emergency app policies.
 */
@Composable
fun FirstLaunchDisclaimerDialog(
    onAccepted: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { /* Non-dismissible on first launch */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(HazardAmber)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(HazardAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Medical Alert",
                        tint = HazardAmber,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "EMERGENCY & MEDICAL NOTICE",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = HazardAmber,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "Aegis Survival • Educational Preparedness Field Manual",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCard)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "1. Non-Medical Professional Disclaimer:\n" +
                                "All content within Aegis Survival—including Tactical Combat Casualty Care (TCCC) hemorrhage control, potassium iodide (KI) dosages, radiation decay rules, and water filtration ratios—is compiled strictly for educational reference and civil emergency preparedness.\n\n" +
                                "2. Not a Substitute for Emergency Services:\n" +
                                "This application is NOT a substitute for licensed medical practitioners, emergency trauma surgeons, 911/emergency dispatch, or local civil defense authorities. Always seek qualified medical guidance and obey official evacuation orders.\n\n" +
                                "3. Non-Governmental Affiliation:\n" +
                                "Citations to FEMA, TCCC, CDC, or WHO doctrines represent public educational standards and do NOT imply official government sponsorship, affiliation, or endorsement.\n\n" +
                                "4. Physical Hardware & Purchases:\n" +
                                "Hardware recommendations link to external Amazon listings. Aegis Survival participates in the Amazon Associates program.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        setDisclaimerAccepted(context, true)
                        onAccepted()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "I Understand & Acknowledge",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AmoledBlack
                    )
                }
            }
        }
    }
}

/**
 * On-demand Legal, Medical & Privacy Policy Dialog accessible anytime from UI.
 */
@Composable
fun LegalAndPrivacyDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = TacticalCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Legal, Privacy & Doctrine",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "App Title: Aegis Survival (v1.0.0)\n" +
                                "Architecture: 100% Offline-First Tactical Field Guide\n\n" +
                                "Medical Doctrine Notice:\n" +
                                "TCCC and radiation protocols are provided for preparedness and civilian education only. They do not constitute formal medical diagnosis or prescription.\n\n" +
                                "Affiliate & Outbound Disclosure:\n" +
                                "As an Amazon Associate, Aegis Survival earns from qualifying purchases. Outbound links open via external Custom Tabs or browser.\n\n" +
                                "Data Safety & Privacy Summary:\n" +
                                "• Zero user location tracked\n" +
                                "• Zero personal identifiers collected\n" +
                                "• Network permissions used solely for optional live catalog sync\n" +
                                "• Offline cache never transmitted externally",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action: Open Official Privacy Policy Webpage
                OutlinedButton(
                    onClick = {
                        openPrivacyPolicy(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TacticalCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "View Online Privacy Policy", fontSize = 12.5.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalOlive)
                ) {
                    Text(text = "Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Launches the hosted Privacy Policy via Chrome Custom Tabs.
 */
fun openPrivacyPolicy(context: Context) {
    try {
        val uri = Uri.parse(PRIVACY_POLICY_URL)
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(context, uri)
    } catch (_: Exception) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
        context.startActivity(intent)
    }
}
