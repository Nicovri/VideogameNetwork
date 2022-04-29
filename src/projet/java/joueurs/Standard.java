package projet.java.joueurs;

import java.util.Date;

public class Standard extends Gold {
	private final int AMIS_MAX = 100;
	private final int JEUX_MAX = 50;
	private final int PARTIES_MAX = 5;

	public Standard(String pseudo, String email, Date dateNaissance, String console) {
		super(pseudo, email, dateNaissance, console);
	}
	
	@Override
	public boolean ajouterAmi(Joueur j) {
		boolean amiAjoute = super.ajouterAmi(j);
		if(amiAjoute) {
			if(this.amis.size() > this.AMIS_MAX) {
				this.amis.remove(j);
				System.out.println("Plus de place dans la liste d'amis...");
				// ExceptionPlusDePlaceListeAmis
			}
		}
		return amiAjoute;
	}
}