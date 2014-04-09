package topicosBasesDatos.frases;

public interface State {
	boolean guardar(String frase, String autor, String id);
	void cancelar();
}
