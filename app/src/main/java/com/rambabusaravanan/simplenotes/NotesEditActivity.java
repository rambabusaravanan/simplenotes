package com.rambabusaravanan.simplenotes;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rambabusaravanan.simplenotes.databinding.ActivityNotesEditBinding;
import com.rambabusaravanan.simplenotes.model.Notes;

public class NotesEditActivity extends AppCompatActivity {

    private Notes notes;
    private ActivityNotesEditBinding binding;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notes_edit);

        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        bar.setDisplayHomeAsUpEnabled(true);

        try {
            notes = (Notes) getIntent().getExtras().get("data");
            if(bar!=null) bar.setTitle(R.string.txt_notes_edit);
        } catch (Exception e) {
            notes = new Notes();
            if(bar!=null) bar.setTitle(R.string.txt_notes_new);
        }
        binding.setNotes(notes);

        progress = new ProgressDialog(this);
        progress.setMessage("Loading ..");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        notes.title = binding.title.getText().toString();
        notes.message = binding.message.getText().toString();

        progress.show();
        Backendless.Persistence.save(notes, new BackendlessCallback<Notes>() {
            @Override
            public void handleResponse(Notes notes) {
                Toast.makeText(NotesEditActivity.this, "Saved ..", Toast.LENGTH_SHORT).show();
                if(progress!=null && progress.isShowing()) progress.dismiss();
                onBackPressed();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getBaseContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                if(progress!=null && progress.isShowing()) progress.dismiss();
            }
        });
    }
}
