package com.example.jstock;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteStocksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteStocksFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String stockName;
    private String ActivityID;
    private String stockName2;

    private Button btnStock;

    public FavoriteStocksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FavoriteStocksFragment.
     */
    public static FavoriteStocksFragment newInstance(String param1, String param2, String param3) {
        FavoriteStocksFragment fragment = new FavoriteStocksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.stockName = getArguments().getString(ARG_PARAM1);
            this.ActivityID = getArguments().getString(ARG_PARAM2);
            this.stockName2 = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_stocks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStock = view.findViewById(R.id.btn_fav_stock);
        btnStock.setText(stockName);
        btnStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(ActivityID.equals("Compare")) {
                    if(stockName.equals(stockName2)) {
                        Toast.makeText(view.getContext(), "Invalid Data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    intent = new Intent(view.getContext(), Info2Activity.class);
                    intent.putExtra("STOCK1", stockName2);
                    intent.putExtra("STOCK2", stockName);
                }
                else {
                    intent = new Intent(view.getContext(), InfoActivity.class);
                    intent.putExtra("STOCK_NAME", stockName);
                }

                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}