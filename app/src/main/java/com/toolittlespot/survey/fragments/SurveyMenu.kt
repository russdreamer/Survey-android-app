package com.toolittlespot.survey.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.toolittlespot.survey.*

import java.io.File
import java.util.regex.Pattern

class SurveyMenu : Fragment() {
    private lateinit var fragmentView: View
    private lateinit var survey: File

    fun passSurvey(survey: File){
        this.survey = survey
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView = inflater.inflate(R.layout.fragment_survey_menu, container, false)
        createResultFile()
        configViews()

        return fragmentView
    }

    private fun configViews() {
        configNewSurveyBtn()
        configResultsBtn()
        configEditBtn()
    }

    private fun configEditBtn() {
        fragmentView.findViewById<Button>(R.id.edit_survey_btn).setOnClickListener {
            val fragment = CreatingSurvey()
            fragment.passFile(survey)
            (activity as MainActivity).changeMainLayout(fragment)
        }
    }

    private fun configNewSurveyBtn() {
        fragmentView.findViewById<Button>(R.id.new_survey_btn).setOnClickListener {
            val surveyInst = extractSurveyInst(readTextFile(survey))

            if (surveyInst != null){
                val questioning = Questioning()
                questioning.passSurvey(surveyInst)
                (activity as MainActivity).changeMainLayout(questioning)
            }
        }
    }

    private fun configResultsBtn() {
        fragmentView.findViewById<Button>(R.id.results_btn).setOnClickListener {
            val result = Result()
            result.passSurvey(survey)
            (activity as MainActivity).changeMainLayout(result)
        }
    }

    private fun createResultFile() {
        val resDir = getApplicationFolder(RESULT_FOLDER)
        val resFile = resDir.resolve(survey.name)
        if (! resFile.exists())
            resFile.createNewFile()
    }

    private fun extractSurveyInst(surveyText: String): Survey?{
        val questionBlockRegex = Pattern.compile("(?s)\\#{2}(.*?)\\#{2}")
        val headerRegex = Pattern.compile("(?s)\\+{2}(.*)\\+{2}")
        val questionRegex = Pattern.compile("(?s)^\\.*(.*?)\\:{2}")
        val valuesRegex = Pattern.compile("(?s)\\:{2}(.*?)\\:{2}")
        val commentRegex = Pattern.compile("(?s)\\!{2}(.*?)\\!{2}")
        val answerRegex = Pattern.compile("([0-9]{3}.*-{1}.*)")
        val answerNumRegex = Pattern.compile("([0-9]{3}).*-{1}.*")
        val headerAnswerRegex = Pattern.compile("[0-9]{3}.*-{1}(.*)")

        val headerList =  getElementByRegex(surveyText, headerRegex)
        val header: String = headerList.getOrElse(0){""}

        val questionBlocks = ArrayList<QuestionBlock> ()
        val questionBlockList = getElementByRegex(surveyText, questionBlockRegex)

        if (questionBlockList.isEmpty()){
            showToast(context!!, BLOCK_NOT_FOUND)
            return null
        }

        questionBlockList.forEach {
            val questions = getElementByRegex(it, questionRegex)
            val answers = getElementByRegex(it, answerRegex)
            val comments = getElementByRegex(it, commentRegex)
            val values = getElementByRegex(it, valuesRegex)

            val wrongElements = getWrongElements(questions, answers, values)
            if (wrongElements.isNotEmpty()){
                showToast(context!!, "Не найден обязательный элемент ${wrongElements.joinToString(", ")} в блоке:\n\n $it")
                return null
            }

            val minAndMax = values.first().split(",")
            val min = Integer.valueOf(minAndMax[0])
            val max = Integer.valueOf(minAndMax[1])

            val answerList = ArrayList<Answer>()

            answers.forEach { answerStr ->
                var answerText: String = answerStr
                val answerNums = getElementByRegex(answerStr, answerNumRegex)
                val num = Integer.valueOf(answerNums.first())

                if (num == 0)
                    answerText = getElementByRegex(answerStr, headerAnswerRegex).first()

                answerList.add(Answer(num, answerText))
            }

            val questionBlock = QuestionBlock(questions.first(), answerList, min, max, comments.getOrElse(0) {""})
            questionBlocks.add(questionBlock)
        }

        return Survey(survey.nameWithoutExtension, header, questionBlocks)
    }

    private fun getWrongElements(questions: List<String>, answers: List<String>, values: List<String>): List<String> {
        val wrongElements = arrayListOf<String>()

        if (questions.isEmpty())
            wrongElements.add("вопроса")

        if (answers.isEmpty())
            wrongElements.add("ответов")

        if (values.isEmpty())
            wrongElements.add("минимального и максимальных значений")

        return wrongElements
    }

    private fun getElementByRegex(text: String, regex: Pattern): List<String> {
        val list = ArrayList<String>()
        val matcher = regex.matcher(text)
        while (matcher.find()){
            if (matcher.group(1).trim().isNotEmpty())
                list.add(matcher.group(1))
        }
        return list
    }
}
