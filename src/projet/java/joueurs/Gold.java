package projet.java.joueurs;

import java.util.Date;

public class Gold extends Humain {
	private final int PARTIES_MAX = 10;
	//private int coins;
	
	public Gold(String pseudo, String email, Date dateNaissance, String console) {
		super(pseudo, email, dateNaissance, console);
	}
	
	@Override
	public String profilPublic() {
		return super.profilPublic();
		// Sans restrictions dans les joueurs standards et gold
	}
	
	public void acheterJeu() {
		
	}
	
	public void offrirJeu() {
		// enfant : recevoir des jeux d'un autre seulement si c'est son parent/tuteur
	}
}
