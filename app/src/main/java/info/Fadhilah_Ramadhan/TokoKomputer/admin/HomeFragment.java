package info.Fadhilah_Ramadhan.TokoKomputer.admin;

import android.app.ProgressDialog;
import android.content.Context;
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

import info.Fadhilah_Ramadhan.TokoKomputer.AdminActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.MainActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.fragment.checkout;

public class HomeFragment  extends Fragment {


    CardView cv_dashboard,cv_barang,cv_transaksi;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        cv_dashboard = view.findViewById(R.id.cv_dashboard);
        cv_barang = view.findViewById(R.id.cv_barang);
        cv_transaksi = view.findViewById(R.id.cv_transaksi);

        cv_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getActivity().getSharedPreferences("load", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                editor.putInt("load", 2);
                editor.commit();
                getActivity().startActivity(new Intent(getActivity(), AdminActivity.class));
                getActivity();
            }
        });

        cv_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getActivity().getSharedPreferences("load", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                editor.putInt("load", 3);
                editor.commit();
                getActivity().startActivity(new Intent(getActivity(), AdminActivity.class));
                getActivity();
            }
        });

        cv_transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getActivity().getSharedPreferences("load", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                editor.putInt("load", 4);
                editor.commit();
                getActivity().startActivity(new Intent(getActivity(), AdminActivity.class));
                getActivity();
            }
        });
        return view;
    }
}
