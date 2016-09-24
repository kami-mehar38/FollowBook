package abbottabad.comsats.followbook;

/**
 * This project FollowBook is created by Kamran Ramzan on 24-Sep-16.
 */
public class Config {
    // Google Console APIs developer key
    // Replace this key with your's
    public static final String DEVELOPER_KEY = "AIzaSyDkLartz1RLM5jUWn0Cx3it3bK2vaibgeg";

    private static String YOUTUBE_LINK;

    public static String getYoutubeLink() {
        return YOUTUBE_LINK;
    }

    public static void setYoutubeLink(String youtubeLink) {
        YOUTUBE_LINK = youtubeLink;
    }
}
