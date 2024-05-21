package com.tsai.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {
    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firebase firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");

    //Firebase Storage
    private StorageReference storageReference;

    //List of Journals
    private List<Journal> journalList;

    //RecyclerView
    private RecyclerView recyclerView;
    private JournalAdapter adapter;
    //widgets
    private FloatingActionButton fbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        //widgets
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //posts arraylists
        journalList = new ArrayList<>();
        fbt = findViewById(R.id.ftb);
        fbt.setOnClickListener(v -> {
            Intent i = new Intent(JournalListActivity.this, AddJournalActivity.class);
            startActivity(i);
        });
    }

    //2- Adding a menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add) {
            if (user != null && firebaseAuth != null) {
                Intent i = new Intent(JournalListActivity.this,
                        AddJournalActivity.class);
                startActivity(i);
            }
        } else if (itemId == R.id.action_signout) {
            if (user != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                Intent i = new Intent(JournalListActivity.this, MainActivity.class);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            //QueryDocumentSnapshot: is an object that represents a single document
            //retrieved from a Firestore query
            for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                Journal journal = journals.toObject(Journal.class);
                journalList.add(journal);
            }
            //adapter, set recyclerview
            adapter = new JournalAdapter(JournalListActivity.this, journalList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(JournalListActivity.this,
                "Oops.. Something went wrong!", Toast.LENGTH_SHORT).show());
    }
}