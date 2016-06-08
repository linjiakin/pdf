package com.comtop.pdf.utils;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * @author Administrator
 *
 */
public class SharedPreferencesUtils {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public SharedPreferencesUtils(Context context,String fileName){
        this.context  = context;
        this.sharedPreferences = this.context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    public String getString(String key,String defaultValue){
        return sharedPreferences.getString(key,defaultValue);
    }

    public int getInt(String key,int defaultValue){
        return sharedPreferences.getInt(key,defaultValue);
    }
    
    public Set<String> getSet(String key,Set<String> defValues){
    	return this.sharedPreferences.getStringSet(key, defValues);
    }

    public void putString(String key,String value){
        this.editor.putString(key,value);
        this.editor.commit();
    }

    public void putInt(String key,int value){
        this.editor.putInt(key,value);
        this.editor.commit();
    }
    
    public void putSetString(String key,Set<String> values){
    	this.editor.putStringSet(key, values);
    	this.editor.commit();
    }
}
