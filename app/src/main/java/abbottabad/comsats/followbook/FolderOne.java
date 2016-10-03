package abbottabad.comsats.followbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FolderOne extends AppCompatActivity implements View.OnClickListener{

    public static GridView gridView;
    public static AlertDialog alertDialog;
    public static GridViewAdapter gridViewAdapter;
    private final int IMAGE_REQUEST_CODE = 12;
    private final int Request_load_image = 9090;
    private final int Video_Request_Code = 8080;
    private final int VIDEO_REQUEST_CODE = 13;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private String mCurrentPhotoPath;
    private File photoFile;
    private static int selectedPosition;
    private static FollowBookDB followBookDB;
    private AlertDialog AD_record;
    private static boolean isRecording = false;
    private Button btnStartStop;
    private TextView tv_status;
    private static RecordAudio recordAudio;
    private Uri videofileUri;
    public static FullscreenVideoLayout videoLayout;
    public static boolean isPlayingAudio = false;
    private Button btnAddLink;
    private AlertDialog AD_link;
    private EditText ET_link;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;
    public static List<Integer> previousPosition;
    private Button btnAddText;
    private EditText ET_text;
    private AlertDialog AD_text;
    public static Runnable runnable;
    public static Handler handler;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_one);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
        sharedPreferences = this.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        if (getActionBar() != null && sharedPreferences.getBoolean("LOCKED", true)) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        gridView = (GridView) findViewById(R.id.GridLayout1);
        editor = sharedPreferences.edit();
        editor.putInt("FOLDER", 1);
        previousPosition = new ArrayList<>();
        followBookDB = new FollowBookDB(this);
        recordAudio = new RecordAudio();
        gridViewAdapter = new GridViewAdapter(FolderOne.this);
        initialSetup();

        videoLayout = (FullscreenVideoLayout) findViewById(R.id.videoview);
        videoLayout.setActivity(this);
        videoLayout.setVisibility(View.GONE);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (sharedPreferences.getBoolean("LOCKED", false)) {
            fab.setAlpha(0);
            fab.setCompatElevation(0);
            fab.setRippleColor(Color.TRANSPARENT);
            fab.setBackgroundTintList(ColorStateList.valueOf(Color
                    .parseColor("#fff7cb")));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sharedPreferences.getBoolean("LOCKED", false)) {
                    int folder = sharedPreferences.getInt("FOLDER", 1);
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setImageId(R.drawable.picture);
                    imageInfo.setImageText("Add Description");
                    int imageId = gridViewAdapter.addImage(imageInfo);
                    gridView.setAdapter(gridViewAdapter);

                    if (folder == 1) {
                        followBookDB.addImage(new int[]{imageId}, "default", "Add Caption");
                        followBookDB.addSound(new int[]{imageId}, "default");
                        followBookDB.addVideo(new int[]{imageId}, "default");
                        followBookDB.addYouTubeLink(new int[]{imageId}, "default");
                    } else {
                        int[] imageIds = new int[previousPosition.size() + 1];
                        for (int i = 0; i < previousPosition.size(); i++) {
                            imageIds[i] = previousPosition.get(i);
                        }
                        imageIds[imageIds.length - 1] = imageId;
                        followBookDB.addImage(imageIds, "default", "Add Caption");
                        followBookDB.addSound(imageIds, "default");
                        followBookDB.addVideo(imageIds, "default");
                        followBookDB.addYouTubeLink(imageIds, "default");
                    }
                }else{
                    if (doubleBackToExitPressedOnce) {
                        finish();
                    }
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(FolderOne.this, "Press again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;
                        }
                    }, 2000);
                }

            }
        });

        String[] STATUS_LIST = new String[]{
                "Take Picture",
                "Choose Picture",
                "Add caption",
                "Record Sound",
                "Record Video",
                "Choose Video",
                "Link youtube",
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
                    case 1: {


                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, Request_load_image);
                        break;
                    }
                    case 2: {
                        View view = LayoutInflater.from(FolderOne.this).inflate(R.layout.add_text_layout, null);
                        Button btnCancel = (Button) view.findViewById(R.id.btn_cancelText);
                        btnCancel.setOnClickListener(FolderOne.this);
                        btnAddText = (Button) view.findViewById(R.id.btn_addText);
                        btnAddText.setOnClickListener(FolderOne.this);
                        ET_text = (EditText) view.findViewById(R.id.ET_text);
                        AlertDialog.Builder builder = new AlertDialog.Builder(FolderOne.this);
                        builder.setView(view);
                        builder.setCancelable(false);
                        AD_text = builder.create();
                        AD_text.show();
                        break;
                    }
                    case 3: {
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
                    case 4: {
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
                    case 5: {
                        Intent videoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(videoIntent, Video_Request_Code);
                        break;
                    }
                    case 6: {
                        View view = LayoutInflater.from(FolderOne.this).inflate(R.layout.add_link_layout, null);
                        Button btnCancel = (Button) view.findViewById(R.id.btn_cancelLink);
                        btnCancel.setOnClickListener(FolderOne.this);
                        btnAddLink = (Button) view.findViewById(R.id.btn_addLink);
                        btnAddLink.setOnClickListener(FolderOne.this);
                        ET_link = (EditText) view.findViewById(R.id.ET_link);
                        AlertDialog.Builder builder = new AlertDialog.Builder(FolderOne.this);
                        builder.setView(view);
                        builder.setCancelable(false);
                        AD_link = builder.create();
                        AD_link.show();
                        break;
                    }
                    case 7: {
                        editor.putInt("FOLDER", sharedPreferences.getInt("FOLDER", 1) + 1);
                        editor.commit();
                        previousPosition.add(selectedPosition);
                        initialSetup();
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

    public void initialSetup() {
        checkForFolder();
        gridViewAdapter.clearAll();
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (folder > 1) {
            followBookDB.upgrade();
        }
        ImageInfo[] imageInfos;
        List<String> image;
        String[] imagePaths = new String[0];
        String[] imageText = new String[0];
        if (folder == 1) {
            image = followBookDB.showIcons(new int[]{});
            if (image != null && image.size() > 0) {
                imagePaths = new String[image.size() / 2];
                imageText = new String[image.size() / 2];
                int j = 1;
                int k = 0;
                for (int i = 0; i < image.size() / 2; i++) {
                    imageText[i] = image.get(j);
                    j += 2;
                    imagePaths[i] = image.get(k);
                    k += 2;
                }
            }

        } else {
            int[] imageIds = new int[previousPosition.size()];
            for (int i = 0; i < previousPosition.size(); i++) {
                imageIds[i] = previousPosition.get(i);
            }
            image = followBookDB.showIcons(imageIds);
            if (image != null && image.size() > 0) {
                imagePaths = new String[image.size() / 2];
                imageText = new String[image.size() / 2];
                int j = 1;
                int k = 0;
                for (int i = 0; i < image.size() / 2; i++) {
                    imageText[i] = image.get(j);
                    j += 2;
                    imagePaths[i] = image.get(k);
                    k += 2;
                }
            }
        }

        int length = imagePaths.length;

        imageInfos = new ImageInfo[length];
        for (int i = 0; i < imageInfos.length; i++) {
            imageInfos[i] = new ImageInfo();
            if (imagePaths[i].equals("default")) {
                imageInfos[i].setImageId(R.drawable.picture);
                imageInfos[i].setImageText(imageText[i]);
                gridViewAdapter.addImage(imageInfos[i]);
            } else {
                imageInfos[i].setImagePath(imagePaths[i]);
                imageInfos[i].setImageText(imageText[i]);
                gridViewAdapter.addImage(imageInfos[i]);

            }
        }
        if (sharedPreferences.getBoolean("LOCKED", true)) {
            if (image != null) {
                if (image.size() > 0) {
                    gridView.setAdapter(gridViewAdapter);
                } else onBackPressed();
            }
        } else gridView.setAdapter(gridViewAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {


            if (folder > 1) {
                int[] imageIds = new int[previousPosition.size() + 1];
                for (int i = 0; i < previousPosition.size(); i++) {
                    imageIds[i] = previousPosition.get(i);
                }
                imageIds[imageIds.length - 1] = selectedPosition;
                followBookDB.updateImagePath(mCurrentPhotoPath, imageIds);
            } else {
                followBookDB.updateImagePath(mCurrentPhotoPath, new int[]{selectedPosition});
            }
            initialSetup();
        }
        if (requestCode == VIDEO_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                if (folder > 1) {
                    int[] imageIds = new int[previousPosition.size() + 1];
                    for (int i = 0; i < previousPosition.size(); i++) {
                        imageIds[i] = previousPosition.get(i);
                    }
                    imageIds[imageIds.length - 1] = selectedPosition;
                    followBookDB.updateVideoPath(String.valueOf(videofileUri), imageIds);
                } else {
                    followBookDB.updateVideoPath(String.valueOf(videofileUri), new int[]{selectedPosition});
                }
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
        if (requestCode == Request_load_image) {
            // Get the Image from data
            if (data != null) {
                Uri selectedImage = data.getData();

                // File imageFile = new File(selectedImage);
                String result;
                Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                if (cursor == null) { // Source is Dropbox or other similar local file path
                    result = selectedImage.getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    result = cursor.getString(idx);
                    cursor.close();
                }
                if (folder > 1) {
                    int[] imageIds = new int[previousPosition.size() + 1];
                    for (int i = 0; i < previousPosition.size(); i++) {
                        imageIds[i] = previousPosition.get(i);
                    }
                    imageIds[imageIds.length - 1] = selectedPosition;
                    followBookDB.updateImagePath(result, imageIds);
                } else {
                    followBookDB.updateImagePath(result, new int[]{selectedPosition});
                }
                initialSetup();
            }
        }
        if (requestCode == Video_Request_Code) {
            if (data != null) {
                String result;
                Uri videoPath = data.getData();
                Cursor cursor = getContentResolver().query(videoPath, null, null, null, null);
                if (cursor == null) { // Source is Dropbox or other similar local file path
                    result = videoPath.getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    result = cursor.getString(idx);
                    cursor.close();
                }
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
                    if (folder > 1) {
                        int[] imageIds = new int[previousPosition.size() + 1];
                        for (int i = 0; i < previousPosition.size(); i++) {
                            imageIds[i] = previousPosition.get(i);
                        }
                        imageIds[imageIds.length - 1] = selectedPosition;
                        followBookDB.updateVideoPath(String.valueOf(result), imageIds);
                    } else {
                        followBookDB.updateVideoPath(String.valueOf(result), new int[]{selectedPosition});
                    }
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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_stop: {
                if (!isRecording) {
                    recordAudio.startRecording();
                    btnStartStop.setText("Stop");
                    tv_status.setText("Recording...");
                    Toast.makeText(FolderOne.this, "Recording", Toast.LENGTH_SHORT).show();
                    isRecording = true;
                    break;
                } else {
                    int folder = sharedPreferences.getInt("FOLDER", 1);
                    String filePath = recordAudio.stopRecording();
                    btnStartStop.setText("Start");
                    tv_status.setText("Record audio");
                    Toast.makeText(FolderOne.this, "Stopped", Toast.LENGTH_SHORT).show();
                    isRecording = false;
                    if (folder > 1) {
                        int[] imageIds = new int[previousPosition.size() + 1];
                        for (int i = 0; i < previousPosition.size(); i++) {
                            imageIds[i] = previousPosition.get(i);
                        }
                        imageIds[imageIds.length - 1] = selectedPosition;
                        new FollowBookDB(FolderOne.this).updateSoundPath(filePath, imageIds);
                    } else {
                        new FollowBookDB(FolderOne.this).updateSoundPath(filePath, new int[]{selectedPosition});
                    }
                    break;
                }
            }
            case R.id.btn_cancel: {
                AD_record.cancel();
                break;
            }
            case R.id.btn_cancelLink: {
                AD_link.cancel();
                break;
            }
            case R.id.btn_addLink: {
                String link = ET_link.getText().toString().trim();
                if (!link.isEmpty()) {
                    link = link.substring(link.lastIndexOf("/") + 1);
                    int folder = sharedPreferences.getInt("FOLDER", 1);
                    if (folder > 1) {
                        int[] imageIds = new int[previousPosition.size() + 1];
                        for (int i = 0; i < previousPosition.size(); i++) {
                            imageIds[i] = previousPosition.get(i);
                        }
                        imageIds[imageIds.length - 1] = selectedPosition;
                        followBookDB.updateYouTubeLink(link, imageIds);
                        Toast.makeText(FolderOne.this, "Youtube linked", Toast.LENGTH_LONG).show();
                        AD_link.cancel();
                    } else {
                        AD_link.cancel();
                        followBookDB.updateYouTubeLink(link, new int[]{selectedPosition});
                        Toast.makeText(FolderOne.this, "Youtube linked", Toast.LENGTH_LONG).show();
                    }
                } else Toast.makeText(FolderOne.this, "Enter some text", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.btn_cancelText: {
                AD_text.cancel();
                break;
            }
            case R.id.btn_addText: {
                String caption = ET_text.getText().toString().trim();
                if (!caption.isEmpty()) {
                    int folder = sharedPreferences.getInt("FOLDER", 1);
                    if (folder > 1) {
                        int[] imageIds = new int[previousPosition.size() + 1];
                        for (int i = 0; i < previousPosition.size(); i++) {
                            imageIds[i] = previousPosition.get(i);
                        }
                        imageIds[imageIds.length - 1] = selectedPosition;
                        followBookDB.updateImageText(caption, imageIds);
                        Toast.makeText(FolderOne.this, "Caption added", Toast.LENGTH_LONG).show();
                        initialSetup();
                    } else {
                        AD_text.cancel();
                        followBookDB.updateImageText(caption, new int[]{selectedPosition});
                        Toast.makeText(FolderOne.this, "Caption added", Toast.LENGTH_LONG).show();
                        initialSetup();
                    }
                } else Toast.makeText(FolderOne.this, "Enter some text", Toast.LENGTH_LONG).show();
                break;
            }
            case android.R.id.home:{
                if (sharedPreferences.getBoolean("LOCKED", true)){
                    onBackPressed();
                    handler.removeCallbacks(runnable);
                }
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

    public void showOptions(int position) {
        selectedPosition = position;
        alertDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (videoLayout != null && videoLayout.getVisibility() == View.VISIBLE) {
            videoLayout.stop();
            videoLayout.setVisibility(View.GONE);
        }
        if (isPlayingAudio) {
            recordAudio.stopPlaying();
        }
        if (folder > 1 && !sharedPreferences.getBoolean("LOCKED", false)) {
            folder = sharedPreferences.getInt("FOLDER", 1) - 1;
            editor.putInt("FOLDER", folder);
            editor.commit();
            if (previousPosition.size() > 0) {
                previousPosition.remove(previousPosition.size() - 1);
            }
            initialSetup();
        } else if (!sharedPreferences.getBoolean("LOCKED", false)) {
            super.onBackPressed();
        } else if (folder > 1){
            folder = sharedPreferences.getInt("FOLDER", 1) - 1;
            editor.putInt("FOLDER", folder);
            editor.commit();
            if (previousPosition.size() > 0) {
                previousPosition.remove(previousPosition.size() - 1);
            }
            initialSetup();
        }
    }

    public void checkForFolder() {
        if (sharedPreferences.getBoolean("LOCKED", false)) {
            final int intervalTime = 10000; // 10 sec
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (sharedPreferences.getInt("FOLDER", 1) > 0) {
                        onBackPressed();
                    }
                }
            };
            handler = new Handler();
            handler.postDelayed(runnable, intervalTime);
        }
    }
}
