package projet.java.err.plusDePlace;

public class PlusDePlaceListeAmisException extends PlusDePlaceException {

	public PlusDePlaceListeAmisException(int max) {
		super(max);
	}
	
	@Override
	public String getMessage() {
		return "Plus de place dans la liste d'amis... " + super.getMessage(); 
	}
}
