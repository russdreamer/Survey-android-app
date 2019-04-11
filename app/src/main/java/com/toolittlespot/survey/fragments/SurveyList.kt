package com.toolittlespot.survey.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.toolittlespot.survey.*
import java.io.File

class SurveyList : Fragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =  inflater.inflate(R.layout.fragment_survey_list, container, false)
        configViews()

        return fragmentView
    }

    private fun configViews() {
        fragmentView.findViewById<TextView>(R.id.survey_path_txt).text = getApplicationFolder(SURVEY_FOLDER).absolutePath
        configInfoBtn()
        configAddSurveyBtn()
        configSurveyList()
    }

    private fun configSurveyList() {
        val surveryList = getSurveyList()
        val listView = fragmentView.findViewById<LinearLayout>(R.id.survey_list)

        surveryList.forEach { file ->
            val child = layoutInflater.inflate(R.layout.survey_item, null)
            child.findViewById<TextView>(R.id.survey_name_txt).text = file.nameWithoutExtension
            setSurveyListener(child, file)
            setDeleteSurveyListener(child, file)

            listView.addView(child)
        }
    }

    private fun setDeleteSurveyListener(child: View, file: File) {
        child.findViewById<Button>(R.id.delete_survey_btn).setOnClickListener {
            val dialog = Dialogs.createNegativeDialog(context!!, DELETE_SURVEY_AND_RESULT)
            dialog.findViewById<Button>(R.id.negative_dialog_btn).setOnClickListener {
                dialog.dismiss()
                deleteSurveyAndResults(file)
            }
            dialog.show()
        }
    }

    private fun deleteSurveyAndResults(file: File) {
        val surveyDir = getApplicationFolder(RESULT_FOLDER)
        deleteFile(file.absoluteFile)
        deleteFile(surveyDir.resolve(file.name))
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
    }

    private fun setSurveyListener(child: View, file: File) {
        child.findViewById<TextView>(R.id.survey_name_txt).setOnClickListener {
            val survMenu = SurveyMenu()
            survMenu.passSurvey(file)
            (activity as MainActivity).changeMainLayout(survMenu)
        }
    }

    private fun getSurveyList(): ArrayList<File> {
        val list = arrayListOf<File>()
        val survDir = getApplicationFolder(SURVEY_FOLDER)
        for (fileEntry in survDir.listFiles()) {
            if (fileEntry.isFile && "txt".equals(fileEntry.extension.toLowerCase()) )
                list.add(fileEntry)
        }

        return list
    }

    private fun configAddSurveyBtn() {
        fragmentView.findViewById<Button>(R.id.add_survey_btn).setOnClickListener {
            (activity as MainActivity).changeMainLayout(CreatingSurvey())
        }
    }

    private fun configInfoBtn() {
        fragmentView.findViewById<Button>(R.id.info_btn).setOnClickListener {
            (activity as MainActivity).changeMainLayout(Info())
        }
    }





}
