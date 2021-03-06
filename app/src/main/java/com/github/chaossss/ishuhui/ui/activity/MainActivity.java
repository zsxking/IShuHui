package com.github.chaossss.ishuhui.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.chaossss.ishuhui.R;
import com.github.chaossss.ishuhui.ui.fragment.CategoryFragment;
import com.github.chaossss.ishuhui.ui.fragment.NewestFragment;
import com.github.chaossss.ishuhui.ui.fragment.SelectedFragment;
import com.github.chaossss.ishuhui.ui.fragment.UpdateWeeklyFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String DRAWER_NEWEST = "Home";
    public static final String DRAWER_CATEGORY = "Category";
    public static final String DRAWER_UPDATE = "Update";
    public static final String DRAWER_SELECTED = "Selected";

    private String hideTag;

    private NewestFragment newestFragment;
    private CategoryFragment categoryFragment;
    private SelectedFragment selectedFragment;
    private UpdateWeeklyFragment updateWeeklyFragment;

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragments();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_menu);
        navigationView.setCheckedItem(R.id.nav_newest);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initFragments(){
        newestFragment = new NewestFragment();
        categoryFragment = new CategoryFragment();
        selectedFragment = new SelectedFragment();
        updateWeeklyFragment = new UpdateWeeklyFragment();

        switchFragment(DRAWER_NEWEST, newestFragment);
    }

    public void switchFragment(String tag, Fragment mFragment) {
        if (hideTag != null && hideTag.equals(tag)) return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment tagFragment = fragmentManager.findFragmentByTag(tag);

        if (tagFragment == null) {
            fragmentTransaction.add(R.id.main_container, mFragment, tag);
        } else {
            fragmentTransaction.show(tagFragment);
        }

        tagFragment = fragmentManager.findFragmentByTag(hideTag);

        if (tagFragment != null) {
            fragmentTransaction.hide(tagFragment);
        }

        hideTag = tag;
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        switch (menuItem.getItemId()) {
            case R.id.nav_newest:
                switchFragment(DRAWER_NEWEST, newestFragment);
                break;
            case R.id.nav_category:
                switchFragment(DRAWER_CATEGORY, categoryFragment);
                break;
            case R.id.nav_selected:
                switchFragment(DRAWER_SELECTED, selectedFragment);
                break;
            case R.id.nav_update:
                switchFragment(DRAWER_UPDATE, updateWeeklyFragment);
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return true;
    }
}
