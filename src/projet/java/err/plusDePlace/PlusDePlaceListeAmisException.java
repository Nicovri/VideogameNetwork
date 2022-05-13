package projet.java.err.plusDePlace;

public class PlusDePlaceListeAmisException extends PlusDePlaceException {
	private static final long serialVersionUID = 344297467512066333L;

	public PlusDePlaceListeAmisException(int max) {
		super(max);
	}
	
	@Override
	public String getMessage() {
		return "Plus de place dans la liste d'amis... " + super.getMessage(); 
	}
}
