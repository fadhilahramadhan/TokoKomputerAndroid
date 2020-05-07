package info.Fadhilah_Ramadhan.TokoKomputer;

import android.app.ProgressDialog;
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

public class RegisterActivity extends AppCompatActivity {

    private ActionBar toolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String TAG = "RegisterActivity";
    EditText nama,email,password,repassword;
    Button daftar;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Akun Baru");
        pd = new ProgressDialog(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nama = findViewById(R.id.tnama);
        email = findViewById(R.id.tspec);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        daftar = findViewById(R.id.btn_daftar);


        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nama.getText().toString().length() == 0 || email.getText().toString().length() == 0 || password.getText().toString().length() == 0 ||repassword.getText().toString().length() == 0)
                {
                   Toast.makeText(RegisterActivity.this,"Masih ada data yang kosong",Toast.LENGTH_SHORT).show();
                }else{

                    if(!password.getText().toString().equals(repassword.getText().toString())){
                        Toast.makeText(RegisterActivity.this,"Password tidak sama",Toast.LENGTH_SHORT).show();
                    }else{
                        proses_daftar();
                    }
                }
            }
        });
    }


    private void proses_daftar(){
        pd.setMessage("Daftar akun baru..");
        pd.setCancelable(false);
        pd.show();
        StringRequest daftar = new StringRequest(Request.Method.POST, URL.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.cancel();
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getString("code").equals("2")){
                                Toast.makeText(RegisterActivity.this, "Pesan : " + res.getString("message"), Toast.LENGTH_SHORT).show();
                            }else if (res.getString("code").equals("1")){
                                Toast.makeText(RegisterActivity.this, "Pesan : " + res.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Pesan : Error ", Toast.LENGTH_SHORT).show();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(RegisterActivity.this,"Pesan : Gagal daftar akun baru", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new HashMap<>();
                map.put("nama", nama.getText().toString());
                map.put("email", email.getText().toString());
                map.put("password", password.getText().toString());
                return map;
            }

        };
        MyApplication.getInstance().addToRequestQueue(daftar);
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
