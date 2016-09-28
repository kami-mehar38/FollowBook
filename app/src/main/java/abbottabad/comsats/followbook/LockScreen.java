package abbottabad.comsats.followbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static abbottabad.comsats.followbook.R.anim.popup;

/**
 * This project FollowBook is created by Kamran Ramzan on 21-Sep-16.
 */
public class LockScreen extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String PREFERENCE_FILE_KEY = "abbottabad.comsats.followbook";
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ImageView lock = (ImageView) findViewById(R.id.lock);
        lock.setOnClickListener(this);
        ImageView unlock = (ImageView) findViewById(R.id.unlock);
        unlock.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lock: {
                editor.putBoolean("LOCKED", true);
                editor.commit();
                startActivity(new Intent(LockScreen.this, FolderOne.class));
                break;
            }
            case R.id.unlock:{
                editor.putBoolean("LOCKED", false);
                editor.commit();
                startActivity(new Intent(LockScreen.this, FolderOne.class));
                break;
            }
        }
    }
}
