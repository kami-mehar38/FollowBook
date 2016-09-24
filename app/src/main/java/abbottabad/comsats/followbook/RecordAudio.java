package abbottabad.comsats.followbook;

/**
 * This project FollowBook is created by Kamran Ramzan on 23-Sep-16.
 */
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordAudio
{
     private static final String LOG_TAG = "RecordAudio";
     private static String mFileName = null;
     private MediaRecorder mRecorder = null;
     private MediaPlayer   mPlayer = null;

    public void startPlaying(String soundPath) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlaying();
            }
        });
        try {
            mPlayer.setDataSource(soundPath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public String stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        return mFileName;
    }

    public RecordAudio() {

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mFileName += "/" +timeStamp+".3gp";
    }
}
