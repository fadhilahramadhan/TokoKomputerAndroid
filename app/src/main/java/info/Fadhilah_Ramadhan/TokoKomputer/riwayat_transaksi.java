package info.Fadhilah_Ramadhan.TokoKomputer;

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

import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.CartFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;

public class riwayat_transaksi extends AppCompatActivity {

    private ActionBar toolbar;
    EditText provinsi,kabupaten,kecamatan,alamat_lengkap;
    ProgressDialog pd;
    int user_id;
    private SharedPreferences sharedPreferences,log_tabhost;
    private SharedPreferences.Editor editor_tab;
    TabHost host;
    private RecyclerView r_dibayar,r_dikirim,r_selesai;
    private List<product> itemsList,iteml_dikirim,iteml_selesai;
    private riwayat_transaksi.StoreAdapter mAdapter,mAdapter_dikirim,mAdapter_selesai;
    double totalharga=0;
    private LinearLayout l_dibayar_kosong,l_dikirim_kosong,l_selesai_kosong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat_transaksi);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Riwayat Transaksi");
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
         host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Dibayar");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Dibayar");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Dikirim");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Dikirim");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Selesai");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Selesai");
        host.addTab(spec);

        host.setCurrentTab(tab_posisi);
        editor_tab = log_tabhost.edit();
        editor_tab.putInt("tabhost", 0);
        editor_tab.commit();

        r_dibayar = findViewById(R.id.r_dibayar);
        r_dikirim = findViewById(R.id.r_dikirim);
        r_selesai = findViewById(R.id.r_selesai);
        itemsList = new ArrayList<>();
        iteml_dikirim = new ArrayList<>();
        iteml_selesai = new ArrayList<>();
        mAdapter = new riwayat_transaksi.StoreAdapter(this, itemsList);
        mAdapter_dikirim = new riwayat_transaksi.StoreAdapter(this, iteml_dikirim);
        mAdapter_selesai = new riwayat_transaksi.StoreAdapter(this, iteml_selesai);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        r_dibayar.setLayoutManager(mLayoutManager);
        r_dibayar.addItemDecoration(new riwayat_transaksi.GridSpacingItemDecoration(3, dpToPx(8), true));
        r_dibayar.setItemAnimator(new DefaultItemAnimator());
        r_dibayar.setAdapter(mAdapter);
        r_dibayar.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager_dikirim = new GridLayoutManager(this, 1);
        r_dikirim.setLayoutManager(mLayoutManager_dikirim);
        r_dikirim.addItemDecoration(new riwayat_transaksi.GridSpacingItemDecoration(3, dpToPx(8), true));
        r_dikirim.setItemAnimator(new DefaultItemAnimator());
        r_dikirim.setAdapter(mAdapter_dikirim);
        r_dikirim.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager_selesai = new GridLayoutManager(this, 1);
        r_selesai.setLayoutManager(mLayoutManager_selesai);
        r_selesai.addItemDecoration(new riwayat_transaksi.GridSpacingItemDecoration(3, dpToPx(8), true));
        r_selesai.setItemAnimator(new DefaultItemAnimator());
        r_selesai.setAdapter(mAdapter_selesai);
        r_selesai.setNestedScrollingEnabled(false);

        pd = new ProgressDialog(this);

        dibayar();
        dikirim();
        selesai();

    }

    private void dibayar() {
        pd.setMessage("Mengambil data..");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_KERANJANG+String.valueOf(user_id)+"&status_transaksi_id=1",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        if (response == null) {
                            Toast.makeText(riwayat_transaksi.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(riwayat_transaksi.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    private void dikirim() {
        pd.setMessage("Mengambil data..");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_KERANJANG+String.valueOf(user_id)+"&status_transaksi_id=2",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        if (response == null) {
                            Toast.makeText(riwayat_transaksi.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<product> items = new Gson().fromJson(response.toString(), new TypeToken<List<product>>() {
                        }.getType());

                        iteml_dikirim.clear();
                        iteml_dikirim.addAll(items);

                        // refreshing recycler view
                        mAdapter_dikirim.notifyDataSetChanged();
                        if(items.size() == 0){
                            l_dikirim_kosong.setVisibility(View.VISIBLE);
                        }else{
                            l_dikirim_kosong.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                pd.cancel();
                Toast.makeText(riwayat_transaksi.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    private void selesai() {
        pd.setMessage("Mengambil data..");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_KERANJANG+String.valueOf(user_id)+"&status_transaksi_id=3",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        if (response == null) {
                            Toast.makeText(riwayat_transaksi.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<product> items = new Gson().fromJson(response.toString(), new TypeToken<List<product>>() {
                        }.getType());

                        iteml_selesai.clear();
                        iteml_selesai.addAll(items);

                        // refreshing recycler view
                        mAdapter_selesai.notifyDataSetChanged();
                        if(items.size() == 0){
                            l_selesai_kosong.setVisibility(View.VISIBLE);
                        }else{
                            l_selesai_kosong.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                pd.cancel();
                Toast.makeText(riwayat_transaksi.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    class StoreAdapter extends RecyclerView.Adapter<riwayat_transaksi.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<product> productList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, total, spec, jumlah;
            public ImageView thumbnail;
            public CardView cv;
            public Button btnhapus;


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


            }
        }


        public StoreAdapter(Context context, List<product> productList) {
            this.context = context;
            this.productList = productList;


        }

        @Override
        public riwayat_transaksi.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cart_item_row, parent, false);
            return new riwayat_transaksi.StoreAdapter.MyViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final riwayat_transaksi.StoreAdapter.MyViewHolder holder, final int position) {
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
                    intent.putExtra("token", host.getCurrentTab());
                    intent.putExtra("merk", product.getmerk());
                    intent.putExtra("gambar", product.getgambar());
                    intent.putExtra("tanggal", product.gettanggal_beli());
                    intent.putExtra("feedback", product.getfeedback());
                    context.startActivity(intent);
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
