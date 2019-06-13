package com.testandroid;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * @author by hs-johnny
 * Created on 2019/6/13
 */
public class ShowMedioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.addRule(RelativeLayout.CENTER_IN_PARENT);
        Uri uri = getIntent().getData();
        if(getIntent().getType().equals("image/*")){
            ImageView iv = new ImageView(this);
            iv.setImageURI(uri);
            iv.setLayoutParams(rl);
            relativeLayout.addView(iv);
        } else {
            MediaController mc = new MediaController(this);
            VideoView vv = new VideoView(this);
            mc.setAnchorView(vv);
            mc.setMediaPlayer(vv);
            vv.setMediaController(mc);
            vv.setVideoURI(uri);
            vv.start();
            vv.setLayoutParams(rl);
            relativeLayout.addView(vv);
        }
        setContentView(relativeLayout);
    }

}
