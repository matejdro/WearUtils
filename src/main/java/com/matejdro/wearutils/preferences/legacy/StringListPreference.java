package com.matejdro.wearutils.preferences.legacy;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matejdro.wearutils.R;
import com.matejdro.wearutils.miscutils.HtmlCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StringListPreference extends DialogPreference {
    private ListAdapter adapter;
    private List<String> itemsDraft = new ArrayList<>();
    private List<String> savedItems = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StringListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public StringListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StringListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StringListPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        adapter = new ListAdapter();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setNeutralButton(R.string.add, null);
    }

    @Override
    protected View onCreateDialogView() {
        @SuppressLint("InflateParams")
        RecyclerView recycler = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_string_set_preference, null);

        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        return recycler;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        // Remap Add button to not close dialog.
        android.app.AlertDialog dialog = (android.app.AlertDialog) getDialog();
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();
            }
        });
    }


    protected void openAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        @SuppressLint("InflateParams")
        ViewGroup dialogView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.dialog_single_edit_text, null);
        final EditText editField = dialogView.findViewById(R.id.edit_text);

        builder.setTitle(R.string.add);
        builder.setView(dialogView);

        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);

        preAddDialogOpen(builder, editField);

        final AlertDialog dialog = builder.create();

        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editField.getText().toString();
                String errorMessage = validateNewEntry(newEntry);
                if (errorMessage == null) {
                    add(newEntry);
                    dialog.dismiss();
                } else {
                    editField.setError(errorMessage);
                }
            }
        });

        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void openConfirmationDialog(final int entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.item_deletion);
        builder.setMessage(R.string.deletion_confirmation);

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemsDraft.remove(entry);
                adapter.notifyItemRemoved(entry);
            }
        });
        builder.setNegativeButton(android.R.string.no, null);

        builder.show();
    }

    public void add(String text) {
        itemsDraft.add(text);
        adapter.notifyItemInserted(itemsDraft.size() - 1);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setItems(loadPersistedValue(restorePersistedValue ? getPersistedString(null) : (String) defaultValue));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }


    public Collection<String> getItems() {
        return savedItems;
    }

    public void setItems(List<String> newItems) {
        boolean change = !this.savedItems.equals(newItems);

        if (change) {
            this.savedItems = newItems;

            persistCurrentValue();
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();

            itemsDraft = new ArrayList<>(newItems);
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> loadPersistedValue(String serializedString) {
        if (serializedString == null) {
            return Collections.emptyList();
        }

        try {
            JSONArray jsonArray = new JSONArray(serializedString);
            List<String> list = new ArrayList<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }

            return list;
        } catch (JSONException e) {
            return null;
        }
    }

    private void persistCurrentValue() {

        JSONArray jsonArray = new JSONArray();
        for (String item : savedItems) {
            jsonArray.put(item);
        }

        persistString(jsonArray.toString());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            setItems(itemsDraft);
        } else {
            itemsDraft = savedItems;
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings("UnusedParameters")
    protected void preAddDialogOpen(AlertDialog.Builder builder, EditText editText) {

    }

    /**
     * @return {@code null} if entry is valid or error message if entry is not valid.
     */
    @SuppressWarnings("SameReturnValue")
    protected String validateNewEntry(String newEntry) {
        return null;
    }

    @Override
    public CharSequence getSummary() {
        return HtmlCompat.fromHtml(super.getSummary().toString());
    }

    private class ListAdapter extends RecyclerView.Adapter<ListItemHolder> {
        @NonNull
        @Override
        public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.simple_selectable_list_item, parent, false);

            final ListItemHolder holder = new ListItemHolder(view);
            holder.textView = view;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openConfirmationDialog(holder.getAdapterPosition());
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(ListItemHolder holder, int position) {
            holder.textView.setText(itemsDraft.get(position));
        }

        @Override
        public int getItemCount() {
            return itemsDraft.size();
        }
    }

    private class ListItemHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ListItemHolder(View itemView) {
            super(itemView);
        }
    }
}
