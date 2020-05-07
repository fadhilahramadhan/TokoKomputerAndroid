package info.Fadhilah_Ramadhan.TokoKomputer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;

public class Profil_data_alamat extends AppCompatActivity {

    private ActionBar toolbar;
    EditText provinsi,kabupaten,kecamatan,alamat_lengkap;
    Button simpan;
    ProgressDialog pd;
    int user_id;
    String token;
    private SharedPreferences sharedPreferences,log_posisi;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_data_alamat);
        pd = new ProgressDialog(this);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Edit data profil");

        sharedPreferences = getSharedPreferences("sesi", MODE_PRIVATE);
        user_id = sharedPreferences.getInt("sesi", 0);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        provinsi = findViewById(R.id.provinsi);
        kabupaten = findViewById(R.id.kabupaten);
        kecamatan = findViewById(R.id.kecamatan);
        alamat_lengkap = findViewById(R.id.alamat_lengkap);

        simpan = findViewById(R.id.btn_simpan);
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(provinsi.getText().toString().length() < 1){
                    provinsi.requestFocus();
                    provinsi.setError("Provinsi tidak Boleh kosong");
                }else if(kabupaten.getText().toString().length() < 1){
                    kabupaten.requestFocus();
                    kabupaten.setError("Kota / Kabupaten tidak Boleh kosong");
                }else if(kecamatan.getText().toString().length() < 1){
                    kecamatan.requestFocus();
                    kecamatan.setError("Kecamatan tidak Boleh kosong");
                }else if(alamat_lengkap.getText().toString().length() < 1){
                    alamat_lengkap.requestFocus();
                    alamat_lengkap.setError("Alamat lengkap tidak Boleh kosong");
                }
                else{
                    simpan();
                }
            }
        });



        datauser();

    }
    private void datauser() {
        try {
            pd.setMessage("Mohon tunggu ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.VIEW_DATA_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                if(res.getString("provinsi").equals("null") ){
                                    provinsi.setText("");
                                }else
                                {
                                    provinsi.setText(res.getString("provinsi").toString());
                                }
                                if(res.getString("kab_kota").equals("null") ){
                                    kabupaten.setText("");
                                }else
                                {
                                    kabupaten.setText(res.getString("kab_kota").toString());
                                }
                                if(res.getString("kecamatan").equals("null") ){
                                    kecamatan.setText("");
                                }else
                                {
                                    kecamatan.setText(res.getString("kecamatan").toString());
                                }
                                if(res.getString("alamat_lengkap").equals("null") ){
                                    alamat_lengkap.setText("");
                                }else
                                {
                                    alamat_lengkap.setText(res.getString("alamat_lengkap").toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(Profil_data_alamat.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(user_id));

                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(Profil_data_alamat.this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }

    private void simpan() {
        try {
            pd.setMessage("Menyimpan data ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.SIMPAN_DATA_ALAMAT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(Profil_data_alamat.this,"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(getIntent().hasExtra("token") ){

                                Intent intent = new Intent(Profil_data_alamat.this, Pembayaran_keranjang.class);
                                startActivity(intent);
                                finish();
                            }else{
                            finish();
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(Profil_data_alamat.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(user_id));
                    map.put("provinsi", provinsi.getText().toString());
                    map.put("kab_kota", kabupaten.getText().toString());
                    map.put("kecamatan", kecamatan.getText().toString());
                    map.put("alamat_lengkap", alamat_lengkap.getText().toString());

                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(Profil_data_alamat.this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
