package info.Fadhilah_Ramadhan.TokoKomputer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
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

import java.util.HashMap;
import java.util.Map;

import info.Fadhilah_Ramadhan.TokoKomputer.LoginActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.Profil_data_alamat;
import info.Fadhilah_Ramadhan.TokoKomputer.Profil_data_edit;
import info.Fadhilah_Ramadhan.TokoKomputer.app.MyApplication;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.app.URL;
import info.Fadhilah_Ramadhan.TokoKomputer.riwayat_transaksi;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    // url to fetch shopping items
    int user_id;
    private ActionBar toolbar;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ProgressDialog pd;
    TextView nama,email,nohp;
    CardView cv_profil,cv_alamat_pengiriman,cv_riwayat_transaksi;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences =getActivity().getSharedPreferences("sesi", MODE_PRIVATE);
        int sesi = sharedPreferences.getInt("sesi", 0);
        user_id = sesi;

        if(sesi == 0){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pd = new ProgressDialog(getActivity());
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        nama = view.findViewById(R.id.tnama);
        email = view.findViewById(R.id.tspec);
        nohp = view.findViewById(R.id.tnohp);
        datauser();

        cv_profil = view.findViewById(R.id.cv_profil);
        cv_alamat_pengiriman = view.findViewById(R.id.cv_alamat_pengiriman);
        cv_riwayat_transaksi = view.findViewById(R.id.cv_riwayat_transaksi);

        cv_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Profil_data_edit.class);
                startActivity(intent);
            }
        });

        cv_alamat_pengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Profil_data_alamat.class);
                startActivity(intent);
            }
        });

        cv_riwayat_transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), riwayat_transaksi.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void datauser(){
        try {
            pd.setMessage("Mengambil data..");
            pd.setCancelable(false);
            pd.show();

            StringRequest ambil_request = new StringRequest(Request.Method.POST, URL.VIEW_DATA_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                pd.cancel();
                                JSONObject res = new JSONObject(response);
                                nama.setText(res.getString("nama").toString());
                                email.setText(res.getString("email").toString());
                                if(res.getString("nohp").equals("null") ){
                                    nohp.setText("");
                                }else
                                {
                                    nohp.setText(res.getString("nohp").toString());
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
                            Toast.makeText(getActivity(), "Pesan : Ada Error", Toast.LENGTH_SHORT).show();
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
       Toast.makeText(getActivity(),String.valueOf(e),Toast.LENGTH_LONG).show();
    }
    }


}