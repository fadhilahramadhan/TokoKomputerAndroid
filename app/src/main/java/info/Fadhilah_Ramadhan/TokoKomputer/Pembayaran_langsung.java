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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;

public class Pembayaran_langsung  extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = Pembayaran_langsung.class.getSimpleName();
    private ActionBar toolbar;
    TextView provinsi,kabupaten,kecamatan,alamat_lengkap,total_harga,alamat;
    Button btn_lengkapi,btn_bayar;

    ProgressDialog pd;
    int user_id,barang_id;
    double totalharga=0,_jumlah;
    boolean cek_alamat = false;
    Spinner spinner,bank;
    ImageView image;
    String gambar_url;

    private SharedPreferences sharedPreferences,log_sesi;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pembayaran_langsung);
        pd = new ProgressDialog(this);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Pembayaran");

        log_sesi = getSharedPreferences("sesi", MODE_PRIVATE);
        user_id = log_sesi.getInt("sesi", 0);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        spinner = (Spinner) findViewById(R.id.jasa);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jasa_pengiriman, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        bank = (Spinner) findViewById(R.id.bank);
        ArrayAdapter<CharSequence> adapter_bank = ArrayAdapter.createFromResource(this,
                R.array.BANK, android.R.layout.simple_spinner_item);
        adapter_bank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank.setAdapter(adapter_bank);
        bank.setOnItemSelectedListener(this);

        provinsi = findViewById(R.id.tprovinsi);
        kabupaten = findViewById(R.id.tkab_kota);
        kecamatan = findViewById(R.id.tkecamatan);
        alamat_lengkap = findViewById(R.id.talamat_lengkap);
        total_harga = findViewById(R.id.total_harga);
        btn_lengkapi = findViewById(R.id.btn_lengkapi);
        btn_bayar = findViewById(R.id.btn_bayar);
        alamat = findViewById(R.id.alamat);
        image = findViewById(R.id.image);
        total_harga = findViewById(R.id.total_harga);



        datauser();



        btn_lengkapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pembayaran_langsung.this, Profil_data_alamat.class);
                intent.putExtra("token", "keranjang");
                startActivity(intent);
                finish();
            }
        });

        btn_bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_lengkapi.getText().equals("Isi Data Alamat") ){
                    alamat.requestFocus();
                    alamat.setError("Alamat anda tidak lengkap, silahkan lengkapi alamat anda");
                }else{
                    transasksi();
                }
            }
        });
        getIncomingIntent();



    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
    }

    public void onNothingSelected(AdapterView<?> parent) {
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
                                Glide.with(Pembayaran_langsung.this)
                                        .asBitmap()
                                        .load("http://"+URL.HOST+URL.LOKASI_GAMBAR+res.getString("gambar").toString())
                                        .into(image);
                                if(res.getString("provinsi") == "null" ){
                                    provinsi.setText("");
                                }else
                                {
                                    provinsi.setText(res.getString("provinsi").toString());

                                }
                                if(res.getString("kab_kota") == "null" ){
                                    kabupaten.setText("");
                                }else
                                {
                                    kabupaten.setText(res.getString("kab_kota").toString());
                                }
                                if(res.getString("kecamatan") == "null" ){
                                    kecamatan.setText("");
                                }else
                                {
                                    kecamatan.setText(res.getString("kecamatan").toString());
                                }
                                if(res.getString("alamat_lengkap") == "null"){
                                    alamat_lengkap.setText("");
                                }else
                                {
                                    alamat_lengkap.setText(res.getString("alamat_lengkap").toString());
                                }
                                if(provinsi.getText().toString().length() < 1 || kabupaten.getText().toString().length() < 1 || kecamatan.getText().toString().length() < 1 || alamat_lengkap.getText().toString().length() < 1){
                                    cek_alamat = false;
                                    btn_lengkapi.setText("Isi Data Alamat");
                                }else{
                                    btn_lengkapi.setText("Edit Alamat");
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
                            Toast.makeText(Pembayaran_langsung.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(user_id));
                    map.put("barang_id", String.valueOf(barang_id));

                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(Pembayaran_langsung.this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }

    private void transasksi() {
        try {
            pd.setMessage("Mohon tunggu ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.TRANSAKSI_LANGSUNG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(Pembayaran_langsung.this, ""+res.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Pembayaran_langsung.this.finish();
                            Pembayaran_langsung.this.startActivity(new Intent(Pembayaran_langsung.this, MainActivity.class));
                            Pembayaran_langsung.this.startActivity(new Intent(Pembayaran_langsung.this, riwayat_transaksi.class));


                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(Pembayaran_langsung.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(user_id));
                    map.put("total", String.valueOf(totalharga));
                    map.put("jasa_pengiriman", spinner.getSelectedItem().toString());
                    map.put("bank", bank.getSelectedItem().toString());
                    map.put("barang_id", String.valueOf(barang_id));
                    map.put("jumlah", String.valueOf(_jumlah));
                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(Pembayaran_langsung.this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }



    private void getIncomingIntent(){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        if(getIntent().hasExtra("barang_id") ){
            int id = getIntent().getIntExtra("barang_id",0);
            barang_id = id;
        }
        if(getIntent().hasExtra("nama") ){
            String nama = getIntent().getStringExtra("nama");
            TextView tnama = findViewById(R.id.tnama);
            tnama.setText(nama);
        }
        if(getIntent().hasExtra("spec") ){
            String spec = getIntent().getStringExtra("spec");
            TextView tspec = findViewById(R.id.tspec);
            tspec.setText(spec);
        }
        if(getIntent().hasExtra("total") ){
            double total_ = getIntent().getDoubleExtra("total", 0);
            TextView total = findViewById(R.id.total);
            total_harga.setText("Rp." + formatter.format(total_));
            total.setText("Total : Rp." + formatter.format(total_));
            totalharga = total_;

        }
        if(getIntent().hasExtra("jumlah") ){
            double jumlah_ = getIntent().getIntExtra("jumlah", 0);
            TextView jumlah = findViewById(R.id.jumlah);
            jumlah.setText("Jumlah : " + formatter.format(jumlah_));
            _jumlah = jumlah_;
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
}
