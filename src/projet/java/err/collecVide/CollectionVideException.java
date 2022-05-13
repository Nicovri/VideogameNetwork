package projet.java.err.collecVide;

import java.util.Collection;

/**
 * Exception à propager lorsqu'une collection qu'on essaie d'utiliser est vide.
 * 
 * @author Nicolas Vrignaud
 * 
 * @see projet.java.err.collecVide.ListeVideException
 * @see projet.java.err.collecVide.MapVideException
 * @see projet.java.err.collecVide.SetVideException
 */
public class CollectionVideException extends Exception {
	private static final long serialVersionUID = 7750470934856056422L;

	public CollectionVideException() {}
	
	public CollectionVideException(String description) { super(description); }
	
	@Override
	public String getMessage() {
		return "La collection de type " + Collection.class + "que vous avez utilisée est vide...";
	}
}
