package com.example.firebaselearning;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ClassInfoFragment extends Fragment{
    private ImageView addBtn;
    private Button postBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference postingDatabase,UserDatabase;
    private EditText postEditText;
    private String OwnerID = "OwnerID not set";
    private RecyclerView recyclerView;
    private ArrayList<PostingModel> list;
    private AdapterClassForPost adapterClassForPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_info, container, false);

        Bundle data = getArguments();
        if(data != null) {
            OwnerID = data.getString("OwnerID");
        }


        postingDatabase = FirebaseDatabase.getInstance().getReference("PostInClassActivity");


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutID);
        recyclerView = view.findViewById(R.id.recyclerViewID);
        addBtn = view.findViewById(R.id.AddbtnId);
        postBtn = view.findViewById(R.id.postBtnID);
        postEditText = view.findViewById(R.id.postEditTextID);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu dropDownMenu = new PopupMenu(getActivity().getApplicationContext(), addBtn);
                dropDownMenu.getMenuInflater().inflate(R.menu.add_menu_item, dropDownMenu.getMenu());

                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId()==R.id.UpImgID)
                            Toast.makeText(getActivity(), "Please Upload a Image", Toast.LENGTH_SHORT).show();
                        else if(menuItem.getItemId() == R.id.UpFileID)
                            Toast.makeText(getActivity(), "Please Upload a File", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String post = postEditText.getText().toString().trim();
                if(post.isEmpty()) {
                    postEditText.setError("Post Field is required");
                    postEditText.requestFocus();
                    return;
                }

               postingDatabase.child(OwnerID).child(postingDatabase.push().getKey()).child("Post").setValue(post);

            }
        });

        // RecyclerView for Post
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        adapterClassForPost = new AdapterClassForPost(getContext(), list);
        recyclerView.setAdapter(adapterClassForPost);

        postingDatabase.child(OwnerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot DS: snapshot.getChildren()) {
                    PostingModel postingModel = DS.getValue(PostingModel.class);
                    list.add(postingModel);
                }
                adapterClassForPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapterClassForPost.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }
}