package com.joo.dayo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.joo.dayo.R;
import com.joo.dayo.adapter.NewPostAdapter;
import com.joo.dayo.data.PostData;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class BestPostFragment extends Fragment {

    FirebaseFirestore firestore;
    DatabaseReference db;
    NewPostAdapter newPostAdapter;
    ArrayList<PostData> newPostList;
    RecyclerView newPostView;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_new_post,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        init();
    }

    void init(){

        newPostList = new ArrayList<>();

        //db에서 가져오기
        firestore.collection("post")
                .orderBy("favorite", Query.Direction.DESCENDING )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d(TAG, document.getId() + " => " + document.getData());
                                newPostList.add(new PostData(document.getData().get("explain").toString(),
                                        document.getData().get("photoName").toString(),
                                        document.getData().get("uid").toString(),
                                        document.getData().get("userId").toString(),
                                        document.getData().get("timeStamp").toString(),
                                        Integer.parseInt(document.getData().get("favorite").toString()),
                                        Integer.parseInt(document.getData().get("folderNum").toString())
                                ));
                            }

                            //연결
                            newPostView = (RecyclerView) getView().findViewById(R.id.newPostView);
                            newPostView.setHasFixedSize(true);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            newPostView.setLayoutManager(linearLayoutManager);
                            newPostAdapter = new NewPostAdapter(newPostList);
                            newPostView.setAdapter(newPostAdapter);

                            newPostAdapter.notifyDataSetChanged();
                        } else {
                            // Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

}
