package uni.UNI4950687.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uni.UNI4950687.R;
import uni.UNI4950687.fragments.HomeFragment;
import uni.UNI4950687.fragments.MeFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static Context mContext;

    private BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment = new HomeFragment();
    private MeFragment meFragment = new MeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
//        bottomNavigationView.setItemIconTintList(null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            case R.id.me:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, meFragment).commit();
                return true;
        }
        return false;
    }
}