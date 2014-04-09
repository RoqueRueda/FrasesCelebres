package topicosBasesDatos.frases;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class FraseDetails extends Activity implements OnClickListener {
	private State state;
	private String id;

	// The text views
	private EditText frase;
	private EditText autor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frase_details);
		Bundle extras = getIntent().getExtras();
		getWidgets();
		getState(extras, this);
		setListeners();
	}

	private void setListeners() {
		// Set listeners to buttons
		View okButton = findViewById(R.id.save_button);
		okButton.setOnClickListener(this);
		View cancelButton = findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
	}

	private void getWidgets() {
		// First we have to save the EditText
		frase = (EditText) this.findViewById(R.id.frase_input);
		autor = (EditText) this.findViewById(R.id.autor_input);
	}

	private void getState(Bundle extras, Activity parent) {
		if (extras != null) {
			state = new EditFrase(parent);
			String fraseText = extras.getString("fraseText");
			String autorText = extras.getString("autorText");
			id = extras.getString("idFraseUpdate");
			setTextToWidgets(fraseText, autorText);
		} else {
			state = new NewFrase(parent);
		}
	}

	private void setTextToWidgets(String fraseText, String autorText) {
		frase.setText(fraseText);
		autor.setText(autorText);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_button: {
			if (state.guardar(frase.getText().toString(), autor.getText()
					.toString(), id)) {
				Toast.makeText(this, "Guardado con exito", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else {
				Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		case R.id.cancel_button: {
			state.cancelar();
			break;
		}
		}
	}
	
	public void onActionBarButtonClick(View v) {
		switch (v.getId()) {
		case R.id.title_logo: {
			state.cancelar();
		}
		}
	}
}
