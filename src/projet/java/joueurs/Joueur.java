package projet.java.joueurs;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import projet.java.jeux.Jeu;
import projet.java.jeux.PartieMultijoueurs;

public abstract class Joueur {
	private String pseudo;
	private String email;
	private Date dateDeNaissance;
	
	protected final static DateFormat DATE_NAISSANCE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRENCH);
	
	protected Set<Joueur> amis = new HashSet<>();
	protected Set<Jeu> jeux = new HashSet<>();
	
	// Garder une compatibilité entre Adulte/Enfant, Standard/Gold pour pouvoir convertir
	
	// "Jeu possédé" par un bot si l'IA est possible pour ce jeu (à partir d'une année donnée)
	// Existe au plus 1 bot par jeu (indépendamment de la machine)
	// Création des bots que lorsque nécessaire (pas d'ami dans sa liste qui peut jouer à ce jeu en question)
	
	public Joueur(String pseudo, String email, Date dateNaissance) {
		this.pseudo = pseudo; // ExceptionPseudoTropCourt
		this.email = email;
		this.dateDeNaissance = dateNaissance;
	}
	
	public String getPseudo() { return this.pseudo; };
	protected String getEmail() { return this.email; }
	protected Date getDateNaissance() { return this.dateDeNaissance; }
	
	public Set<Jeu> getJeux() { return this.jeux; }
	public Set<String> getAmis() {
		Set<String> pseudos = new HashSet<>();
		for(Joueur j : this.amis) {
			pseudos.add(j.getPseudo());
		}
		return pseudos;
	}
	
	public String profilPublic() {
		return this.pseudo;
	}
	
	public boolean ajouterAmi(Joueur j) /*throws ExceptionAmiNonTrouve*/ {
		int tailleAvant = this.amis.size();
		this.amis.add(j);
		int tailleApres = this.amis.size();
		if(tailleAvant == tailleApres) {
			System.out.println("Cet ami est déjà dans votre liste.");
			return false;
		}
		if(j.getPseudo() == pseudo) {
			System.out.println("Vous ne pouvez pas vous inviter vous-même.");
			return false;
		}
		if(this instanceof Gold && j instanceof Enfant) {
			if(!((Enfant) j).getPseudosParents()[0].contains(this.getPseudo()) || !((Enfant) j).getPseudosParents()[1].contains(this.getPseudo())) {					
				this.amis.remove(j);
				System.out.println("Vous ne pouvez pas inviter un enfant si ce n'est pas le vôtre.");
				return false;
			}
		}
		return true;
	}
	
	public boolean supprimerAmi(Joueur j) /*throws ExceptionAmiNonTrouve*/ {
		int tailleAvant = this.amis.size();
		this.amis.remove(j);
		int tailleApres = this.amis.size();
		if(tailleAvant == tailleApres) {
			System.out.println("Cet ami ne se trouvait pas dans votre liste.");
			return false;
		}
		if(this instanceof Gold && j instanceof Enfant) {
			if(((Enfant) j).getPseudosParents()[0].contains(this.getPseudo()) || ((Enfant) j).getPseudosParents()[1].contains(this.getPseudo())) {
				this.amis.add(j);
				System.out.println("Vous ne pouvez pas retirer votre enfant de la liste d'amis.");
				return false;
			}
		}
		return true;
	}
	
	// ExceptionJeuNonTrouve
	public boolean ajouterJeu(Jeu j) {
		if(j != null) {
			int tailleAvant = this.jeux.size();
			this.jeux.add(j);
			int tailleApres = this.jeux.size();
			if(tailleAvant == tailleApres) {
				System.out.println("Ce jeu est déjà dans votre liste.");
				return false;
			}
			return true;
		}
		return false;
	}
	
	// ExceptionJeuNonTrouve
	public boolean supprimerJeu(Jeu j) {
		if(j != null) {
			int tailleAvant = this.jeux.size();
			this.jeux.remove(j);
			int tailleApres = this.jeux.size();
			if(tailleAvant == tailleApres) {
				System.out.println("Ce jeu ne se trouvait pas dans votre liste.");
				return false;
			}
			return true;
		}
		return false;
	}
	
	// 10, 11, 12, 13, 14 optionnels
	// Note 9 ?
	
	// Traitement des erreurs
	// JavaDoc (au niveau de la classe principale)
}
