package com.android.takeme_es.Common;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 7/24/2017.
 */

public class FileSearch {

    private static final String TAG = "FileSearch";
    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);

        Log.d(TAG, "getDirectoryPaths: ");
        File[] listfiles = file.listFiles();

        if(listfiles == null)
            return null;

        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        Log.d(TAG, "getFilePaths: ");
        
        File[] listfiles = file.listFiles();

        if(listfiles == null)
            return null;

        Log.d(TAG, "getFilePaths: listfiles.length" + listfiles.length);
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isFile()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
