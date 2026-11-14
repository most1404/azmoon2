package com.example.quizapp

/** قالب متنی برای ارسال آزمون به دانش‌آموزان و دریافت آن */
object ExamText {
    private const val TITLE_PREFIX = "آزمون:"
    private const val Q_MARK = "[سوالات]"
    private const val A_MARK = "[پاسخنامه]"

    fun toShareText(exam: Exam): String {
        val sb = StringBuilder()
        sb.append(TITLE_PREFIX).append(' ').append(exam.title).append("\n\n")
        sb.append(Q_MARK).append('\n')
        exam.questions.forEachIndexed { i, q -> sb.append(i + 1).append(". ").append(q.q).append('\n') }
        sb.append('\n').append(A_MARK).append('\n')
        exam.questions.forEachIndexed { i, q -> sb.append(i + 1).append(". ").append(q.a).append('\n') }
        return sb.toString()
    }

    /** اگر متن معتبر نباشد null برمی‌گرداند */
    fun fromShareText(text: String): Exam? {
        val qIdx = text.indexOf(Q_MARK)
        val aIdx = text.indexOf(A_MARK)
        if (qIdx < 0 || aIdx < 0 || aIdx < qIdx) return null

        val header = text.substring(0, qIdx)
        val titleLine = header.lines().firstOrNull { it.contains(TITLE_PREFIX) }
        val title = titleLine
            ?.substringAfter(TITLE_PREFIX)?.trim()
            ?.takeIf { it.isNotEmpty() } ?: "آزمون دریافتی"

        val questions = QuestionParser.parse(text.substring(qIdx + Q_MARK.length, aIdx))
        val answers = QuestionParser.parse(text.substring(aIdx + A_MARK.length))
        if (questions.isEmpty() || questions.size != answers.size) return null

        return Exam(title, questions.zip(answers) { q, a -> Question(q, a) })
    }
}
