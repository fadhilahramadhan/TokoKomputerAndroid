package info.Fadhilah_Ramadhan.TokoKomputer.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.Fadhilah_Ramadhan.TokoKomputer.LoginActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.MainActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.Pembayaran_langsung;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.product;

/**
 * Created by User on 1/2/2018.
 */

public class checkout extends AppCompatActivity {
    private ActionBar toolbar;
    private Context context;
    Button tambah,kurang,keranjang,beli,btn_beli;
    EditText jumlah;
    int jumlah_beli,barang_id,user_id;
    double total,harga;
    ProgressDialog pd;
    String nama_barang;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    TextView tnama,tspec,tharga,tmerk,tdeskripsi;
    ImageView image,favorit;
    private RecyclerView recyclerView;
    private List<product> itemsList;
    private checkout.StoreAdapter mAdapter;
    private LinearLayout l_kosong;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        toolbar = getSupportActionBar();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.r_feedback);
        itemsList = new ArrayList<>();
        mAdapter = new StoreAdapter(this, itemsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new checkout.GridSpacingItemDecoration(3, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        tambah = (Button) findViewById(R.id.tambah);
        kurang = (Button) findViewById(R.id.kurang);
        jumlah = (EditText) findViewById(R.id.t_jumlah);
        keranjang = (Button) findViewById(R.id.btn_keranjang);
        beli = findViewById(R.id.btn_beli);
        favorit = findViewById(R.id.btnfavorit);
        btn_beli = findViewById(R.id.btn_beli);
        jumlah_beli = Integer.parseInt(jumlah.getText().toString());
        l_kosong = findViewById(R.id.l_kosong);
        tnama = findViewById(R.id.tnama);
         tspec = findViewById(R.id.tspec);
         tharga = findViewById(R.id.price);
         tmerk = findViewById(R.id.merk);
        tdeskripsi = findViewById(R.id.t_deskripsi);
         image = findViewById(R.id.image);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Deskripsi");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Deskripsi");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Feedback");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Feedback");
        host.addTab(spec);

        pd = new ProgressDialog(this);

        keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences =checkout.this.getSharedPreferences("sesi", MODE_PRIVATE);
                int sesi = sharedPreferences.getInt("sesi", 0);
                user_id = sesi;
                if(sesi == 0){
                    Intent intent = new Intent(checkout.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    TambahKeranjang();

                }
            }
        });

        favorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences =checkout.this.getSharedPreferences("sesi", MODE_PRIVATE);
                int sesi = sharedPreferences.getInt("sesi", 0);
                user_id = sesi;
                if(sesi == 0){
                    Intent intent = new Intent(checkout.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    TambahFavorit();
                }
            }
        });

        btn_beli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences =checkout.this.getSharedPreferences("sesi", MODE_PRIVATE);
                int sesi = sharedPreferences.getInt("sesi", 0);
                user_id = sesi;
                if(sesi == 0){
                    Intent intent = new Intent(checkout.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    total = jumlah_beli * harga;
                    finish();
                    Intent intent = new Intent(checkout.this, Pembayaran_langsung.class);
                    intent.putExtra("barang_id", barang_id);
                    intent.putExtra("nama", tnama.getText().toString());
                    intent.putExtra("spec", tspec.getText().toString());
                    intent.putExtra("total", total);
                    intent.putExtra("jumlah", jumlah_beli);
                    startActivity(intent);
                }

            }
        });
        getIncomingIntent();
        feedback();
    }
    private void feedback() {

        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_FEEDBACK+String.valueOf(barang_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(checkout.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<product> items = new Gson().fromJson(response.toString(), new TypeToken<List<product>>() {
                        }.getType());

                        itemsList.clear();
                        itemsList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        if(items.size() == 0){
                            l_kosong.setVisibility(View.VISIBLE);
                        }else{
                            l_kosong.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Toast.makeText(checkout.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }
    private void TambahKeranjang(){
        pd.setMessage("Tambah ke Keranjang");
        pd.setCancelable(false);
        pd.show();

        StringRequest tambahkeranjang = new StringRequest(Request.Method.POST, URL.TAMBAH_DATA_KERANJANG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.cancel();
                        try {
                            JSONObject res = new JSONObject(response);
                            Toast.makeText(checkout.this,"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        sharedPreferences = checkout.this.getSharedPreferences("load", Context.MODE_PRIVATE);
                                        editor = sharedPreferences.edit();

                                        editor.putInt("load", 1);
                                        editor.commit();
                                        checkout.this.startActivity(new Intent(checkout.this, MainActivity.class));
                                        checkout.this.finish();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        sharedPreferences = checkout.this.getSharedPreferences("load", Context.MODE_PRIVATE);
                                        editor = sharedPreferences.edit();

                                        editor.putInt("load", 3);
                                        editor.commit();
                                        checkout.this.startActivity(new Intent(checkout.this, MainActivity.class));
                                        checkout.this.finish();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(checkout.this);
                        builder.setMessage("Barang Berhasil ditambhakan ke keranjang, Lanjut belanja?").setPositiveButton("Ya", dialogClickListener)
                                .setNegativeButton("Lihat keranjang", dialogClickListener).show();


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Toast.makeText(checkout.this,"Pesan : Gagal tambah ke keranjang", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("barang_id", String.valueOf(barang_id));
                map.put("jumlah", jumlah.getText().toString());
                map.put("user_id", String.valueOf(user_id));

                return map;
            }

        };
        MyApplication.getInstance().addToRequestQueue(tambahkeranjang);
    }

    private void TambahFavorit(){
        pd.setMessage("Tambah ke Favorit");
        pd.setCancelable(false);
        pd.show();

        StringRequest tambahfavorit = new StringRequest(Request.Method.POST, URL.TAMBAH_DATA_FAVORIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.cancel();
                        try {
                            JSONObject res = new JSONObject(response);
                            Toast.makeText(checkout.this,"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        sharedPreferences = checkout.this.getSharedPreferences("load", Context.MODE_PRIVATE);
                                        editor = sharedPreferences.edit();

                                        editor.putInt("load", 1);
                                        editor.commit();
                                        checkout.this.startActivity(new Intent(checkout.this, MainActivity.class));
                                        checkout.this.finish();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        sharedPreferences = checkout.this.getSharedPreferences("load", Context.MODE_PRIVATE);
                                        editor = sharedPreferences.edit();

                                        editor.putInt("load", 2);
                                        editor.commit();
                                        checkout.this.startActivity(new Intent(checkout.this, MainActivity.class));
                                        checkout.this.finish();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(checkout.this);
                        builder.setMessage("Barang Berhasil ditambhakan ke favorit, Lanjut belanja?").setPositiveButton("Ya", dialogClickListener)
                                .setNegativeButton("Lihat favorit", dialogClickListener).show();

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Toast.makeText(checkout.this,"Pesan : Gagal tambah ke Favorit", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("barang_id", String.valueOf(barang_id));
                map.put("user_id", String.valueOf(user_id));

                return map;
            }

        };
        MyApplication.getInstance().addToRequestQueue(tambahfavorit);
    }

    private void getIncomingIntent(){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        if(getIntent().hasExtra("barang_id") ){
            int id = getIntent().getIntExtra("barang_id",0);
            barang_id = id;
        }
        if(getIntent().hasExtra("nama") ){
            String nama = getIntent().getStringExtra("nama");

            nama_barang = getIntent().getStringExtra("nama");
            toolbar.setTitle(nama_barang);
            tnama.setText(nama);
        }
        if(getIntent().hasExtra("spec") ){
            String spec = getIntent().getStringExtra("spec");

            tspec.setText(spec);
        }
        if(getIntent().hasExtra("harga") ){

            harga = getIntent().getDoubleExtra("harga",0);

            tharga.setText("Rp." + formatter.format(harga));
        }
        if(getIntent().hasExtra("merk") ){
            String merk = getIntent().getStringExtra("merk");

            tmerk.setText(merk);
        }
        if(getIntent().hasExtra("deskripsi") ){
            String deskripsi = getIntent().getStringExtra("deskripsi");

            tdeskripsi.setText(deskripsi);
        }
        if(getIntent().hasExtra("gambar") ){
            String gambar = getIntent().getStringExtra("gambar");

            Glide.with(this)
                    .asBitmap()
                    .load("http://"+URL.HOST+URL.LOKASI_GAMBAR+gambar)
                    .into(image);
        }

    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column


        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class StoreAdapter extends RecyclerView.Adapter<checkout.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<product> productList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, feedback;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.tnama);
                feedback = view.findViewById(R.id.tfeedback);



            }
        }



        public StoreAdapter(Context context, List<product> productList) {
            this.context = context;
            this.productList = productList;


        }

        @Override
        public checkout.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feedback_item_row, parent, false);
            return new checkout.StoreAdapter.MyViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final checkout.StoreAdapter.MyViewHolder holder, final int position) {
            final product product = productList.get(position);
            DecimalFormat formatter = new DecimalFormat("#,###,###");

            holder.name.setText(product.getnama());
            holder.feedback.setText(product.getfeedback());



        }

        @Override
        public int getItemCount() {

            return productList.size();
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


    public void tambah(View v){

        jumlah_beli = jumlah_beli + 1;
        jumlah.setText( String.valueOf(jumlah_beli));
    }

    public void kurang(View v){
        if(jumlah_beli > 1)
        jumlah_beli = jumlah_beli - 1;
        jumlah.setText( String.valueOf(jumlah_beli));
    }

}

