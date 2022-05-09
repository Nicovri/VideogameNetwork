package projet.java.err.joueur2;

import projet.java.joueurs.Joueur;

public class Joueur2Exception extends Exception {
	private Joueur joueur2;

	public Joueur2Exception(Joueur joueur2) {
		this.joueur2 = joueur2;
	}
	
	public Class<?> getTypeJoueur() { return this.joueur2.getClass(); }
	
	@Override
	public String getMessage() {
		return "Le joueur 2 est un " + this.getTypeJoueur() + "... Vous ne pouvez pas jouer avec lui dans ce cas précis.";
	}
}
