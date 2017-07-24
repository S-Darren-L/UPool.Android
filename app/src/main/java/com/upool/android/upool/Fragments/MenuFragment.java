package com.upool.android.upool.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.upool.android.upool.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Darren on 7/24/2017.
 */

public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.menuLogOut)
    public void onMenuLogOutClicked(){
        Log.i(TAG, "onMenuLogOutClicked");
        FirebaseAuth.getInstance().signOut();
    }
}
