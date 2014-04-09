package topicosBasesDatos.frases;

import static android.provider.BaseColumns._ID;
import static topicosBasesDatos.frases.Constantes.AUTOR;
import static topicosBasesDatos.frases.Constantes.FRASE;
import static topicosBasesDatos.frases.Constantes.TABLE_NAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

public class FraseData extends SQLiteOpenHelper {
	private static final String TAG = "FrasesDatabase";
	// The columns we'll include in the dictionary table
	public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;
	
	private static final String DATABASE_NAME = "frases.db";
	private static final int DATABASE_VERSION = 11;
	
	private Context context;
	private SQLiteDatabase db;
	private ProgressBar pb;
	
	private Handler mHandler = new Handler();
	private VisorFrases parent;

	public FraseData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	public FraseData(Context context, ProgressBar pb, VisorFrases parent){
		this(context);
		this.pb = pb;
		this.parent = parent;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.w(TAG, "Creating database");
		this.db = db;
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FRASE
				+ " TEXT NOT NULL," + AUTOR + " TEXT NOT NULL);");
		fillTable();
	}
	
	/**
	 * Starts a thread to load the database table with words
	 */
	private void fillTable() {
		new Thread(new Runnable() {
			public void run() {
				try {
					mHandler.post(new Runnable() {
						public void run() {
							pb.setVisibility(ProgressBar.VISIBLE);
						}
					});
					loadFrases();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}
	
	private void loadFrases() throws IOException {
        Log.d(TAG, "Loading frases...");
        final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.frases);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int progress = 0;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
            	progress ++;
            	pb.setProgress(progress);
                String[] strings = TextUtils.split(line, "-");
                if (strings.length < 2) continue;
                long id = addFrase(strings[0].trim(), strings[1].trim());
                if (id < 0) {
                    Log.e(TAG, "unable to add word: " + strings[0].trim());
                }
            }
        } finally {
            reader.close();
            mHandler.post(new Runnable() {
				
				public void run() {
					pb.setVisibility(ProgressBar.GONE);
					parent.refresh();
				}
			});
        }
        Log.d(TAG, "DONE loading words.");
    }
	
	/**
     * Add a word to the dictionary.
     * @return rowId or -1 if failed
     */
    public long addFrase(String autor, String frase) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(AUTOR, autor);
        initialValues.put(FRASE, frase);

        return db.insert(TABLE_NAME, null, initialValues);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}