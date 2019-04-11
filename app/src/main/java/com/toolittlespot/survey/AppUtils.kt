package com.toolittlespot.survey

import android.content.Context
import android.os.Environment
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.PrintWriter

fun getApplicationFolder(folderName: String): File {
    return Environment.getExternalStorageDirectory().resolve(APP_FOLDER).resolve(folderName)
}

fun saveSurvey(text: String, surveyName: String){
    val surveyDir = getApplicationFolder(SURVEY_FOLDER).resolve(surveyName.plus(".txt"))
    PrintWriter(surveyDir).use { out -> out.println(text) }
}

fun saveResultFile(text: String, surveyName: String){
    val resDir = getApplicationFolder(RESULT_FOLDER).resolve(surveyName.plus(".txt"))
    PrintWriter(resDir).use { out -> out.println(text) }
}

fun appendResult(text: String, surveyName: String){
    val resDir = getApplicationFolder(RESULT_FOLDER).resolve(surveyName.plus(".txt"))
    FileOutputStream(resDir, true).bufferedWriter().use { writer -> writer.append(text)}
}

fun readTextFile(textFile: File):String{
    val fr = FileReader(textFile)
    return fr.readText().trim()
}

fun showSnackBar(view: View, text: String){
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        .setAction("Action", null).show()
}

fun showToast(context: Context, text: String){
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun deleteFile(file: File){
    if (file.exists()){
        file.delete()
    }
}