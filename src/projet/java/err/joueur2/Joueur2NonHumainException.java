package projet.java.err.joueur2;

import projet.java.joueurs.Joueur;

public class Joueur2NonHumainException extends Joueur2Exception {
	private static final long serialVersionUID = -5883673408964729654L;

	public Joueur2NonHumainException(Joueur joueur2) {
		super(joueur2);
	}

	public Joueur2NonHumainException(Joueur joueur2, String description) {
		super(joueur2, description);
	}
}
