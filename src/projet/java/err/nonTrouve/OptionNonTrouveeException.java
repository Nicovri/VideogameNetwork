package projet.java.err.nonTrouve;

import projet.java.utils.Options;

public class OptionNonTrouveeException extends ObjetNonTrouveException {

	public OptionNonTrouveeException() {}
	
	@Override
	public Class<?> getClasseObjet() { return Options.class; }
	
}
