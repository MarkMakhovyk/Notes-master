package com.dev.mark.notes.ui.list_notes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.mark.notes.R;
import com.dev.mark.notes.data.database.NoteDAO;
import com.dev.mark.notes.domain.model.Note;
import com.dev.mark.notes.ui.global.FullScrinPhotoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    private Context context;
    private Click clickCallback;
    public List<Note> notes;
    public boolean isMultiChoice = false;
    public List<Note> choiceArray = new ArrayList();

    public ListAdapter(Context context, List<Note> notes, @NonNull Click click) {
        this.context = context;
        this.notes = notes;
        clickCallback = click;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return  new ListHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_notes, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListHolder viewHolder, int i) {
        File photoFile = NoteDAO.get(context).getPhotoFile(context, notes.get(i));
        viewHolder.bind(notes.get(i), photoFile);
        setListenerForPhoto(viewHolder, photoFile);

        viewHolder.checkBox.setVisibility(isMultiChoice ? View.VISIBLE : View.GONE);
        viewHolder.checkBox.setChecked(choiceArray.indexOf(viewHolder.note) == -1 ? false : true);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMultiChoice)
                    clickCallback.clickItem(viewHolder);
                else {
                    bindChoice(viewHolder);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isMultiChoice = true;
                clickCallback.choiceItem(viewHolder.note);
                bindChoice(viewHolder);
                return true;
            }
        });
    }

    private void setListenerForPhoto(ListHolder holder, final File file) {
        if (file.exists())
            holder.photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = FullScrinPhotoActivity.newIntent(context, file);
                    context.startActivity(intent);
                }
            });
    }

    private void bindChoice(@NonNull ListHolder viewHolder) {
        if (choiceArray.indexOf(viewHolder.note) == -1)
            choiceArray.add(viewHolder.note);
        else choiceArray.remove(viewHolder.note);

        viewHolder.checkBox.setVisibility(isMultiChoice ? View.VISIBLE : View.GONE);
        viewHolder.checkBox.setChecked(choiceArray.indexOf(viewHolder.note) == -1 ? false : true);
        clickCallback.setCountChoice(choiceArray.size());
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface Click {
        void clickItem(ListHolder holder);
        void choiceItem(Note note);
        void setCountChoice(int count);
    }
}
