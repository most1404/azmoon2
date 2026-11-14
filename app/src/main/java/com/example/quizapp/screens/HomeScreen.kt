package com.example.quizapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.quizapp.Exam

/** صفحهٔ اصلی: فهرست آزمون‌ها */
@Composable
fun HomeScreen(
    exams: List<Exam>,
    onStart: (Exam) -> Unit,
    onTeacher: () -> Unit,
    onImport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onTeacher) { Text("👨‍🏫 پنل معلم") }
            TextButton(onClick = onImport) { Text("📥 دریافت آزمون") }
        }
        Spacer(Modifier.height(8.dp))
        Text("🎓 آزمون‌ساز", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("آزمون را انتخاب کنید", color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        exams.forEach { exam ->
            Button(
                onClick = { onStart(exam) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("${exam.title} (${exam.questions.size} سوال)")
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
