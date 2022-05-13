package projet.java.err.nonTrouve;

import projet.java.jeux.Jeu;

public class JeuNonTrouveException extends ObjetNonTrouveException {
	private static final long serialVersionUID = 4887492982544840614L;

	public JeuNonTrouveException() {}
	
	public JeuNonTrouveException(String description) { super(description); }
	
	@Override
	public Class<?> getClasseObjet() { return Jeu.class; }
	
	@Override
	public String getMessage() {
		return "L'objet de type" + this.getClasseObjet() + " que vous essayez d'utiliser n'a pas été trouvé...";
	}
}
