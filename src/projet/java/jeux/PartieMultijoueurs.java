package projet.java.jeux;

import projet.java.joueurs.Joueur;
import projet.java.err.joueur2.Joueur2NonBotException;
import projet.java.err.joueur2.Joueur2NonHumainException;
import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.joueurs.Bot;
import projet.java.joueurs.Humain;

/**
 * Classe modèle d'une partie multijoueurs entre 2 joueurs.<br/>
 * Contient aussi le jeu auquel ils vont jouer, ainsi que les pseudos du gagnant et du perdant.<br/>
 * joueurs[0] doit être le joueur qui demande à jouer, et joueurs[1] doit être son ami ou un bot.
 * 
 * @author Nicolas Vrignaud
 */
public class PartieMultijoueurs {
	private Jeu jeu;
	private Joueur[] joueurs = new Joueur[2];
	private String pseudoGagnant = "";
	private String pseudoPerdant = "";
	
	public PartieMultijoueurs(Jeu jeu, Joueur moi, Joueur ami) {
		this.jeu = jeu;
		this.joueurs[0] = moi;
		this.joueurs[1] = ami;
	}
	
	public Jeu getJeu() { return this.jeu; }
	public String getPseudoGagnant() { return this.pseudoGagnant; }
	public String getPseudoPerdant() { return this.pseudoPerdant; }
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Partie Multijoueurs sur le jeu \"" + jeu.getNom() + "\":\n");
		b.append("    - Joueur gagnant : " + this.pseudoGagnant);
		b.append("\n    - Joueur perdant : " + this.pseudoPerdant);
		return b.toString();
	}
	
	/**
	 * On vérifie si la partie est possible entre les 2 joueurs {@code Humain} :<br/>
	 * - Sont-ils amis réciproquement ?<br/>
	 * - Joueur1 a-t-il le jeu en question et la console qui lui permet-il de jouer à son propre jeu ?<br/>
	 * - Joueur2 a-t-il le jeu, indépendamment de la console de Joueur1, et sa console lui permet-il de jouer à son propre jeu ?<br/>
	 * 
	 * @return la partie entre 2 joueurs {@code Humain} est-elle possible ?
	 * 
	 * @throws Joueur2NonHumainException
	 */
	public boolean partiePossibleHumain() throws Joueur2NonHumainException {
		if(!(joueurs[1] instanceof Humain)) {
			throw new Joueur2NonHumainException(joueurs[1]);
		}
		if(joueurs[1] == null) {
			return false;
		}
		boolean partiePossible = false;
		// Les 2 joueurs sont-ils amis réciproquement ?
		if(!joueurs[0].getAmis().contains(joueurs[1].getPseudo()) || !joueurs[1].getAmis().contains(joueurs[0].getPseudo())) {
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
	
	/**
	 * On vérifie si la partie est possible entre les 2 joueurs, 1 {@code Humain} et 1 {@code Bot} :<br/>
	 * - Joueur1 a-t-il le jeu en question et la console qui lui permet-il de jouer à son propre jeu ?<br/>
	 * - Le jeu contient-il un module d'IA disponible ? (on regarde l'année à partir de laquelle les modules d'IA sont disponibles)
	 * 
	 * @return la partie entre 2 joueurs, 1 {@code Humain} et 1 {@code Bot} est-elle possible ?
	 * 
	 * @throws Joueur2NonBotException
	 */
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
					break;
				}
			}
		}
		if(Integer.parseInt(jeu.getAnnee()) < Integer.parseInt(Jeu.DATE_IA)) {
			partiePossible = false;
		}
		return partiePossible;
	}
	
	/**
	 * Jeu joué par les 2 joueurs pour déterminer qui est le gagant et qui es le perdant.<br/>
	 * <br/>
	 * Hypothèse : il n'y a jamais de match nul.
	 */
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
	
	/**
	 * Regarde si la partie est possible, les joueurs jouent, et les données de partie sont enregistrées dans leur liste respective.
	 * 
	 * @return la partie a-t-elle bien été jouée et ajoutée à la liste de partie des joueurs (sauf {@code Bot})
	 * 
	 * @throws PlusDePlaceNombreDePartiesException
	 */
	public boolean resultatsDePartie() throws PlusDePlaceNombreDePartiesException {
		boolean partiePossible;
		try {
			partiePossible = joueurs[1] instanceof Bot ? this.partiePossibleBot() : this.partiePossibleHumain();
		} catch (Exception e) {
			return false;
		}
		if(partiePossible) {
			if(this.joueurs[1] instanceof Bot) {
				((Bot)this.joueurs[1]).toggleJoue();
				((Bot)this.joueurs[1]).setJeuEnCours(this.jeu.getNom());
			}
			this.jouer();
			if(this.joueurs[1] instanceof Bot) {				
				((Bot)this.joueurs[1]).toggleJoue();
				((Bot)this.joueurs[1]).resetJeuEnCours();
			}
			boolean partieAjoutee = ((Humain)this.joueurs[0]).ajouterPartie(this).getSecond().getSecond();
			if(partieAjoutee) {
				if(this.joueurs[1] instanceof Humain) {					
					partieAjoutee = ((Humain)this.joueurs[1]).ajouterPartie(this).getSecond().getSecond();
					if(partieAjoutee) {
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
