package com.example.quizapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapp.ExamStore
import com.example.quizapp.ExamText
import com.example.quizapp.ui.theme.GreenPrimary

/** دانش‌آموز متن آزمونِ ارسالی معلم را اینجا می‌چسباند */
@Composable
fun ImportScreen(onImported: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("→ بازگشت") }
        Text("دریافت آزمون", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "پیام آزمون را که معلم فرستاده کپی کنید و اینجا بچسبانید.",
            color = Color.Gray,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("متن آزمون") },
            minLines = 8,
            maxLines = 14,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val exam = ExamText.fromShareText(text)
                if (exam == null) {
                    Toast.makeText(context, "متن آزمون معتبر نیست", Toast.LENGTH_LONG).show()
                } else {
                    ExamStore.save(context, exam)
                    Toast.makeText(context, "آزمون «${exam.title}» ذخیره شد", Toast.LENGTH_SHORT).show()
                    onImported()
                }
            },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenPrimary,
                contentColor = Color.White
            )
        ) {
            Text("ذخیرهٔ آزمون", fontSize = 18.sp)
        }
    }
}
