package com.testandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author by hs-johnny
 * Created on 2019/6/12
 */
public class MainActivity extends Activity {

    FrameLayout preview;
    Button btn;
    Button tackpicBtn, videoBtn;
    CameraPreview cameraPreview;
    ImageView previewIv;
    Button startBtn, endBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        previewIv = findViewById(R.id.preview_iv);
        tackpicBtn = findViewById(R.id.tackpic_btn);
        videoBtn = findViewById(R.id.video_btn);
        startBtn = findViewById(R.id.start_btn);
        endBtn = findViewById(R.id.end_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.camera_preview,
                        new SettingFragment()).addToBackStack(null).commit();
            }
        });
        tackpicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.takePicture(previewIv);
            }
        });
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraPreview.isRecording()){
                    cameraPreview.stopRecording(previewIv);
                    videoBtn.setText("录像");
                } else {
                    if(cameraPreview.startRecording()){
                        videoBtn.setText("停止");
                    }
                }
            }
        });
        previewIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowMedioActivity.class);
                intent.setDataAndType(cameraPreview.getOutputMediaFileUri(), cameraPreview.getOutputMediaFileType());
                startActivityForResult(intent, 0);
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCamera();
            }
        });
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPreview();
            }
        });
    }

    private void initCamera(){
        preview = findViewById(R.id.camera_preview);
        cameraPreview = new CameraPreview(this);
        preview.addView(cameraPreview);
        SettingFragment.passCamera(cameraPreview.getCameraInstance());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SettingFragment.setDefault(PreferenceManager.getDefaultSharedPreferences(this));
        SettingFragment.init(PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cameraPreview == null){
            initCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview = null;
    }

    public void stopPreview(){
        preview.removeAllViews();
    }
}
