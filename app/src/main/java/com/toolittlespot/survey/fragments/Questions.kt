package com.toolittlespot.survey.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.toolittlespot.survey.MainActivity
import com.toolittlespot.survey.QuestionBlock

import com.toolittlespot.survey.R

class Questions : Fragment() {
    private lateinit var fragmentView: View
    private lateinit var questions: List<QuestionBlock>

    fun passQuestions(questions: List<QuestionBlock>){
        this.questions = questions
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_questions, container, false)
        configViews()

        return fragmentView
    }

    private fun configViews() {
        fragmentView.findViewById<TextView>(R.id.questions_amount_txt).text = questions.size.toString()
        fragmentView.findViewById<TextView>(R.id.done_questions_txt).text = countCorrectBlocks().toString()

        val questionsView = fragmentView.findViewById<LinearLayout>(R.id.questions_view)
        var blockNum = 0
        questions.forEach { block ->
            val questionsLayer = layoutInflater.inflate(R.layout.qustion_descriptoin_item, null)
            val question = questionsLayer.findViewById<TextView>(R.id.question_txt)
            question.text = block.question
            if (isBlockCorrect(block))
                question.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            else question.setTextColor(resources.getColor(R.color.default_text_color))
            val num = blockNum

            questionsLayer.setOnClickListener {
                Questioning.questionToScroll = num
                (activity as MainActivity).onBackPressed()
            }

            questionsView.addView(questionsLayer)
            blockNum++
        }
    }

    private fun isBlockCorrect(questionBlock: QuestionBlock): Boolean {
        return (questionBlock.currentChecked >= questionBlock.minAnswers
                && questionBlock.currentChecked <= questionBlock.maxAnswers)
    }

    private fun countCorrectBlocks(): Int {
        var correctBlocks: Int = 0
        questions.forEach { block->
            if (isBlockCorrect(block))
                correctBlocks++
        }
        return  correctBlocks
    }
}
