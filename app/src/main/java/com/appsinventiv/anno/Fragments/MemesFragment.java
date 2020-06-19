package com.appsinventiv.anno.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsinventiv.anno.Adapter.MemesAdapter;
import com.appsinventiv.anno.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MemesFragment extends Fragment {

    RecyclerView recyclerview;
    Context context;
    private DatabaseReference mDatabase;
    MemesAdapter adapter;
    ArrayList<String> itemList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_memes, container, false);
        recyclerview = rootView.findViewById(R.id.recyclerview);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        adapter = new MemesAdapter(context, itemList);
        recyclerview.setLayoutManager(new GridLayoutManager(context,4));
        recyclerview.setAdapter(adapter);

        getDataFromDB();

        return rootView;
    }

    private void getDataFromDB() {
        mDatabase.child("Media").child("Memes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String url = snapshot.getValue(String.class);
                        itemList.add(url);
                    }
                }
                adapter.setItemList(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }


}
