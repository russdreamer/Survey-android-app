package com.toolittlespot.survey

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.toolittlespot.survey.fragments.Questioning
import com.toolittlespot.survey.fragments.SurveyList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        requestPermission(this)
    }

    private fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun changeMainLayout(newLayout: Fragment, addToBackStack: Boolean = true) {
        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(
                R.id.mainFragment,
                newLayout
            )
        if (addToBackStack)
            transaction.addToBackStack(null)

        transaction.commit()
    }

    override fun onBackPressed() {
        if (this.supportFragmentManager.fragments.last() is Questioning
        && Questioning.resultAnswers.isNotEmpty()) {
            val dialog = Dialogs.createNegativeDialog(this, SURVEY_WILL_BE_LOST)
            dialog.findViewById<Button>(R.id.negative_dialog_btn).setOnClickListener {
                dialog.dismiss()
                Questioning.resultAnswers.clear()
                LastSurveyState.removeSurveyState(this)
                super.onBackPressed()
            }
            dialog.show()
        }
        else {
            super.onBackPressed()
            if (supportFragmentManager.backStackEntryCount == 0)
                super.onBackPressed()
        }
    }

    private fun requestPermission(context: Activity) {
        val hasPermission = (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                context,
                Array(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                REQUEST_WRITE_STORAGE
            )
        } else {
            startApplication()
        }
    }

    private fun startApplication(){
        val surveys = getApplicationFolder(SURVEY_FOLDER)
        val results = getApplicationFolder(RESULT_FOLDER)
        if (!surveys.exists()){
            surveys.mkdirs()
        }

        if (!results.exists())
            results.mkdirs()

        changeMainLayout(SurveyList())
        checkIsIncompleteSurvey()
    }

    private fun checkIsIncompleteSurvey() {
        val restoredState  = LastSurveyState.loadSurveyState(this)
        if (restoredState != null && restoredState.resultAnswers.isNotEmpty()){
            LastSurveyState.removeSurveyState(this)
            val dialog = Dialogs.createPositiveDialog(this, INCOMPLETE_SURVEY)
            dialog.findViewById<Button>(R.id.positive_dialog_btn).setOnClickListener {
                dialog.dismiss()
                val fragment = Questioning()
                fragment.passSurvey(restoredState.survey)
                fragment.passResultAnswers(restoredState.resultAnswers)
                changeMainLayout(fragment)
            }
            dialog.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startApplication()
                    Toast.makeText(this, PERMISSION_ACCEPT, Toast.LENGTH_LONG).show()
                    createTestSurvey()
                } else {
                    finishAffinity()
                }
            }
        }
    }

    private fun createTestSurvey() {
        saveSurvey(TEST_SURVEY_TEXT, TEST_SURVEY)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (this.supportFragmentManager.fragments.last() is Questioning) {
            checkPressedKey(keyCode)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun checkPressedKey(keyCode: Int) {
        var num: Int? = null

        when (keyCode){
            KeyEvent.KEYCODE_0 -> num = 0
            KeyEvent.KEYCODE_NUMPAD_0 -> num = 0
            KeyEvent.KEYCODE_1 -> num = 1
            KeyEvent.KEYCODE_NUMPAD_1 -> num = 1
            KeyEvent.KEYCODE_2 -> num = 2
            KeyEvent.KEYCODE_NUMPAD_2 -> num = 2
            KeyEvent.KEYCODE_3 -> num = 3
            KeyEvent.KEYCODE_NUMPAD_3 -> num = 3
            KeyEvent.KEYCODE_4 -> num = 4
            KeyEvent.KEYCODE_NUMPAD_4 -> num = 4
            KeyEvent.KEYCODE_5 -> num = 5
            KeyEvent.KEYCODE_NUMPAD_5 -> num = 5
            KeyEvent.KEYCODE_6 -> num = 6
            KeyEvent.KEYCODE_NUMPAD_6 -> num = 6
            KeyEvent.KEYCODE_7 -> num = 7
            KeyEvent.KEYCODE_NUMPAD_7 -> num = 7
            KeyEvent.KEYCODE_8 -> num = 8
            KeyEvent.KEYCODE_NUMPAD_8 -> num = 8
            KeyEvent.KEYCODE_9 -> num = 9
            KeyEvent.KEYCODE_NUMPAD_9 -> num = 9
        }

        if (num != null) {
            Questioning.number = Questioning.number.plus(num)
            checkNum()
        }
    }

    private fun checkNum() {
        if (Questioning.number.length == 3){
            (this.supportFragmentManager.fragments.last() as Questioning).checkAnswerIfExists(Questioning.number)
            Questioning.number = ""
        }
    }
}
