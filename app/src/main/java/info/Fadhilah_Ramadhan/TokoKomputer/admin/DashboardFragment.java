package info.Fadhilah_Ramadhan.TokoKomputer.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import info.Fadhilah_Ramadhan.TokoKomputer.MainActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;

public class DashboardFragment extends Fragment {


    TextView t_jumlah_barng,t_jumlah_transaksi,t_barang_terjual,t_total_pendapatan,t_user_terdaftar,t_jumlah_feedback;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ProgressDialog pd;
    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        pd = new ProgressDialog(getActivity());
        t_jumlah_barng = view.findViewById(R.id.t_jumlah_barang);
        t_barang_terjual = view.findViewById(R.id.t_barang_terjual);
        t_jumlah_transaksi = view.findViewById(R.id.t_jumlah_transaksi);
        t_total_pendapatan = view.findViewById(R.id.t_total_pendapatan);
        t_user_terdaftar = view.findViewById(R.id.t_user_terdaftar);
        t_jumlah_feedback = view.findViewById(R.id.t_jumlah_feedback);
        data_dashboard();

        return view;
    }

    private void data_dashboard(){
        pd.setMessage("Mohon Tunggu..");
        pd.setCancelable(false);
        pd.show();

        StringRequest request = new StringRequest( URL.VIEW_DATA_DASHBOARD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.cancel();
                        DecimalFormat formatter = new DecimalFormat("#,###,###");

                        try {
                            JSONObject res = new JSONObject(response);
                            t_jumlah_barng.setText( res.getString("jumlah_barang")+" Barang");
                            t_jumlah_transaksi.setText(res.getString("jumlah_trankasi")+ " Transaksi");
                            t_barang_terjual.setText(res.getString("jumlah_barang_terjual")+" Barang terjual");
                            int total_pendapatan = Integer.parseInt(res.getString("jumlah_total_pendapatan"));
                            t_total_pendapatan.setText("Rp."+formatter.format(total_pendapatan));
                            t_user_terdaftar.setText(res.getString("jumlah_user") + " User");
                            t_jumlah_feedback.setText(res.getString("jumlah_feedback") + " Feedback");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Toast.makeText(getActivity(),"Pesan : Gagal mengambil data", Toast.LENGTH_SHORT).show();
                    }
                });
        MyApplication.getInstance().addToRequestQueue(request);
    }
}
