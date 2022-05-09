package projet.java.joueurs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.utils.Pair;

public abstract class Humain extends Joueur {
	// Attributs que les bots n'ont pas
	protected Map<String, PartieMultijoueurs> parties = new HashMap<>();
	private Set<String> machinesDeJeu = new HashSet<>();
		
	private Humain(String pseudo, String email, Date dateNaissance) {
		super(pseudo, email, dateNaissance);
	}
	
	public Humain(String pseudo, String email, Date dateNaissance, String console) {
		this(pseudo, email, dateNaissance);
		this.ajouterNouvelleConsole(console);
	}
	
	public Set<String> getMachines() { return this.machinesDeJeu; }
	
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
	
	// Profil privé (un bot ne peut pas voir son propre profil car ce n'est pas un humain, inutile dans la classe Bot, donc dans la classe Joueur)
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.getPseudo());
		b.append("\n-  " + this.getEmail());
		b.append("\n-  " + Joueur.DATE_NAISSANCE_FORMAT.format(this.getDateNaissance()) + "\n");
		for(String machine : this.machinesDeJeu) {
			b.append(machine + " / ");
		}
		return b.toString();
	}
	
	public Pair<String, Pair<Integer, Boolean>> ajouterPartie(PartieMultijoueurs pm) throws PlusDePlaceNombreDePartiesException {
		int nbrPartiesCeJour = 0;
		String ceJour = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		String clePartie = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		for(String d : this.parties.keySet()) {
			if(d.contains(ceJour)) {
				nbrPartiesCeJour++;
			}
		}
		int tailleAvant = this.parties.size();
		parties.put(clePartie, pm);
		int tailleApres = this.parties.size();
		if(tailleAvant == tailleApres) {
			// Partie non ajoutée
			return new Pair<>(clePartie, new Pair<>(nbrPartiesCeJour, false));
		}
		return new Pair<>(clePartie, new Pair<>(nbrPartiesCeJour + 1, true));
	}
}
