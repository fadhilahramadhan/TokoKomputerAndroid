package info.Fadhilah_Ramadhan.TokoKomputer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import info.Fadhilah_Ramadhan.TokoKomputer.admin.BarangFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.admin.DashboardFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.admin.HomeFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.admin.TransaksiFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.BelumLoginFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.CartFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.FavoritFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.ProfileFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.StoreFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.helper.BottomNavigationBehavior;

public class AdminActivity extends AppCompatActivity {

    private ActionBar toolbar;
    private SharedPreferences log_posisi,log_sesi,log_userlevel;
    private SharedPreferences.Editor editor_posisi,editor_sesi,editor_userlevel;
    private BottomNavigationView navigation;
    int sesi,posisi;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        toolbar = getSupportActionBar();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        // load the store fragment by default



        log_posisi = getSharedPreferences("load", MODE_PRIVATE);
        log_sesi = getSharedPreferences("sesi", MODE_PRIVATE);
        log_userlevel = getSharedPreferences("userlevel", MODE_PRIVATE);

        posisi = log_posisi.getInt("load", 0);
        sesi = log_sesi.getInt("sesi", 0);


        editor_posisi = log_posisi.edit();
        editor_sesi = log_sesi.edit();
        editor_userlevel = log_userlevel.edit();



        Fragment fragment;
        switch (posisi) {
            case 1:

                toolbar.setTitle("Home");
                fragment = new HomeFragment();
                loadFragment(fragment);
                editor_posisi.putInt("load", 1);
                editor_posisi.commit();
                navigation.getMenu().getItem(0).setChecked(true);
                break;
            case 2:
                toolbar.setTitle("Dashboard");
                fragment = new DashboardFragment();
                loadFragment(fragment);
                editor_posisi.putInt("load", 1);
                editor_posisi.commit();
                navigation.getMenu().getItem(1).setChecked(true);
                break;
            case 3:
                toolbar.setTitle("Barang");
                fragment = new BarangFragment();

                loadFragment(fragment);
                editor_posisi.putInt("load", 1);
                editor_posisi.commit();
                navigation.getMenu().getItem(2).setChecked(true);
                break;
            case 4:
                toolbar.setTitle("Transaksi");
                fragment = new TransaksiFragment();
                loadFragment(fragment);
                editor_posisi.putInt("load", 1);
                editor_posisi.commit();
                navigation.getMenu().getItem(3).setChecked(true);
                break;
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem login_menu = menu.findItem(R.id.login);
        MenuItem logout_menu = menu.findItem(R.id.logout);

        if(sesi == 0){
            login_menu.setVisible(true);
            logout_menu.setVisible(false);
        }else{
            login_menu.setVisible(false);
            logout_menu.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                editor_sesi.putInt("sesi", 0);
                                editor_sesi.commit();
                                editor_posisi.putInt("load", 1);
                                editor_posisi.commit();
                                editor_userlevel.putInt("userlevel",1);
                                editor_userlevel.commit();
                                finishAffinity();
                                AdminActivity.this.startActivity(new Intent(AdminActivity.this, MainActivity.class));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Anda yakin ingin Logout?").setPositiveButton("Ya", dialogClickListener)
                        .setNegativeButton("tidak", dialogClickListener).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    toolbar.setTitle("Home");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.dashboard:

                    toolbar.setTitle("Dashboard");
                    if(sesi == 0){
                        editor_posisi.putInt("load", 1);
                        editor_posisi.commit();
                        fragment = new BelumLoginFragment();
                        loadFragment(fragment);

                    }else {
                        fragment = new DashboardFragment();
                        loadFragment(fragment);
                    }
                    return true;
                case R.id.barang:

                    toolbar.setTitle("Barang");
                    if(sesi == 0){
                        editor_posisi.putInt("load", 1);
                        editor_posisi.commit();
                        fragment = new BelumLoginFragment();
                        loadFragment(fragment);
                    }else {
                        fragment = new BarangFragment();
                        loadFragment(fragment);
                    }
                    return true;
                case R.id.transaksi:
                    toolbar.setTitle("Transaksi");
                    if(sesi == 0){
                        editor_posisi.putInt("load", 1);
                        editor_posisi.commit();


                        fragment = new BelumLoginFragment();
                        loadFragment(fragment);
                    }else {
                        fragment = new TransaksiFragment();
                        loadFragment(fragment);
                    }
                    return true;
            }

            return false;
        }
    };


    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

}

