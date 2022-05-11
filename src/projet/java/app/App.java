package projet.java.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import projet.java.err.collecVide.ListeVideException;
import projet.java.err.collecVide.SetVideException;
import projet.java.err.plusDePlace.PlusDePlaceCollectionJeuxException;
import projet.java.err.plusDePlace.PlusDePlaceListeAmisException;
import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.Jeu;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.joueurs.Enfant;
import projet.java.joueurs.Gold;
import projet.java.joueurs.Humain;
import projet.java.joueurs.Joueur;
import projet.java.joueurs.Standard;
import projet.java.utils.Options;
import projet.java.utils.Pair;

/**
 * Classe principale de l'application.</br>
 * Arguments par d�faut pour les tests : https://raw.githubusercontent.com/stef-aramp/video_games_sales/master/vgsales.csv 1
 * 
 * @author Nicolas Vrignaud
 * 
 */
public class App {
	private List<Jeu> dataJeux = new ArrayList<>();
	private Map<String, Joueur> joueurs = new HashMap<>();
	
	// Le premier �l�ment est la prochaine action � effectuer dans la boucle infinie
	// Le second �l�ment est le pseudo du joueur actif de l'application
	private Pair<Options, String> parametres = new Pair<>(Options.ACCUEIL, "");
	
	// Pour faciliter le classement des jeux par plateformes et cat�gories
	private SortedSet<String> plateformes = new TreeSet<>();
	private SortedSet<String> categories = new TreeSet<>();
	
	private App() {}
	
	/**
	 * Constructeur de la classe App.
	 * 
	 * <ul>
	 * <li>Parse le fichier CSV des jeux disponibles lors de la cr�ation de l'application.</li>
	 * <li>Stocke les donn�es sous forme de {@code Jeu} dans la variable dataJeux.</li>
	 * <li>Stocke les plateformes et les categories dans les variables du m�me nom.</li>
	 * <li>(Eventuellement, initialise des joueurs pour �viter d'avoir � tout refaire � chaque d�marrage)</li>
	 * </ul>
	 * 
	 * @param dataURL : URL du fichier CSV � parser
	 * @param indiceDebut : indice de la ligne du premier jeu � stocker dans la collection de jeux
	 * @param nombreJeux : nombre de jeux � stocker � partir de indiceDebut
	 * 
	 * @throws IOException
	 * @throws SetVideException 
	 * @throws ListeVideException 
	 */
	private App(String dataURL, int indiceDebut, int nombreJeux) throws IOException, SetVideException, ListeVideException {
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
		
		if(this.plateformes.isEmpty() || this.categories.isEmpty()) {
			throw new SetVideException();
		}
		if(this.dataJeux.isEmpty()) {
			throw new ListeVideException();
		}
		
		// Ajout de joueurs par d�faut pour tests
		this.joueurs.put("nicolas", new Gold("nicolas", "nicolas@nicolas.com", new Date(), "DS"));
		this.joueurs.put("john178", new Standard("john178", "John.178@gmail.com", new Date(), "PC"));
		this.joueurs.put("paul56", new Gold("paul56", "paul.5.6@test.org", new Date(), "XB"));
		this.joueurs.put("enfant", new Enfant("enfant", "enfant@test.fr", new Date(), "Wii", "G"));
		// Test limite du nombre d'amis (fonctionne de la m�me mani�re pour les jeux)
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
		// Test pour la m�thode jouer
		try {
			this.joueurs.get("nicolas").ajouterAmi(this.joueurs.get("paul56"));
			this.joueurs.get("nicolas").ajouterJeu(this.dataJeux.get(0));
			this.joueurs.get("paul56").ajouterAmi(this.joueurs.get("nicolas"));
			this.joueurs.get("paul56").ajouterJeu(this.dataJeux.get(8));
		} catch (PlusDePlaceListeAmisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PlusDePlaceCollectionJeuxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Structure d'ex�cution de l'application.
	 * 
	 * @param args :</br>
	 * <ul>
	 * <li>args[0] est l'URL du fichier CSV de jeux � parser</li>
	 * <li>args[1] est le mode d'affichage de l'application</li>
	 * <ul>
	 * <li>1 pour CLI</li>
	 * <li>2 pour JavaFX</li>
	 * <li>Un autre nombre ne lancera pas l'application</li>
	 * </ul>
	 * </ul>
	 * 
	 * En mode CLI, chaque fonction utilis�e retourne une valeur pour la variable parametres.
	 * Selon l'option retourn�e, la fonction suivante sera appel�e, tant que l'option quitter n'est pas retourn�e.
	 * 
	 * @see projet.java.app.Menus
	 * @see projet.java.utils.Options
	 */
	public static void main(String[] args) {
		App app = new App();
		try {			
			app = new App(args[0], 30, 100);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Il n'y a pas autant de jeux dans l'URL donn�, r�essayez.");
			e.printStackTrace();
		} catch (ConnectException | UnknownHostException e) {
			System.out.println("Pas de connexion � Internet pour obtenir les donn�es sur les jeux, r�essayez.");
			e.printStackTrace();
		} catch (SetVideException | ListeVideException e) {
			System.out.println("Les donn�es n'ont pas le bon format. Veuillez r�essayer avec un fichier CSV appropri�.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Si on entre autre chose qu'un nombre pour args[1], choix aura la valeur 0 et l'application ne se lancera pas
		// De m�me si on oublie d'indiquer ce param�tre dans la ligne de commande (IndexOutOfBoundsException)
		int choix = 0;
		try {			
			choix = Integer.parseInt(args[1]);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			System.out.println("Le choix du mode d'affichage n'a pas �t� correctement configur�.");
			choix = 0;
		}
		
		// Choix du mode d'affichage
		switch(choix) {
		case 1:
			// On ex�cute la fonction correspondant au code de retour, selon les choix de l'utilisateur
			while(!app.parametres.getFirst().equals(Options.QUITTER)) {
				switch(app.parametres.getFirst()) {
				case ACCUEIL:
					app.parametres = Menus.Profil.accueilJoueur(app.joueurs, app.plateformes);
					break;
				case AFFICHAGE_PROFIL:
					app.parametres = Menus.Profil.afficherProfil(app.joueurs, app.parametres.getSecond());
					break;
				case JOUER:
					app.parametres = Menus.PartieMulti.jouer(app.joueurs, app.dataJeux, app.plateformes, app.categories, app.parametres.getSecond());
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
					app.parametres = Menus.Interactions.offrirJeu(app.joueurs, app.plateformes, app.categories, app.parametres.getSecond());
					break;
				case AFFICHAGE_AMIS:
					app.parametres = Menus.ListeAmis.afficherListeAmis(app.joueurs, app.parametres.getSecond());
					break;
				case DETAILS_PUBLIQUES_AMIS:
					app.parametres = Menus.ListeAmis.afficherDetailsPubliquesAmis(app.joueurs, app.parametres.getSecond());
					break;
				case STATISTIQUES:
					app.parametres = Menus.Statistiques.affichageStatistiquesJoueur(app.joueurs, app.parametres.getSecond());
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
					app.parametres.setFirst(Options.QUITTER);
					app.parametres.setSecond("");
					break;
				default:
					app.parametres.setFirst(Options.QUITTER);
					app.parametres.setSecond("");
					break;
				}
			}
			// Faire le n�cessaire avant de quitter l'aplication (base de donn�es, lib�ration de la m�moire, etc.)
			app.parametres.setFirst(Options.ACCUEIL);
			app.parametres.setSecond("");
			break;
			
		case 2:
			break;
		default:
			break;
		}
	}
}
