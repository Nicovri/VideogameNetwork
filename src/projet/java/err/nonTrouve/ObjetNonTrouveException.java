package projet.java.err.nonTrouve;

public class ObjetNonTrouveException extends Exception {
	
	public ObjetNonTrouveException() {}
	
	public Class<?> getClasseObjet() { return Object.class; }
}
