package com.toolittlespot.survey.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.toolittlespot.survey.*
import java.io.File
import java.util.*


class CreatingSurvey : Fragment() {
    private lateinit var fragmentView: View
    private lateinit var surveyContentTxt: EditText
    private lateinit var surveyNameTxt: EditText
    var survey: File? = null

    fun passFile(survey: File){
        this.survey = survey
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =  inflater.inflate(R.layout.fragment_creating_survey, container, false)
        configViews()

        return fragmentView
    }

    private fun configViews() {
        configSurveyContentTxt()
        configSurveyNameTxt()
        configCreateInfoTxt()
        configSaveBtn()
    }

    private fun configSurveyNameTxt() {
        surveyNameTxt = fragmentView.findViewById(R.id.survey_name_txt)
        if (survey != null)
            surveyNameTxt.setText(survey!!.nameWithoutExtension)
    }

    private fun configSurveyContentTxt() {
        surveyContentTxt = fragmentView.findViewById(R.id.new_survey_txt)
        if (survey != null)
            surveyContentTxt.setText(readTextFile(survey!!))
    }

    private fun configSaveBtn() {
        fragmentView.findViewById<Button>(R.id.save_survey_btn).setOnClickListener {
            if (survey == null)
                checkAndSave()
            else saveNewSurvey()
        }
    }

    private fun checkAndSave() {
        val name = surveyNameTxt.text.toString()
        if (getApplicationFolder(SURVEY_FOLDER).resolve(name.plus(".txt")).exists()) {
            val dialog = Dialogs.createNegativeDialog(context!!, SURVEY_EXISTS)
            dialog.findViewById<Button>(R.id.negative_dialog_btn).setOnClickListener {
                dialog.dismiss()
                saveNewSurvey()
                showSnackBar(fragmentView, "Опрос сохранён")
            }
            dialog.show()
        }
        else {
            saveNewSurvey()
        }
    }

    private fun saveNewSurvey() {
        var name = surveyNameTxt.text.toString()
        if (name.isEmpty()){
            name = Date().toString()
        }
        saveSurvey(surveyContentTxt.text.toString(), name)
        (activity as MainActivity).onBackPressed()
    }

    private fun configCreateInfoTxt() {
        val createInfo = fragmentView.findViewById<TextView>(R.id.how_create_txt)
        val content = SpannableString(CREATE_INFO)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        createInfo.text = content

        createInfo.setOnClickListener {
            (activity as MainActivity).changeMainLayout(Info())
        }
    }


}
