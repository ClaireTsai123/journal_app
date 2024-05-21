package com.tsai.journalapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {
    //widgets
    EditText title_et, thoughts_et;
    Button save_btn;
    ProgressBar progressBar;
    // TextView title_tv, date;
    ImageView postImageBtn, imageView;
    //Firebase Auth: get userId and username
    private String currentUserId;
    private String currentUsername;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firebase(FireStore)
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");
    //Firebase (Storage)
    private StorageReference storageReference;

    //using Activity Result launcher
    ActivityResultLauncher<String> mTakePhoto;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);
        title_et = findViewById(R.id.post_title_et);
        thoughts_et = findViewById(R.id.post_description_et);
        save_btn = findViewById(R.id.post_save_journal_btn);
        progressBar = findViewById(R.id.post_progressBar);
        imageView = findViewById(R.id.post_imageView);
        postImageBtn = findViewById(R.id.photoCameraButton);

        progressBar.setVisibility(View.INVISIBLE);

        //Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        //Auth
        firebaseAuth = FirebaseAuth.getInstance();
        //getting current user
        if (user != null) {
            currentUserId = user.getUid();
            currentUsername = user.getDisplayName();
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveJournal();
            }
        });

        mTakePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        //showing image
                        imageView.setImageURI(o);
                        // get the image uri
                        imageUri = o;
                    }
                }
        );

        postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting image from the gallery
                mTakePhoto.launch("image/*");
            }
        });
    }

    private void SaveJournal() {
        String title = title_et.getText().toString().trim();
        String thoughts = thoughts_et.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) &&
                imageUri != null) {
            //the saving path of the images in Firebase Storage;
            //.../journal_images/my_image_202405081800.png
            final StorageReference filePath = storageReference.child("journal_images")
                    .child("my_image_" + Timestamp.now().getSeconds());
            //upload the image
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    //create a journal object
                                    Journal journal = new Journal();
                                    journal.setTitle(title);
                                    journal.setThoughts(thoughts);
                                    journal.setImageUrl(imageUrl);
                                    journal.setTimeAdded(new Timestamp(new Date()));
                                    journal.setUserName(currentUsername);
                                    journal.setUserId(currentUserId);

                                    collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressBar.setVisibility(View.INVISIBLE);

                                            Intent i = new Intent(AddJournalActivity.this, JournalListActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddJournalActivity.this,
                                                    "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddJournalActivity.this,
                                    "Failed to upload photo : " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
    }
}