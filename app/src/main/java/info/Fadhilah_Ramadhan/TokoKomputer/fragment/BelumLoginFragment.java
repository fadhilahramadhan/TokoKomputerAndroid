package info.Fadhilah_Ramadhan.TokoKomputer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import info.Fadhilah_Ramadhan.TokoKomputer.LoginActivity;
import info.Fadhilah_Ramadhan.TokoKomputer.Profil_data_alamat;
import info.Fadhilah_Ramadhan.TokoKomputer.Profil_data_edit;
import info.Fadhilah_Ramadhan.TokoKomputer.R;
import info.Fadhilah_Ramadhan.TokoKomputer.riwayat_transaksi;

public class BelumLoginFragment extends Fragment {

    Button btn_login;
    public BelumLoginFragment() {
        // Required empty public constructor
    }

    public static BelumLoginFragment newInstance(String param1, String param2) {
        BelumLoginFragment fragment = new BelumLoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_belum_login, container, false);
        btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
