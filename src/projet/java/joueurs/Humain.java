package projet.java.joueurs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import projet.java.jeux.PartieMultijoueurs;

public abstract class Humain extends Joueur {
	// Attributs que les bots n'ont pas
	protected List<PartieMultijoueurs> parties = new ArrayList<>();
	private Set<String> machinesDeJeu = new HashSet<>();
		
	private Humain(String pseudo, String email, Date dateNaissance) {
		super(pseudo, email, dateNaissance);
	}
	
	public Humain(String pseudo, String email, Date dateNaissance, String console) {
		this(pseudo, email, dateNaissance);
		this.ajouterNouvelleConsole(console);
	}
	
	public Set<String> getMachines() { return this.machinesDeJeu; }
	
	// A inclure dans une méthode de Menus
	// Ne pas oublier de dire si la machine a été ajoutée ou non
	public boolean ajouterNouvelleConsole(String console) {
		int tailleAvant = this.machinesDeJeu.size();
		this.machinesDeJeu.add(console);
		int tailleApres = this.machinesDeJeu.size();
		if(tailleAvant == tailleApres) {
			return false;
		}
		return true;
	}
	public boolean supprimerConsole(String console) {
		if(this.machinesDeJeu.contains(console)) {
			this.machinesDeJeu.remove(console);
			return true;
		}
		return false;
	}
	
	public void inscrire() {
		// Si bot (interdit)
		// Inscription d'un enfant passe par son parent/tuteur (1 ou 2 MAX) (mention de l'autre parent possible)
		// Forcément amis avec ses parents/tuteurs
	}
	
	// Profil privé (un bot ne peut pas voir son propre profil car ce n'est pas un humain)
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.getPseudo());
		b.append("\n-  " + this.getEmail());
		b.append("\n-  " + Joueur.DATE_NAISSANCE_FORMAT.format(this.getDateNaissance()) + "\n");
		// Affichage des parties ?
		for(String machine : this.machinesDeJeu) {
			b.append(machine + " / ");
		}
		return b.toString();
	}
}
