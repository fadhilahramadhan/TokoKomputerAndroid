package info.Fadhilah_Ramadhan.TokoKomputer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;

public class riwayat_transaksi_detail extends AppCompatActivity {

    private ActionBar toolbar;
    ProgressDialog pd;
    int user_id,barang_id,keranjang_id;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    double totalharga=0;

    TextView tnama,tspec,tjumlah,ttotal,ttangal,tmerk,tfeedback,nama_user,email,nohp,tprovinsi,tkab_kota,tkecamatan,talamat_lengkap;
    EditText feedback;
    Button btn_terima;
    ImageView image;
    CardView cv_feedback,cv_feedback_detail,cv_data_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat_transaksi_detail);

        pd = new ProgressDialog(this);
        toolbar = getSupportActionBar();
        toolbar.setTitle("");
        sharedPreferences = getSharedPreferences("sesi", MODE_PRIVATE);
        int sesi = sharedPreferences.getInt("sesi", 0);
        user_id = sesi;
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tnama = findViewById(R.id.tnama);
        tspec = findViewById(R.id.tspec);
        tjumlah = findViewById(R.id.tjumlah);
        ttangal = findViewById(R.id.tanggal);
        ttotal = findViewById(R.id.ttotal);
        feedback = findViewById(R.id.feedback);
        tfeedback = findViewById(R.id.tfeedback);
        tmerk = findViewById(R.id.tmerk);
        btn_terima = findViewById(R.id.btn_terima_barang);
        image = findViewById(R.id.image);
        cv_feedback = findViewById(R.id.cv_feedback);
        cv_feedback_detail = findViewById(R.id.cv_feedback_detail);
        cv_data_user = findViewById(R.id.cv_data_user);
        nama_user = findViewById(R.id.nama_user);
        email = findViewById(R.id.email);
        nohp = findViewById(R.id.nohp);
        tprovinsi = findViewById(R.id.tprovinsi);
        tkab_kota = findViewById(R.id.tkab_kota);
        tkecamatan = findViewById(R.id.tkecamatan);
        talamat_lengkap = findViewById(R.id.talamat_lengkap);

        getIncomingIntent();

        btn_terima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terima_barang();
            }
        });
    }

    private void terima_barang() {
        try {
            pd.setMessage("Mohon tunggu ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.TERIMA_BARANG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(riwayat_transaksi_detail.this, ""+res.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sharedPreferences = riwayat_transaksi_detail.this.getSharedPreferences("tabhost", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putInt("tabhost", 2);
                            editor.commit();
                            riwayat_transaksi_detail.this.startActivity(new Intent(riwayat_transaksi_detail.this, riwayat_transaksi.class));
                            riwayat_transaksi_detail.this.finish();

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(riwayat_transaksi_detail.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(user_id));
                    map.put("keranjang_id", String.valueOf(keranjang_id));
                    map.put("barang_id", String.valueOf(barang_id));
                    map.put("feedback", feedback.getText().toString());
                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(riwayat_transaksi_detail.this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }

    private void getIncomingIntent(){
        DecimalFormat formatter = new DecimalFormat("#,###,###");

        if(getIntent().hasExtra("keranjang_id") ){
            int id_keranjang = getIntent().getIntExtra("keranjang_id",0);
            keranjang_id = id_keranjang;
        }
        if(getIntent().hasExtra("barang_id") ){
            int id = getIntent().getIntExtra("barang_id",0);
            barang_id = id;
        }
        if(getIntent().hasExtra("nama") ){
            String nama = getIntent().getStringExtra("nama");
            tnama.setText(nama);
            toolbar.setTitle(nama);
            tnama.setText(nama);
        }
        if(getIntent().hasExtra("token") ){
            int token = getIntent().getIntExtra("token",0);
            if(token == 2 ){
                cv_feedback_detail.setVisibility(View.VISIBLE);
            }
            if(token == 1){
                cv_feedback.setVisibility(View.VISIBLE);
            }
            if(token == 3){
                cv_data_user.setVisibility(View.VISIBLE);
            }
        }
        if(getIntent().hasExtra("spec") ){
            String spec = getIntent().getStringExtra("spec");
            tspec.setText(spec);
        }
        if(getIntent().hasExtra("feedback") ){
            String feedback_s = getIntent().getStringExtra("feedback");
            tfeedback.setText(feedback_s);
        }
        if(getIntent().hasExtra("jumlah") ){

            double jumlah = getIntent().getDoubleExtra("jumlah",0);
            tjumlah.setText("Jumlah : " + formatter.format(jumlah));
        }
        if(getIntent().hasExtra("total") ){

            double total = getIntent().getDoubleExtra("total",0);
            ttotal.setText("Rp." + formatter.format(total));
        }
        if(getIntent().hasExtra("merk") ){
            String merk = getIntent().getStringExtra("merk");

            tmerk.setText(merk);
        }
        if(getIntent().hasExtra("tanggal") ){
            String tanggal = getIntent().getStringExtra("tanggal");
            ttangal.setText("Tanggal beli : "+tanggal);
        }
        if(getIntent().hasExtra("gambar") ){
            String gambar = getIntent().getStringExtra("gambar");

            Glide.with(this)
                    .asBitmap()
                    .load("http://"+ URL.HOST+URL.LOKASI_GAMBAR+gambar)
                    .into(image);
        }
        if(getIntent().hasExtra("nama_user") ){
            String nama = getIntent().getStringExtra("nama_user");
            nama_user.setText(nama);
        }
        if(getIntent().hasExtra("email") ){
            String email_ = getIntent().getStringExtra("email");
            email.setText(email_);
        }
        if(getIntent().hasExtra("nohp") ){
            String nohp_ = getIntent().getStringExtra("nohp");
            nohp.setText(nohp_);
        }
        if(getIntent().hasExtra("provinsi") ){
            String provinsi = getIntent().getStringExtra("provinsi");
            tprovinsi.setText(provinsi);
        }
        if(getIntent().hasExtra("kab_kota") ){
            String kab_kota = getIntent().getStringExtra("kab_kota");
            tkab_kota.setText(kab_kota);
        }
        if(getIntent().hasExtra("kecamatan") ){
            String kecamatan = getIntent().getStringExtra("kecamatan");
            tkecamatan.setText(kecamatan);
        }
        if(getIntent().hasExtra("alamat_lengkap") ){
            String alamat_lengkap = getIntent().getStringExtra("alamat_lengkap");
            talamat_lengkap.setText(alamat_lengkap);
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
