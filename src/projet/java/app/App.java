package projet.java.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import projet.java.jeux.Jeu;
import projet.java.joueurs.Bot;
import projet.java.joueurs.Enfant;
import projet.java.joueurs.Gold;
import projet.java.joueurs.Joueur;
import projet.java.joueurs.Standard;
import projet.java.utils.Options;
import projet.java.utils.Pair;

/**
 * Classe principale de l'application.
 * @author Nicolas Vrignaud
 *
 */
public class App {
	private List<Jeu> dataJeux = new ArrayList<>();
	private Map<String, Joueur> joueurs = new HashMap<>();
	// Le premier élément est la prochaine action à éffectuer dans la boucle infinie
	// Le second élément est le pseudo du joueur actif de l'application
	private Pair<Options, String> parametres = new Pair<>(Options.ACCUEIL, "");
	// Collection de bots
	
	// Pour faciliter le classement des jeux par plateformes et catégories
	private SortedSet<String> plateformes = new TreeSet<>();
	private SortedSet<String> categories = new TreeSet<>();
	
	private App() {
		
	}
	
	/**
	 * Constructeur de la classe App.
	 * 
	 * Parse le fichier CSV des jeux disponibles lors de la création de l'application.
	 * Stocke les jeux dans la variable dataJeux.
	 * Stocke les plateformes et les categories dans les variables du même nom.
	 * 
	 * @param dataURL : URL du fichier CSV à parser
	 * @param indiceDebut : indice de la ligne du premier jeu à stocker dans la collection de jeux
	 * @param nombreJeux : nombre de jeux à stocker à partir de indiceDebut
	 * 
	 * @throws IOException
	 */
	private App(String dataURL, int indiceDebut, int nombreJeux) throws IOException {
		URL url = new URL(dataURL);
		HttpURLConnection response = (HttpURLConnection)url.openConnection();
		
		if(response.getResponseCode()==200) {
			InputStream im = response.getInputStream();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(im));
			
			String ligne = "";
			for(int i = 0; i < indiceDebut + 1; i++) {				
				ligne = br.readLine();
			}
			
			String[] detailsJeux;
			int indiceFin = indiceDebut + nombreJeux;
			
			while(indiceDebut != indiceFin + 1 && ligne != null) {
				
				if(ligne.contains("\"")) {
			        detailsJeux = ligne.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				} else {					
					detailsJeux = ligne.split(",");
				}
				
				if(detailsJeux.length > 0) {
					Jeu jeu = new Jeu(
							Integer.parseInt(detailsJeux[0]),
							detailsJeux[1],
							detailsJeux[2],
							Year.parse(detailsJeux[3]),
							detailsJeux[4],
							detailsJeux[5],
							Float.parseFloat(detailsJeux[6]),
							Float.parseFloat(detailsJeux[7]),
							Float.parseFloat(detailsJeux[8]),
							Float.parseFloat(detailsJeux[9]),
							Float.parseFloat(detailsJeux[10]));
					dataJeux.add(jeu);
					plateformes.add(detailsJeux[2]);
					categories.add(detailsJeux[4]);
				}
				ligne = br.readLine();
				indiceDebut ++;
			}
		}
		
		// Ajout de joueurs par défaut pour tests
		this.joueurs.put("nicovri", new Gold("nicovri", "nico@vri.com", new Date(), "DS"));
		this.joueurs.put("john178", new Standard("john178", "John.178@gmail.com", new Date(), "PC"));
		this.joueurs.put("enfant", new Enfant("enfant", "enfant@test.fr", new Date(), "Wii", "G"));
		
		this.joueurs.put("enfant1", new Enfant("enfant1", "enfant@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant2", new Enfant("enfant2", "enfant2@test.fr", new Date(), "Wii", "S"));
		this.joueurs.put("enfant3", new Enfant("enfant3", "enfant3@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant4", new Enfant("enfant4", "enfant4@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant5", new Enfant("enfant5", "enfant5@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant6", new Enfant("enfant6", "enfant6@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant7", new Enfant("enfant7", "enfant7@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant8", new Enfant("enfant8", "enfant8@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant9", new Enfant("enfant9", "enfant9@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant10", new Enfant("enfant10", "enfant10@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant11", new Enfant("enfant11", "enfant11@test.fr", new Date(), "Wii", "G"));
		this.joueurs.put("enfant12", new Enfant("enfant12", "enfant12@test.fr", new Date(), "Wii", "G"));
	}

	/**
	 * Structure d'exécution de l'application.
	 * 
	 * @param args :
	 * 	- args[0] est l'URL du fichier CSV de jeux à parser
	 * 	- args[1] est le mode d'affichage de l'application
	 * 		- 1 pour CLI
	 * 		- 2 pour JavaFX
	 * 		- Un autre nombre ne lancera pas l'application
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		App app = new App();
		try {			
			app = new App(args[0], 55, 100);
		} catch (IndexOutOfBoundsException e) {}
		
		// Si on entre autre chose qu'un nombre pour args[1], choix aura la valeur 0 et l'application ne se lancera pas
		// De même si on oublie d'indiquer ce paramètre dans la ligne de commande (IndexOutOfBoundsException)
		int choix = 0;
		try {			
			choix = Integer.parseInt(args[1]);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			choix = 0;
		}
		
		// Choix du mode d'affichage
		switch(choix) {
		case 1:
			// On exécute la fonction correspondant au code de retour, selon les choix de l'utilisateur
			// (Voir projet.java.app.Menus)
			while(app.parametres.getFirst() != Options.QUITTER) {
				switch(app.parametres.getFirst()) {
				case ACCUEIL:
					app.parametres = Menus.Profil.accueilJoueur(app.joueurs, app.plateformes);
					break;
				case AFFICHAGE_PROFIL:
					app.parametres = Menus.Profil.afficherProfil(app.joueurs, app.parametres.getSecond());
					break;
				case JOUER:
					break;
				case COLLECTION:
					app.parametres = Menus.CollectionJeux.afficherListeJeux(app.joueurs.get(app.parametres.getSecond()).getJeux(),
							app.plateformes, app.categories, app.parametres.getSecond());
					break;
				case DETAILS_JEU_PERSO:
					app.parametres = Menus.CollectionJeux.afficherDetailsJeu(app.joueurs.get(app.parametres.getSecond()).getJeux(), app.parametres.getSecond()).getSecond();
					break;
				case BOUTIQUE:
					app.parametres = Menus.Boutique.acheterJeu(app.joueurs, app.dataJeux, app.plateformes, app.categories, app.parametres.getSecond());
					break;
				case CADEAU:
					break;
				case AFFICHAGE_AMIS:
					break;
				case INSCRIRE_ENFANT:
					app.parametres = Menus.Interactions.inscrireSonEnfant(app.joueurs, app.plateformes, app.parametres.getSecond());
					break;
				case INVITER:
					app.parametres = Menus.Interactions.ajouterAmi(app.joueurs, app.parametres.getSecond());
					break;
				case SUPPRIMER:
					app.parametres = Menus.Interactions.supprimerAmi(app.joueurs, app.parametres.getSecond());
					break;
				case GESTION_CONSOLE:
					app.parametres = Menus.Consoles.gestionConsoles(app.joueurs, app.plateformes, app.parametres.getSecond());
					break;
				case DECONNEXION:
					app.parametres = Menus.Profil.deconnexion(app.parametres.getSecond());
					break;
				case QUITTER:
					// Faire le nécessaire avant de quitter l'aplication
					break;
				default:
					// De même ici car on quitte l'application (+ potentielles erreurs car on n'est pas censé passer par là)
					app.parametres.setFirst(Options.QUITTER);
					app.parametres.setSecond("");
					break;
				}
			}
			break;
			
		case 2:
			break;
		default:
			break;
		}
	}
}
