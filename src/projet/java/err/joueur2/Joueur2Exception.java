package projet.java.err.joueur2;

import projet.java.joueurs.Joueur;

/**
 * Exception à propager lorsque le joueur 2 d'une partie multijoueur n'est pas du type attendu.
 * 
 * @author Nicolas Vrignaud
 * 
 * @see projet.java.err.joueur2.Joueur2NonBotException
 * @see projet.java.err.joueur2.Joueur2NonHumainException
 */
public class Joueur2Exception extends Exception {
	private static final long serialVersionUID = -3862655330992711280L;
	
	private Joueur joueur2;

	public Joueur2Exception(Joueur joueur2) {
		this.joueur2 = joueur2;
	}
	
	public Joueur2Exception(Joueur joueur2, String description) {
		super(description);
		this.joueur2 = joueur2;
	}
	
	public Class<?> getTypeJoueur() { return this.joueur2.getClass(); }
	
	@Override
	public String getMessage() {
		return "Le joueur 2 est un " + this.getTypeJoueur() + "... Vous ne pouvez pas jouer avec lui dans ce cas précis.";
	}
}
