package projet.java.joueurs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import projet.java.jeux.Jeu;

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
		futurStatut = this.futurStatut; // Verifier que c'est soit S soit G
	}
	
	public String[] getPseudosParents() { return parents; }
		
	public void setPseudoParent1(String pseudo) { this.parents[0] = pseudo; }
	public void setPseudoParent2(String pseudo) { this.parents[1] = pseudo; }
	
	@Override
	public String profilPublic() {
		StringBuilder b = new StringBuilder();
		b.append(super.profilPublic());
		b.append(" (nombre de parties jouées : " + this.parties.size() + ")");
		return b.toString();
	}
	
	@Override
	public boolean ajouterAmi(Joueur j) {
		boolean amiAjoute = super.ajouterAmi(j);
		if(amiAjoute) {
			if(this.amis.size() > this.AMIS_MAX) {
				this.amis.remove(j);
				System.out.println("Plus de place dans la liste d'amis...");
				return false;
				// ExceptionPlusDePlaceListeAmis
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
		boolean amiSupprime = super.ajouterAmi(j);
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
	public boolean ajouterJeu(Jeu j) {
		boolean jeuAjoute = super.ajouterJeu(j);
		if(jeuAjoute) {
			if(this.jeux.size() > this.JEUX_MAX) {
				this.jeux.remove(j);
				System.out.println("Plus de place dans la collection de jeux...");
				return false;
				// ExceptionPlusDePlaceCollectionJeux
			}
		}
		return jeuAjoute;
	}
}
