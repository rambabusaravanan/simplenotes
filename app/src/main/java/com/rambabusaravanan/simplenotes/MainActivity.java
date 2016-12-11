package com.rambabusaravanan.simplenotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.rambabusaravanan.simplenotes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        if (userToken != null && !userToken.equals("")) {
            openNotesListActivity();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(this);

        progress = new ProgressDialog(this);
        progress.setMessage("Loading ..");
    }

    public void login(View view) {
        String email = (binding.email.getText().toString());
        String password = (binding.password.getText().toString());

        progress.show();
        Backendless.UserService.login(email, password, new BackendlessCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                Toast.makeText(MainActivity.this, "Login success ..", Toast.LENGTH_SHORT).show();
                if(progress!=null && progress.isShowing()) progress.dismiss();
                openNotesListActivity();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                if(progress!=null && progress.isShowing()) progress.dismiss();
            }
        }, true);
    }

    private void openNotesListActivity() {
        Intent intent = new Intent(getBaseContext(), NotesListActivity.class);
        startActivity(intent);
        finish();
    }

    public void register(View view) {
        BackendlessUser user = new BackendlessUser();
        user.setEmail(binding.email.getText().toString());
        user.setPassword(binding.password.getText().toString());

        progress.show();
        Backendless.UserService.register(user, new BackendlessCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                Toast.makeText(MainActivity.this, "Registration success ..", Toast.LENGTH_SHORT).show();
                login(null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                if(progress!=null && progress.isShowing()) progress.dismiss();
            }
        });
    }
}
