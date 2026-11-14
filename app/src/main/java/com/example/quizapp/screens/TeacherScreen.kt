package com.example.quizapp.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapp.Exam
import com.example.quizapp.ExamStore
import com.example.quizapp.ExamText
import com.example.quizapp.Question
import com.example.quizapp.QuestionParser
import com.example.quizapp.ui.theme.GreenPrimary

/** پنل معلم: وارد کردن یکجای سوال‌ها و پاسخ‌ها */
@Composable
fun TeacherScreen(exams: List<Exam>, onChanged: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var questionsText by remember { mutableStateOf("") }
    var answersText by remember { mutableStateOf("") }
    var examToDelete by remember { mutableStateOf<Exam?>(null) }

    val questions = QuestionParser.parse(questionsText)
    val answers = QuestionParser.parse(answersText)
    val countsMatch = questions.size == answers.size
    val canSave = title.isNotBlank() && questions.isNotEmpty() && countsMatch

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("→ بازگشت") }
        Text("پنل معلم", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "سوال‌ها را یکجا در کادر اول و پاسخ‌ها را یکجا در کادر دوم بچسبانید. " +
                "هر مورد را با شماره شروع کنید (مثل ۱. یا 2) ) یا با یک خط خالی جدا کنید. " +
                "پاسخ شمارهٔ ۱ مربوط به سوال شمارهٔ ۱ است.",
            color = Color.Gray,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("عنوان آزمون (مثلاً ریاضی - فصل ۲)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = questionsText,
            onValueChange = { questionsText = it },
            label = { Text("همهٔ سوال‌ها") },
            minLines = 6,
            maxLines = 12,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = answersText,
            onValueChange = { answersText = it },
            label = { Text("همهٔ پاسخ‌ها") },
            minLines = 6,
            maxLines = 12,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Text(
            "تعداد سوال‌ها: ${questions.size} | تعداد پاسخ‌ها: ${answers.size}",
            color = if (countsMatch) GreenPrimary else Color(0xFFC62828),
            fontSize = 14.sp
        )
        if (!countsMatch) {
            Text("تعداد سوال‌ها و پاسخ‌ها باید برابر باشد.", color = Color(0xFFC62828), fontSize = 13.sp)
        }
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val exam = Exam(
                    title = title.trim(),
                    questions = questions.zip(answers) { q, a -> Question(q, a) }
                )
                ExamStore.save(context, exam)
                Toast.makeText(context, "آزمون ذخیره شد", Toast.LENGTH_SHORT).show()
                title = ""
                questionsText = ""
                answersText = ""
                onChanged()
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenPrimary,
                contentColor = Color.White
            )
        ) {
            Text("💾 ذخیرهٔ آزمون", fontSize = 18.sp)
        }

        // فهرست آزمون‌های ذخیره‌شده
        val saved = exams.filter { !it.builtIn }
        if (saved.isNotEmpty()) {
            Spacer(Modifier.height(28.dp))
            Text("آزمون‌های ذخیره‌شده", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            saved.forEach { exam ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${exam.title} (${exam.questions.size})",
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, ExamText.toShareText(exam))
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "ارسال آزمون به دانش‌آموزان"))
                    }) { Text("📤 ارسال") }
                    TextButton(onClick = { examToDelete = exam }) { Text("🗑 حذف") }
                }
            }
        }
    }

    val target = examToDelete
    if (target != null) {
        AlertDialog(
            onDismissRequest = { examToDelete = null },
            title = { Text("حذف آزمون") },
            text = { Text("آزمون «${target.title}» حذف شود؟") },
            confirmButton = {
                TextButton(onClick = {
                    ExamStore.delete(context, target.title)
                    examToDelete = null
                    onChanged()
                }) { Text("حذف") }
            },
            dismissButton = {
                TextButton(onClick = { examToDelete = null }) { Text("انصراف") }
            }
        )
    }
}
