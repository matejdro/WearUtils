package com.matejdro.wearutils.preferences.legacy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.matejdro.wearutils.R;
import com.matejdro.wearutils.miscutils.BitmapUtils;
import com.matejdro.wearutils.miscutils.HtmlCompat;

import java.lang.ref.WeakReference;

interface ImagePickerListener {
    void onImagePicked(Uri imageUri);
}

public class ImagePickerPreference extends Preference implements ImagePickerListener {

    private String summaryFormat;

    private Uri currentUri;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImagePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ImagePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ImagePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImagePickerPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        summaryFormat = getSummary().toString();

        setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ImagePickerFragment(getTitle().toString(), currentUri, ImagePickerPreference.this)
                        .show(((Activity) getContext()).getFragmentManager(), "ImagePickerFragment");
                return true;
            }
        });
    }

    @Override
    public void onImagePicked(Uri imageUri) {
        setCurrentUri(imageUri);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setCurrentUri(restorePersistedValue ? getPersistedString((String) defaultValue) : (String) defaultValue);
    }

    public void setCurrentUri(String uriString) {
        if (uriString == null) {
            setCurrentUri((Uri) null);
        } else {
            setCurrentUri(Uri.parse(uriString));
        }
    }

    public void setCurrentUri(Uri newUri) {
        boolean change = currentUri == null || !currentUri.equals(newUri);
        if (!change) {
            return;
        }

        this.currentUri = newUri;

        String summary = String.format(summaryFormat, currentUri.toString());
        setSummary(HtmlCompat.fromHtml(summary));
        persistString(this.currentUri.toString());

        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    public static class ImagePickerFragment extends DialogFragment {
        private static final int REQUEST_CODE_SYSTEM_PICKER = 0;

        private String dialogTitle;
        private Uri selectedUri;
        private ImagePickerListener imagePickerListener;

        private ImageView imageBox;
        private EditText imagePathBox;
        private ProgressBar progressBar;

        private ImageUpdateTask currentUpdateTask;

        public ImagePickerFragment() {

        }

        @SuppressLint("ValidFragment")
        // Fragment depends on the Preference state, so it cannot be reserialized - use constructor
        public ImagePickerFragment(String dialogTitle, Uri selectedUri, ImagePickerListener imagePickerListener) {
            this.dialogTitle = dialogTitle;
            this.selectedUri = selectedUri;
            this.imagePickerListener = imagePickerListener;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            setRetainInstance(true);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (imagePickerListener == null) {
                dismiss();
                return;
            }

            if (requestCode == REQUEST_CODE_SYSTEM_PICKER && resultCode == Activity.RESULT_OK) {
                selectedUri = data.getData();
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (imagePickerListener == null) {
                return null;
            }

            @SuppressLint("InflateParams")
            ViewGroup root = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image_picker, null);

            imagePathBox = root.findViewById(R.id.image_path);
            imageBox = root.findViewById(R.id.image);
            progressBar = root.findViewById(R.id.progress);

            root.findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshImageBox();
                }
            });

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle(dialogTitle)
                    .setView(root)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirm();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setNeutralButton(R.string.browse, null);

            return dialogBuilder.create();
        }

        @Override
        public void onStart() {
            super.onStart();

            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImagePicker();
                }
            });

            updatePicture();

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20);
            }
        }

        private void updatePicture() {
            if (currentUpdateTask != null) {
                currentUpdateTask.cancel(true);
                currentUpdateTask = null;
            }

            imagePathBox.setText(null);
            imageBox.setImageDrawable(null);

            if (selectedUri != null && !selectedUri.toString().trim().isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);

                new ImageUpdateTask(this, selectedUri).execute((Void) null);
            }
        }

        private void refreshImageBox() {
            selectedUri = Uri.parse(imagePathBox.getText().toString());
            updatePicture();
        }

        private void confirm() {
            if (imagePickerListener != null) {
                imagePickerListener.onImagePicked(Uri.parse(imagePathBox.getText().toString()));
            }
        }

        private void showImagePicker() {
            if (imagePickerListener == null) {
                dismiss();
            }

            Intent pickerIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                pickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                pickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            } else {
                pickerIntent = new Intent(Intent.ACTION_PICK);
            }

            pickerIntent.setType("image/*");

            try {
                startActivityForResult(pickerIntent, REQUEST_CODE_SYSTEM_PICKER);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.error_no_gallery, Toast.LENGTH_SHORT).show();
            }
        }

        private static class ImageUpdateTask extends AsyncTask<Void, Void, Drawable> {
            private WeakReference<ImagePickerFragment> targetFragment;
            private Uri uri;
            private String error;

            public ImageUpdateTask(ImagePickerFragment targetFragment, Uri uri) {
                this.targetFragment = new WeakReference<>(targetFragment);
                this.uri = uri;

                error = targetFragment.getString(R.string.image_not_found);
            }

            @Override
            protected Drawable doInBackground(Void... voids) {
                ImagePickerFragment targetFragment = this.targetFragment.get();
                if (targetFragment == null) {
                    return null;
                }

                Context context = targetFragment.getActivity();
                if (context == null) {
                    return null;
                }


                Drawable image;
                try {
                    image = BitmapUtils.getDrawableFromUri(context, uri);
                } catch (SecurityException e) {
                    error = context.getString(R.string.no_storage_permission);
                    return null;
                }

                // Make sure image can be opened later.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException ignored) {
                    }
                }

                return image;
            }


            @Override
            protected void onPostExecute(Drawable drawable) {
                ImagePickerFragment targetFragment = this.targetFragment.get();
                if (targetFragment == null) {
                    return;
                }

                targetFragment.progressBar.setVisibility(View.GONE);

                Context context = targetFragment.getActivity();
                if (context == null) {
                    return;
                }

                targetFragment.imagePathBox.setText(uri.toString());

                if (drawable == null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                targetFragment.imageBox.setImageDrawable(drawable);
            }
        }
    }
}
