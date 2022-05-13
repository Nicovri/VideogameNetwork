package projet.java.err.nonTrouve;

import projet.java.jeux.PartieMultijoueurs;

public class PartieNonTrouveeException extends ObjetNonTrouveException {
	private static final long serialVersionUID = 7866682155272540941L;

	public PartieNonTrouveeException() {}
	
	public PartieNonTrouveeException(String description) { super(description); }
	
	@Override
	public Class<?> getClasseObjet() { return PartieMultijoueurs.class; }
	
	@Override
	public String getMessage() {
		return "L'objet de type" + this.getClasseObjet() + " que vous essayez d'utiliser n'a pas été trouvé...";
	}
}
