package com.example.firebaselearning;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class ClassActivity extends AppCompatActivity {

    private TextView className, section, ownerName;
    private String OwnerID = "OwnerID not set";
    private String OwnerName = "OwnerName not set";
    private String Subject = "Subject not set";
    private String Section = "Section not set";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerID, new ClassInfoFragment()).commit();

        className = findViewById(R.id.tvClassName);
        section = findViewById(R.id.tvSection);
        ownerName = findViewById(R.id.tvOwnerName);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Subject = extras.getString("ClassName");
            Section = extras.getString("Section");
            OwnerName = extras.getString("OwnerName");
            OwnerID = extras.getString("OwnerID");
        }
        OwnerID += Subject;
        className.setText(Subject);
        section.setText(Section);
        ownerName.setText(OwnerName);
    }
}