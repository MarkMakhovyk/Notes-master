package com.dev.mark.notes.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.MenuItem;

import com.dev.mark.notes.R;
import com.dev.mark.notes.data.database.NoteDAO;
import com.dev.mark.notes.domain.model.Note;
import com.dev.mark.notes.ui.editNotes.EditNoteFragment;
import com.dev.mark.notes.ui.list_notes.ListNotesFragment;

import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListNotesFragment listNotesFragment;
    private static final String EXTRA_UUID = "uuid";
    public NavigationView navigationView;

    public static Intent newInstance(Context context, UUID uuid) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_UUID, uuid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.layout_choice);
        setSupportActionBar(toolbar);
        getWindow().setExitTransition(new Explode());
        getWindow().setEnterTransition(new Explode());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        createStartFragment();
    }

    private void createStartFragment() {
        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_UUID);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (uuid == null) {
            listNotesFragment = new ListNotesFragment();
            transaction.replace(R.id.container, listNotesFragment).commit();
            navigationView.setCheckedItem(R.id.all_notes);
        } else {
            Note note = NoteDAO.get(this).getNote(uuid);
            Fragment fragment = EditNoteFragment.newInstance(note);
            fragment.setEnterTransition(new Fade());
            fragment.setEnterTransition(new Fade());
            transaction.replace(R.id.container, fragment).commit();
            navigationView.setCheckedItem(R.id.add_note);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.add_note) {
            fragment = new EditNoteFragment();
            fragment.setEnterTransition(new Fade());
            fragment.setEnterTransition(new Fade());
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null).commit();
        } else if (id == R.id.all_notes) {
            getSupportFragmentManager().popBackStack();
        } else if (id == R.id.reminder_notes) {
            fragment = ListNotesFragment.newInstance(ListNotesFragment.REMINDER_LIST);
            fragment.setEnterTransition(new Fade());
            fragment.setEnterTransition(new Fade());
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
