package projet.java.joueurs;

import java.util.Date;

import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.Jeu;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.utils.Pair;

public class Gold extends Humain {
	private final int PARTIES_MAX = 10;
	//private int argent;
	
	public Gold(String pseudo, String email, Date dateNaissance, String console) {
		super(pseudo, email, dateNaissance, console);
	}
	
	@Override
	public String profilPublic() {
		// Sans restrictions dans les joueurs standards et gold
		StringBuilder b = new StringBuilder();
		b.append(super.profilPublic());
		b.append("\n-  " + Joueur.DATE_NAISSANCE_FORMAT.format(this.getDateNaissance()) + "\n");
		for(String machine : this.getMachines()) {
			b.append(machine + " / ");
		}
		b.append("\nJeux:\n");
		for(Jeu jeu : this.getJeux()) {
			b.append("- " + jeu.getNom() + " : " + jeu.getPlateforme() + "\n");
		}
		return b.toString();
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
