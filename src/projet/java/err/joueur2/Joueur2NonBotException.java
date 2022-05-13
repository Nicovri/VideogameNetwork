package projet.java.err.joueur2;

import projet.java.joueurs.Joueur;

public class Joueur2NonBotException extends Joueur2Exception {
	private static final long serialVersionUID = -4128381302327697977L;

	public Joueur2NonBotException(Joueur joueur2) {
		super(joueur2);
	}
	
	public Joueur2NonBotException(Joueur joueur2, String description) {
		super(joueur2, description);
	}
}
