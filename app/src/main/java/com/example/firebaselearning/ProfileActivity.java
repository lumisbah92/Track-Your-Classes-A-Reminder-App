package com.example.firebaselearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference profileReference,createClassReference,joinClassReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    String userID,ownerName="";

    private FloatingActionButton floatinActionBtn;
    private RecyclerView recyclerView;
    private ArrayList<ModelClass> list;
    private AdapterClass adapterClass;
    private AdapterClass.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileReference = FirebaseDatabase.getInstance().getReference("Users");
        createClassReference = FirebaseDatabase.getInstance().getReference("Create Class");
        joinClassReference = FirebaseDatabase.getInstance().getReference("Join Class");

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutID);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        recyclerView = findViewById(R.id.recyclerViewID);

        final TextView fullnameProfile = findViewById(R.id.FullNameProfile);
        final TextView sectionProfile = findViewById(R.id.SectionProfile);

        // fatching Profile data from website
        profileReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){
                    String fullname = userProfile.fullname;
                    String email = userProfile.email;
                    String section = userProfile.section;

                    ownerName = fullname;

                    fullnameProfile.setText(fullname);
                    sectionProfile.setText(section);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });

        // Floating Action Bution(+)
        floatinActionBtn= findViewById(R.id.floatingactionBtnID);
        floatinActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), floatinActionBtn);
                dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());

                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId()==R.id.createClassID)
                            showCreateClassDialog();
                        else if(menuItem.getItemId() == R.id.joinClassID)
                            showJoinClassDialog();
                        return true;
                    }
                });
                dropDownMenu.show();

            }
        });

        // Recycler View
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileActivity.this);
        // to reverse the recycler view and show latest item on top
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        listener = new AdapterClass.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
                intent.putExtra("ClassName", list.get(position).getName());
                intent.putExtra("Section", list.get(position).getSection());
                intent.putExtra("OwnerName", list.get(position).getOwnerName());
                intent.putExtra("OwnerID", list.get(position).getUserID());
                startActivity(intent);
            }
        };

        adapterClass = new AdapterClass(this, list,listener);
        recyclerView.setAdapter(adapterClass);

        joinClassReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot DS: snapshot.getChildren()) {
                    ModelClass modelClass = DS.getValue(ModelClass.class);
                    list.add(modelClass);
                }
                adapterClass.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapterClass.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // swipe left to right or right to right of cardview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // swipe left to right or right to right of cardview

    List<ModelClass> archivedList = new ArrayList<>();

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            switch (direction)
            {
                case ItemTouchHelper.LEFT:
                    // Should also delete from database like as delete from list
                    ModelClass deletedModelClass = list.get(position);
                    final String deletedItem = deletedModelClass.getName();

                    list.remove(position);
                    adapterClass.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, deletedItem + " Deleted.", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    list.add(position, deletedModelClass);
                                    adapterClass.notifyItemInserted(position);
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    ModelClass archivedModelClass = list.get(position);
                    final String archivedItem = archivedModelClass.getName();
                    archivedList.add(archivedModelClass);

                    list.remove(position);
                    adapterClass.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, archivedItem + " Archived.", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    archivedList.remove(archivedList.lastIndexOf(archivedModelClass));
                                    list.add(position, archivedModelClass);
                                    adapterClass.notifyItemInserted(position);
                                }
                            }).show();
                    break;
            }
        }
    };

    // Adding Menu on Profile Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.manu_layout, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.LogoutMenuID){

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Create Class Dialog Showing
    void showCreateClassDialog() {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialog_create_class);

        //Initializing the views of the dialog.
        final EditText nameEt = dialog.findViewById(R.id.nameCreateDialogId);
        final EditText subjectEt = dialog.findViewById(R.id.subjectCreateDialogId);
        final EditText sectionEt = dialog.findViewById(R.id.sectionCreateDialogId);
        final EditText roomEt = dialog.findViewById(R.id.roomCreateDialogId);
        Button submitButton = dialog.findViewById(R.id.submitbtnCreateDialogId);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                String name = nameEt.getText().toString().trim();
                String subject = subjectEt.getText().toString().trim();
                String section = sectionEt.getText().toString().trim();
                String room = roomEt.getText().toString().trim();
                String UserID = userID;

                String key = createClassReference.push().getKey();

                // Generate random String
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 6;
                Random random = new Random();
                StringBuilder buffer = new StringBuilder(targetStringLength);
                for (int i = 0; i < targetStringLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    buffer.append((char) randomLimitedInt);
                }
                String generatedString = buffer.toString().trim();

                CreateClass createClass = new CreateClass(name, subject, section, room, generatedString, UserID,ownerName);
                createClassReference.child(key).setValue(createClass);
                joinClassReference.child(userID).child(joinClassReference.push().getKey()).setValue(createClass);
                Toast.makeText(getApplicationContext(), "Create Class Info Added", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Join Class Dialog Showing
    void showJoinClassDialog() {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialog_join_class);

        //Initializing the views of the dialog.
        final EditText CodeEt = dialog.findViewById(R.id.CodeJoinDialogId);
        Button submitButton = dialog.findViewById(R.id.submitbtnJoinDialogId);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Code = CodeEt.getText().toString().trim();

                createClassReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot DS: snapshot.getChildren())
                        {
                            ModelClass modelClass = DS.getValue(ModelClass.class);
                            if(Code.equals(modelClass.getGeneratedString())) {
                                joinClassReference.child(userID).child(joinClassReference.push().getKey()).setValue(modelClass);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast.makeText(getApplicationContext(), "Join Class Info Added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}