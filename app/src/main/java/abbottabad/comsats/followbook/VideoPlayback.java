package abbottabad.comsats.followbook;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;

/**
 * This project FollowBook is created by Kamran Ramzan on 24-Sep-16.
 */
public class VideoPlayback {

    private Context context;
    private AlertDialog AD_playVideo;
    private VideoView videoView;
    FullscreenVideoLayout videoLayout;
    private final View view;

    public VideoPlayback(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_folder_one, null);
        videoLayout = (FullscreenVideoLayout) view.findViewById(R.id.videoview);
    }

    public void playVideo(String videoPath) {
        /*AD_playVideo.show();
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.setMediaController(new MediaController(context));
        videoView.start();*/


        videoLayout.setActivity((FolderOne)context);

        try {
            videoLayout.setVideoURI(Uri.parse(videoPath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
