package com.toolittlespot.survey

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LastSurveyState {
    companion object {

        fun saveSurveyState(state: SurveyState, activity: Activity) {
            val prefs = activity.getSharedPreferences(SAVED_PREFERENCES, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            val json = Gson().toJson(state)
            editor.putString(SURVEY_STATE, json)
            editor.apply()
        }

        fun loadSurveyState(activity: Activity): SurveyState? {
            val prefs = activity.getSharedPreferences(SAVED_PREFERENCES, Context.MODE_PRIVATE)
            val json = prefs.getString(SURVEY_STATE, null)
            val type = object : TypeToken<SurveyState>() {}.type
            return Gson().fromJson(json, type)
        }

        fun removeSurveyState(activity: Activity) {
            activity.getSharedPreferences(SAVED_PREFERENCES, Context.MODE_PRIVATE).edit().clear().apply()
        }
    }
}