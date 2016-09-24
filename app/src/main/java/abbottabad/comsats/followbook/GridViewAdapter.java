package abbottabad.comsats.followbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

    private Context context;
    private List<ImageInfo> imageInfoList = new ArrayList<>();
    private int orientation;

    public GridViewAdapter(Context c) {
        this.context = c;
    }

    public int addImage(ImageInfo imageInfo){
        int position = imageInfoList.size();
        imageInfoList.add(position ,imageInfo);
        return position;
    }

    public int getCount() {
        return imageInfoList.size();
    }

    public ImageInfo getItem(int position){
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
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        } else {
            imageView = (ImageView) convertView;
        }
        if (imageInfo.getImageId() != 0) {
            imageView.setImageResource(imageInfo.getImageId());
        }
        if (imageInfo.getImagePath() != null){

            Bitmap bitmap = BitmapFactory.decodeFile(imageInfo.getImagePath());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 200, 200, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imageInfo.getImagePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
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
                    String soundPath = new FollowBookDB(context).getSound(position);
                    if (!soundPath.equals("default")) {
                        new RecordAudio().startPlaying(soundPath);
                    }

                    String videoPath = new FollowBookDB(context).getVideo(position);
                    if (videoPath != null && !videoPath.equals("default")) {
                        /*FolderOne.videoLayout.reset();
                        FolderOne.videoLayout.setVisibility(View.VISIBLE);
                        FolderOne.videoLayout.setVideoURI(Uri.parse("https://www.youtube.com/watch?v=AaXaig_43lU"));*/
                        context.startActivity(new Intent(context, YouTubePlayerUtils.class));
                    }
                }
            });
        }
        return imageView;
    }

}
