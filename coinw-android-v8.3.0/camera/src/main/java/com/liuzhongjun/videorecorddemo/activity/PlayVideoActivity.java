package com.liuzhongjun.videorecorddemo.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.liuzhongjun.videorecorddemo.R;
import com.liuzhongjun.videorecorddemo.util.VideoInfo;
import com.liuzhongjun.videorecorddemo.util.mMediaController;
import com.liuzhongjun.videorecorddemo.view.MyVideoView;

import java.io.File;

public class PlayVideoActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, mMediaController.MediaPlayerControl, View.OnClickListener {
    public static final String TAG = "PlayVideo";
    private MyVideoView videoView;
    private mMediaController controller;
    private String mVideoPath;
    private TextView toRecord;//重录
    private TextView NextRecord;//录制下一段
    private boolean isFirst = true;
    private String time;
    private View play_video;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_playvideo);
        initView();
    }

    private void initView() {
        play_video = findViewById(R.id.play_video);
        play_video.setOnClickListener(this);
        isFirst = getIntent().getBooleanExtra("isFirst", true);
        mVideoPath = getIntent().getExtras().getString("videoPath");
        time = getIntent().getExtras().getString("time");
        toRecord = findViewById(R.id.re_record);
        NextRecord = findViewById(R.id.next_record);
        findViewById(R.id.back_iv).setOnClickListener(this);
        NextRecord.setText(isFirst ? getResources().getString(R.string.camera_d7) : getResources().getString(R.string.camera_d12));
        toRecord.setOnClickListener(this);
        NextRecord.setOnClickListener(this);
        View videoControl = findViewById(R.id.videoControl);
        videoControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    play_video.setVisibility(View.VISIBLE);
                }
            }
        });
        File sourceVideoFile = new File(mVideoPath);
        if (isFirst) {
            VideoInfo.file1 = mVideoPath;
            VideoInfo.time1 = time;
        } else {
            VideoInfo.file2 = mVideoPath;
            VideoInfo.time2 = time;
        }
        videoView = (MyVideoView) findViewById(R.id.videoView);
        int screenW = getWindowManager().getDefaultDisplay().getWidth();
        int screenH = getWindowManager().getDefaultDisplay().getHeight();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoView.getLayoutParams();
        params.width = screenW;
        params.height = screenH;
        params.gravity = Gravity.BOTTOM;
        videoView.setLayoutParams(params);
        videoView.setOnPreparedListener(this);
        controller = new mMediaController(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            videoView.setVideoURI(Uri.fromFile(sourceVideoFile));
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((ViewGroup) findViewById(R.id.fl_videoView_parent));
//        controller.show();
        videoView.start();
        play_video.setVisibility(View.GONE);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play_video.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void start() {
        videoView.start();
    }

    @Override
    public void pause() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    public int getDuration() {
        return videoView.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return videoView.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        videoView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return videoView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return videoView.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        return videoView.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        return videoView.canSeekForward();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.re_record) {
            if (isFirst) {
                VideoInfo.file1 = "";
                VideoInfo.time1 = "";
            } else {
                VideoInfo.file2 = "";
                VideoInfo.time2 = "";
            }
            Intent intent = new Intent(this, CustomRecordActivity.class);
            intent.putExtra("isFirst", isFirst);
            intent.putExtra("showDes", false);
            startActivity(intent);
            finish();
        } else if (i == R.id.next_record) {
            if (isFirst) {
                Intent intent = new Intent(this, CustomRecordActivity.class);
                intent.putExtra("isFirst", false);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, UploadActivity.class);
                intent.putExtra("isFirst", false);
                startActivity(intent);
            }
            finish();
        } else if (i == R.id.play_video) {
            videoView.start();
            play_video.setVisibility(View.GONE);
        } else if (i == R.id.back_iv) {
            finish();
        }

    }
}
