package com.nelsonalmendra.videoplayer;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private CustomVideoView videoView;
    private int position = 0;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);

        if (mediaController == null) {
            mediaController = new MediaController(MainActivity.this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }

        try {
            int id = this.getRawResIdByName("myvideo");
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + id));

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.seekTo(position);
                videoView.start();

                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mediaController.setAnchorView(videoView);
                    }
                });
            }
        });

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setFullscreenMode();
        }
    }

    private void setFullscreenMode(){
        RelativeLayout.LayoutParams fullscreenParams = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        fullscreenParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        fullscreenParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        fullscreenParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fullscreenParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();
        return this.getResources().getIdentifier(resName, "raw", pkgName);
    }

    @Override
    protected void onPause() {
        videoView.pause();
        position = videoView.getCurrentPosition();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", position);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("position");
    }
}
