package info.Fadhilah_Ramadhan.TokoKomputer.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import info.Fadhilah_Ramadhan.TokoKomputer.MainActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.product;
import info.Fadhilah_Ramadhan.TokoKomputer.riwayat_transaksi;
import info.Fadhilah_Ramadhan.TokoKomputer.riwayat_transaksi_detail;

public class KonfirmasiBarang extends AppCompatActivity {

    private ActionBar toolbar;
    ProgressDialog pd;
    int user_id;
    private SharedPreferences sharedPreferences,log_tabhost;
    private SharedPreferences.Editor editor_tab;
    TabHost host;
    private RecyclerView r_dibayar;
    private List<product> itemsList;
    private KonfirmasiBarang.StoreAdapter mAdapter;
    double totalharga=0;
    private LinearLayout l_dibayar_kosong,l_dikirim_kosong,l_selesai_kosong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_konfirmasi_barang);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Proses pengiriman");
        sharedPreferences = getSharedPreferences("sesi", MODE_PRIVATE);
        log_tabhost = getSharedPreferences("tabhost", MODE_PRIVATE);
        int sesi = sharedPreferences.getInt("sesi", 0);
        int tab_posisi = log_tabhost.getInt("tabhost",0);
        user_id = sesi;
        l_dibayar_kosong = findViewById(R.id.l_dibayar_kosong);
        l_dikirim_kosong = findViewById(R.id.l_dikirim_kosong);
        l_selesai_kosong = findViewById(R.id.l_selesai_kosong);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        r_dibayar = findViewById(R.id.r_dibayar);
        itemsList = new ArrayList<>();
        mAdapter = new KonfirmasiBarang.StoreAdapter(this, itemsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        r_dibayar.setLayoutManager(mLayoutManager);
        r_dibayar.addItemDecoration(new KonfirmasiBarang.GridSpacingItemDecoration(3, dpToPx(8), true));
        r_dibayar.setItemAnimator(new DefaultItemAnimator());
        r_dibayar.setAdapter(mAdapter);
        r_dibayar.setNestedScrollingEnabled(false);


        pd = new ProgressDialog(this);

        ambildata();

    }

    private void ambildata() {
        pd.setMessage("Mengambil data..");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_KONFIRMASI,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        if (response == null) {
                            Toast.makeText(KonfirmasiBarang.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<product> items = new Gson().fromJson(response.toString(), new TypeToken<List<product>>() {
                        }.getType());

                        itemsList.clear();
                        itemsList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        if(items.size() == 0){
                            l_dibayar_kosong.setVisibility(View.VISIBLE);
                        }else{
                            l_dibayar_kosong.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                pd.cancel();
                Toast.makeText(KonfirmasiBarang.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
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


    /**
     * RecyclerView adapter class to render items
     * This class can go into another separate class, but for simplicity
     */
    class StoreAdapter extends RecyclerView.Adapter<KonfirmasiBarang.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<product> productList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, total, spec, jumlah;
            public ImageView thumbnail;
            public CardView cv;
            public Button btnhapus,btn_proses;
            public ActionBar toolbar;


            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.tnama);
                total = view.findViewById(R.id.total);
                spec = view.findViewById(R.id.tspec);
                jumlah = view.findViewById(R.id.jumlah);
                thumbnail = view.findViewById(R.id.gambar);
                cv = view.findViewById(R.id.card_view);

                btnhapus = view.findViewById(R.id.btnhapus);
                btnhapus.setVisibility(View.GONE);

                btn_proses = view.findViewById(R.id.btn_proses);
                btn_proses.setVisibility(View.VISIBLE);


            }
        }
        private void proses_pengiriman(final int keranjang_id){
            pd.setMessage("Proses pengiriman..");
            pd.setCancelable(false);
            pd.show();
            StringRequest hapuskeranjang = new StringRequest(Request.Method.POST, URL.PROSES_KIRIM_BARANG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(KonfirmasiBarang.this,"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            KonfirmasiBarang.this.startActivity(new Intent(KonfirmasiBarang.this, KonfirmasiBarang.class));
                            KonfirmasiBarang.this.finish();

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(KonfirmasiBarang.this,"Pesan : Gagal proses pengiriman", Toast.LENGTH_SHORT).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> map = new HashMap<>();
                    map.put("keranjang_id", String.valueOf(keranjang_id));

                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(hapuskeranjang);
        }

        public StoreAdapter(Context context, List<product> productList) {
            this.context = context;
            this.productList = productList;


        }

        @Override
        public KonfirmasiBarang.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cart_item_row, parent, false);
            return new KonfirmasiBarang.StoreAdapter.MyViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final KonfirmasiBarang.StoreAdapter.MyViewHolder holder, final int position) {
            final product product = productList.get(position);
            DecimalFormat formatter = new DecimalFormat("#,###,###");

            holder.name.setText(product.getnama());
            holder.spec.setText(product.getspesifikasi());
            holder.jumlah.setText("Jumlah : " + formatter.format(product.getjumlah()));
            holder.total.setText("Total : Rp." + formatter.format(product.gettotal_harga()));
            if( position < productList.size())
            {

                totalharga += product.gettotal();

            }

            Glide.with(context)
                    .load("http://"+URL.HOST+URL.LOKASI_GAMBAR+product.getgambar())
                    .into(holder.thumbnail);

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, product.getnama(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, riwayat_transaksi_detail.class);
                    intent.putExtra("barang_id", product.getbarang_id());
                    intent.putExtra("keranjang_id", product.getkeranjang_id());
                    intent.putExtra("nama", product.getnama());
                    intent.putExtra("spec", product.getspesifikasi());
                    intent.putExtra("harga", product.getharga());
                    intent.putExtra("jumlah", product.getjumlah());
                    intent.putExtra("total", product.gettotal_harga());
                    intent.putExtra("token", 3);
                    intent.putExtra("merk", product.getmerk());
                    intent.putExtra("gambar", product.getgambar());
                    intent.putExtra("tanggal", product.gettanggal_beli());
                    intent.putExtra("feedback", product.getfeedback());
                    intent.putExtra("nama_user", product.getNama_user());
                    intent.putExtra("email", product.getemail());
                    intent.putExtra("nohp", product.getNohp());
                    intent.putExtra("provinsi", product.getProvinsi());
                    intent.putExtra("kecamatan", product.getKecamatan());
                    intent.putExtra("kab_kota", product.getKab_kota());
                    intent.putExtra("alamat_lengkap", product.getAlamat_lengkap());

                    context.startActivity(intent);
                }
            });

            holder.btn_proses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    proses_pengiriman(product.getkeranjang_id());
                }
            });


        }

        @Override
        public int getItemCount() {

            return productList.size();
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

