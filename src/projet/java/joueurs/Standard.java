package projet.java.joueurs;

import java.util.Date;

import projet.java.err.plusDePlace.PlusDePlaceCollectionJeuxException;
import projet.java.err.plusDePlace.PlusDePlaceListeAmisException;
import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.Jeu;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.utils.Pair;

public class Standard extends Gold {
	private final int AMIS_MAX = 100;
	private final int JEUX_MAX = 50;
	private final int PARTIES_MAX = 5;

	public Standard(String pseudo, String email, Date dateNaissance, String console) {
		super(pseudo, email, dateNaissance, console);
	}
	
	public int getAmisMax() { return this.AMIS_MAX; }
	
	@Override
	public boolean ajouterAmi(Joueur j) throws PlusDePlaceListeAmisException {
		boolean amiAjoute = super.ajouterAmi(j);
		if(amiAjoute) {
			if(this.amis.size() > this.AMIS_MAX) {
				this.amis.remove(j);
				throw new PlusDePlaceListeAmisException(this.AMIS_MAX);
			}
		}
		return amiAjoute;
	}
	
	@Override
	public boolean ajouterJeu(Jeu j) throws PlusDePlaceCollectionJeuxException {
		boolean jeuAjoute = super.ajouterJeu(j);
		if(jeuAjoute) {
			if(this.jeux.size() > this.JEUX_MAX) {
				this.jeux.remove(j);
				throw new PlusDePlaceCollectionJeuxException(this.JEUX_MAX);
			}
		}
		return jeuAjoute;
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
