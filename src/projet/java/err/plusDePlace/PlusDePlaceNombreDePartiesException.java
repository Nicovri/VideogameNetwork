package projet.java.err.plusDePlace;

public class PlusDePlaceNombreDePartiesException extends PlusDePlaceException {
	private static final long serialVersionUID = 7823850264331749833L;

	public PlusDePlaceNombreDePartiesException(int max) {
		super(max);
	}

	@Override
	public String getMessage() {
		return "Plus de parties disponibles... " + super.getMessage(); 
	}
}
