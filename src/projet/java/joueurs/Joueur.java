package projet.java.joueurs;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import projet.java.err.plusDePlace.PlusDePlaceCollectionJeuxException;
import projet.java.err.plusDePlace.PlusDePlaceListeAmisException;
import projet.java.jeux.Jeu;

/**
 * Classe de base d'un joueur.<br/>
 * Se décline en plusieurs catégories plus précises qui permettent de différencier le statut des joueurs (c'est pour cette raison que la classe est {@code abstract}, on ne veut pas pouvoir l'instancier)
 * 
 * @author Nicolas Vrignaud
 * 
 * @see projet.java.joueurs.Humain
 * @see projet.java.joueurs.Bot
 */
public abstract class Joueur {
	private String pseudo;
	private String email;
	private Date dateDeNaissance;
	
	/**
	 * Format de la date de naissance du joueur en français. Nom du jour et du mois écrit en entier.
	 */
	protected final static DateFormat DATE_NAISSANCE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRENCH);
	
	protected Set<Joueur> amis = new HashSet<>();
	protected Set<Jeu> jeux = new HashSet<>();
	
	public Joueur(String pseudo, String email, Date dateNaissance) {
		this.pseudo = pseudo;
		this.email = email;
		this.dateDeNaissance = dateNaissance;
	}
	
	public String getPseudo() { return this.pseudo; };
	protected String getEmail() { return this.email; }
	protected Date getDateNaissance() { return (Date) this.dateDeNaissance.clone(); }
	
	public Set<Jeu> getJeux() { return this.jeux; }
	
	/**
	 * @return un set composé des pseudos de ses amis (peut être {@code null}).
	 */
	public Set<String> getAmis() {
		Set<String> pseudos = new HashSet<>();
		for(Joueur j : this.amis) {
			pseudos.add(j.getPseudo());
		}
		return pseudos;
	}
	
	public String profilPublic() { return this.pseudo; }
	
	/**
	 * Essaie d'ajouter un ami selon les conditions demandées.<br/>
	 * (des conditions plus précises, notamment de place sont traités dans les classes filles)
	 * 
	 * @param j : {@code Joueur} à ajouter en ami
	 * 
	 * @return l'ajout a-t-il été effectué correctement ?
	 * 
	 * @throws PlusDePlaceListeAmisException
	 */
	public boolean ajouterAmi(Joueur j) throws PlusDePlaceListeAmisException {
		if(j != null) {			
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
				if(!((Enfant) j).getPseudosParents()[0].contains(this.getPseudo()) && !((Enfant) j).getPseudosParents()[1].contains(this.getPseudo())) {					
					this.amis.remove(j);
					System.out.println("Vous ne pouvez pas inviter un enfant si ce n'est pas le vôtre.");
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Essaie de supprimer un ami selon les conditions demandées.<br/>
	 * (des conditions plus précises, notamment de place sont traités dans les classes filles)
	 * 
	 * @param j : {@code Joueur} à supprimer de sa liste d'amis
	 * 
	 * @return la suppression a-t-elle été effectuée correctement ?
	 */
	public boolean supprimerAmi(Joueur j) {
		if(j != null) {			
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
		return false;
	}
	
	/**
	 * Essaie d'ajouter un {@code Jeu} selon les conditions demandées.<br/>
	 * (des conditions plus précises, notamment de place sont traités dans les classes filles)
	 * 
	 * @param j : le {@code Jeu} à ajouter à sa collection
	 * 
	 * @return l'ajout a-t-il été effectué correctement ?
	 * 
	 * @throws PlusDePlaceCollectionJeuxException
	 */
	public boolean ajouterJeu(Jeu j) throws PlusDePlaceCollectionJeuxException {
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
	
	/**
	 * Essaie de supprimer un {@code Jeu} selon les conditions demandées.<br/>
	 * (des conditions plus précises, notamment de place sont traités dans les classes filles)
	 * 
	 * @param j : le {@code Jeu} à supprimer de sa collection de jeux
	 * 
	 * @return la suppression a-t-elle été effectuée correctement ?
	 */
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
}
