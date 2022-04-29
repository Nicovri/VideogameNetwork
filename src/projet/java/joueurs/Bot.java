package projet.java.joueurs;

import java.util.Date;

import projet.java.jeux.Jeu;

public class Bot extends Joueur {
	private static int id = 0;
	private final static String pseudoBot = "bot";
	//private final static String IA = "IA";

	private Bot(String pseudo, String email, Date dateNaissance) {
		super(pseudo, email, dateNaissance);
		Bot.id++;
	}
	
	public Bot(Jeu jeu) {
		this(Bot.pseudoBot + Bot.id, Bot.pseudoBot + "@" + Bot.pseudoBot, new Date());
		this.jeux.add(jeu);
	}
	
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
	
}
