package projet.java.jeux;

import projet.java.joueurs.Joueur;
import projet.java.joueurs.Bot;
import projet.java.joueurs.Humain;

public class PartieMultijoueurs {
	private Jeu jeu;
	private Joueur[] joueurs = new Joueur[2];
	
	public PartieMultijoueurs(Jeu jeu, Joueur moi, Joueur ami) {
		this.jeu = jeu;
		this.joueurs[0] = moi;
		this.joueurs[1] = ami;
	}
	
	// ExceptionJoueur2DoitEtreUnHumain
	public boolean partiePossibleHumain() {
		// ExceptionJoueur2PasUnAmi
		boolean partiePossible = false;
		for(Jeu j : this.joueurs[0].getJeux()) {
			if(jeu.getNom().equals(j.getNom())) {
				if(((Humain) this.joueurs[0]).getMachines().contains(j.getPlateforme())) {					
					partiePossible = true;
				}
			}
		}
		for(Jeu j : this.joueurs[1].getJeux()) {
			if(jeu.getNom().equals(j.getNom())) {
				if(((Humain) this.joueurs[1]).getMachines().contains(j.getPlateforme())) {
					partiePossible = true;
				} else {
					partiePossible = false;
				}
			}
		}
		
		return partiePossible;
	}
	
	// ExceptionJoueur2DoitEtreUnBot
	public boolean partiePossibleBot() {
		boolean partiePossible = false;
		for(Jeu j : this.joueurs[0].getJeux()) {
			if(jeu.getNom().equals(j.getNom())) {
				if(((Humain) this.joueurs[0]).getMachines().contains(j.getPlateforme())) {					
					partiePossible = true;
				}
			}
		}
		for(Jeu j : this.joueurs[1].getJeux()) {
			if(jeu.getNom().equals(j.getNom())) {
				partiePossible = true;
			}
		}
		if(Integer.parseInt(jeu.getAnnee()) < Integer.parseInt(Jeu.DATE_IA)) {
			partiePossible = false;
		}
		return partiePossible;
	}
	
	public void resultatsDePartie() {
		boolean partiePossible = joueurs[1] instanceof Bot ? this.partiePossibleBot() : this.partiePossibleHumain();
		if(partiePossible) {
			// Ajout des parties jouées aux joueurs
			// Victoire / défaite pour statistiques (random)
		}
	}
}
