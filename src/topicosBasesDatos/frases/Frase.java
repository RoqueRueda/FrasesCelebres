package topicosBasesDatos.frases;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class Frase extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Set up click listeners for all the buttons
		View fraseDiaButton = findViewById(R.id.frase_dia_button);
		fraseDiaButton.setOnClickListener(this);

		View agregarFrase = findViewById(R.id.agregar_button);
		agregarFrase.setOnClickListener(this);

		View salir = findViewById(R.id.exit_button);
		salir.setOnClickListener(this);

		View verTodo = findViewById(R.id.ver_todo_button);
		verTodo.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.frase_dia_button:
			Intent i = new Intent(this, FraseDiaria.class);
			startActivity(i);
			break;

		case R.id.agregar_button:
			Intent agregar = new Intent(this, FraseDetails.class);
			startActivity(agregar);
			break;

		case R.id.exit_button:
			finish();
			break;

		case R.id.ver_todo_button:
			finish();
			break;

		// More buttons go here (if any) ...
		}
	}

	public void onActionBarButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_search: {
			onSearchRequested();
			break;
		}
		}
	}
}