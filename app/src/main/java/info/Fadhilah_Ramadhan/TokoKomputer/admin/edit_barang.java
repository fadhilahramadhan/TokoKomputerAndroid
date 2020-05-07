package info.Fadhilah_Ramadhan.TokoKomputer.admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import info.Fadhilah_Ramadhan.TokoKomputer.MainActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;

public class edit_barang extends AppCompatActivity {

    private ActionBar toolbar;
    EditText nama_barang,harga,spesifikasi,deskripsi,merk;
    Button btn_upload,btn_edit;
    ProgressDialog pd;
    ImageView gambar;
    Bitmap bitmap;
    int barang_id;
    String gambarnya;
    private SharedPreferences sharedPreferences,log_posisi;
    private SharedPreferences.Editor editor;
    final int CODE_GALERY_REQUEST = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_barang);
        pd = new ProgressDialog(this);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Edit barang");

        if(getIntent().hasExtra("barang_id") ){
            barang_id = getIntent().getIntExtra("barang_id",0);
        }


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        nama_barang = findViewById(R.id.nama_barang);
        harga = findViewById(R.id.harga);
        spesifikasi = findViewById(R.id.spesifikasi);
        deskripsi = findViewById(R.id.deskripsi);
        btn_upload = findViewById(R.id.btn_upload);
        btn_edit = findViewById(R.id.btn_edit);
        merk = findViewById(R.id.merk);
        gambar = findViewById(R.id.gambar);

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        if(getIntent().hasExtra("nama") ){
            String nama = getIntent().getStringExtra("nama");
            nama_barang.setText(nama);
        }
        if(getIntent().hasExtra("spec") ){
            String spec = getIntent().getStringExtra("spec");
            spesifikasi.setText(spec);
        }
        if(getIntent().hasExtra("harga") ){
            double harga_ = getIntent().getDoubleExtra("harga",0);

            harga.setText(formatter.format(harga_));
        }
        if(getIntent().hasExtra("merk") ){
            String merk_ = getIntent().getStringExtra("merk");
            merk.setText(merk_);
        }
        if(getIntent().hasExtra("deskripsi") ){
            String deskripsi_ = getIntent().getStringExtra("deskripsi");

            deskripsi.setText(deskripsi_);
        }
        if(getIntent().hasExtra("gambar") ){
            String gambar_ = getIntent().getStringExtra("gambar");

            Glide.with(this)
                    .asBitmap()
                    .load("http://"+URL.HOST+URL.LOKASI_GAMBAR+gambar_)
                    .into(gambar);
            gambarnya = gambar_;
        }

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        edit_barang.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALERY_REQUEST
                );
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_data();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CODE_GALERY_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), CODE_GALERY_REQUEST);

            }else{
                Toast.makeText(getApplicationContext(), "Kamu tidak memiliki izin untuk mengakses galeri", Toast.LENGTH_SHORT).show();

            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_GALERY_REQUEST && resultCode == RESULT_OK && data !=null){
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                gambar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodeImage;
    }
    private void edit_data() {
        try {
            pd.setMessage("Merubah data ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.EDIT_BARANG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(edit_barang.this,"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sharedPreferences = edit_barang.this.getSharedPreferences("load", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putInt("load", 3);
                            editor.commit();
                            edit_barang.this.startActivity(new Intent(edit_barang.this, MainActivity.class));
                            edit_barang.this.finish();
                            finish();
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(edit_barang.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("barang_id", String.valueOf(barang_id));
                    map.put("nama_barang", nama_barang.getText().toString());
                    map.put("harga", harga.getText().toString());
                    map.put("merk", merk.getText().toString());
                    map.put("spesifikasi", spesifikasi.getText().toString());
                    map.put("deskripsi", deskripsi.getText().toString());
                    String imageData = imageToString(bitmap);
                        map.put("gambar", imageData);

                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(edit_barang.this, String.valueOf(e), Toast.LENGTH_LONG).show();
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

