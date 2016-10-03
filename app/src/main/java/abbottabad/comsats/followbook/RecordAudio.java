package abbottabad.comsats.followbook;

/**
 * This project FollowBook is created by Kamran Ramzan on 23-Sep-16.
 */
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordAudio
{
     private static String mFileName = null;
     private MediaRecorder mRecorder = null;
     private static MediaPlayer   mPlayer = null;

    public void startPlaying(String soundPath, final int position) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                FolderOne.isPlayingAudio = false;
                stopPlaying();
                GridViewAdapter.playVideo(position);
            }
        });
        try {
            mPlayer.setDataSource(soundPath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
        }
    }

    public void stopPlaying() {
        if (mRecorder != null)
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
