package com.example.rq.chatwithserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by rq on 16/5/3.
 */
public class CheckPermission extends Activity{
    @Override
    public void onCreate(Bundle onSaveInstance){
        super.onCreate(onSaveInstance);
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("edu.stevens.cs522.capture").putExtra("from","chat");
        startActivity(launchIntent);
    }
}
