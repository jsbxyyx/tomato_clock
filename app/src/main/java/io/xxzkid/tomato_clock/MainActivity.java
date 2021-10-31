package io.xxzkid.tomato_clock;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getBaseContext();

        keepScreenLongLight(this, true);

        mpInit();

        final EditText etTime = findViewById(R.id.et_time);

        final TextView tvCountTime = findViewById(R.id.tv_count_time);

        final Button start = findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String timeStr = etTime.getText().toString();

                if (TextUtils.isEmpty(timeStr)) {
                    Toast.makeText(context, "请填写时间", Toast.LENGTH_LONG).show();
                    return;
                }

                final long time = Long.parseLong(timeStr) * 60 * 1000;

                countDownTimer = new CountDownTimer(time, 1 * 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        start.setEnabled(false);
                        etTime.setEnabled(false);
                        long s = millisUntilFinished / 1000;
                        String min = s / 60 + "";
                        String sec = s % 60 + "";
                        tvCountTime.setText(
                                (min.length() == 1 ? "0" + min : min) +
                                ":" +
                                (sec.length() == 1 ? "0" + sec : sec));
                    }

                    @Override
                    public void onFinish() {
                        start.setEnabled(true);
                        etTime.setEnabled(true);
                        clearCountDownTimer();
                        mpPlay();
                    }
                };
                countDownTimer.start();
            }
        });

        final Button reset = findViewById(R.id.btn_reset);
        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearCountDownTimer();
                start.setEnabled(true);
                etTime.setEnabled(true);
                etTime.setText("");
                tvCountTime.setText("00:00");
                mpStop();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCountDownTimer();
    }

    private void clearCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void keepScreenLongLight(Activity activity, boolean openLight) {
        Window window = activity.getWindow();
        if (openLight) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    private void mpInit() {
        try {
            Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        } catch(Exception e) {
        }
    }

    private void mpPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void mpStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

}
