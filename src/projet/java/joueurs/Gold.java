package projet.java.joueurs;

import java.util.Date;

import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.utils.Pair;

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
	
	@Override
	public Pair<String, Pair<Integer, Boolean>> ajouterPartie(PartieMultijoueurs pm) throws PlusDePlaceNombreDePartiesException {
		Pair<String, Pair<Integer, Boolean>> res = super.ajouterPartie(pm);
		if(res.getSecond().getSecond()) {
			if(res.getSecond().getFirst() > this.PARTIES_MAX) {
				this.parties.remove(res.getFirst());
				throw new PlusDePlaceNombreDePartiesException(this.PARTIES_MAX);
			}
		}
		return res;
	}
}
