package projet.java.err.plusDePlace;

public class PlusDePlaceCollectionJeuxException extends PlusDePlaceException {
	private static final long serialVersionUID = -4779017665211743259L;

	public PlusDePlaceCollectionJeuxException(int max) {
		super(max);
	}

	@Override
	public String getMessage() {
		return "Plus de place dans la collection de jeux... " + super.getMessage(); 
	}
}
