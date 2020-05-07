package info.Fadhilah_Ramadhan.TokoKomputer.admin;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.Fadhilah_Ramadhan.TokoKomputer.MainActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.Pembayaran_keranjang;
import info.Fadhilah_Ramadhan.TokoKomputer.Profil_data_alamat;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.CartFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;
import info.Fadhilah_Ramadhan.TokoKomputer.product;

public class tambah_barang extends AppCompatActivity{

    private ActionBar toolbar;
    EditText nama_barang,harga,spesifikasi,deskripsi,merk;
    Button btn_upload,btn_tambah;
    ProgressDialog pd;
    ImageView gambar;
    Bitmap bitmap;
    private SharedPreferences sharedPreferences,log_posisi;
    private SharedPreferences.Editor editor;
    final int CODE_GALERY_REQUEST = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_barang);
        pd = new ProgressDialog(this);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Tambah barang");

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        nama_barang = findViewById(R.id.nama_barang);
        harga = findViewById(R.id.harga);
        spesifikasi = findViewById(R.id.spesifikasi);
        deskripsi = findViewById(R.id.deskripsi);
        btn_upload = findViewById(R.id.btn_upload);
        btn_tambah = findViewById(R.id.btn_tambah);
        merk = findViewById(R.id.merk);
        gambar = findViewById(R.id.gambar);


        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        tambah_barang.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALERY_REQUEST
                );
            }
        });

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambah_data();
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
    private void tambah_data() {
        try {
            pd.setMessage("Menambahkan data ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.TAMBAH_BARANG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(tambah_barang.this,"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sharedPreferences = tambah_barang.this.getSharedPreferences("load", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putInt("load", 3);
                            editor.commit();
                            tambah_barang.this.startActivity(new Intent(tambah_barang.this, MainActivity.class));
                            tambah_barang.this.finish();
                                finish();
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(tambah_barang.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
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
            Toast.makeText(tambah_barang.this, String.valueOf(e), Toast.LENGTH_LONG).show();
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
