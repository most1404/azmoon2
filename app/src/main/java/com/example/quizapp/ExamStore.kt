package com.example.quizapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Question(val q: String, val a: String)
data class Exam(val title: String, val questions: List<Question>, val builtIn: Boolean = false)

/** ذخیره و بازیابی آزمون‌ها روی خود گوشی (آفلاین) */
object ExamStore {
    private const val PREFS = "exam_store"
    private const val KEY = "exams"

    /** آزمون‌های ذخیره‌شده توسط معلم + آزمون نمونه */
    fun loadAll(context: Context): List<Exam> = loadSaved(context) + loadSample(context)

    fun loadSaved(context: Context): List<Exam> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val result = mutableListOf<Exam>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val qArr = obj.getJSONArray("questions")
                val questions = mutableListOf<Question>()
                for (j in 0 until qArr.length()) {
                    val qObj = qArr.getJSONObject(j)
                    questions.add(Question(qObj.getString("q"), qObj.getString("a")))
                }
                result.add(Exam(obj.getString("title"), questions))
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** ذخیره آزمون؛ اگر هم‌نام وجود داشته باشد جایگزین می‌شود */
    fun save(context: Context, exam: Exam) {
        val list = loadSaved(context).filter { it.title != exam.title } + exam.copy(builtIn = false)
        write(context, list)
    }

    fun delete(context: Context, title: String) {
        write(context, loadSaved(context).filter { it.title != title })
    }

    private fun write(context: Context, list: List<Exam>) {
        val arr = JSONArray()
        for (exam in list) {
            val qArr = JSONArray()
            for (question in exam.questions) {
                qArr.put(JSONObject().put("q", question.q).put("a", question.a))
            }
            arr.put(JSONObject().put("title", exam.title).put("questions", qArr))
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, arr.toString()).apply()
    }

    /** آزمون نمونه از assets/questions.json (همهٔ سوال‌ها در یک آزمون) */
    private fun loadSample(context: Context): List<Exam> {
        return try {
            val text = context.assets.open("questions.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val lessons = JSONObject(text).getJSONArray("lessons")
            val questions = mutableListOf<Question>()
            for (i in 0 until lessons.length()) {
                val chapters = lessons.getJSONObject(i).getJSONArray("chapters")
                for (j in 0 until chapters.length()) {
                    val qs = chapters.getJSONObject(j).getJSONArray("questions")
                    for (k in 0 until qs.length()) {
                        val o = qs.getJSONObject(k)
                        questions.add(Question(o.getString("q"), o.getString("a")))
                    }
                }
            }
            if (questions.isEmpty()) emptyList()
            else listOf(Exam("آزمون نمونه", questions, builtIn = true))
        } catch (e: Exception) {
            emptyList()
        }
    }
}
