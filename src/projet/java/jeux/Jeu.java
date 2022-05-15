package projet.java.jeux;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import projet.java.err.nonTrouve.JeuNonTrouveException;

/**
 * Classe modèle d'un {@code Jeu}.<br/>
 * Ses données membres permettent d'identifier quelles informations peuvent être parsées depuis le fichier d'entrée.
 * 
 * @author Nicolas Vrignaud
 *
 */
public class Jeu {
	private int rang;
	private String nom;
	private String plateforme;
	private Year annee;
	private String genre;
	private String editeur;
	private float ventesAN;
	private float ventesEU;
	private float ventesJP;
	private float autresVentes;
	private float ventesTotales;
	
	public final static String DATE_IA = "1993";
	
	public Jeu(int rang, String nom, String plateforme, Year annee, String genre, String editeur,
			float ventesAN, float ventesEU, float ventesJP, float autresVentes, float ventesTotales) {
		this.rang = rang;
		this.nom = nom;
		this.plateforme = plateforme;
		this.annee = annee;
		this.genre = genre;
		this.editeur = editeur;
		this.ventesAN = ventesAN;
		this.ventesEU = ventesEU;
		this.ventesJP = ventesJP;
		this.autresVentes = autresVentes;
		this.ventesTotales = ventesTotales;
	}
	
	public int getRang() { return this.rang; }
	public String getNom() { return this.nom; }
	public String getPlateforme() { return this.plateforme; }
	public String getGenre() { return this.genre; }
	public String getAnnee() { return this.annee.toString(); }
	
	/**
	 * Affichage des informations précises sur un {@code Jeu}.<br/>
	 * (Utile lorsque les détails d'un jeu doivent être parcourus, lors de l'achat par exemple)
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.nom + "\n");
		b.append("\t- ID : " + this.rang + "\n");
		b.append("\t- Plateforme : " + this.plateforme + "\n");
		b.append("\t- Editeur : " + this.editeur + "\n");
		b.append("\t- Année de sortie : " + this.annee + "\n");
		b.append("\t- Genre : " + this.genre + "\n");
		b.append("\t- Ventes (en millions) : ");
		b.append(this.ventesAN + "(Amérique du Nord), " + this.ventesEU + "(Europe), " +
				this.ventesJP + "(Japon), " + this.autresVentes + "(Autres)\n");
		b.append("\t  -> Ventes totales : " + this.ventesTotales);
		return b.toString();
	}
	
	/**
	 * Affichage rapide d'un jeu, càd le rang, suivi de son som.
	 * 
	 * @return chaine de caractères des informations simplifiées d'un jeu
	 */
	public String affichageRapide() {
		StringBuilder b = new StringBuilder();
		b.append(this.rang + ". " + this.nom);
		return b.toString();
	}
	
	/**
	 * Retourne la collection de jeux entrée en paramètres triée par machines de jeu (dans l'ordre alphabétique).<br/>
	 * Les jeux gardent leur rang lors de l'affichage, ce qui permet de choisir facilement le jeu par la suite.
	 * 
	 * @param jeux : une collection de jeux
	 * @param plateformes : le set des plateformes de jeu disponibles
	 * 
	 * @return la collection de jeux triée par machines
	 */
	public static Collection<Jeu> triParMachine(Collection<Jeu> jeux, SortedSet<String> plateformes) {
		List<Jeu> jeuxTries = new ArrayList<>();
		for(String machine : plateformes) {
			for(Jeu jeu : jeux) {
				if(jeu.plateforme.equals(machine)) {
					jeuxTries.add(jeu);
				}
			}
		}
		return jeuxTries;
	}
	
	/**
	 * Retourne la collection de jeux entrée en paramètres triée par genres (dans l'ordre alphabétique).<br/>
	 * Les jeux gardent leur rang lors de l'affichage, ce qui permet de choisir facilement le jeu par la suite.
	 * 
	 * @param jeux : une collection de jeux
	 * @param genres : le set des plateformes de jeu disponibles
	 * 
	 * @return la collection de jeux triée par genres
	 */
	public static Collection<Jeu> triParGenre(Collection<Jeu> jeux, SortedSet<String> genres) {
		List<Jeu> jeuxTries = new ArrayList<>();
		for(String genre : genres) {
			for(Jeu jeu : jeux) {
				if(jeu.genre.equals(genre)) {
					jeuxTries.add(jeu);
				}
			}
		}
		return jeuxTries;
	}
	
	/**
	 * Recherche un jeu dans une collection en fonction de son titre.<br/>
	 * Si son titre contient le mot clé demandé, il est ajouté à la liste des jeux correspondants à la recherche.
	 * 
	 * @param jeux : une collection de jeux
	 * @param recherche : le mot clé entré lors d'une recherche de jeux
	 * 
	 * @return liste du ou des jeux dont le titre contient le mot clé passé en paramètres.
	 */
	public static Collection<Jeu> rechercheMotCleTitre(Collection<Jeu> jeux, String recherche) {
		List<Jeu> jeuxRecherche = new ArrayList<>();
		for(Jeu j : jeux) {
			if(j.getNom().contains(recherche)) {
				jeuxRecherche.add(j);
			}
		}
		return jeuxRecherche;
	}
	
	/**
	 * Retourne le jeu ayant le rang demandé parmi une collection d'objets {@code Jeu}.
	 * 
	 * @param jeux : collection de jeux
	 * @param rang : le rang du jeu à retourner
	 * 
	 * @return le jeu ayant le rang souhaité en donnée membre, s'il existe, sinon
	 * @throws JeuNonTrouveException
	 */
	public static Jeu trouverJeuSelonRang(Collection<Jeu> jeux, int rang) throws JeuNonTrouveException {
		for(Jeu jeu : jeux) {
			if(jeu.getRang() == rang) {
				return jeu;
			}
		}
		throw new JeuNonTrouveException();
	}
}
