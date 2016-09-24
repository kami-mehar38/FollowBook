package abbottabad.comsats.followbook;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * This project FollowBook is created by Kamran Ramzan on 20-Sep-16.
 */
public class ImageInfo {
    int imageId;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    String imagePath = null;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    String soundPath = null;

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }
}
