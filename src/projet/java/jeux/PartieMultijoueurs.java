package projet.java.jeux;

import projet.java.joueurs.Joueur;
import projet.java.joueurs.Humain;

public class PartieMultijoueurs {
	private Jeu jeu;
	private Joueur[] joueurs = new Joueur[2];
	
	public PartieMultijoueurs(Jeu jeu, Joueur moi, Joueur ami) {
		this.jeu = jeu;
		this.joueurs[0] = moi;
		this.joueurs[1] = ami;
	}
	
	// 0 si partie impossible
	// 1 si partie possible
	// 2 si partie possible sous condition d'un bot disponible (à gérer ailleurs)
	public int jouer() {
		boolean partiePossible = false;
		for(Jeu j : this.joueurs[1].getJeux()) {
			if(jeu.getNom() == j.getNom()) {
				if(((Humain) this.joueurs[1]).getMachines().contains(j.getPlateforme())) {					
					partiePossible = true;
				}
			}
		}
		for(Jeu j : this.joueurs[0].getJeux()) {
			if(jeu.getNom() == j.getNom()) {
				if(((Humain) this.joueurs[0]).getMachines().contains(j.getPlateforme())) {
					partiePossible = true;
				} else {					
					partiePossible = false;
				}
			}
		}
		
		if(partiePossible) {
			return 1;
		}
		if(!this.jeu.getAnnee().equals(Jeu.DATE_IA)) {
			return 0;
		}
		return 2;
		// Un bot peut jouer plusieurs parties en même temps
	}
}
