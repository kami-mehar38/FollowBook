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
class FollowBookDB extends SQLiteOpenHelper {
    private final static int VERSION = 1;
    private final static String DATABASE_NAME = "followbook.db";
    private final SharedPreferences sharedPreferences;

    FollowBookDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
        sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    private void createTable(SQLiteDatabase db) {
        String querryString = "";
        String querryString1 = "";
        String querryString2 = "";
        String querryString3 = "";

        int value = sharedPreferences.getInt("FOLDER", 1);
        for (int i = 1; i <= value; i++) {
            querryString += FollowBookInfo.innerClass.Image_Id + i;
            querryString += " INTEGER";
            querryString += ",";

            querryString1 += FollowBookInfo.innerClass.Image_Id + i;
            querryString1 += " INTEGER";
            querryString1 += ",";

            querryString2 += FollowBookInfo.innerClass.Image_Id + i;
            querryString2 += " INTEGER";
            querryString2 += ",";

            querryString3 += FollowBookInfo.innerClass.Image_Id + i;
            querryString3 += " INTEGER";
            querryString3 += ",";
        }
        querryString += FollowBookInfo.innerClass.Image_Path + " TEXT,";
        querryString += FollowBookInfo.innerClass.Image_Text + " TEXT";
        querryString1 += FollowBookInfo.innerClass.Sound_Path + " TEXT";
        querryString2 += FollowBookInfo.innerClass.Video_Path + " TEXT";
        querryString3 += FollowBookInfo.innerClass.YouTube_Path + " TEXT";

        String query = "create table if not exists " + FollowBookInfo.innerClass.Table_Image + value + "(" + querryString + ");";
        db.execSQL(query);

        String query1 = "create table if not exists " + FollowBookInfo.innerClass.Table_Sound + value + "(" + querryString1 + ");";
        db.execSQL(query1);

        String query2 = "create table if not exists " + FollowBookInfo.innerClass.Table_Video + value + "(" + querryString2 + ");";
        db.execSQL(query2);

        String query3 = "create table if not exists " + FollowBookInfo.innerClass.Table_YouTube + value + "(" + querryString3 + ");";
        db.execSQL(query3);
    }

    void upgrade() {
        SQLiteDatabase db = this.getWritableDatabase();
        createTable(db);
    }

    void addImage(int image_id[], String image_path, String image_text) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        int i = 1;
        for (int anImage_id : image_id) {
            values.put(FollowBookInfo.innerClass.Image_Id + i, anImage_id);
            i++;
        }
        values.put(FollowBookInfo.innerClass.Image_Path, image_path);
        values.put(FollowBookInfo.innerClass.Image_Text, image_text);
        db.insert(FollowBookInfo.innerClass.Table_Image + value, null, values);
        db.close();
    }

    void updateImagePath(String imagePath, int[] imageId) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int i = 0;
        String whereClause = "";
        String[] compareClause = new String[imageId.length];
        for (int anImage_id : imageId) {
            compareClause[i] = String.valueOf(anImage_id);
            if (i == 0) {
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ?";
            } else {
                whereClause += " AND ";
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ? ";
            }
            i++;
        }

        values.put(FollowBookInfo.innerClass.Image_Path, imagePath);
        db.update(FollowBookInfo.innerClass.Table_Image + value, values, whereClause, compareClause);
        db.close();
    }

    void updateImageText(String imageText, int[] imageId) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int i = 0;
        String whereClause = "";
        String[] compareClause = new String[imageId.length];
        for (int anImage_id : imageId) {
            compareClause[i] = String.valueOf(anImage_id);
            if (i == 0) {
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ?";
            } else {
                whereClause += " AND ";
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ? ";
            }
            i++;
        }

        values.put(FollowBookInfo.innerClass.Image_Text, imageText);
        db.update(FollowBookInfo.innerClass.Table_Image + value, values, whereClause, compareClause);
        db.close();
    }

    List<String> showIcons(int[] imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int value = sharedPreferences.getInt("FOLDER", 1);

        String selectQuery;
        if (value == 1) {
            selectQuery = "SELECT " + FollowBookInfo.innerClass.Image_Path + ", " + FollowBookInfo.innerClass.Image_Text + " FROM " + FollowBookInfo.innerClass.Table_Image + value;
        } else {
            int i = 1;
            selectQuery = "SELECT " + FollowBookInfo.innerClass.Image_Path + ", " + FollowBookInfo.innerClass.Image_Text + " FROM " + FollowBookInfo.innerClass.Table_Image + value;

            for (int anImage_id : imageId) {
                if (i == 1) {
                    selectQuery += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
                } else {
                    selectQuery += " AND ";
                    selectQuery += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
                }
                i++;
            }
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        List<String> imagePaths = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            imagePaths.add(cursor.getString(0));
            imagePaths.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return imagePaths;
    }

    void addSound(int sound_id[], String sound_path) {
        Log.i("TAG", "addSound: " + sound_path);
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        int i = 1;
        for (int aSound_id : sound_id) {
            values.put(FollowBookInfo.innerClass.Image_Id + i, aSound_id);
            i++;
        }
        values.put(FollowBookInfo.innerClass.Sound_Path, sound_path);
        db.insert(FollowBookInfo.innerClass.Table_Sound + value, null, values);
        db.close();
        Log.e("database operations", "one row inserted...");
    }

    void updateSoundPath(String soundPath, int[] imageId) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int i = 0;
        String whereClause = "";
        String[] compareClause = new String[imageId.length];
        for (int anImage_id : imageId) {
            compareClause[i] = String.valueOf(anImage_id);
            Log.e("database operations", anImage_id + "");

            if (i == 0) {

                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ?";
            } else {
                whereClause += " AND ";
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ? ";
            }
            i++;
        }

        values.put(FollowBookInfo.innerClass.Sound_Path, soundPath);
        db.update(FollowBookInfo.innerClass.Table_Sound + value, values, whereClause, compareClause);
        db.close();
    }

    String getSound(int[] imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int value = sharedPreferences.getInt("FOLDER", 1);

        String selectQuery;

        int i = 1;
        selectQuery = "SELECT " + FollowBookInfo.innerClass.Sound_Path + " FROM " + FollowBookInfo.innerClass.Table_Sound + value;

        for (int anImage_id : imageId) {
            if (i == 1) {
                selectQuery += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
            } else {
                selectQuery += " AND ";
                selectQuery += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
            }
            i++;

        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String soundPath = null;
        while (!cursor.isAfterLast()) {
            soundPath = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return soundPath;
    }

    void addVideo(int video_id[], String sound_path) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        int i = 1;
        for (int aVideo_id : video_id) {
            values.put(FollowBookInfo.innerClass.Image_Id + i, aVideo_id);
            i++;
        }
        values.put(FollowBookInfo.innerClass.Video_Path, sound_path);
        db.insert(FollowBookInfo.innerClass.Table_Video + value, null, values);
        db.close();
    }

    void updateVideoPath(String videoPath, int[] imageId) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int i = 0;
        String whereClause = "";
        String[] compareClause = new String[imageId.length];
        for (int anImage_id : imageId) {
            compareClause[i] = String.valueOf(anImage_id);
            if (i == 0) {
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ?";
            } else {
                whereClause += " AND ";
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ? ";
            }
            i++;
        }


        values.put(FollowBookInfo.innerClass.Video_Path, videoPath);
        db.update(FollowBookInfo.innerClass.Table_Video + value, values, whereClause, compareClause);
        db.close();
    }

    String getVideo(int[] imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int value = sharedPreferences.getInt("FOLDER", 1);

        String selectQuery;

        int i = 1;
        selectQuery = "SELECT " + FollowBookInfo.innerClass.Video_Path + " FROM " + FollowBookInfo.innerClass.Table_Video + value;

        for (int anImage_id : imageId) {
            if (i == 1) {
                selectQuery += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
            } else {
                selectQuery += " AND ";
                selectQuery += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
            }
            i++;

        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String soundPath = null;
        while (!cursor.isAfterLast()) {
            soundPath = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return soundPath;
    }

    void addYouTubeLink(int link_id[], String link_path) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        int i = 1;
        for (int aLink_id : link_id) {
            values.put(FollowBookInfo.innerClass.Image_Id + i, aLink_id);
            i++;
        }
        values.put(FollowBookInfo.innerClass.YouTube_Path, link_path);
        db.insert(FollowBookInfo.innerClass.Table_YouTube + value, null, values);
        db.close();
        db.close();
    }

    void updateYouTubeLink(String linkPath, int[] linkId) {
        int value = sharedPreferences.getInt("FOLDER", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int i = 0;
        String whereClause = "";
        String[] compareClause = new String[linkId.length];
        for (int anImage_id : linkId) {
            compareClause[i] = String.valueOf(anImage_id);
            if (i == 0) {
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ?";
            } else {
                whereClause += " AND ";
                whereClause += FollowBookInfo.innerClass.Image_Id + (i + 1) + " = ? ";
            }
            i++;
        }

        values.put(FollowBookInfo.innerClass.YouTube_Path, linkPath);
        db.update(FollowBookInfo.innerClass.Table_YouTube + value, values, whereClause, compareClause);
        db.close();
    }

    String getYouTubeLink(int[] imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int value = sharedPreferences.getInt("FOLDER", 1);

        String selectQuery;

        int i = 1;
        selectQuery = "SELECT " + FollowBookInfo.innerClass.YouTube_Path + " FROM " + FollowBookInfo.innerClass.Table_YouTube + value;

        for (int anImage_id : imageId) {
            if (i == 1) {
                selectQuery += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
            } else {
                selectQuery += " AND ";
                selectQuery += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
            }
            i++;

        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String soundPath = null;
        while (!cursor.isAfterLast()) {
            soundPath = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return soundPath;
    }

    void Delete(int[] imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int value = sharedPreferences.getInt("FOLDER", 1);
        int count = sharedPreferences.getInt("COUNT", 1);

        for (int j = value; j <= count; j++) {
            String deleteQuery;
            String updateQuery;
            deleteQuery = "DELETE FROM " + FollowBookInfo.innerClass.Table_Image + j;
            updateQuery = "UPDATE " + FollowBookInfo.innerClass.Table_Image + j;
            int i=1;
            int k=value;
            for (int anImage_id : imageId) {
                if(i==1) {
                    deleteQuery += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
                    updateQuery += " SET " + (FollowBookInfo.innerClass.Image_Id + k) + " = " + (FollowBookInfo.innerClass.Image_Id + value) +
                            " -1 WHERE " + FollowBookInfo.innerClass.Image_Id + k + " > " + anImage_id;
                }
               else {
                    deleteQuery += " AND ";
                    deleteQuery += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;

                    updateQuery += " AND ";
                    k--;
                    updateQuery += FollowBookInfo.innerClass.Image_Id + k + " = " + anImage_id;
                }

                i++;
            }
            db.execSQL(deleteQuery);
            db.execSQL(updateQuery);

            String deleteQuerySound;
            String updateQuerySound;
            deleteQuerySound = "DELETE FROM " + FollowBookInfo.innerClass.Table_Sound + j;
            updateQuerySound = "UPDATE " + FollowBookInfo.innerClass.Table_Sound + j;
            i=1;
             k=value;
            for (int anImage_id : imageId) {
              if(i==1) {
                  deleteQuerySound += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
                  updateQuerySound += " SET " + (FollowBookInfo.innerClass.Image_Id + k) + " = " + (FollowBookInfo.innerClass.Image_Id + value) +
                          " -1 WHERE " + FollowBookInfo.innerClass.Image_Id + k + " > " + anImage_id;
              }
                else{
                  deleteQuerySound += " AND ";
                  deleteQuerySound += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;

                  updateQuerySound += " AND ";
                  k--;
                  updateQuerySound += FollowBookInfo.innerClass.Image_Id + k + " = " + anImage_id;
              }
                i++;
            }
            db.execSQL(deleteQuerySound);
            db.execSQL(updateQuerySound);

            Log.i("FUCK", "delete fuck off");

            String deleteQueryVideo;
            String updateQueryVideo;
            deleteQueryVideo = "DELETE FROM " + FollowBookInfo.innerClass.Table_Video + j;
            updateQueryVideo = "UPDATE " + FollowBookInfo.innerClass.Table_Video + j;
            i=1;
             k=value;
            for (int anImage_id : imageId) {
                if(i==1) {
                    deleteQueryVideo += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
                    updateQueryVideo += " SET " + (FollowBookInfo.innerClass.Image_Id + k) + " = " + (FollowBookInfo.innerClass.Image_Id + value) +
                            " -1 WHERE " + FollowBookInfo.innerClass.Image_Id + k + " > " + anImage_id;
                }
                else{
                    deleteQueryVideo += " AND ";
                    deleteQueryVideo += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;

                    updateQueryVideo += " AND ";
                    k--;
                    updateQueryVideo += FollowBookInfo.innerClass.Image_Id + k + " = " + anImage_id;
                }
                i++;
            }


            db.execSQL(deleteQueryVideo);
            db.execSQL(updateQueryVideo);


            String deleteQueryYoutubeLink;

            String updateQueryLink;

            deleteQueryYoutubeLink = "DELETE FROM " + FollowBookInfo.innerClass.Table_YouTube + j;
            updateQueryLink = "UPDATE " + FollowBookInfo.innerClass.Table_YouTube + j;

            i=1;
             k=value;
            for (int anImage_id : imageId) {
                if(i==1) {
                    deleteQueryYoutubeLink += " WHERE " + FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;
                    updateQueryLink += " SET " + (FollowBookInfo.innerClass.Image_Id + k) + " = " + (FollowBookInfo.innerClass.Image_Id + value) +
                            " -1 WHERE " + FollowBookInfo.innerClass.Image_Id + k + " > " + anImage_id;
                }
                else{
                    deleteQueryYoutubeLink += " AND ";
                    deleteQueryYoutubeLink += FollowBookInfo.innerClass.Image_Id + i + " = " + anImage_id;

                    updateQueryLink += " AND ";
                    k--;
                    updateQueryLink += FollowBookInfo.innerClass.Image_Id + k + " = " + anImage_id;
                }
i++;
            }
            db.execSQL(deleteQueryYoutubeLink);
            db.execSQL(updateQueryLink);
        }
        db.close();
        value++;

    }
}
