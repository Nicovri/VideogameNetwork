package projet.java.err.nonTrouve;

/**
 * Exception � propager lorsque l'objet que l'on essaie de trouver dans une fonction n'a pas �t� trouv�, ou encore que la fonction en question a renvoy� un objet {@code null}.
 * 
 * @author Nicolas Vrignaud
 *
 * @see projet.java.err.nonTrouve.JeuNonTrouveException
 * @see projet.java.err.nonTrouve.JoueurNonTrouveException
 * @see projet.java.err.nonTrouve.OptionNonTrouveeException
 * @see projet.java.err.nonTrouve.PartieNonTrouveeException
 */
public class ObjetNonTrouveException extends Exception {
	private static final long serialVersionUID = 8002827000769940414L;

	public ObjetNonTrouveException() {}
	
	public ObjetNonTrouveException(String description) { super(description); }
	
	public Class<?> getClasseObjet() { return Object.class; }
	
	@Override
	public String getMessage() {
		return "L'objet de type" + this.getClasseObjet() + " que vous essayez d'utiliser n'a pas �t� trouv�...";
	}
}
