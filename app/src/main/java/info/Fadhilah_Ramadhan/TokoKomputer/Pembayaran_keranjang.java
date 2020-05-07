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
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;

public class Pembayaran_keranjang extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = Pembayaran_keranjang.class.getSimpleName();
    private ActionBar toolbar;
    TextView provinsi,kabupaten,kecamatan,alamat_lengkap,total_harga,alamat,tjasa,tbank;
    Button btn_lengkapi,btn_bayar;

    ProgressDialog pd;
    int user_id,transaksi_id;
    double totalharga=0;
    boolean cek_alamat = false;
    Spinner spinner,bank;
    private RecyclerView recyclerView;
    private List<product> itemsList;
    private Pembayaran_keranjang.StoreAdapter mAdapter;

    private SharedPreferences sharedPreferences,log_sesi;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pembayaran_keranjang);

        pd = new ProgressDialog(this);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Pembayaran");

        log_sesi = getSharedPreferences("sesi", MODE_PRIVATE);
        if(getIntent().hasExtra("user_id") ){
            int id = getIntent().getIntExtra("user_id",0);
            user_id = id;
            toolbar.setTitle("Detail Transaksi");
        }else {
            user_id = log_sesi.getInt("sesi", 0);
            toolbar.setTitle("Pembayaran");
        }

        if(getIntent().hasExtra("transaksi_id") ){
            int id = getIntent().getIntExtra("transaksi_id",0);
            transaksi_id = id;
        }
        //Toast.makeText(Pembayaran_keranjang.this,String.valueOf(user_id) + " - " +String.valueOf(transaksi_id),Toast.LENGTH_LONG).show();
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
        tjasa = findViewById(R.id.tjasa);
        tbank = findViewById(R.id.tbank);

        recyclerView = findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new Pembayaran_keranjang.StoreAdapter(this, itemsList);
        total_harga = findViewById(R.id.total_harga);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(Pembayaran_keranjang.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new Pembayaran_keranjang.GridSpacingItemDecoration(3, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);


        if(getIntent().hasExtra("token") ){
            int token_ = getIntent().getIntExtra("token",0);

            if(token_ == 1){
                btn_bayar.setVisibility(View.GONE);
                btn_lengkapi.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                bank.setVisibility(View.GONE);
                tjasa.setVisibility(View.VISIBLE);
                tbank.setVisibility(View.VISIBLE);
                if(getIntent().hasExtra("bank") ) {
                    String bank_ = getIntent().getStringExtra("bank");
                    tbank.setText(bank_);
                }
                if(getIntent().hasExtra("jasa_pengiriman") ) {
                    String jasa_ = getIntent().getStringExtra("jasa_pengiriman");
                    tjasa.setText(jasa_);
                }
            }
        }

        datauser();
        fetchStoreItems();



        btn_lengkapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pembayaran_keranjang.this, Profil_data_alamat.class);
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



    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void fetchStoreItems() {

        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_KERANJANG+String.valueOf(user_id)+"&transaksi_id="+String.valueOf(transaksi_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(Pembayaran_keranjang.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<product> items = new Gson().fromJson(response.toString(), new TypeToken<List<product>>() {
                        }.getType());

                        itemsList.clear();
                        itemsList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(Pembayaran_keranjang.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
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
                            Toast.makeText(Pembayaran_keranjang.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Pembayaran_keranjang.this, String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }

    private void transasksi() {
        try {
            pd.setMessage("Mohon tunggu ..");
            pd.setCancelable(false);
            pd.show();
            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.TRANSAKSI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(Pembayaran_keranjang.this, ""+res.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Pembayaran_keranjang.this.finish();
                            Pembayaran_keranjang.this.startActivity(new Intent(Pembayaran_keranjang.this, MainActivity.class));
                            Pembayaran_keranjang.this.startActivity(new Intent(Pembayaran_keranjang.this, riwayat_transaksi.class));


                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(Pembayaran_keranjang.this, "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(user_id));
                    map.put("total", String.valueOf(totalharga));
                    map.put("jasa_pengiriman", spinner.getSelectedItem().toString());
                    map.put("bank", bank.getSelectedItem().toString());
                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(ambil_request);
        } catch (Exception e) {
            Toast.makeText(Pembayaran_keranjang.this, String.valueOf(e), Toast.LENGTH_LONG).show();
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
    class StoreAdapter extends RecyclerView.Adapter<Pembayaran_keranjang.StoreAdapter.MyViewHolder> {
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
        public Pembayaran_keranjang.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cart_item_row, parent, false);
            return new Pembayaran_keranjang.StoreAdapter.MyViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final Pembayaran_keranjang.StoreAdapter.MyViewHolder holder, final int position) {
            final product product = productList.get(position);
            DecimalFormat formatter = new DecimalFormat("#,###,###");

            holder.name.setText(product.getnama());
            holder.spec.setText(product.getspesifikasi());
            holder.jumlah.setText("Jumlah : " + formatter.format(product.getjumlah()));
            holder.total.setText("Total : Rp." + formatter.format(product.gettotal()));
            if( position < productList.size())
            {

                totalharga += product.gettotal();

            }

            total_harga.setText("Rp." + formatter.format(totalharga));

            Glide.with(context)
                    .load("http://"+URL.HOST+URL.LOKASI_GAMBAR+product.getgambar())
                    .into(holder.thumbnail);




        }

        @Override
        public int getItemCount() {

            return productList.size();
        }
    }


}
