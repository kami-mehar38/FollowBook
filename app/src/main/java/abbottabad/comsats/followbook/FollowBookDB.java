package abbottabad.comsats.followbook;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This project FollowBook is created by Abdul Hakeem on 9/21/2016.
 */
public class FollowBookDB extends SQLiteOpenHelper {
    public final static int VERSION =1;
    public final static String DATABASE_NAME = "followbook.db";
    private final SharedPreferences sharedPreferences;

    public FollowBookDB(Context context) {
        super(context, DATABASE_NAME,null, VERSION);
        final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
        sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        Log.e("database operations","database created/open...");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String querryString = FollowBookInfo.innerClass.Image_Id+" INTEGER,";
        String querryString1 = FollowBookInfo.innerClass.Image_Id+" INTEGER,";
        String querryString2 = FollowBookInfo.innerClass.Image_Id+" INTEGER,";
        String querryString3 = FollowBookInfo.innerClass.Image_Id+" INTEGER,";

        int value =  sharedPreferences.getInt("FOLDER", 1);
        for (int i=1;i<value;i++){
            querryString += FollowBookInfo.innerClass.Image_Id+i;
            querryString +=" INTEGER";
            querryString +=",";

            querryString1 += FollowBookInfo.innerClass.Image_Id+i;
            querryString1 +=" INTEGER";
            querryString1 +=",";

            querryString2 += FollowBookInfo.innerClass.Image_Id+i;
            querryString2 +=" INTEGER";
            querryString2 +=",";

            querryString3 += FollowBookInfo.innerClass.Image_Id+i;
            querryString3 +=" INTEGER";
            querryString3 +=",";
        }
        querryString += FollowBookInfo.innerClass.Image_Path+ " TEXT";
        querryString1 += FollowBookInfo.innerClass.Sound_Path+ " TEXT";
        querryString2 += FollowBookInfo.innerClass.Video_Path+ " TEXT";
        querryString3 += FollowBookInfo.innerClass.YouTube_Path+ " TEXT";

        String query="create table if not exists "+ FollowBookInfo.innerClass.Table_Image+value+ "("+querryString+");";
        db.execSQL(query);

        String query1="create table if not exists "+ FollowBookInfo.innerClass.Table_Sound+value+ "("+querryString1+");";
        db.execSQL(query1);

        String query2="create table if not exists "+ FollowBookInfo.innerClass.Table_Video+value+ "("+querryString2+");";
        db.execSQL(query2);

        String query3="create table if not exists "+ FollowBookInfo.innerClass.Table_YouTube+value+ "("+querryString3+");";
        db.execSQL(query3);

        Log.e("database" +
                " operations","tables created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addImage(int image_id[], String image_path){
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        for (int anImage_id : image_id) {
            values.put(FollowBookInfo.innerClass.Image_Id, anImage_id);
        }
        values.put(FollowBookInfo.innerClass.Image_Path, image_path);
        db.insert(FollowBookInfo.innerClass.Table_Image+value, null, values);
        db.close();
        Log.e("database operations","one row inserted...");
    }

    public void addSound(int sound_id[], String sound_path){
        Log.i("TAG", "addSound: "+ sound_path);
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        for (int aSound_id : sound_id) {
            values.put(FollowBookInfo.innerClass.Image_Id, aSound_id);
        }
        values.put(FollowBookInfo.innerClass.Sound_Path, sound_path);
        db.insert(FollowBookInfo.innerClass.Table_Sound+value, null, values);
        db.close();
        Log.e("database operations","one row inserted...");
    }

    public void updateSoundPath(String soundPath, int imageId){
        Log.i("TAG", "updateSoundPath: "+ soundPath);
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FollowBookInfo.innerClass.Sound_Path,soundPath);
        db.update(FollowBookInfo.innerClass.Table_Sound+value, values, FollowBookInfo.innerClass.Image_Id +
                " = ? ",new String[]{ String.valueOf(imageId) });
    }

    public String getSound(int imageId){
        SQLiteDatabase db = this.getReadableDatabase();
        int value =  sharedPreferences.getInt("FOLDER", 1);
        Cursor cursor = db.query(FollowBookInfo.innerClass.Table_Sound+value, new String[] {FollowBookInfo.innerClass.Sound_Path},
                FollowBookInfo.innerClass.Image_Id + "=?",
                new String[] { String.valueOf(imageId) }, null, null, null, null);
        cursor.moveToFirst();
        String soundPath = null;
        while (!cursor.isAfterLast()){
            soundPath = cursor.getString(0);
            Log.i("TAG", "getSound: "+ soundPath);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return soundPath;
    }

    public void addVideo(int video_id[], String sound_path){
        Log.i("TAG", "addVideo: "+ sound_path);
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        for (int aVideo_id : video_id) {
            values.put(FollowBookInfo.innerClass.Image_Id, aVideo_id);
        }
        values.put(FollowBookInfo.innerClass.Video_Path, sound_path);
        db.insert(FollowBookInfo.innerClass.Table_Video+value, null, values);
        db.close();
        Log.e("database operations","one row inserted...");
    }

    public void updateVideoPath(String videoPath, int imageId){
        Log.i("TAG", "updateVideoPath: "+ videoPath);
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FollowBookInfo.innerClass.Video_Path,videoPath);
        db.update(FollowBookInfo.innerClass.Table_Video+value, values, FollowBookInfo.innerClass.Image_Id +
                " = ? ",new String[]{ String.valueOf(imageId) });
    }

    public String getVideo(int imageId){
        SQLiteDatabase db = this.getReadableDatabase();
        int value =  sharedPreferences.getInt("FOLDER", 1);
        Cursor cursor = db.query(FollowBookInfo.innerClass.Table_Video+value, new String[] {FollowBookInfo.innerClass.Video_Path},
                FollowBookInfo.innerClass.Image_Id + "=?",
                new String[] { String.valueOf(imageId) }, null, null, null, null);
        cursor.moveToFirst();
        String soundPath = null;
        while (!cursor.isAfterLast()){
            soundPath = cursor.getString(0);
            Log.i("TAG", "getVideo: "+ soundPath);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return soundPath;
    }

    public void addYouTubeLink(int link_id[], String link_path){
        Log.i("TAG", "addVideo: "+ link_path);
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        for (int aLink_id : link_id) {
            values.put(FollowBookInfo.innerClass.Image_Id, aLink_id);
        }
        values.put(FollowBookInfo.innerClass.YouTube_Path, link_path);
        db.insert(FollowBookInfo.innerClass.Table_YouTube+value, null, values);
        db.close();
        Log.e("database operations","one row inserted...");
    }

    public void updateYouTubeLink(String linkPath, int linkId){
        Log.i("TAG", "updateVideoPath: "+ linkPath);
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FollowBookInfo.innerClass.YouTube_Path,linkPath);
        db.update(FollowBookInfo.innerClass.Table_YouTube+value, values, FollowBookInfo.innerClass.Image_Id +
                " = ? ",new String[]{ String.valueOf(linkId) });
    }

    public String getYouTubeLink(int linkId){
        SQLiteDatabase db = this.getReadableDatabase();
        int value =  sharedPreferences.getInt("FOLDER", 1);
        Cursor cursor = db.query(FollowBookInfo.innerClass.Table_YouTube+value, new String[] {FollowBookInfo.innerClass.YouTube_Path},
                FollowBookInfo.innerClass.Image_Id + "=?",
                new String[] { String.valueOf(linkId) }, null, null, null, null);
        cursor.moveToFirst();
        String soundPath = null;
        while (!cursor.isAfterLast()){
            soundPath = cursor.getString(0);
            Log.i("TAG", "getVideo: "+ soundPath);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return soundPath;
    }

    public List<String> showIcons(){
        SQLiteDatabase db = this.getReadableDatabase();
        int value =  sharedPreferences.getInt("FOLDER", 1);
        String selectQuery= "SELECT " + FollowBookInfo.innerClass.Image_Path + " FROM " +FollowBookInfo.innerClass.Table_Image+value;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        List<String> imagePaths = new ArrayList<>();
        while (!cursor.isAfterLast()){
            imagePaths.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return imagePaths;
    }
    public void updateImagePath(String imagePath, int imageId){
        int value =  sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FollowBookInfo.innerClass.Image_Path,imagePath);
        db.update(FollowBookInfo.innerClass.Table_Image+value, values, FollowBookInfo.innerClass.Image_Id +
                " = ? ",new String[]{ String.valueOf(imageId) });
    }


}
