package com.rambabusaravanan.simplenotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.rambabusaravanan.simplenotes.databinding.ActivityNotesListBinding;
import com.rambabusaravanan.simplenotes.databinding.ItemNotesListBinding;
import com.rambabusaravanan.simplenotes.model.Notes;

import java.util.List;

public class NotesListActivity extends AppCompatActivity {

    private Adapter adapter;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        final ActivityNotesListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notes_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.recycler.addItemDecoration(dividerItemDecoration);

        adapter = new Adapter();
        binding.recycler.setAdapter(adapter);

        progress = new ProgressDialog(this);
        progress.setMessage("Loading ..");
        loadData();
    }

    private void loadData() {
        progress.show();
        String userId = Backendless.UserService.loggedInUser();

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setPageSize(100);
        dataQuery.setWhereClause("userId = '"+userId+'\'');
        Backendless.Persistence.find(Notes.class, dataQuery, new BackendlessCallback<BackendlessCollection<Notes>>() {
            @Override
            public void handleResponse(BackendlessCollection<Notes> notesCollection) {
                adapter.set(notesCollection.getData());
                adapter.notifyDataSetChanged();
                if(progress!=null && progress.isShowing()) progress.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getBaseContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                if(progress!=null && progress.isShowing()) progress.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create:
                openNew();
                break;
            case R.id.refresh:
                loadData();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Backendless.UserService.logout(new BackendlessCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getBaseContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                if(progress!=null && progress.isShowing()) progress.dismiss();
            }
        });
    }

    public void openNew() {
        Intent intent = new Intent(this, NotesEditActivity.class);
        startActivity(intent);
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<Notes> notes;

        void set(List<Notes> notes) {
            this.notes = notes;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemNotesListBinding binding = ItemNotesListBinding.inflate(inflater, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.binding.setNotes(notes.get(position));
        }

        @Override
        public int getItemCount() {
            return notes != null ? notes.size() : 0;
        }


        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ItemNotesListBinding binding;

            ViewHolder(ItemNotesListBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                this.binding.setListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NotesEditActivity.class);
                intent.putExtra("data", notes.get(this.getLayoutPosition()));
                startActivity(intent);
            }
        }
    }
}
