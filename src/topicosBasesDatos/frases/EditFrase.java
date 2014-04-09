package topicosBasesDatos.frases;

import static android.provider.BaseColumns._ID;
import static topicosBasesDatos.frases.Constantes.AUTOR;
import static topicosBasesDatos.frases.Constantes.FRASE;
import static topicosBasesDatos.frases.Constantes.TABLE_NAME;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class EditFrase implements State {
	
	private FraseData frases;
	private Activity parent;

	public EditFrase(Activity parent){
		frases = new FraseData(parent);
		this.parent = parent;
	}	
	
	public boolean guardar(String frase, String autor, String id) {
		try {
			// Wrap the values passing as arguments
			ContentValues values = new ContentValues();
			values.put(FRASE, frase);
			values.put(AUTOR, autor);
			// Get the writable database
			SQLiteDatabase db = frases.getWritableDatabase();
			// use the method that update the row		
			return db.update(TABLE_NAME, values, _ID + "=" + id, null) > 0;
		} finally
		{
			frases.close();
		}
	}

	public void cancelar() {
		parent.finish();
	}
}
