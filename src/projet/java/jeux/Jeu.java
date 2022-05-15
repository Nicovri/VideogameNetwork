package projet.java.jeux;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import projet.java.err.nonTrouve.JeuNonTrouveException;

/**
 * Classe mod�le d'un {@code Jeu}.<br/>
 * Ses donn�es membres permettent d'identifier quelles informations peuvent �tre pars�es depuis le fichier d'entr�e.
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
	 * Affichage des informations pr�cises sur un {@code Jeu}.<br/>
	 * (Utile lorsque les d�tails d'un jeu doivent �tre parcourus, lors de l'achat par exemple)
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.nom + "\n");
		b.append("\t- ID : " + this.rang + "\n");
		b.append("\t- Plateforme : " + this.plateforme + "\n");
		b.append("\t- Editeur : " + this.editeur + "\n");
		b.append("\t- Ann�e de sortie : " + this.annee + "\n");
		b.append("\t- Genre : " + this.genre + "\n");
		b.append("\t- Ventes (en millions) : ");
		b.append(this.ventesAN + "(Am�rique du Nord), " + this.ventesEU + "(Europe), " +
				this.ventesJP + "(Japon), " + this.autresVentes + "(Autres)\n");
		b.append("\t  -> Ventes totales : " + this.ventesTotales);
		return b.toString();
	}
	
	/**
	 * Affichage rapide d'un jeu, c�d le rang, suivi de son som.
	 * 
	 * @return chaine de caract�res des informations simplifi�es d'un jeu
	 */
	public String affichageRapide() {
		StringBuilder b = new StringBuilder();
		b.append(this.rang + ". " + this.nom);
		return b.toString();
	}
	
	/**
	 * Retourne la collection de jeux entr�e en param�tres tri�e par machines de jeu (dans l'ordre alphab�tique).<br/>
	 * Les jeux gardent leur rang lors de l'affichage, ce qui permet de choisir facilement le jeu par la suite.
	 * 
	 * @param jeux : une collection de jeux
	 * @param plateformes : le set des plateformes de jeu disponibles
	 * 
	 * @return la collection de jeux tri�e par machines
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
	 * Retourne la collection de jeux entr�e en param�tres tri�e par genres (dans l'ordre alphab�tique).<br/>
	 * Les jeux gardent leur rang lors de l'affichage, ce qui permet de choisir facilement le jeu par la suite.
	 * 
	 * @param jeux : une collection de jeux
	 * @param genres : le set des plateformes de jeu disponibles
	 * 
	 * @return la collection de jeux tri�e par genres
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
	 * Si son titre contient le mot cl� demand�, il est ajout� � la liste des jeux correspondants � la recherche.
	 * 
	 * @param jeux : une collection de jeux
	 * @param recherche : le mot cl� entr� lors d'une recherche de jeux
	 * 
	 * @return liste du ou des jeux dont le titre contient le mot cl� pass� en param�tres.
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
	 * Retourne le jeu ayant le rang demand� parmi une collection d'objets {@code Jeu}.
	 * 
	 * @param jeux : collection de jeux
	 * @param rang : le rang du jeu � retourner
	 * 
	 * @return le jeu ayant le rang souhait� en donn�e membre, s'il existe, sinon
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
