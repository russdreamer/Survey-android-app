package com.toolittlespot.survey

class QuestionBlock(
    val question: String,
    val answers: List<Answer>,
    val minAnswers: Int,
    val maxAnswers: Int,
    val comment: String
) {
    var currentChecked: Int = 0
        private set

    init {
        this.currentChecked = 0
    }

    fun uncheck() {
        this.currentChecked--
    }

    fun check() {
        this.currentChecked++
    }

    fun resetChecking() {
        this.currentChecked = 0
    }
}
