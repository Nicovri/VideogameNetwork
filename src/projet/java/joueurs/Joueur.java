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
	
	// Garder une compatibilit� entre Adulte/Enfant, Standard/Gold pour pouvoir convertir
	
	// "Jeu poss�d�" par un bot si l'IA est possible pour ce jeu (� partir d'une ann�e donn�e)
	// Existe au plus 1 bot par jeu (ind�pendamment de la machine)
	// Cr�ation des bots que lorsque n�cessaire (pas d'ami dans sa liste qui peut jouer � ce jeu en question)
	
	public Joueur(String pseudo, String email, Date dateNaissance) {
		this.pseudo = pseudo; // ExceptionPseudoTropCourt
		this.email = email;
		this.dateDeNaissance = dateNaissance;
	}
	
	public String getPseudo() { return this.pseudo; };
	protected String getEmail() { return this.email; }
	protected Date getDateNaissance() { return this.dateDeNaissance; }
	
	public String profilPublic() {
		return this.pseudo;
	}
	
	public boolean ajouterAmi(Joueur j) /*throws ExceptionAmiNonTrouve*/ {
		int tailleAvant = this.amis.size();
		this.amis.add(j);
		int tailleApres = this.amis.size();
		if(tailleAvant == tailleApres) {
			System.out.println("Cet ami est d�j� dans votre liste.");
			return false;
		}
		if(j.getPseudo() == pseudo) {
			System.out.println("Vous ne pouvez pas vous inviter vous-m�me.");
			return false;
		}
		if(this instanceof Gold && j instanceof Enfant) {
			if(!((Enfant) j).getPseudosParents()[0].contains(this.getPseudo()) || !((Enfant) j).getPseudosParents()[1].contains(this.getPseudo())) {					
				this.amis.remove(j);
				System.out.println("Vous ne pouvez pas inviter un enfant si ce n'est pas le v�tre.");
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
				System.out.print("Vous ne pouvez pas retirer votre enfant de la liste d'amis.");
				return false;
			}
		}
		return true;
	}
	
	public void jouer() {
		// Proposer une partie multijoueurs � un ami
		
		// Un bot peut jouer plusieurs parties en m�me temps
		// jouer avec un membre de sa liste d'amis qui le peut (poss�de le m�me jeu)
		// Jeu compatible entre eux peu importe la machine
	}
	
	// Cahier des charges
	// Parcours de la liste des jeux par machine et cat�gorie (des jeux du joueur? pour jouer ou afficher les infos? tous les jeux pour en acheter un?)
	// Affichage des infos sur un jeu
	// 10, 11, 12, 13, 14 optionnels
	// Note 9 ?
	
	// Traitement des erreurs
	// JavaDoc (au niveau de la classe principale)
}