package com.example.efedoseeva.mynotepad;

/**
 * Created by efedoseeva on 14.06.2016.
 */
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button deleteButton = (Button) findViewById(R.id.delete);

        mRowId = savedInstanceState != null ? savedInstanceState.getLong(NotesDbAdapter.KEY_ROWID)
                : null;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }
        populateFields();
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                saveState();
                setResult(RESULT_OK);
                finish();
            }
        });

       deleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (mRowId != null) {
                    mDbHelper.deleteNote(mRowId);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (!(title.equals("")) || !(body.equals(""))) {
            int endInd = body.length() > 10 ? 10
                        : body.length();

            String title_new = title.equals("") ? body.substring(0, endInd)
                        : title;
            if (mRowId == null){
                long id = mDbHelper.createNote(title_new, body);
                if (id > 0) {
                    mRowId = id;
                }
            } else {
                mDbHelper.updateNote(mRowId, title_new, body);
                }
        }
        else {
            if (mRowId != null) {
                mDbHelper.deleteNote(mRowId);
            }
        }

    }

}

