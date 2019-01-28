package com.dev.mark.notes.ui.list_notes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.dev.mark.notes.R;
import com.dev.mark.notes.data.database.NoteDAO;
import com.dev.mark.notes.data.database.RememberService;
import com.dev.mark.notes.domain.model.Note;
import com.dev.mark.notes.ui.editNotes.EditNoteFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListNotesFragment extends Fragment implements ListAdapter.Click {
    public static int REMINDER_LIST = 0;
    public final static String ARG_VAR_USE = "method_use";

    private List<Note> notes;
    private ListAdapter listAdapter;
    private ActionBar actionBar;
    private NavigationView navigationView;

    @BindView(R.id.count_choice)
    TextView countChoice;

    @BindView(R.id.cancel)
    TextView cancelView;

    @BindView(R.id.all_choice)
    TextView allChoice;

    @BindView(R.id.list_notes)
    RecyclerView listNotesRecyclerView;

    @BindView(R.id.empty_list)
    LinearLayout emptyListLayout;

    @BindView(R.id.layout_choice)
    LinearLayout layoutChoice;

    @BindView(R.id.layout_delete)
    LinearLayout layoutDelete;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    public static ListNotesFragment newInstance(int idList) {
        Bundle args = new Bundle();
        ListNotesFragment fragment = new ListNotesFragment();
        args.putInt(ARG_VAR_USE, idList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.notes = query(newText);
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listAdapter.notes = notes;
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private List<Note> query(String query) {
        List<Note> queryList = new ArrayList<>();
        for (Note note : notes) {
            if (note.getTitle().contains(query) || note.getTextNote().contains(query))
                queryList.add(note);
        }
        return queryList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_notes, container, false);
        ButterKnife.bind(this, view);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        listNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        listAdapter = new ListAdapter(getContext(), notes, this);
        listNotesRecyclerView.setAdapter(listAdapter);
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditNoteFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                navigationView.setCheckedItem(R.id.add_note);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        if (getArguments() != null
                && (int) getArguments().getSerializable(ARG_VAR_USE) == REMINDER_LIST) {
            notes = NoteDAO.get(getContext()).getNotesReminder();
            getActivity().setTitle(R.string.list_reminders);
            navigationView.setCheckedItem(R.id.reminder_notes);
        } else {
            getActivity().setTitle(R.string.app_name);
            notes = NoteDAO.get(getContext()).getNotes();
            navigationView.setCheckedItem(R.id.all_notes);
        }

        listAdapter.notes = notes;
        listAdapter.notifyDataSetChanged();
        emptyList();
    }

    void emptyList() {
        if (notes.size() == 0) {
            listNotesRecyclerView.setVisibility(View.GONE);
            emptyListLayout.setVisibility(View.VISIBLE);
        } else {
            listNotesRecyclerView.setVisibility(View.VISIBLE);
            emptyListLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listAdapter.isMultiChoice)
            collapseMultiChoice();
    }

    @Override
    public void clickItem(ListHolder holder) {

        Fragment fragment = EditNoteFragment.newInstance(holder.note);
        fragment.setEnterTransition(new Explode());
        fragment.setExitTransition(new Fade());

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();

        navigationView.setCheckedItem(R.id.add_note);
    }

    @Override
    public void setCountChoice(int count) {
        countChoice.setText(getString(R.string.choice_d, count));
        allChoice.setText(listAdapter.choiceArray.size() == listAdapter.notes.size() ?
                getString(R.string.nil_choice) : getString(R.string.all_choice));
    }

    @Override
    public void choiceItem(Note click) {
        listAdapter.notifyDataSetChanged();
        actionBar.hide();
        fab.hide();
        layoutChoice.setVisibility(View.VISIBLE);
        layoutDelete.setVisibility(View.VISIBLE);
        addListenerToolbarButton();
    }

    private void addListenerToolbarButton() {
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseMultiChoice();
            }
        });
        allChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listAdapter.choiceArray.size() != listAdapter.notes.size()) {
                    listAdapter.choiceArray.clear();
                    listAdapter.choiceArray.addAll(listAdapter.notes);
                } else
                    listAdapter.choiceArray.clear();

                setCountChoice(listAdapter.choiceArray.size());
                listAdapter.notifyDataSetChanged();
            }
        });

        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteChoiceNotes();
                collapseMultiChoice();
                updateData();
            }
        });
    }

    private void DeleteChoiceNotes() {
        List<Note> delete = listAdapter.choiceArray;
        for (Note note : delete) {
            File file = NoteDAO.get(getContext()).getPhotoFile(getActivity(), note);
            NoteDAO.get(getContext()).deleteNote(note, file);
        }
        RememberService.setServiceWakeUp(getContext());
    }

    private void collapseMultiChoice() {
        fab.show();
        actionBar.show();
        layoutChoice.setVisibility(View.GONE);
        layoutDelete.setVisibility(View.GONE);
        listAdapter.choiceArray.clear();
        listAdapter.isMultiChoice = false;
        listAdapter.notifyDataSetChanged();
    }
}
