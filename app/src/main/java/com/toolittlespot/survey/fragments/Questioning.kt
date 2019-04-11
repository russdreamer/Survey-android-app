package com.toolittlespot.survey.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.toolittlespot.survey.*

class Questioning : Fragment() {
    companion object {
        var questionToScroll: Int? = null
        var number: String = ""
        val resultAnswers = hashSetOf<Int>()
    }

    private lateinit var fragmentView: View
    private lateinit var progressBar: ProgressBar
    private lateinit var survey: Survey
    private lateinit var surveyLayout: LinearLayout
    private lateinit var scroll: ScrollView
    private val checkBoxViews = hashMapOf<Int, CheckBoxWrapper>()
    private var isViewConfigured = false

    fun passSurvey(survey: Survey){
        this.survey = survey
    }

    fun passResultAnswers(answers: HashSet<Int>){
        resultAnswers.addAll(answers)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        number = ""
        questionToScroll = null
        //resultAnswers.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView = inflater.inflate(R.layout.fragment_questioning, container, false)
        survey.questions.forEach { question -> question.resetChecking() }
        configViews()
        isViewConfigured = true

        return fragmentView
    }

    private fun configViews() {
        fragmentView.findViewById<TextView>(R.id.header_txt).text = survey.header
        configSurveyLayout()
        configScrollView()
        configProgressBar()
        configRefreshBtn()
        configQuestionsBtn()
        configDoneBtn()
    }

    private fun configScrollView() {
        scroll = fragmentView.findViewById(R.id.scroll_view)
    }

    private fun configDoneBtn() {
        fragmentView.findViewById<Button>(R.id.done_btn).setOnClickListener {
            checkBlocksAndSave()
        }
    }

    private fun checkBlocksAndSave() {
        val wrongQuestions = arrayListOf<Int>()
        for (i in 1..survey.questions.size) {
            if (! isBlockCorrect(survey.questions[i-1]) ){
                wrongQuestions.add(i)
            }
        }

        if (wrongQuestions.isEmpty()) {
            saveResult()
            resultAnswers.clear()
            LastSurveyState.removeSurveyState(activity!!)
            (activity as MainActivity).onBackPressed()
            (activity as MainActivity).changeMainLayout(SavedSurveyPage())
        }
        else showToast(context!!, CORRECT_ANSWERS + wrongQuestions.joinToString())
    }

    private fun saveResult() {
        appendResult(resultAnswers.sorted()
            .joinToString(",", transform = {num-> String.format("%03d", num)}) + END_RESULT_FILE, survey.name)
    }

    private fun configProgressBar() {
        progressBar = fragmentView.findViewById(R.id.progress_bar)
        progressBar.progress = 0
    }

    private fun configQuestionsBtn() {
        fragmentView.findViewById<Button>(R.id.questions_btn).setOnClickListener {
            val fragment = Questions()
            fragment.passQuestions(survey.questions)
            (activity as MainActivity).changeMainLayout(fragment)
        }
    }

    private fun configRefreshBtn() {
        fragmentView.findViewById<Button>(R.id.refresh_btn).setOnClickListener {
            if (resultAnswers.isNotEmpty()){
                val dialog = Dialogs.createNegativeDialog(context!!, SURVEY_REFRESH)
                dialog.findViewById<Button>(R.id.negative_dialog_btn).setOnClickListener {
                    dialog.dismiss()
                    refreshSurvey()
                    LastSurveyState.removeSurveyState(activity!!)
                }
                dialog.show()
            }
        }
    }

    private fun refreshSurvey() {
        val ft = fragmentManager!!.beginTransaction()
        resultAnswers.clear()
        progressBar.progress = 0
        ft.detach(this).attach(this).commit()
    }

    private fun configQuestionBlocks() {
        survey.questions.forEach { questionBlock ->
            val questionLayer = layoutInflater.inflate(R.layout.question_item, null)
            questionLayer.findViewById<TextView>(R.id.question_txt).text = questionBlock.question
            questionLayer.findViewById<TextView>(R.id.comment_txt).text = questionBlock.comment
            val indicator = questionLayer.findViewById<TextView>(R.id.block_indicator)
            val answersList = questionLayer.findViewById<LinearLayout>(R.id.answers)

            questionBlock.answers.forEach { answer ->
                val element: View =
                    if (answer.number != 0)
                        getAnswerView(answer, questionBlock, indicator)
                    else getHeaderView(answer)

                answersList.addView(element)
            }
            updateProgress(questionBlock, indicator)
            surveyLayout.addView(questionLayer)
        }
    }

    private fun getHeaderView(answer: Answer): View {
        val headerLayer = layoutInflater.inflate(R.layout.header_answer_item, null)
        val header = headerLayer.findViewById<TextView>(R.id.header_txt)
        header.text = answer.text

        return headerLayer
    }

    private fun getAnswerView(answer: Answer, questionBlock: QuestionBlock, indicator: TextView): View {
        val answerLayer = layoutInflater.inflate(R.layout.answer_item, null)
        val checkBox = answerLayer.findViewById<CheckBox>(R.id.checkBox)
        checkBox.text = answer.text

        val checkBoxWr = CheckBoxWrapper(checkBox, questionBlock, indicator)
        checkBoxViews[answer.number] = checkBoxWr

        if (resultAnswers.contains(answer.number)){
            checkBox.isChecked = true
            clickOnCheckBox(answer.number)
        }

        checkBox.setOnClickListener {
            clickOnCheckBox(answer.number)
        }

        return answerLayer
    }

    private fun clickOnCheckBox(number: Int) {
        val checkBoxWr = checkBoxViews[number]
        if (checkBoxWr != null){
            if (checkBoxWr.checkBox.isChecked) {
                checkBoxWr.questionBlock.check()
                resultAnswers.add(number)
            }
            else {
                checkBoxWr.questionBlock.uncheck()
                resultAnswers.remove(number)
            }
            updateProgress(checkBoxWr.questionBlock, checkBoxWr.indicator)
            saveSurveyState()
        }
    }

    private fun updateProgress(questionBlock: QuestionBlock, indicator: TextView) {
        setBlockColor(questionBlock, indicator)
        updateProgressBar()
    }

    private fun saveSurveyState() {
        val state = SurveyState(survey, resultAnswers)
        LastSurveyState.saveSurveyState(state, activity!!)
    }

    private fun setBlockColor(questionBlock: QuestionBlock, indicator: TextView) {
        if (isBlockCorrect(questionBlock))
            indicator.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        else indicator.setBackgroundColor(resources.getColor(R.color.colorAccent))
    }

    private fun updateProgressBar() {
        val doneBlocks = countCorrectBlocks()
        progressBar.progress = 100 * doneBlocks / survey.questions.size
    }

    private fun countCorrectBlocks(): Int {
        var correctBlocks: Int = 0
        survey.questions.forEach { block->
            if (isBlockCorrect(block))
                correctBlocks++
        }
        return  correctBlocks
    }

    private fun isBlockCorrect(questionBlock: QuestionBlock): Boolean {
        return (questionBlock.currentChecked >= questionBlock.minAnswers
                && questionBlock.currentChecked <= questionBlock.maxAnswers)
    }

    private fun configSurveyLayout() {
        surveyLayout = fragmentView.findViewById(R.id.survey_layout)
    }

    override fun onResume() {
        super.onResume()
        if (!isViewConfigured){
            val ft = fragmentManager!!.beginTransaction()
            ft.detach(this).attach(this).commit()
        }

        configQuestionBlocks()
        scrollWindow()
        isViewConfigured = false
    }

    fun checkAnswerIfExists(number: String) {
        if (number.toInt() == 999){
            checkBlocksAndSave()
            return
        }
        val checkBoxWr = checkBoxViews[number.toInt()]
        if (checkBoxWr != null){
            checkBoxWr.checkBox.isChecked = checkBoxWr.checkBox.isChecked.not()
            clickOnCheckBox(number.toInt())
            showSnackBar(fragmentView, number)
        }
        else showSnackBar(fragmentView, "Ответа с номером $number не существует")
    }

    private fun scrollWindow() {
        val vto = scroll.viewTreeObserver
        vto.addOnGlobalLayoutListener {
            if (questionToScroll != null) {
                scroll.scrollTo(0, surveyLayout.getChildAt(questionToScroll!!).top + surveyLayout.top)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        questionToScroll = null
    }
}
