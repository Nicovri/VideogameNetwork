package projet.java.joueurs;

import java.util.Date;

import projet.java.err.plusDePlace.PlusDePlaceCollectionJeuxException;
import projet.java.jeux.Jeu;

public class Bot extends Joueur {
	private static int id = 1;
	public final static String PSEUDO_BOT = "bot";
	private boolean joue;
	private String jeuEnCours;

	private Bot(String pseudo, String email, Date dateNaissance) {
		super(pseudo, email, dateNaissance);
		this.joue = false;
		this.jeuEnCours = "";
		Bot.id++;
	}
	
	public Bot(Jeu jeu) {
		this(Bot.PSEUDO_BOT + Bot.id, Bot.PSEUDO_BOT + "@" + Bot.PSEUDO_BOT, new Date());
		this.jeux.add(jeu);
	}
	
	public static int getId() { return Bot.id; }
	
	public void toggleJoue() { this.joue = !this.joue; }
	public boolean getJoue() { return this.joue; }
	
	public void setJeuEnCours(String j) { this.jeuEnCours = j; }
	public String getJeuEnCours() { return this.jeuEnCours; }
	
	@Override
	public String profilPublic() {
		StringBuilder b = new StringBuilder();
		b.append(super.profilPublic());
		b.append("\nListe de jeux :\n");
		for(Jeu j : this.jeux) {
			b.append("\t- " + j.affichageRapide());
		}
		return b.toString();
	}
	
	@Override
	public boolean ajouterJeu(Jeu j) throws PlusDePlaceCollectionJeuxException {
		boolean jeuAjoute = super.ajouterJeu(j);
		if(jeuAjoute) {
			if(Integer.parseInt(j.getAnnee()) < Integer.parseInt(Jeu.DATE_IA)) {
				System.out.println("Le jeu ne possède pas de module d'IA...");
				this.getJeux().remove(j);
				return false;
			}
		}
		return jeuAjoute;
	}
	
}
