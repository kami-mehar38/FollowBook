package abbottabad.comsats.followbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FolderOne extends AppCompatActivity implements View.OnClickListener {

    private GridView gridView;
    public static AlertDialog alertDialog;
    private GridViewAdapter gridViewAdapter;
    private static ImageView selectedImageView;
    private final int IMAGE_REQUEST_CODE = 12;
    private final int VIDEO_REQUEST_CODE = 13;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private String mCurrentPhotoPath;
    private File photoFile;
    private static int selectedPosition;
    private FollowBookDB followBookDB;
    private AlertDialog AD_record;
    private static boolean isRecording = false;
    private Button btnStartStop;
    private TextView tv_status;
    private RecordAudio recordAudio;
    private Uri videofileUri;
    public static FullscreenVideoLayout videoLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_one);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = initialSetup();
        videoLayout = (FullscreenVideoLayout) findViewById(R.id.videoview);
        videoLayout.setActivity(this);
        videoLayout.setVisibility(View.GONE);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("FOLDER", 1);
        if (sharedPreferences.getBoolean("LOCKED", false)) {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setImageId(R.drawable.picture);
                int imageId = gridViewAdapter.addImage(imageInfo);
                gridView.setAdapter(gridViewAdapter);
                followBookDB.addImage(new int[]{imageId}, "default");
                followBookDB.addSound(new int[]{imageId}, "default");
                followBookDB.addVideo(new int[]{imageId}, "default");
            }
        });

        String[] STATUS_LIST = new String[]{
                "Take Picture",
                "Record Sound",
                "Record Video",
                "Link youtube or web",
                "Sub folder"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select option");
        builder.setItems(STATUS_LIST, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0: {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File

                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(FolderOne.this,
                                        "abbottabad.comsats.followbook.fileprovider",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
                            }
                        }
                        break;
                    }
                    case 1:{
                        View view = LayoutInflater.from(FolderOne.this).inflate(R.layout.record_audio_layout, null);
                        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                        btnCancel.setOnClickListener(FolderOne.this);
                        btnStartStop = (Button) view.findViewById(R.id.btn_start_stop);
                        btnStartStop.setOnClickListener(FolderOne.this);
                        tv_status = (TextView) view.findViewById(R.id.TV_status);
                        AlertDialog.Builder builder = new AlertDialog.Builder(FolderOne.this);
                        builder.setView(view);
                        builder.setCancelable(false);
                        AD_record = builder.create();
                        AD_record.show();
                        break;
                    }
                    case 2:{
                        // create new Intentwith with Standard Intent action that can be
                        // sent to have the camera application capture an video and return it.


                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                        // create a file to save the video
                        videofileUri = new RecordVideo().getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

                        // set the image file name
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, videofileUri);

                        // set the video image quality to high
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                        // start the Video Capture Intent
                        startActivityForResult(intent, VIDEO_REQUEST_CODE);

                        break;
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
            }
        });
        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    @NonNull
    private SharedPreferences initialSetup() {
        final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        gridView = (GridView) findViewById(R.id.GridLayout1);
        gridViewAdapter = new GridViewAdapter(FolderOne.this);
        followBookDB = new FollowBookDB(this);
        recordAudio = new RecordAudio();
        int folder = sharedPreferences.getInt("FOLDER", 1);
        ImageInfo[] imageInfos;
        List<String> imagePaths = followBookDB.showIcons();
        if (folder == 1) {
            int length = imagePaths.size();
            imageInfos = new ImageInfo[length];
            for (int i = 0; i < imageInfos.length; i++) {
                imageInfos[i] = new ImageInfo();
                if (imagePaths.get(i).equals("default")) {
                    imageInfos[i].setImageId(R.drawable.picture);
                    gridViewAdapter.addImage(imageInfos[i]);
                } else {
                    imageInfos[i].setImagePath(imagePaths.get(i));
                    gridViewAdapter.addImage(imageInfos[i]);

                }
            }
            gridView.setAdapter(gridViewAdapter);
        }
        return sharedPreferences;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_folder_one, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            followBookDB.updateImagePath(mCurrentPhotoPath, selectedPosition);
            initialSetup();
        }
        if (requestCode == VIDEO_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                followBookDB.updateVideoPath(String.valueOf(videofileUri), selectedPosition);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
                Toast.makeText(this, "User cancelled the video capture.",
                        Toast.LENGTH_LONG).show();

            } else {
                // Video capture failed, advise user
                Toast.makeText(this, "Video capture failed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_stop :{
                if (!isRecording){
                    recordAudio.startRecording();
                    btnStartStop.setText("Stop");
                    tv_status.setText("Recording...");
                    Toast.makeText(FolderOne.this, "Recording", Toast.LENGTH_SHORT).show();
                    isRecording = true;
                    break;
                } else {
                    String filePath = recordAudio.stopRecording();
                    btnStartStop.setText("Start");
                    tv_status.setText("Record audio");
                    Toast.makeText(FolderOne.this, "Stoped", Toast.LENGTH_SHORT).show();
                    isRecording = false;
                    new FollowBookDB(FolderOne.this).updateSoundPath(filePath, selectedPosition);
                    break;
                }
            }
            case R.id.btn_cancel :{
                AD_record.cancel();
                break;
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void showOptions(View view, int position) {
        selectedImageView = (ImageView) view;
        selectedPosition = position;
        alertDialog.show();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (videoLayout.getVisibility() == View.VISIBLE){
            videoLayout.stop();
            videoLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}