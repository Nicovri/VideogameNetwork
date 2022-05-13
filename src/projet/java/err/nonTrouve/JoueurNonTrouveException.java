package projet.java.err.nonTrouve;

import projet.java.joueurs.Joueur;

public class JoueurNonTrouveException extends ObjetNonTrouveException {
	private static final long serialVersionUID = -6031627440743530981L;

	public JoueurNonTrouveException() {}
	
	public JoueurNonTrouveException(String description) { super(description); }
	
	@Override
	public Class<?> getClasseObjet() { return Joueur.class; }
	
	@Override
	public String getMessage() {
		return "L'objet de type" + this.getClasseObjet() + " que vous essayez d'utiliser n'a pas été trouvé...";
	}
}
