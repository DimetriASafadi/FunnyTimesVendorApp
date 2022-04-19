package com.example.funnytimesvendorapp.CommonSection

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.example.funnytimesvendorapp.CommonSection.Constants.AppSPName
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyAppLanguage
import com.example.funnytimesvendorapp.MainMenu
import com.example.funnytimesvendorapp.R
import java.io.File
import java.util.*

class CommonFuncs {

    val SPName:String = AppSPName
    var loadingDia: Dialog? = null
    var codePhone: Dialog? = null
    var passwordDoneDialog: Dialog? = null


    @Suppress("DEPRECATION")
    fun setLocale2(context: Activity, langua:String) {
        WriteOnSP(context,KeyAppLanguage,langua)
        //Log.e("Lan",session.getLanguage());
        val locale = Locale(langua)
        val config = Configuration(context.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.baseContext.resources.updateConfiguration(config,
            context.baseContext.resources.displayMetrics)
    }


    fun GetFromSP(context: Context, key: String):String?{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "NoValue")
    }
    fun GetLongFromSP(context: Context, key: String):Long?{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        return sharedPreferences.getLong(key, 0)
    }
    fun WriteOnSP(context: Context, key: String, value: String){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
        editor.commit()
    }
    fun WriteLongOnSP(context: Context, key: String, value: Long){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
        editor.commit()
    }
    fun DeleteFromSP(context: Context, key: String){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
        editor.commit()
    }
    fun IsInSP(context: Context, key: String):Boolean{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        return sharedPreferences.contains(key)
    }

    fun showDefaultDialog(context: Context, title: String, messsage: String){
        val builder = AlertDialog.Builder(context)
        if (!title.isNullOrEmpty()){
            builder.setTitle(title)
        }
        builder.setMessage(messsage)
        builder.setNeutralButton("حسناً"){ _, _ ->
        }
        builder.show()
    }

    fun showLoadingDialog(activity: Activity) {
        loadingDia = Dialog(activity)
        loadingDia?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDia?.setCancelable(false)
        loadingDia?.setContentView(R.layout.ftp_dialog_loading)
        val window: Window = loadingDia?.window!!
        window.setBackgroundDrawable(
            ColorDrawable(activity.resources
                .getColor(R.color.tk_dialog_bg, null))
        )
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        loadingDia?.show()
    }

    fun hideLoadingDialog(){
        if (loadingDia != null){
            if (loadingDia?.isShowing!!){
                loadingDia?.dismiss()
            }
        }
    }

    fun showCodeDoneDialog(activity: Activity) {
        codePhone = Dialog(activity)
        codePhone?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        codePhone?.setCancelable(false)
        codePhone?.setContentView(R.layout.ftp_dialog_phone_added_successfully)
        val donebtn = codePhone?.findViewById<TextView>(R.id.GoToHome)
        donebtn?.setOnClickListener {
            codePhone!!.dismiss()
            showLoadingDialog(activity)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    hideLoadingDialog()
//                        .....................................................................................................
                    val intent = Intent(activity, MainMenu::class.java).apply {}
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    activity.finish()
                }
            }, 3000)
        }

        val window: Window = codePhone?.window!!
        window.setBackgroundDrawable(
            ColorDrawable(activity.resources
                .getColor(R.color.tk_dialog_bg, null))
        )
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        codePhone?.show()
    }

    fun showPasswordDoneDialog(activity: Activity) {
        passwordDoneDialog = Dialog(activity)
        passwordDoneDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        passwordDoneDialog?.setCancelable(false)
        passwordDoneDialog?.setContentView(R.layout.ftp_dialog_password_reset_done)
        val donebtn = passwordDoneDialog?.findViewById<TextView>(R.id.GoToSignIn)
        donebtn!!.setOnClickListener {
            passwordDoneDialog!!.dismiss()
            showLoadingDialog(activity)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    hideLoadingDialog()
                    val intent = Intent(activity, MainMenu::class.java).apply {}
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    activity.finish()
                }
            }, 3000)
        }

        val window: Window = passwordDoneDialog?.window!!
        window.setBackgroundDrawable(
            ColorDrawable(activity.resources
                .getColor(R.color.tk_dialog_bg, null))
        )
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        passwordDoneDialog?.show()
    }

    fun getFileFromUri(uri: Uri,context: Context): File? {
        if (uri.path == null) {
            return null
        }
        var realPath = String()
        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (uri.path!!.contains("/document/image:")) {
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else {
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query(
                databaseUri,
                projection,
                selection,
                selectionArgs,
                null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.i("GetFileUri Exception:", e.message ?: "")
        }
        val path = if (realPath.isNotEmpty()) realPath else {
            when {
                uri.path!!.contains("/document/raw:") -> uri.path!!.replace(
                    "/document/raw:",
                    ""
                )
                uri.path!!.contains("/document/primary:") -> uri.path!!.replace(
                    "/document/primary:",
                    "/storage/emulated/0/"
                )
                else -> return null
            }
        }
        return File(path)
    }

    infix fun Any?.ifNull(block: () -> Unit) {
        if (this == null) block()
    }

}