package info.Fadhilah_Ramadhan.TokoKomputer.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.product;

public class StoreFragment extends Fragment {

    private static final String TAG = StoreFragment.class.getSimpleName();

    // url to fetch shopping items
    private ActionBar toolbar;
    private RecyclerView recyclerView;
    private List<product> itemsList;
    private StoreAdapter mAdapter;
    ProgressDialog pd;



    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new StoreAdapter(getActivity(), itemsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        pd = new ProgressDialog(getActivity());
        fetchStoreItems();

        return view;
    }

    /**
     * fetching shopping item by making http call
     */
    private void fetchStoreItems() {
        pd.setMessage("Mengambil data..");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest request = new JsonArrayRequest(URL.VIEW_DATA,
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

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
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
    class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
        private Context context;
        private List<product> productList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, price, spec,merk;
            public ImageView thumbnail;
            public RelativeLayout parentLayout;
            public CardView cv;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.tnama);
                price = view.findViewById(R.id.price);
                spec = view.findViewById(R.id.tspec);
                merk = view.findViewById(R.id.merk);
                thumbnail = view.findViewById(R.id.gambar);
                parentLayout = view.findViewById(R.id.parrent_layout);
                cv = view.findViewById(R.id.card_view);
            }
        }


        public StoreAdapter(Context context, List<product> productList) {
            this.context = context;
            this.productList = productList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.store_item_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            final product product = productList.get(position);
            holder.name.setText(product.getnama());
            holder.spec.setText(product.getspesifikasi());
            holder.merk.setText(product.getmerk());
            holder.price.setText("Rp."+ formatter.format(product.getharga()));

            Glide.with(context)
                    .load("http://"+URL.HOST+URL.LOKASI_GAMBAR+product.getgambar())
                    .into(holder.thumbnail);

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, product.getnama(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, checkout.class);
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


        }

        @Override
        public int getItemCount() {
            return productList.size();
        }


    }

}
