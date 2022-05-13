package projet.java.err.collecVide;

import java.util.Set;

public class SetVideException extends CollectionVideException {
	private static final long serialVersionUID = -5563175744362501744L;

	public SetVideException() {}
	
	public SetVideException(String description) { super(description); }
	
	@Override
	public String getMessage() {
		return "La collection de type " + Set.class + "que vous avez utilisée est vide...";
	}
}
