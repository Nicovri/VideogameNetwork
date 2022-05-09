package projet.java.err.plusDePlace;

import java.util.Collection;

public class PlusDePlaceException extends Exception {
	private int tailleMax;
	
	public PlusDePlaceException(int max) {
		this.tailleMax = max;
	}
	
	public int getTailleMax() { return this.tailleMax; }
	
	@Override
	public String getMessage() {
		return "Taille max : " + this.getTailleMax();
	}
}
