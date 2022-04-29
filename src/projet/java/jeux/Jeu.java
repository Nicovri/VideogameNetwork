package projet.java.jeux;

import java.time.Year;

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
	
	private final int DATE_IA = 1993;
	
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
	
	public void setRang(int rang) { this.rang = rang; }
	public void setNom(String nom) { this.nom = nom; }
	public void setConsole(String plateforme) { this.plateforme = plateforme; }
	public void setAnnee(Year annee) { this.annee = annee; }
	public void setEditeur(String editeur) { this.editeur = editeur; }
	public void setVentesAN(float ventesAN) { this.ventesAN = ventesAN; }
	public void setVentesEU(float ventesEU) { this.ventesEU = ventesEU; }
	public void setVentesJP(float ventesJP) { this.ventesJP = ventesJP; }
	public void setAutresVentes(float autresVentes) { this.autresVentes = autresVentes; }
	public void setVentesTotales(float ventesTotales) { this.ventesTotales = ventesTotales; }
	
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
	
	public String affichageRapide() {
		StringBuilder b = new StringBuilder();
		b.append(this.rang + ". " + this.nom);
		return b.toString();
	}
}
