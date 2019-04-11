package com.toolittlespot.survey.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.toolittlespot.survey.MainActivity

import com.toolittlespot.survey.R

class SavedSurveyPage : Fragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_saved_survey_page, container, false)
        configViews()

        return fragmentView
    }

    private fun configViews() {
        configNewSurveyBtn()
        configBackToSurveyListBtn()
    }

    private fun configBackToSurveyListBtn() {
        fragmentView.findViewById<Button>(R.id.to_survey_list_btn).setOnClickListener {
            val fm = fragmentManager!!
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            (activity as MainActivity).changeMainLayout(SurveyList())
        }
    }

    private fun configNewSurveyBtn() {
        fragmentView.findViewById<Button>(R.id.new_survey_btn).setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }


}
