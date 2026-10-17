
package com.labsol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

private val Teal = Color(0xFF087F8C)
private val Background = Color(0xFFF4F8FA)
private val Ink = Color(0xFF18323D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LabSolApp()
        }
    }
}

enum class CalcType(val title: String) {
    MOLARITY("مولاریته"),
    DILUTION("رقیق‌سازی"),
    PREPARATION("تهیه محلول")
}

@Composable
fun LabSolApp() {
    var selected by remember {
        mutableStateOf(CalcType.MOLARITY)
    }

    var mass by remember { mutableStateOf("") }
    var mw by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }
    var c1 by remember { mutableStateOf("") }
    var c2 by remember { mutableStateOf("") }
    var v2 by remember { mutableStateOf("") }

    val result = calculate(
        selected,
        mass, mw, volume, c1, c2, v2
    )

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = Teal,
                background = Background,
                surface = Color.White
            )
        ) {
            Scaffold(
                containerColor = Background
            ) { padding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Header()

                    Text(
                        "نوع محاسبه",
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )

                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        CalcType.entries.forEach { type ->
                            FilterChip(
                                selected = selected == type,
                                onClick = {
                                    selected = type
                                },
                                label = {
                                    Text(type.title)
                                }
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "ورود اطلاعات",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Ink
                            )

                            when (selected) {
                                CalcType.MOLARITY -> {
                                    NumberField(
                                        "جرم ماده (g)",
                                        mass
                                    ) { mass = it }

                                    NumberField(
                                        "جرم مولی (g/mol)",
                                        mw
                                    ) { mw = it }

                                    NumberField(
                                        "حجم محلول (mL)",
                                        volume
                                    ) { volume = it }
                                }

                                CalcType.PREPARATION -> {
                                    NumberField(
                                        "مولاریته مورد نظر (mol/L)",
                                        c2
                                    ) { c2 = it }

                                    NumberField(
                                        "جرم مولی (g/mol)",
                                        mw
                                    ) { mw = it }

                                    NumberField(
                                        "حجم نهایی (mL)",
                                        v2
                                    ) { v2 = it }
                                }

                                CalcType.DILUTION -> {
                                    NumberField(
                                        "غلظت محلول مادر C1",
                                        c1
                                    ) { c1 = it }

                                    NumberField(
                                        "غلظت نهایی C2",
                                        c2
                                    ) { c2 = it }

                                    NumberField(
                                        "حجم نهایی (mL)",
                                        v2
                                    ) { v2 = it }
                                }
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F4F2)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "نتیجه محاسبه",
                                color = Teal,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                result,
                                color = Ink,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                formula(selected),
                                color = Color(0xFF47636B),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Text(
                        "LabSol • ابزار محاسبات آزمایشگاهی",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = Teal,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Science,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Column {
            Text(
                "LabSol",
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink
            )

            Text(
                "محاسبات محلول‌ها",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { text ->
            // پذیرش ارقام و اعشار؛ تبدیل ارقام فارسی
            val normalized = text
                .replace('۰', '0')
                .replace('۱', '1')
                .replace('۲', '2')
                .replace('۳', '3')
                .replace('۴', '4')
                .replace('۵', '5')
                .replace('۶', '6')
                .replace('۷', '7')
                .replace('۸', '8')
                .replace('۹', '9')
                .replace(',', '.')

            if (normalized.matches(
                    Regex("""\d*\.?\d*""")
                )
            ) {
                onValueChange(normalized)
            }
        },
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

fun number(value: String): Double? =
    value.toDoubleOrNull()?.takeIf {
        it.isFinite() && it > 0
    }

fun calculate(
    type: CalcType,
    mass: String,
    mw: String,
    volume: String,
    c1: String,
    c2: String,
    v2: String
): String {
    val result = when (type) {
        CalcType.MOLARITY -> {
            val m = number(mass)
            val w = number(mw)
            val v = number(volume)

            if (m != null && w != null && v != null)
                m / (w * (v / 1000.0))
            else null
        }

        CalcType.PREPARATION -> {
            val c = number(c2)
            val w = number(mw)
            val v = number(v2)

            if (c != null && w != null && v != null)
                c * w * (v / 1000.0)
            else null
        }

        CalcType.DILUTION -> {
            val source = number(c1)
            val target = number(c2)
            val finalVolume = number(v2)

            if (source != null && target != null &&
                finalVolume != null && target <= source
            ) {
                target * finalVolume / source
            } else null
        }
    }

    if (result == null) {
        return when (type) {
            CalcType.DILUTION ->
                "اطلاعات معتبر وارد کنید؛ C2 باید از C1 بیشتر نباشد."
            else ->
                "همه مقادیر را به‌صورت عدد مثبت وارد کنید."
        }
    }

    val formatted = String.format(
        Locale.US, "%.6g", result
    )

    return when (type) {
        CalcType.MOLARITY ->
            "$formatted mol/L"

        CalcType.PREPARATION ->
            "$formatted g ماده"

        CalcType.DILUTION ->
            "$formatted mL محلول مادر"
    }
}

fun formula(type: CalcType): String =
    when (type) {
        CalcType.MOLARITY ->
            "M = جرم ÷ (جرم مولی × حجم بر حسب لیتر)"

        CalcType.PREPARATION ->
            "جرم = M × جرم مولی × حجم بر حسب لیتر"

        CalcType.DILUTION ->
            "C1 × V1 = C2 × V2"
    }
