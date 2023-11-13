package com.devfelix.smartnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    Intent data;
    EditText mEditTitle, mEditContent;
    FloatingActionButton mSaveNote;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    ImageButton button_capture;

    Bitmap bitmap;

    public static final int REQUEST_CAMERA_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mEditTitle = findViewById(R.id.edittitlenote);
        mEditContent = findViewById(R.id.editcontentnote);
        mSaveNote = findViewById(R.id.saveeditnote);
        data = getIntent();

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        button_capture = findViewById(R.id.openCameraButton);

        Toolbar toolbar = findViewById(R.id.toolbareditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        mSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Save button Clciked", Toast.LENGTH_SHORT).show();

                String newTitle = mEditTitle.getText().toString();
                String newContent = mEditContent.getText().toString();

                if(newTitle.isEmpty() || newContent.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Title and content cannot be empty. Please fill in both fields.", Toast.LENGTH_SHORT).show();

                }
                else {
                    DocumentReference documentReference = firestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newTitle);
                    note.put("content", newContent);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note successfully updated", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(EditNoteActivity.this, NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to update note", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        mEditContent.setText(noteContent);
        mEditTitle.setText(noteTitle);

        if(ContextCompat.checkSelfPermission(EditNoteActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EditNoteActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(EditNoteActivity.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("INFO", "Got here");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            Log.i("INFO", "Got here 2");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.i("INFO", String.valueOf(resultCode));
            if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // Handle crop error
                Exception error = result.getError();
                Log.e("ERROR", "Crop error: " + error.getMessage());
            } else if (resultCode == RESULT_OK ) {
                Log.i("INFO", "Got here 3");

                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void getTextFromImage(Bitmap bitmap){
        Log.i("INFO", "Got here 4");
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if(!recognizer.isOperational()){
            Toast.makeText(EditNoteActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<textBlockSparseArray.size(); i++){
                TextBlock textBlock= textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            Log.i("INFO", stringBuilder.toString());
            StringBuilder currentText = new StringBuilder();
            currentText.append(mEditContent.getText() + "");
            currentText.append(stringBuilder);
            mEditContent.setText(currentText);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditNoteActivity.this, NotesActivity.class);
        startActivity(intent);
        finish();
    }
}