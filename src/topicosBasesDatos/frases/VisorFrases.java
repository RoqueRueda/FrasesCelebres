package topicosBasesDatos.frases;

import static android.provider.BaseColumns._ID;
import static topicosBasesDatos.frases.Constantes.AUTOR;
import static topicosBasesDatos.frases.Constantes.FRASE;
import static topicosBasesDatos.frases.Constantes.TABLE_NAME;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.content.*;

public class VisorFrases extends ListActivity {

	// The manager of the SQLiteDatabase
	private FraseData frases;
	// The cursor result of the query
	private Cursor cursorFrase;
	// List view
	private ListView frasesList;
	// A String to save the Id that belong to a Frase
	private int currentItem;
	// The adapter
	private SimpleCursorAdapter adapter;
	// Tag of the debbuger
	private static final String TAG = "Lista Frases";
	// Text
	private TextView text;
	
	private ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frases_list);
		pb = (ProgressBar)findViewById(R.id.loadingFrases);
		pb.setVisibility(ProgressBar.GONE);
		frases = new FraseData(this, pb, this);
		// Get the query
		refresh();

		text = (TextView) findViewById(R.id.visor_frases_text);

		final ActionItem editAction = new ActionItem();

		editAction.setTitle("Editar");
		editAction.setIcon(getResources().getDrawable(
				R.drawable.ic_editar_default));

		final ActionItem deleteAction = new ActionItem();

		deleteAction.setTitle("Eliminar");
		deleteAction.setIcon(getResources().getDrawable(
				R.drawable.ic_eliminar_default));

		frasesList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "Showing quick actions");
				final QuickAction mQuickAction = new QuickAction(view);

				final ImageView mMoreImage = (ImageView) view
						.findViewById(R.id.i_more);

				if (cursorFrase.moveToPosition(position)) {
					currentItem = cursorFrase.getPosition();
				}

				mMoreImage.setImageResource(R.drawable.ic_list_more_selected);

				editAction.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (cursorFrase.moveToPosition(currentItem)) {
							Intent i = new Intent(VisorFrases.this,
									FraseDetails.class);
							i.putExtra("fraseText", cursorFrase.getString(1));
							i.putExtra("autorText", cursorFrase.getString(2));
							i.putExtra("idFraseUpdate", cursorFrase
									.getString(0));
							// Begin the activity
							startActivityForResult(i, 10);
							Log.d(TAG, "Start the Editor");
						}
						mQuickAction.dismiss();
					}
				});

				deleteAction.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Log.d(TAG, "Deleting frase");
						if (cursorFrase.moveToPosition(currentItem)) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									VisorFrases.this);
							builder
									.setMessage(
											"¿Está seguro de que quiere eliminar la frase: "
													+ "\""
													+ cursorFrase.getString(1)
													+ "\""
													+ "? Esta acción no se puede deshacer")
									.setTitle("¿Está seguro de que eliminar?")
									.setCancelable(false)
									.setPositiveButton(
											"Ok",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													if (cursorFrase
															.moveToPosition(currentItem)) {
														if (deleteFrase(cursorFrase
																.getString(0))) {
															cursorFrase = getFrases();
															showFrases(cursorFrase);
														}
													}
												}
											})
									.setNegativeButton(
											"Cancel",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.cancel();
												}
											});
							AlertDialog alert = builder.create();
							alert.show();
						}
						mQuickAction.dismiss();
					}
				});

				mQuickAction.addActionItem(editAction);
				mQuickAction.addActionItem(deleteAction);
				mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
				mQuickAction.setOnDismissListener(new OnDismissListener() {
					public void onDismiss() {
						mMoreImage.setImageResource(R.drawable.ic_list_more);
					}
				});
				mQuickAction.show();
			}
		});

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			searchFrase(query);
		}

	}

	public void refresh() {
		Log.d(TAG, "Get the frases");
		cursorFrase = getFrases();
		frasesList = getListView();
		Log.d(TAG, "Showing frases");
		showFrases(cursorFrase);
	}

	private void searchFrase(String query) {
		SQLiteDatabase db = frases.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + AUTOR
				+ " LIKE '%" + query + "%' " + " OR " + FRASE + " LIKE '%"
				+ query + "%';";
		cursorFrase = db.rawQuery(sql, null);

		if (cursorFrase == null) {
			// There are no results
			text.setText(getString(R.string.no_results,
							new Object[] { query }));
		} else {
			// Display the number of results
			int count = cursorFrase.getCount();
			String countString = getResources().getQuantityString(
					R.plurals.search_results, count,
					new Object[] { count, query });
			text.setText(countString);

			showFrases(cursorFrase);
		}

	}

	private Cursor getFrases() {
		// Perform a managed query. The Activity will handle closing
		// and re-querying the cursor when needed.
		SQLiteDatabase db = frases.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null,
				ORDER_BY);
		startManagingCursor(cursor);
		return cursor;
	}

	private static String[] FROM = { _ID, FRASE, AUTOR, };
	private static String ORDER_BY = AUTOR + " ASC";
	private static int[] TO = { R.id.id_frase, R.id.frase_text,
			R.id.autor_text, };

	private void showFrases(Cursor cursor) {
		adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor,
				FROM, TO);
		setListAdapter(adapter);
	}

	private boolean deleteFrase(String idFrase) {
		SQLiteDatabase db = frases.getWritableDatabase();
		return db.delete(TABLE_NAME, _ID + "=" + idFrase, null) > 0;
	}

	private String fraseText, autorText;

	public void setFraseAndAutor(String fraseText, String autorText) {
		this.fraseText = fraseText;
		this.autorText = autorText;
	}

	public boolean IsFraseAutorNull() {
		return fraseText == null || autorText == null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			Log.d(TAG, "Recive an afirmative answer");
			cursorFrase = getFrases();
			adapter.notifyDataSetChanged();
			// Toast.makeText(this, "Editado con exito", Toast.LENGTH_SHORT)
			// .show();
			break;

		case RESULT_CANCELED:
			// I don't have idea
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		default:
			return false;
		}
	}
	
	public void onActionBarButtonClick(View v) {
		switch (v.getId()) {
		case R.id.title_logo: {
			Intent i = new Intent(this, Frase.class);
			startActivity(i);
		}
		case R.id.btn_title_search: {
			onSearchRequested();
			break;
		}
		}
	}
}