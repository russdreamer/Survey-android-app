package com.toolittlespot.survey.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.toolittlespot.survey.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.text.SpannableString
import android.text.style.UnderlineSpan


class Info : Fragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_info, container, false)
        configViews()

        return fragmentView
    }

    private fun configViews() {
        fragmentView.findViewById<TextView>(R.id.how_create_survey_txt).text = HOW_CREATE_SURVEY
        fragmentView.findViewById<TextView>(R.id.where_surveys_store_txt).text = getApplicationFolder(SURVEY_FOLDER).absolutePath
        fragmentView.findViewById<TextView>(R.id.where_results_store_txt).text = getApplicationFolder(RESULT_FOLDER).absolutePath
        fragmentView.findViewById<TextView>(R.id.keyboard_input_txt).text = KEYBOARD_INPUT_INFO
        fragmentView.findViewById<TextView>(R.id.app_version_txt).text = APPLICATION_VERSION
        configAuthorInfo()
    }

    private fun configAuthorInfo() {
        fragmentView.findViewById<TextView>(R.id.author_git_txt).text = AUTHOR_GIT
        val mail = fragmentView.findViewById<TextView>(R.id.author_mail_txt)

        val content = SpannableString(AUTHOR_MAIL)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        mail.text = content

        mail.setOnClickListener {
            val clipboard = activity!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("e-mail", AUTHOR_MAIL)
            clipboard!!.primaryClip = clip
            showSnackBar(fragmentView, "E-mail скопирован!")
        }
    }


}
