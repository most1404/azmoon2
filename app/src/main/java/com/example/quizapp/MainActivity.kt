package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.example.quizapp.screens.HomeScreen
import com.example.quizapp.screens.ImportScreen
import com.example.quizapp.screens.QuizScreen
import com.example.quizapp.screens.SetupScreen
import com.example.quizapp.screens.TeacherScreen
import com.example.quizapp.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            QuizAppTheme {
                // اجبار RTL برای کل رابط کاربری
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AppRoot()
                }
            }
        }
    }
}

/** صفحه‌های اپ */
sealed interface Screen {
    object Home : Screen
    object Teacher : Screen
    object Import : Screen
    data class Setup(val exam: Exam) : Screen
    data class Quiz(val exam: Exam, val count: Int) : Screen
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    var exams by remember { mutableStateOf(ExamStore.loadAll(context)) }
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    fun reload() {
        exams = ExamStore.loadAll(context)
    }

    BackHandler(enabled = screen !is Screen.Home) {
        val current = screen
        screen = if (current is Screen.Quiz) Screen.Setup(current.exam) else Screen.Home
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            when (val s = screen) {
                is Screen.Home -> HomeScreen(
                    exams = exams,
                    onStart = { screen = Screen.Setup(it) },
                    onTeacher = { screen = Screen.Teacher },
                    onImport = { screen = Screen.Import }
                )
                is Screen.Teacher -> TeacherScreen(
                    exams = exams,
                    onChanged = { reload() },
                    onBack = { screen = Screen.Home }
                )
                is Screen.Import -> ImportScreen(
                    onImported = {
                        reload()
                        screen = Screen.Home
                    },
                    onBack = { screen = Screen.Home }
                )
                is Screen.Setup -> SetupScreen(
                    exam = s.exam,
                    onStart = { count -> screen = Screen.Quiz(s.exam, count) },
                    onBack = { screen = Screen.Home }
                )
                is Screen.Quiz -> QuizScreen(
                    exam = s.exam,
                    count = s.count,
                    onFinish = { screen = Screen.Home }
                )
            }
        }
    }
}
