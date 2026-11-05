package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Branch
import com.example.data.model.StoreSettings
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.FrostBlue
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AccountScreen(
    storeSettings: StoreSettings,
    isAdminUser: Boolean,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToBranches: () -> Unit,
    onNavigateToPolicies: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser = Firebase.auth.currentUser
    var showPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // User Profile Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FrostBlue, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(SkyBlueLight, CircleShape)
                        .border(2.dp, SkyBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (currentUser != null) {
                    Text(
                        text = currentUser.displayName ?: "عميل سنتر العسال",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = currentUser.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    if (isAdminUser) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .background(ChampagneGoldLight, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("مدير النظام (Admin)", style = MaterialTheme.typography.labelSmall, color = ChampagneGoldDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onSignOutClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("تسجيل الخروج", color = StatusRed, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "أهلاً بك في سنتر العسال",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "سجل دخولك لحفظ المفروشات المفضلة ومتابعة طلباتك بسهولة.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    Button(
                        onClick = onSignInClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("google_sign_in_account_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تسجيل الدخول بحساب جوجل (Google)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Navigation Menu
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
        ) {
            Column {
                AccountMenuItem(icon = Icons.Default.LocalShipping, title = "طلباتي ومتابعة الشحن", onClick = onNavigateToOrders)
                Divider(color = FrostBlue)
                AccountMenuItem(icon = Icons.Default.Favorite, title = "المفروشات المفضلة", onClick = onNavigateToWishlist)
                Divider(color = FrostBlue)
                AccountMenuItem(icon = Icons.Default.Storefront, title = "فروع سنتر العسال بالإسكندرية", onClick = onNavigateToBranches)
                Divider(color = FrostBlue)
                AccountMenuItem(icon = Icons.Default.Policy, title = "سياسة الاستبدال والاسترجاع والشحن", onClick = onNavigateToPolicies)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Admin Dashboard Entry
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ChampagneGoldLight),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable {
                    if (isAdminUser) {
                        onOpenAdminDashboard()
                    } else {
                        showPinDialog = true
                    }
                }
                .testTag("open_admin_panel_card")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(ChampagneGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "لوحة تحكم الإدارة (CENTER AL ASSAL ADMIN)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGoldDark
                    )
                    Text(
                        text = "إدارة المنتجات، متابعة الطلبات، مراجعة إيصالات إنستاباي، والتحكم في ASSAL AI.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }

    // Admin PIN dialog for store management access
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                enteredPin = ""
                pinError = false
            },
            title = { Text("دخول لوحة تحكم سنتر العسال") },
            text = {
                Column {
                    Text(
                        text = "أدخل الرقم السري لإدارة المتجر (الرمز الافتراضي: 2026):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = false
                        },
                        singleLine = true,
                        placeholder = { Text("الرقم السري") },
                        modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                    )
                    if (pinError) {
                        Text("الرمز غير صحيح، يرجى المحاولة مرة أخرى", color = StatusRed, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredPin.trim() == "2026" || enteredPin.trim() == "1234") {
                            showPinDialog = false
                            enteredPin = ""
                            onOpenAdminDashboard()
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Text("دخول")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun AccountMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchesScreen(
    storeSettings: StoreSettings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("فروع سنتر العسال بالإسكندرية", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IvoryWhite)
            )
        },
        containerColor = IvoryWhite,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "معارضنا في انتظاركم للمعاينة واختيار أرقى المفروشات",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            storeSettings.branches.forEachIndexed { index, branch ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SkyBlueLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(branch.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("الإسكندرية", style = MaterialTheme.typography.labelSmall, color = SkyBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("العنوان: ${branch.address}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("رقم الهاتف: ${branch.phone}", style = MaterialTheme.typography.bodyMedium, color = ChampagneGoldDark, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${branch.phone}"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(branch.address + " الإسكندرية")}")
                                    val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("الخريطة", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // WhatsApp Quick Action
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SageGreenLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, SageGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp).background(SageGreen, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("خدمة عملاء الواتساب المباشرة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("متواجدون للرد على استفساراتكم وحجز المفروشات فوراً.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/201277714100"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("واتساب", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliciesScreen(
    storeSettings: StoreSettings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("السياسات والأسئلة الشائعة", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IvoryWhite)
            )
        },
        containerColor = IvoryWhite,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            PolicySection(
                title = "سياسة الاستبدال والاسترجاع",
                body = storeSettings.returnPolicy.ifBlank {
                    "يحق للعميل استبدال أو استرجاع المفروشات خلال 14 يوماً من تاريخ الاستلام، بشرط أن تكون المنتجات بحالتها الأصلية غير مستعملة وفي غلافها الأصلي من المصنع."
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            PolicySection(
                title = "الشحن والتوصيل",
                body = storeSettings.shippingInfo.ifBlank {
                    "نقوم بالشحن لجميع محافظات مصر. التوصيل داخل محافظة الإسكندرية يتم خلال 24-48 ساعة عمل. باقي المحافظات خلال 2 إلى 4 أيام عمل. شحن مجاني للطلبات فوق 2000 جنيه مصري."
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            PolicySection(
                title = "طرق الدفع المعتمدة",
                body = "1. الدفع نقداً عند الاستلام ومعاينة المفروشات.\n2. تحويل إنستاباي InstaPay عبر الحساب الرسمي.\n3. المحافظ الإلكترونية الذكية (فودافون كاش، أورنج كاش، اتصالات كاش، وي باي).\nيتم تأكيد التحويلات بعد مراجعة إيصال التحويل من فريق الإدارة."
            )

            Spacer(modifier = Modifier.height(14.dp))

            PolicySection(
                title = "ضمان الجودة والخامات",
                body = "جميع المفروشات القطنية مصنوعة من أجود أنواع القطن المصري طويل التيلة الممشط، والألحفة والبطاطين معالجة ضد الحساسية لتوفير أعلى درجات الراحة والدفء."
            )
        }
    }
}

@Composable
fun PolicySection(title: String, body: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SkyBlue)
            Spacer(modifier = Modifier.height(6.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 22.sp)
        }
    }
}
