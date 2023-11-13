package com.devfelix.smartnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteDetailsActivity extends AppCompatActivity {

    private TextView mTitleOfNote, mContentOfNote;
    FloatingActionButton goToEditNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        mTitleOfNote = findViewById(R.id.titlenotedetail);
        mContentOfNote = findViewById(R.id.contentnotedetail);
        goToEditNote = findViewById(R.id.gotoeditnote);

        Toolbar toolbar = findViewById(R.id.toolbarnotedetail);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent data = getIntent();

        goToEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditNoteActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                view.getContext().startActivity(intent);
            }
        });

        mContentOfNote.setText(data.getStringExtra("content"));
        mTitleOfNote.setText(data.getStringExtra("title"));
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
        Intent intent = new Intent(NoteDetailsActivity.this, NotesActivity.class);
        startActivity(intent);
        finish();
    }
}