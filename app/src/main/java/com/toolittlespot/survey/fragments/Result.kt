package com.toolittlespot.survey.fragments


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.toolittlespot.survey.*
import java.io.File


class Result : Fragment() {
    private lateinit var fragmentView: View
    private lateinit var survey: File
    private lateinit var surveyTxt: EditText
    private lateinit var resultCountTxt: TextView

    fun passSurvey(survey: File){
        this.survey = survey
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView = inflater.inflate(R.layout.fragment_result, container, false)
        configViews()

        return fragmentView
    }

    private fun configViews() {
        configSurveyTxt()
        configCopyBtn()
        configSaveBtn()
        configResultCountTxt()
    }

    private fun configResultCountTxt() {
        resultCountTxt = fragmentView.findViewById(R.id.count_result_txt)
        val lines = surveyTxt.text.toString().lines()
        val filledLines = removeEmptyLines(lines)
        resultCountTxt.text = filledLines.size.toString()
    }

    private fun removeEmptyLines(lines: List<String>): List<String> {
        val list = arrayListOf<String>()
        lines.forEach { line ->
            if (line.trim().isNotEmpty())
                list.add(line)
        }
        return list
    }

    private fun configSaveBtn() {
        fragmentView.findViewById<Button>(R.id.save_btn).setOnClickListener {
            saveResultFile(surveyTxt.text.toString(), survey.nameWithoutExtension)
            showSnackBar(fragmentView, "Результат сохранён!")
            (activity as MainActivity).onBackPressed()
        }
    }

    private fun configCopyBtn() {
        fragmentView.findViewById<Button>(R.id.copy_btn).setOnClickListener {
            val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("text", surveyTxt.text.toString())
            clipboard!!.primaryClip = clip
            showSnackBar(fragmentView, "Текст скопирован!")
        }
    }

    private fun configSurveyTxt() {
        val resDir = getApplicationFolder(RESULT_FOLDER)
        val resFile = resDir.resolve(survey.name)
        surveyTxt = fragmentView.findViewById(R.id.result_txt)
        surveyTxt.setText(readTextFile(resFile))
    }


}
