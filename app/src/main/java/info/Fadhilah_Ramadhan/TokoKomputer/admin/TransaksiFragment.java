package info.Fadhilah_Ramadhan.TokoKomputer.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import info.Fadhilah_Ramadhan.TokoKomputer.Pembayaran_keranjang;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.CartFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;
import info.Fadhilah_Ramadhan.TokoKomputer.product;

import static android.content.Context.MODE_PRIVATE;

public class TransaksiFragment extends Fragment {
    private static final String TAG = CartFragment.class.getSimpleName();

    int user_id;
    private ActionBar toolbar;

    private RecyclerView recyclerView;
    private List<product> itemsList;
    private TransaksiFragment.StoreAdapter mAdapter;
    int totaltransaksi = 0,totalnotifikasi;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout l_kosong;

    TextView total_transaksi,t_konfirmasi;
    ProgressDialog pd;
    Button btn_konfirmasi;
   private CardView cv_konfirmasi;


    public TransaksiFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("sesi", MODE_PRIVATE);
        int sesi = sharedPreferences.getInt("sesi", 0);
        user_id = sesi;
        if (sesi == 0) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frament_transaksi_admin, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new TransaksiFragment.StoreAdapter(getActivity(), itemsList);
        total_transaksi = view.findViewById(R.id.total_transaksi);
        l_kosong = view.findViewById(R.id.l_kosong);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new TransaksiFragment.GridSpacingItemDecoration(3, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        pd = new ProgressDialog(getActivity());
        btn_konfirmasi = view.findViewById(R.id.btn_konfirmasi);
        cv_konfirmasi = view.findViewById(R.id.cv_konfirmasi);
        t_konfirmasi = view.findViewById(R.id.t_konfirmasi);

        btn_konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), KonfirmasiBarang.class);
                    startActivity(intent);

            }
        });


        fetchStoreItems();


        return view;

    }


    private void fetchStoreItems() {
        pd.setMessage("Mengambil data..");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_TRANSAKSI,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.cancel();
                        if (response == null) {
                            Toast.makeText(getActivity(), "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
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
                pd.cancel();
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    class StoreAdapter extends RecyclerView.Adapter<TransaksiFragment.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<product> productList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView nama, total, tanggal, jumlah;
            public CardView cv;

            public MyViewHolder(View view) {
                super(view);
                nama = view.findViewById(R.id.nama_user);
                total = view.findViewById(R.id.total);
                tanggal = view.findViewById(R.id.tanggal);
                jumlah = view.findViewById(R.id.jumlah);
                cv = view.findViewById(R.id.card_view);



            }
        }


        public StoreAdapter(Context context, List<product> productList) {
            this.context = context;
            this.productList = productList;

        }

        @Override
        public TransaksiFragment.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaksi_admin_item_row, parent, false);
            return new TransaksiFragment.StoreAdapter.MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(final TransaksiFragment.StoreAdapter.MyViewHolder holder, final int position) {
            final product product = productList.get(position);
            DecimalFormat formatter = new DecimalFormat("#,###,###");

            holder.nama.setText(product.getnama());
            holder.tanggal.setText(product.gettanggal_beli());
            holder.jumlah.setText("Jumlah barang : " + formatter.format(product.getjumlah()));
            holder.total.setText("Total : Rp." + formatter.format(product.gettotal()));
            totalnotifikasi = product.getJumlah_notifikasi();
            if (position < productList.size()) {

                totaltransaksi ++;

            }



            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, product.getnama(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Pembayaran_keranjang.class);
                    intent.putExtra("user_id", product.getUser_id());
                    intent.putExtra("transaksi_id", product.getTransaksi_id());
                    intent.putExtra("bank", product.getBank());
                    intent.putExtra("jasa_pengiriman", product.getJasa_pengiriman());
                    intent.putExtra("token", 1);
                    context.startActivity(intent);
                }
            });

            total_transaksi.setText(formatter.format(product.getJumlah_transaksi())+" Transaksi");
            t_konfirmasi.setText(formatter.format(totalnotifikasi) + " Barang menunggu dikirim");
            cv_konfirmasi.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {

            return productList.size();
        }
    }
}
