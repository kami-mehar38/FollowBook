package abbottabad.comsats.followbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This project FollowBook is created by Kamran Ramzan on 20-Sep-16.
 */
public class GridViewAdapter extends BaseAdapter {



    private static Context context;
    private static final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
    private List<ImageInfo> imageInfoList = new ArrayList<>();
    private static List<Integer> previousPosition;
    private static FollowBookDB followBookDB;
    private static int selectedPosition;

    public GridViewAdapter(Context c) {
        this.context = c;
        previousPosition = new ArrayList<>();
        followBookDB = new FollowBookDB(c);
    }

    public int addImage(ImageInfo imageInfo) {
        int position = imageInfoList.size();
        imageInfoList.add(position, imageInfo);
        return position;
    }

    public int getCount() {
        return imageInfoList.size();
    }

    public ImageInfo getItem(int position) {
        return imageInfoList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageInfo imageInfo = getItem(position);
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        } else {
            imageView = (ImageView) convertView;
        }
        if (imageInfo.getImageId() != 0) {
            imageView.setImageResource(imageInfo.getImageId());
        }
        if (imageInfo.getImagePath() != null) {

            Bitmap bitmap = BitmapFactory.decodeFile(imageInfo.getImagePath());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            ExifInterface exif;
            try {
                exif = new ExifInterface(imageInfo.getImagePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("LOCKED", false)) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FolderOne().showOptions(view, position);
                }
            });

        } else {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(position);
                    Toast.makeText(context, "OK CALLING", Toast.LENGTH_LONG).show();
                    selectedPosition = position;
                }
            });
        }
        return imageView;
    }

    private void playSound(int position) {

        SharedPreferences sharedPreferences  = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (folder > 1){
            int[] imageIds = new int[FolderOne.previousPosition.size() + 1];
            for (int i = 0; i < FolderOne.previousPosition.size(); i++){
                imageIds[i] = FolderOne.previousPosition.get(i);
            }
            imageIds[imageIds.length - 1] = position;
            String soundPath = new FollowBookDB(context).getSound(imageIds);
            if (!soundPath.equals("default")) {
                Log.i("TAG", "playSound: " + soundPath);
                FolderOne.isPlayingAudio = true;
                new RecordAudio().startPlaying(soundPath, position);
            } else {
                playVideo(position);
            }
        } else {
            String soundPath = new FollowBookDB(context).getSound(new int[]{position});
            if (!soundPath.equals("default")) {
                Log.i("TAG", "playSound: " + soundPath);
                FolderOne.isPlayingAudio = true;
                new RecordAudio().startPlaying(soundPath, position);
            } else {
                playVideo(position);
            }
        }
    }

    static void playVideo(final int position) {
        SharedPreferences sharedPreferences  = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (folder > 1) {
            int[] imageIds = new int[FolderOne.previousPosition.size() + 1];
            for (int i = 0; i < FolderOne.previousPosition.size(); i++){
                imageIds[i] = FolderOne.previousPosition.get(i);
            }
            imageIds[imageIds.length - 1] = position;
            String videoPath = new FollowBookDB(context).getVideo(imageIds);
            if (!videoPath.equals("default")) {
                FolderOne.videoLayout.reset();
                FolderOne.videoLayout.setVisibility(View.VISIBLE);
                try {
                    FolderOne.videoLayout.setVideoURI(Uri.parse(videoPath));
                    FolderOne.videoLayout.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            FolderOne.videoLayout.setVisibility(View.GONE);
                            playYoutubeVideo(position);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            String videoPath = new FollowBookDB(context).getVideo(new int[]{position});
            if (!videoPath.equals("default")) {
                FolderOne.videoLayout.reset();
                FolderOne.videoLayout.setVisibility(View.VISIBLE);
                try {
                    FolderOne.videoLayout.setVideoURI(Uri.parse(videoPath));
                    FolderOne.videoLayout.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            FolderOne.videoLayout.setVisibility(View.GONE);
                            playYoutubeVideo(position);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void playYoutubeVideo(int position) {
        SharedPreferences sharedPreferences  = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (folder > 1){
            int[] imageIds = new int[FolderOne.previousPosition.size() + 1];
            for (int i = 0; i < FolderOne.previousPosition.size(); i++){
                imageIds[i] = FolderOne.previousPosition.get(i);
            }
            imageIds[imageIds.length - 1] = position;
            String link = new FollowBookDB(context).getYouTubeLink(imageIds);
            if (!link.equals("default")) {
                Config.setYoutubeLink(link);
                context.startActivity(new Intent(context, YouTubePlayerUtils.class));
            }
        } else {
            String link = new FollowBookDB(context).getYouTubeLink(new int[]{position});
            if (!link.equals("default")) {
                Config.setYoutubeLink(link);
                context.startActivity(new Intent(context, YouTubePlayerUtils.class));
            }
        }
    }

    public static void openSubfolder(){
        SharedPreferences sharedPreferences  = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        int folder = sharedPreferences.getInt("FOLDER", 1);
            ImageInfo[] imageInfos;
            List<String> imagePaths;
            if (folder == 1){
                Log.i("TAG", "initialSetup: " + folder);
                imagePaths = followBookDB.showIcons(new int[]{});
            } else {
                previousPosition.add(selectedPosition);
                int[] imageIds = new int[previousPosition.size()];
                for (int i = 0; i< previousPosition.size(); i++){
                    imageIds[i] = previousPosition.get(i);
                }
                imagePaths = followBookDB.showIcons(imageIds);
            }
            int length = imagePaths.size();
            imageInfos = new ImageInfo[length];
            for (int i = 0; i < imageInfos.length; i++) {
                imageInfos[i] = new ImageInfo();
                if (imagePaths.get(i).equals("default")) {
                    imageInfos[i].setImageId(R.drawable.picture);
                    FolderOne.gridViewAdapter.addImage(imageInfos[i]);
                } else {
                    imageInfos[i].setImagePath(imagePaths.get(i));
                    FolderOne.gridViewAdapter.addImage(imageInfos[i]);

                }
            }
            FolderOne.gridView.setAdapter(FolderOne.gridViewAdapter);

    }
}
