package projet.java.err.nonTrouve;

import projet.java.joueurs.Joueur;
import projet.java.utils.Options;

public class JoueurNonTrouveException extends ObjetNonTrouveException {

	public JoueurNonTrouveException() {}
	
	@Override
	public Class<?> getClasseObjet() { return Joueur.class; }
}
