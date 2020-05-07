package info.Fadhilah_Ramadhan.TokoKomputer;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
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

public class LoginActivity extends AppCompatActivity {

    private ActionBar toolbar;
    private SharedPreferences sharedPreferences,userlevel;
    private SharedPreferences.Editor editor, editor_user_level;
    private static final String TAG = "LoginActivity";
    EditText email,password;
    TextView daftar;
    Button login;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Login");
        pd = new ProgressDialog(this);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        daftar= findViewById(R.id.daftar);

        login =  findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().length() == 0  || password.getText().toString().length() == 0 ){
                    Toast.makeText(LoginActivity.this, "Email atau Password masih kosong", Toast.LENGTH_LONG).show();
                }else{
                    proses_login();
                }
            }
        });

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void proses_login(){
        pd.setMessage("Sedang proses login..");
        pd.setCancelable(false);
        pd.show();

        StringRequest login = new StringRequest(Request.Method.POST, URL.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.cancel();
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getString("code").equals("0")){
                                Toast.makeText(LoginActivity.this, "Pesan : " + res.getString("message"), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this, "Pesan : " + res.getString("message"), Toast.LENGTH_SHORT).show();

                                sharedPreferences = getSharedPreferences("sesi", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putInt("sesi",Integer.parseInt(res.getString("id")));
                                editor.commit();

                                userlevel = getSharedPreferences("userlevel", MODE_PRIVATE);
                                editor_user_level = userlevel.edit();
                                editor_user_level.putInt("userlevel",Integer.parseInt(res.getString("userlevel")));
                                editor_user_level.commit();

                                int userlevel_ = userlevel.getInt("userlevel",1);
                                if(userlevel_ == 0){
                                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                    ComponentName cn = intent.getComponent();
                                    Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                                    startActivity(mainIntent);
                                    LoginActivity.this.finish();
                                }else {
                                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    LoginActivity.this.finish();
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Toast.makeText(LoginActivity.this,"Pesan : Gagal saat mencoba login", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new HashMap<>();
                map.put("email", email.getText().toString());
                map.put("password", password.getText().toString());

                return map;
            }

        };
        MyApplication.getInstance().addToRequestQueue(login);
    }
}
