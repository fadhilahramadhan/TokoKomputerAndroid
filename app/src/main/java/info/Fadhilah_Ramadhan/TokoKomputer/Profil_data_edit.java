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

public class Profil_data_edit extends AppCompatActivity {

    private ActionBar toolbar;
    EditText nama,email,nohp;
    Button simpan;
    ProgressDialog pd;
    int user_id;
    private SharedPreferences sharedPreferences,log_posisi;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_data_edit);
        pd = new ProgressDialog(this);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Edit data profil");

        sharedPreferences = getSharedPreferences("sesi", MODE_PRIVATE);
        user_id = sharedPreferences.getInt("sesi", 0);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nama = findViewById(R.id.tnama);
        email = findViewById(R.id.tspec);
        nohp = findViewById(R.id.nohp);

        simpan = findViewById(R.id.btn_simpan);
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nama.getText().toString().length() < 1){
                    nama.requestFocus();
                    nama.setError("Nama tidak Boleh kosong");
                }else{
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
                                nama.setText(res.getString("nama").toString());
                                email.setText(res.getString("email").toString());
                                if(res.getString("nohp").equals("null") ){
                                    nohp.setText("");
                                }else
                                {
                                    nohp.setText(res.getString("nohp").toString());
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
                            Toast.makeText(Profil_data_edit.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Profil_data_edit.this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }

    private void simpan() {
        try {
            pd.setMessage("Menyimpan data ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.SIMPAN_DATA_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(Profil_data_edit.this,"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            log_posisi = Profil_data_edit.this.getSharedPreferences("load", Context.MODE_PRIVATE);
                            editor = log_posisi.edit();

                            editor.putInt("load", 4);
                            editor.commit();
                            Profil_data_edit.this.startActivity(new Intent(Profil_data_edit.this, MainActivity.class));
                            Profil_data_edit.this.finish();
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(Profil_data_edit.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(user_id));
                    map.put("nama", nama.getText().toString());
                    map.put("nohp", nohp.getText().toString());

                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(Profil_data_edit.this, String.valueOf(e), Toast.LENGTH_LONG).show();
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
