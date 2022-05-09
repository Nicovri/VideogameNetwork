package projet.java.jeux;

import projet.java.joueurs.Joueur;
import projet.java.err.joueur2.Joueur2NonBotException;
import projet.java.err.joueur2.Joueur2NonHumainException;
import projet.java.err.nonTrouve.JoueurNonTrouveException;
import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.joueurs.Bot;
import projet.java.joueurs.Humain;

public class PartieMultijoueurs {
	private Jeu jeu;
	private Joueur[] joueurs = new Joueur[2];
	private String pseudoGagnant;
	private String pseudoPerdant;
	
	public PartieMultijoueurs(Jeu jeu, Joueur moi, Joueur ami) {
		this.jeu = jeu;
		this.joueurs[0] = moi;
		this.joueurs[1] = ami;
	}
	
	public String getPseudoGagnant() { return this.pseudoGagnant; }
	public String getPseudoPerdant() { return this.pseudoPerdant; }
	
	// ExceptionJoueur2DoitEtreUnHumain
	public boolean partiePossibleHumain() throws Joueur2NonHumainException {
		if(!(joueurs[1] instanceof Humain)) {
			throw new Joueur2NonHumainException(joueurs[1]);
		}
		if(joueurs[1] == null) {
			return false;
		}
		boolean partiePossible = false;
		// Les 2 joueurs sont-ils amis réciproquement ?
		if(!joueurs[0].getAmis().contains(joueurs[1]) || !joueurs[1].getAmis().contains(joueurs[0])) {
			return false;
		}
		// Joueur1 a-t-il le jeu en question et son jeu est-il compatible avec les consoles qu'il possède ?
		for(Jeu j : this.joueurs[0].getJeux()) {
			if(jeu.getNom().equals(j.getNom())) {
				if(((Humain) this.joueurs[0]).getMachines().contains(j.getPlateforme())) {					
					partiePossible = true;
				}
			}
		}
		// Idem pour joueur2
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
	
	public boolean partiePossibleBot() throws Joueur2NonBotException {
		if(!(joueurs[1] instanceof Bot)) {
			throw new Joueur2NonBotException(joueurs[1]);
		}
		if(joueurs[1] == null) {
			return false;
		}
		boolean partiePossible = false;
		// Même test sur joueur1
		for(Jeu j : this.joueurs[0].getJeux()) {
			if(jeu.getNom().equals(j.getNom())) {
				if(((Humain) this.joueurs[0]).getMachines().contains(j.getPlateforme())) {					
					partiePossible = true;
				}
			}
		}
		// Le bot a-t-il le module d'IA
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
	
	// Si choix de jeu possible, paramètres à ajouter à cette fonction
	private void jouer() {
		if(this.pseudoGagnant.isEmpty() && this.pseudoPerdant.isEmpty()) {			
			double res1 = Math.random() * 100;
			double res2 = Math.random() * 100;
			if(res1 > res2) {
				this.pseudoGagnant = joueurs[0].getPseudo();
				this.pseudoPerdant = joueurs[1].getPseudo();
			} else {
				this.pseudoGagnant = joueurs[1].getPseudo();
				this.pseudoPerdant = joueurs[0].getPseudo();
			}
		} else {
			System.out.println("Une partie a déjà été jouée avec cette instance... Modification impossible.");
		}
	}
	
	public boolean resultatsDePartie() throws PlusDePlaceNombreDePartiesException {
		boolean partiePossible;
		try {
			partiePossible = joueurs[1] instanceof Bot ? this.partiePossibleBot() : this.partiePossibleHumain();
		} catch (Exception e) {
			return false;
		}
		if(partiePossible) {
			this.jouer();
			boolean partieAjoutee = ((Humain)this.joueurs[0]).ajouterPartie(this).getSecond().getSecond();
			if(partieAjoutee) {
				if(this.joueurs[1] instanceof Humain) {					
					partieAjoutee = ((Humain)this.joueurs[1]).ajouterPartie(this).getSecond().getSecond();
					if(partieAjoutee) {
						// Parties ajoutées avec succès
						return true;
					}
				}
				if(this.joueurs[1] instanceof Bot) {
					return true;
				}
			}
		}
		System.out.println("Partie impossible...");
		return false;
	}
}
