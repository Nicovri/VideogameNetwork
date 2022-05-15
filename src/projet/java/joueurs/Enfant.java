package projet.java.joueurs;

import java.util.Date;

import projet.java.err.plusDePlace.PlusDePlaceCollectionJeuxException;
import projet.java.err.plusDePlace.PlusDePlaceListeAmisException;
import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.Jeu;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.utils.Pair;

/**
 * Classe représentant un joueur enfant.
 * 
 * @author Nicolas Vrignaud
 * 
 * @see projet.java.joueurs.Humain
 */
public class Enfant extends Humain {
	private String[] parents = new String[]{"", ""};
	private String futurStatut;
	private final int AMIS_MAX = 10;
	private final int JEUX_MAX = 30;
	private final int PARTIES_MAX = 3;
	
	private Enfant(String pseudo, String email, Date dateNaissance, String console) {
		super(pseudo, email, dateNaissance, console);
	}
	
	public Enfant(String pseudo, String email, Date dateNaissance, String console, String futurStatut) {
		this(pseudo, email, dateNaissance, console);
		futurStatut = this.futurStatut;
	}
	
	public String[] getPseudosParents() { return parents; }
	
	public void setPseudoParent1(String pseudo) { this.parents[0] = pseudo; }
	public void setPseudoParent2(String pseudo) { this.parents[1] = pseudo; }
	
	public int getAmisMax() { return this.AMIS_MAX; }
	
	@Override
	public String profilPublic() {
		StringBuilder b = new StringBuilder();
		b.append(super.profilPublic());
		b.append(" (nombre de parties jouées : " + this.parties.size() + ")");
		return b.toString();
	}
	
	/**
	 * Inscription de l'enfant par son parent : ajoute le pseudo du parent dans le tableau des parents et ajout respectif dans la liste d'amis.
	 * 
	 * @param parent1Ou2 : est-ce le parent 1 ou 2 (si autre chose, la méthode ajouterAmi rendra une erreur)
	 * @param parent : joueur parent/tuteur
	 * 
	 * @throws PlusDePlaceListeAmisException
	 */
	public void inscrire(int parent1Ou2, Gold parent) throws PlusDePlaceListeAmisException {
		if(parent1Ou2 == 1) {
			this.setPseudoParent1(parent.getPseudo());
		} else if(parent1Ou2 == 2) {
			this.setPseudoParent2(parent.getPseudo());
		}
		this.ajouterAmi(parent);
		parent.ajouterAmi(this);
	}
	
	@Override
	public boolean ajouterAmi(Joueur j) throws PlusDePlaceListeAmisException {
		boolean amiAjoute = super.ajouterAmi(j);
		if(amiAjoute) {
			if(this.amis.size() > this.AMIS_MAX) {
				this.amis.remove(j);
				throw new PlusDePlaceListeAmisException(this.AMIS_MAX);
			}
			if(!(j instanceof Enfant) && !(this.parents[0].contains(j.getPseudo()) || this.parents[1].contains(j.getPseudo()))) {
				this.amis.remove(j);
				System.out.println("Vous ne pouvez pas inviter cet ami adulte si ce n'est pas l'un de vos parents...");
				return false;
			}
		}
		return amiAjoute;
	}
	
	@Override
	public boolean supprimerAmi(Joueur j) {
		boolean amiSupprime = super.supprimerAmi(j);
		if(amiSupprime) {
			if(!(j instanceof Enfant) && (this.parents[0].contains(j.getPseudo()) || this.parents[1].contains(j.getPseudo()))) {
				this.amis.add(j);
				System.out.println("Vous ne pouvez pas supprimer votre parent de la liste d'amis.");
				return false;
			}
		}
		return amiSupprime;
	}
	
	@Override
	public boolean ajouterJeu(Jeu j) throws PlusDePlaceCollectionJeuxException {
		boolean jeuAjoute = super.ajouterJeu(j);
		if(jeuAjoute) {
			if(this.jeux.size() > this.JEUX_MAX) {
				this.jeux.remove(j);
				throw new PlusDePlaceCollectionJeuxException(this.JEUX_MAX);
			}
		}
		return jeuAjoute;
	}
	
	@Override
	public Pair<String, Pair<Integer, Boolean>> ajouterPartie(PartieMultijoueurs pm) throws PlusDePlaceNombreDePartiesException {
		Pair<String, Pair<Integer, Boolean>> res = super.ajouterPartie(pm);
		if(res.getSecond().getSecond()) {
			if(res.getSecond().getFirst() > this.PARTIES_MAX) {
				this.parties.remove(res.getFirst());
				throw new PlusDePlaceNombreDePartiesException(this.PARTIES_MAX);
			}
		}
		return res;
	}
	
	/**
	 * Vérification de l'âge de l'enfant.
	 * 
	 * @return l'enfant a-t-il plus de 18 ans au moment de l'appel de cette fonction ?
	 */
	@SuppressWarnings("deprecation")
	public boolean aPlusDe18Ans() {
		Date now = new Date();
		Date adulte = this.getDateNaissance();
		adulte.setYear(this.getDateNaissance().getYear() + 18);
		// Peut-être vrai du moment qu'on est l'année de ses 18 ans
		if(now.getYear() - this.getDateNaissance().getYear() >= 18) {
			// Donc on vérifie si la date de son anniversaire de 18 ans est bien avant la date de maintenant
			if(adulte.before(now)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Conversion d'un joueur {@code Enfant} en un joueur {@code Standard} ou {@code Gold}, selon la valeur de la donnée memebre {@code futurStatut}.<br/>
	 * Les données correspondantes sont conservées dans la limite du possible (ex : les amis encore enfants vont disparaitre car un adulte ne peut pas être ami avec des enfants si il n'est pas un de leurs parent)
	 * 
	 * @return le joueur adulte correspondant
	 */
	public Humain devenirAdulte() {
		if(this.futurStatut == null) futurStatut = "S";
		Humain adulte;
		if(this.futurStatut.equals("G")) {
			adulte = new Gold(this.getPseudo(), this.getEmail(), this.getDateNaissance(), this.getMachines().toArray()[0].toString());
		} else {
			adulte = new Standard(this.getPseudo(), this.getEmail(), this.getDateNaissance(), this.getMachines().toArray()[0].toString());
		}
		adulte.jeux = this.getJeux();
		adulte.machinesDeJeu = this.getMachines();
		
		
		for(Joueur j : this.amis) {
			j.supprimerAmi(this);
			
			// L'enfant n'avait que des amis enfants en dehors de ses parents, donc on ne garde en ami que ses parents
			if(j.getPseudo().equals(this.parents[0]) || j.getPseudo().equals(this.parents[1])) {				
				try {
					adulte.ajouterAmi(j);
					j.ajouterAmi(adulte);
				} catch (PlusDePlaceListeAmisException e) {
					adulte.supprimerAmi(j);
					j.supprimerAmi(adulte);
				}
			}		
		}
		
		adulte.parties = this.parties;
		
		return adulte;
	}
}
