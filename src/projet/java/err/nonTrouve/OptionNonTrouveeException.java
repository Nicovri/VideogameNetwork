package projet.java.err.nonTrouve;

import projet.java.utils.Options;

public class OptionNonTrouveeException extends ObjetNonTrouveException {
	private static final long serialVersionUID = -431579251011240499L;

	public OptionNonTrouveeException() {}
	
	public OptionNonTrouveeException(String description) { super(description); }
	
	@Override
	public Class<?> getClasseObjet() { return Options.class; }
	
	@Override
	public String getMessage() {
		return "L'objet de type" + this.getClasseObjet() + " que vous essayez d'utiliser n'a pas été trouvé...";
	}
}
