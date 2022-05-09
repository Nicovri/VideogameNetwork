package projet.java.err.plusDePlace;

public class PlusDePlaceNombreDePartiesException extends PlusDePlaceException {

	public PlusDePlaceNombreDePartiesException(int max) {
		super(max);
	}

	@Override
	public String getMessage() {
		return "Plus de parties disponibles... " + super.getMessage(); 
	}
}
