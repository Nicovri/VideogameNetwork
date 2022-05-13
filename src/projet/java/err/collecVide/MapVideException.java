package projet.java.err.collecVide;

import java.util.Map;

public class MapVideException extends CollectionVideException{
	private static final long serialVersionUID = 5055140341373120694L;

	public MapVideException() {}
	
	public MapVideException(String description) { super(description); }
	
	@Override
	public String getMessage() {
		return "La collection de type " + Map.class + "que vous avez utilisée est vide...";
	}
}
