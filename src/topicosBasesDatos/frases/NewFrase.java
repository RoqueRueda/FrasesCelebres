package topicosBasesDatos.frases;

import static topicosBasesDatos.frases.Constantes.AUTOR;
import static topicosBasesDatos.frases.Constantes.FRASE;
import static topicosBasesDatos.frases.Constantes.TABLE_NAME;
import android.app.Activity;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NewFrase implements State {

	private FraseData frases;
	private Activity parent;

	public NewFrase(Activity parent) {
		frases = new FraseData(parent);
		this.parent = parent;
	}

	public boolean guardar(String frase, String autor, String id) {
		try {
			SQLiteDatabase db = frases.getWritableDatabase();
			ContentValues nuevoRegistro = new ContentValues();
			nuevoRegistro.put(FRASE, frase);
			nuevoRegistro.put(AUTOR, autor);
			db.insertOrThrow(TABLE_NAME, null, nuevoRegistro);
			return true;
		} catch (SQLException e) {
			return false;
		} finally {
			frases.close();
		}
	}

	public void cancelar() {
		parent.finish();
	}

}
