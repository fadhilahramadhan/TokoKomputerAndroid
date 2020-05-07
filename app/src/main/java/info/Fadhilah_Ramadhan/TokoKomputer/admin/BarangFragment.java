package info.Fadhilah_Ramadhan.TokoKomputer.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.CartFragment;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;
import info.Fadhilah_Ramadhan.TokoKomputer.product;

import static android.content.Context.MODE_PRIVATE;

public class BarangFragment extends Fragment {
    private static final String TAG = CartFragment.class.getSimpleName();

    int jumlah_barang = 0;

    private RecyclerView recyclerView;
    private List<product> itemsList;
    private BarangFragment.StoreAdapter mAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    ProgressDialog pd;
    Button btntambah;
    TextView jumlahbarang;


    public BarangFragment() {
        // Required empty public constructor
    }

    public static BarangFragment newInstance(String param1, String param2) {
        BarangFragment fragment = new BarangFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barang, container, false);
        pd = new ProgressDialog(getActivity());
        recyclerView = view.findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new BarangFragment.StoreAdapter(getActivity(), itemsList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new BarangFragment.GridSpacingItemDecoration(3, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        jumlahbarang = view.findViewById(R.id.jumlah_barang);
        btntambah = view.findViewById(R.id.btn_tambah);

        barang();

        jumlahbarang.setText(String.valueOf( jumlah_barang));

        btntambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), tambah_barang.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void barang() {
        pd.setMessage("Mengambil data..");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA_BARANG,
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
    class StoreAdapter extends RecyclerView.Adapter<BarangFragment.StoreAdapter.MyViewHolder> {
        private Context context;
        private List<product> productList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, total, spec, harga;
            public ImageView thumbnail;
            public CardView cv;
            public ImageView btnhapus,btnedit;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.tnama);
                harga = view.findViewById(R.id.harga);
                spec = view.findViewById(R.id.tspec);
                thumbnail = view.findViewById(R.id.gambar);
                cv = view.findViewById(R.id.card_view);

                btnhapus = view.findViewById(R.id.btnhapus);
                btnedit = view.findViewById(R.id.btnedit);



            }
        }

        private void Hapus(final int barang_id){
            pd.setMessage("Hapus barang");
            pd.setCancelable(false);
            pd.show();
            StringRequest hapusbarang = new StringRequest(Request.Method.POST, URL.HAPUS_DATA_BARANG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.cancel();
                            try {
                                JSONObject res = new JSONObject(response);
                                Toast.makeText(getActivity(),"Pesan : "+ res.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            sharedPreferences = getActivity().getSharedPreferences("load", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putInt("load", 3);
                            editor.commit();
                            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.cancel();
                            Toast.makeText(getActivity(),"Pesan : Gagal menghapus barang", Toast.LENGTH_SHORT).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> map = new HashMap<>();
                    map.put("barang_id", String.valueOf(barang_id));

                    return map;
                }

            };
            MyApplication.getInstance().addToRequestQueue(hapusbarang);
        }

        public StoreAdapter(Context context, List<product> productList) {
            this.context = context;
            this.productList = productList;


        }

        @Override
        public BarangFragment.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.barang_item_row, parent, false);
            return new BarangFragment.StoreAdapter.MyViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final BarangFragment.StoreAdapter.MyViewHolder holder, final int position) {
            final product product = productList.get(position);
            DecimalFormat formatter = new DecimalFormat("#,###,###");

            holder.name.setText(product.getnama());
            holder.spec.setText(product.getspesifikasi());
            holder.harga.setText("Rp." + formatter.format(product.getharga()));


            Glide.with(context)
                    .load("http://"+URL.HOST+URL.LOKASI_GAMBAR+ product.getgambar())
                    .into(holder.thumbnail);


            holder.btnhapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Hapus(product.getbarang_id());
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Hapus Barang?").setPositiveButton("Ya", dialogClickListener)
                            .setNegativeButton("tidak", dialogClickListener).show();

                }
            });

            holder.btnedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, edit_barang.class);
                    intent.putExtra("barang_id", product.getbarang_id());
                    intent.putExtra("nama", product.getnama());
                    intent.putExtra("spec", product.getspesifikasi());
                    intent.putExtra("harga", product.getharga());
                    intent.putExtra("merk", product.getmerk());
                    intent.putExtra("gambar", product.getgambar());
                    intent.putExtra("deskripsi", product.getdeskripsi());
                    context.startActivity(intent);
                }
            });

                jumlahbarang.setText(String.valueOf( product.getJumlah_barang()) + " Barang");



        }

        @Override
        public int getItemCount() {

            return productList.size();
        }
    }
}
