package projet.java.joueurs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import projet.java.err.nonTrouve.PartieNonTrouveeException;
import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.utils.Pair;

/**
 * Classe repr�sentant un joueur {@code Humain} (Standard, Gold ou Enfant).</br>
 * Se d�cline en plusieurs cat�gories plus pr�cises qui permettent de diff�rencier le statut des joueurs (c'est pour cette raison que la classe est {@code abstract}, on ne veut pas pouvoir l'instancier)
 * 
 * @author Nicolas Vrignaud
 * 
 * @see projet.java.joueurs.Standard
 * @see projet.java.joueurs.Gold
 * @see projet.java.joueurs.Enfant
 */
public abstract class Humain extends Joueur {
	// Attributs que les bots n'ont pas
	protected Map<String, PartieMultijoueurs> parties = new HashMap<>();
	protected Set<String> machinesDeJeu = new HashSet<>();
		
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
	
	/**
	 * Profil priv� du joueur.</br>
	 * Un bot ne peut pas voir son propre profil car ce n'est pas un humain, inutile donc d'avoir cette m�thode dans la classe {@code Bot}, donc dans la classe {@code Joueur}
	 */
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
	
	/**
	 * Essaie d'ajouter une partie jou�e � la liste selon les conditions demand�es.</br>
	 * (des conditions plus pr�cises, notamment de place sont trait�s dans les classes filles)
	 * 
	 * @param pm : partie multijoueurs jou�e par le joueur
	 * 
	 * @return une pair compos�e de la cl� de partie, et d'une autre paire (elle-m�me compos�e du nombre de parties du jour, et du boolean de si la partie a �t� ajout�e correctement)
	 * 
	 * @throws PlusDePlaceNombreDePartiesException
	 */
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
			// Partie non ajout�e
			return new Pair<>(clePartie, new Pair<>(nbrPartiesCeJour, false));
		}
		return new Pair<>(clePartie, new Pair<>(nbrPartiesCeJour + 1, true));
	}
	
	/**
	 * @return une paire compos�e du nombre de victoire et du nombre de d�faites
	 */
	public Pair<Integer, Integer> proportionVictoiresDefaites() {
		Pair<Integer, Integer> res = new Pair<>(0, 0);
		for(PartieMultijoueurs pm : this.parties.values()) {
			if(this.getPseudo().equals(pm.getPseudoGagnant())) res.setFirst(res.getFirst() + 1);
			if(this.getPseudo().equals(pm.getPseudoPerdant())) res.setSecond(res.getSecond() + 1);
		}
		return res;
	}
	
	/**
	 * @return le pourcentage de victoires de toutes les parties jou�es
	 */
	public double pourcentageDeVictoire() {
		Pair<Integer, Integer> res = this.proportionVictoiresDefaites();
		double pourcent = res.getFirst().doubleValue() / (res.getFirst() + res.getSecond());
		pourcent *= 100;
		return pourcent;
	}
	
	/**
	 * 
	 * @param date : jour souhait� au format AAAA/MM/JJ
	 * 
	 * @return la map des parties jou�es le jour demand� (peut �tre {@code null}).
	 * 
	 * @throws PartieNonTrouveeException
	 */
	public Map<String, PartieMultijoueurs> getPartiesJour(String date) throws PartieNonTrouveeException {
		Date jour = null;
		try {
			jour = new SimpleDateFormat("yyyy/MM/dd").parse(date);
		} catch (ParseException e) {
			throw new PartieNonTrouveeException();
		}
		String jourDemande = new SimpleDateFormat("yyyy/MM/dd").format(jour);
		Map<String, PartieMultijoueurs> partiesJour = new HashMap<>();
		for(String temps : this.parties.keySet()) {
			if(temps.contains(jourDemande)) {
				partiesJour.put(temps, this.parties.get(temps));
			}
		}
		if(partiesJour.isEmpty()) {
			throw new PartieNonTrouveeException();
		}
		return partiesJour;
	}
}
