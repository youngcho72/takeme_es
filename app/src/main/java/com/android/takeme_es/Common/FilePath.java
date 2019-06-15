package com.android.takeme_es.Common;

import android.os.Environment;

public class FilePath {

        //"storage/emulated/0"
        public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

        public String PICTURES = ROOT_DIR + "/Pictures";
        public String CAMERA = ROOT_DIR + "/DCIM/camera";

        public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}
