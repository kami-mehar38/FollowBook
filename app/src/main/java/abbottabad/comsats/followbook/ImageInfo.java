package abbottabad.comsats.followbook;

/**
 * This project FollowBook is created by Kamran Ramzan on 20-Sep-16.
 */
class ImageInfo {
    private int imageId;

    String getImagePath() {
        return imagePath;
    }

    void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String imagePath = null;

    int getImageId() {
        return imageId;
    }

    void setImageId(int imageId) {
        this.imageId = imageId;
    }

    private String imageText;

    String getImageText() {
        return imageText;
    }

    void setImageText(String imageText) {
        this.imageText = imageText;
    }
}
