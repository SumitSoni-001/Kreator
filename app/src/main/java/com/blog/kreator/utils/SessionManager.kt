package com.blog.kreator.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val SHARED_PREFS = "SHARED_PREFS"
    private val TOKEN = "TOKEN"
    private val USERNAME = "USERNAME"
    private val EMAIL = "EMAIL"
    private val ABOUT = "ABOUT"
    private val USERID = "USER_ID"
    private val YOUR_CATEGORIES = "CATEGORIES"


    private var prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    private var editor = prefs.edit()

    fun clearData(){
        editor.clear()
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, null)
    }

    fun setToken(token: String) {
        editor.putString(TOKEN, "Bearer $token").apply()
    }

    fun getUserName(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun setUserName(name: String) {
        editor.putString(USERNAME, name).apply()
    }

    fun getEmail(): String? {
        return prefs.getString(EMAIL, null)
    }

    fun setEmail(email: String) {
        editor.putString(EMAIL, email).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USERID, null)
    }

    fun setUserId(id: String) {
        editor.putString(USERID, id).apply()
    }

    fun getAbout(): String? {
        return prefs.getString(ABOUT, null)
    }

    fun setAbout(about: String) {
        editor.putString(ABOUT, about).apply()
    }

    fun getCategories() : ArrayList<String> {
//        return prefs.getStringSet(YOUR_CATEGORIES , null) as Set<String>
        var categories: ArrayList<String>? = null
        val serializedObject = prefs.getString(YOUR_CATEGORIES, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type = object : TypeToken<List<String>?>() {}.type
            categories = gson.fromJson(serializedObject, type)
        }
        return categories!!
    }

    fun setCategories(catList : ArrayList<String>){
//        editor.putStringSet(YOUR_CATEGORIES , catList).apply()
        val gson = Gson()
        val json = gson.toJson(catList)
        editor.putString(YOUR_CATEGORIES, json).commit()
    }

}