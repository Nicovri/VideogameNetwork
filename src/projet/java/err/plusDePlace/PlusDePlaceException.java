package projet.java.err.plusDePlace;

/**
 * Exception à propager lorsqu'une collection appartenant à un {@code Joueur} est remplie et ne peut plus recevoir de nouvel objet.
 * 
 * @author Nicolas Vrignaud
 * 
 * @see projet.java.err.plusDePlace.PlusDePlaceCollectionJeuxException
 * @see projet.java.err.plusDePlace.PlusDePlaceListeAmisException
 * @see projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException
 */
public class PlusDePlaceException extends Exception {
	private static final long serialVersionUID = -6976047798571157858L;
	
	private int tailleMax;
	
	public PlusDePlaceException(int max) {
		this.tailleMax = max;
	}
	
	public int getTailleMax() { return this.tailleMax; }
	
	@Override
	public String getMessage() {
		return "Taille max autorisée : " + this.getTailleMax();
	}
}
