package abbottabad.comsats.followbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
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
class GridViewAdapter extends BaseAdapter {


    private static Context context;
    private static final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
    private List<ImageInfo> imageInfoList = new ArrayList<>();


    GridViewAdapter(Context c) {
        context = c;
    }

    int addImage(ImageInfo imageInfo) {
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

    void clearAll() {
        imageInfoList.clear();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageInfo imageInfo = getItem(position);
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        } else {
            imageView = (ImageView) convertView;
        }
        if (imageInfo.getImageId() != 0) {
            imageView.setImageDrawable(writeTextOnDrawable(imageInfo.getImageId(), imageInfo.getImageText()));
        }
        if (imageInfo.getImagePath() != null) {

            int MY_DIP_VALUE = 140; //5dp
            int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    MY_DIP_VALUE, context.getResources().getDisplayMetrics());
            Bitmap bitmap = BitmapFactory.decodeFile(imageInfo.getImagePath());
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, pixel, pixel, false);
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

                    imageView.setImageBitmap(writeTextOnBitmap(bitmap, imageInfo.getImageText()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("LOCKED", false)) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FolderOne().showOptions(position);
                }
            });

        } else {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(position);
                    Toast.makeText(context, ""+position, Toast.LENGTH_LONG).show();
                }
            });
        }
        return imageView;
    }

    private void playSound(int position) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (folder > 1) {
            int[] imageIds = new int[FolderOne.previousPosition.size() + 1];
            for (int i = 0; i < FolderOne.previousPosition.size(); i++) {
                imageIds[i] = FolderOne.previousPosition.get(i);
            }
            imageIds[imageIds.length - 1] = position;
            String soundPath = new FollowBookDB(context).getSound(imageIds);
            if (soundPath != null &&!soundPath.equals("default")) {
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (folder > 1) {
            int[] imageIds = new int[FolderOne.previousPosition.size() + 1];
            for (int i = 0; i < FolderOne.previousPosition.size(); i++) {
                imageIds[i] = FolderOne.previousPosition.get(i);
            }
            imageIds[imageIds.length - 1] = position;
            String videoPath = new FollowBookDB(context).getVideo(imageIds);
            if (videoPath != null &&!videoPath.equals("default")) {
                try {
                    FolderOne.videoLayout.reset();
                    FolderOne.videoLayout.setShouldAutoplay(true);
                    FolderOne.videoLayout.setVisibility(View.VISIBLE);
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
            } else {
                playYoutubeVideo(position);
            }
        } else {
            String videoPath = new FollowBookDB(context).getVideo(new int[]{position});
            if (!videoPath.equals("default")) {
                FolderOne.videoLayout.reset();
                FolderOne.videoLayout.setShouldAutoplay(true);
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
            } else {
                playYoutubeVideo(position);
            }
        }
    }

    private static void playYoutubeVideo(int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        int folder = sharedPreferences.getInt("FOLDER", 1);
        if (folder > 1) {
            Log.i("TAG", "playYoutubeVideo: " + folder);
            int[] imageIds = new int[FolderOne.previousPosition.size() + 1];
            for (int i = 0; i < FolderOne.previousPosition.size(); i++) {
                imageIds[i] = FolderOne.previousPosition.get(i);
            }
            imageIds[imageIds.length - 1] = position;
            String link = new FollowBookDB(context).getYouTubeLink(imageIds);
            if (link != null &&!link.equals("default")) {
                Config.setYoutubeLink(link);
                Config.setPosition(position);
                context.startActivity(new Intent(context, YouTubePlayerUtils.class));
            } else {
                FolderOne.previousPosition.add(position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("FOLDER", sharedPreferences.getInt("FOLDER", 1) + 1);
                editor.apply();
                new FolderOne().initialSetup();
            }
        } else {
            String link = new FollowBookDB(context).getYouTubeLink(new int[]{position});
            if (!link.equals("default")) {
                Config.setYoutubeLink(link);
                context.startActivity(new Intent(context, YouTubePlayerUtils.class));
            } else {

                Log.i("TAG", "playYoutubeVideo: Position " + position);
                FolderOne.previousPosition.add(position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("FOLDER", sharedPreferences.getInt("FOLDER", 1) + 1);
                editor.apply();
                new FolderOne().initialSetup();
            }
        }
    }

    private BitmapDrawable writeTextOnDrawable(int drawableId, String text) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        int MY_SP_VALUE = 18; //5sp

        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                MY_SP_VALUE, context.getResources().getDisplayMetrics());
        paint.setTextSize(pixel);
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        Canvas canvas = new Canvas(bm);

        canvas.drawText(text, canvas.getWidth() / 2, canvas.getHeight() - 10, paint);
        return new BitmapDrawable(context.getResources(), bm);
    }

    private Bitmap writeTextOnBitmap(Bitmap bitmap, String text) {
        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        int MY_SP_VALUE = 18; //5sp
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                MY_SP_VALUE, context.getResources().getDisplayMetrics());
        paint.setTextSize(pixel);
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, canvas.getWidth() / 2, canvas.getHeight() - 10, paint);
        return bitmap;
    }
}
