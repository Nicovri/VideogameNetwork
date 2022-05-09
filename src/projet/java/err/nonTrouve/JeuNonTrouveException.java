package projet.java.err.nonTrouve;

import projet.java.jeux.Jeu;
import projet.java.utils.Options;

public class JeuNonTrouveException extends ObjetNonTrouveException {
	
	public JeuNonTrouveException() {}
	
	@Override
	public Class<?> getClasseObjet() { return Jeu.class; }

}
