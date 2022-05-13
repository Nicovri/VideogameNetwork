package projet.java.err.collecVide;

import java.util.List;

public class ListeVideException extends CollectionVideException {
	private static final long serialVersionUID = 7151243943368614062L;

	public ListeVideException() {}
	
	public ListeVideException(String description) { super(description); }
	
	@Override
	public String getMessage() {
		return "La collection de type " + List.class + "que vous avez utilisée est vide...";
	}
}
