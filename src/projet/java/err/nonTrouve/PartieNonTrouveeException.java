package projet.java.err.nonTrouve;

import projet.java.jeux.PartieMultijoueurs;
import projet.java.utils.Options;

public class PartieNonTrouveeException extends ObjetNonTrouveException {
	
	public PartieNonTrouveeException() {}
	
	@Override
	public Class<?> getClasseObjet() { return PartieMultijoueurs.class; }
}
