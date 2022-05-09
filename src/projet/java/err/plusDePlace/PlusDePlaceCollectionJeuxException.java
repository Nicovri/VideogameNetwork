package projet.java.err.plusDePlace;

public class PlusDePlaceCollectionJeuxException extends PlusDePlaceException {

	public PlusDePlaceCollectionJeuxException(int max) {
		super(max);
	}

	@Override
	public String getMessage() {
		return "Plus de place dans la collection de jeux... " + super.getMessage(); 
	}
}
