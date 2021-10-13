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

public class ClassActivity extends AppCompatActivity{

    private TextView subject,section,ownerName;
    private BottomNavigationView navigationView;
    private String OwnerID = "OwnerID not set";
    private String OwnerName ="OwnerName not set";
    private String Subject ="Subject not set";
    private String Section ="Section not set";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerID, new ClassInfoFragment()).commit();

        subject = findViewById(R.id.subjectcardID);
        section = findViewById(R.id.sectionCardID);
        ownerName = findViewById(R.id.ownerCardID);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            Subject = extras.getString("ClassName");
            Section = extras.getString("Section");
            OwnerName = extras.getString("OwnerName");
            OwnerID = extras.getString("OwnerID");
        }

        subject.setText(Subject);
        section.setText(Section);
        ownerName.setText(OwnerName);

        navigationView = findViewById(R.id.navBarID);
        navigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId())
                {
                    case R.id.ClassInfoID:
                        ClassInfoFragment classInfoFragment = new ClassInfoFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        Bundle data = new Bundle();
                        data.putString("OwnerID", OwnerID);
                        classInfoFragment.setArguments(data);
                        fragmentTransaction.replace(R.id.fragmentContainerID, classInfoFragment).commit();
                        break;
                    case R.id.ExamInfoID:
                        fragment = new ExamInfoFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerID, fragment).commit();
                        break;
                    case R.id.AssignmentInfoID:
                        fragment = new AssignmentInfoFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerID, fragment).commit();
                        break;
                    case R.id.PresentationInfoID:
                        fragment = new PresentationInfoFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerID, fragment).commit();
                        break;
                }

                return true;
        }
    };
}