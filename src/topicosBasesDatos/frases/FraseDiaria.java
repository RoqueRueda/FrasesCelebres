package topicosBasesDatos.frases;

import static topicosBasesDatos.frases.Constantes.TABLE_NAME;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class FraseDiaria extends Activity{
	// The widget were the frase is showed
	private TextView fraseDiariaText;
	// The manager of the database
	private FraseData dbHelperFrases;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frase_dia);
		// Set the widgets
		fraseDiariaText = (TextView) findViewById(R.id.fraseDiaria_text);
		// Get the frase from the data base
		Cursor fraseAleatoria = getRandomFrase();
		// Show the frase
		if(fraseAleatoria.moveToFirst())
		{
			// Display the frase with some format
			setFraseDiaText(fraseAleatoria.getString(2)+" dice: " + "\n" +
					"\""+fraseAleatoria.getString(1)+"\"!!!");
		}
		else
		{
			// If no frase was found this text is displayed
			setFraseDiaText("Ninguna frase");
		}
	}
	
	/** Set the text to show in the widget */
	public void setFraseDiaText(String text)
	{
		fraseDiariaText.setText(text);
	}
	
	private Cursor getRandomFrase()
	{
		dbHelperFrases = new FraseData(this);
		SQLiteDatabase db = dbHelperFrases.getReadableDatabase();
		return db.rawQuery("SELECT * FROM " +
				TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;", null);
	}

}
