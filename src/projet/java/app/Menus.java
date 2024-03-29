package projet.java.app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;

import projet.java.err.joueur2.Joueur2NonHumainException;
import projet.java.err.nonTrouve.JeuNonTrouveException;
import projet.java.err.nonTrouve.JoueurNonTrouveException;
import projet.java.err.nonTrouve.PartieNonTrouveeException;
import projet.java.err.plusDePlace.PlusDePlaceCollectionJeuxException;
import projet.java.err.plusDePlace.PlusDePlaceListeAmisException;
import projet.java.err.plusDePlace.PlusDePlaceNombreDePartiesException;
import projet.java.jeux.Jeu;
import projet.java.jeux.PartieMultijoueurs;
import projet.java.joueurs.Bot;
import projet.java.joueurs.Enfant;
import projet.java.joueurs.Gold;
import projet.java.joueurs.Humain;
import projet.java.joueurs.Joueur;
import projet.java.joueurs.Standard;
import projet.java.utils.Options;
import projet.java.utils.Pair;

/**
 * Classe secondaire de l'application.<br/>
 * Les sous-classes sont directement li�es � la class projet.java.app.App et repr�sentent les fonctionnalit�s disponibles.
 * 
 * @author Nicolas Vrignaud
 * 
 * @see projet.java.app.App
 *
 */
public class Menus {
	private static Pair<Options, String> resultat = new Pair<>();
	private static Scanner sc = new Scanner(System.in);
	
	/**
	 * Format de la date de naissance du joueur en fran�ais. Nom du jour et du mois �crit en entier.
	 */
	private final static DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRENCH);
	
	/**
	 * S�parateur utilis� lors de l'affichage pour plus de facilit� de lecture
	 */
	private final static String SEPARATEUR = "\n".concat("=====".repeat(15)).concat("\n");	
	
	/**
	 * (Quitter) Avant une entr�e de texte par l'utilisateur, permet un affichage des choix alternatifs propos�s � l'utilisateur
	 * 
	 * @see projet.java.app.Menus#CHOIX_RECOMMENCER <br/>(Recommencer)
	 * @see projet.java.app.Menus#CHOIX_OUI <br/>(Oui)
	 * @see projet.java.app.Menus#CHOIX_NON <br/>(Non)
	 */
	private final static String CHOIX_QUITTER = "(Pour quitter, entrez Q)";
	private final static String CHOIX_RECOMMENCER = "(Pour recommencer, entrez R)";
	private final static String CHOIX_OUI = "(Pour valider, entrez O)";
	private final static String CHOIX_NON = "(Pour refuser, entrez N)";
	
	/**
	 * Ferme le {@code Scanner} ouvert lors de sa d�claration.<br/>
	 * A utiliser UNIQUEMENT � la fin du programme, sinon System.in n'est plus accessible et propage {@code NoSuchElementException} lors de l'utilisation suivante de sc.
	 */
	public static void closeSc() {
		sc.close();
	}
	
	/**
	 * M�me fonctionnalit� que {@code trouverJeuSelonRang}, sauf que la fonction affiche le jeu au lieu de le retourner.
	 * 
	 * @param jeux
	 * @param rang
	 * 
	 * @throws JeuNonTrouveException
	 * 
	 * @see {@link projet.java.jeux.Jeu#trouverJeuSelonRang(Collection, int)}
	 */
	private static void afficherDetailsJeuSelonRang(Collection<Jeu> jeux, int rang) throws JeuNonTrouveException {
		Jeu jeu = Jeu.trouverJeuSelonRang(jeux, rang);
		System.out.println(Menus.SEPARATEUR);
		System.out.println(jeu.toString());
		System.out.println(Menus.SEPARATEUR);
	}
	
	/**
	 * Retourne la liste des pseudos des amis d'un joueur en enlevant les joueurs de l'instance {@code Bot}.
	 * Elle ajoute une s�curit� avant d'appliquer d'autres m�thodes pour �viter des actions ind�sirables.
	 * (notamment offrir un jeu ou jouer avec un ami)
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
	 * 
	 * @return la liste des amis de {@code joueurActif} apr�s avoir enlev� les bots, s'il y en a, sinon
	 * @throws JoueurNonTrouveException : aucun ami trouv�
	 */
	private static Set<String> listeAmisSansBots(Map<String, Joueur> joueurs, String joueurActif) throws JoueurNonTrouveException {
		Set<String> amisSansBots = new HashSet<>();
		int c = 0;
		if(joueurs.get(joueurActif).getAmis().isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
			throw new JoueurNonTrouveException();
		}
		for(String pseudo : joueurs.get(joueurActif).getAmis()) {
			if(!(joueurs.get(pseudo) instanceof Bot)) {
				amisSansBots.add(pseudo);
				c++;
			}
		}
		if(c == 0 && !joueurs.get(joueurActif).getAmis().isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis non bots dans votre liste.");
			throw new JoueurNonTrouveException();
		}
		return amisSansBots;
	}
	
	/**
	 * Retourne la liste des amis qui peuvent jouer avec nous � un jeu pass� en param�tres.
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 * @param jeu : le jeu auquel le joueur voudrait jouer
	 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
	 * 
	 * @return la liste des amis de {@code joueurActif} apr�s avoir enlev� les bots et qui peuvent jouer au jeu, s'il y en a, sinon
	 * @throws JoueurNonTrouveException : aucun ami trouv�
	 * @throws Joueur2NonHumainException : si le joueur n'est pas un humain, lors de l'appel de {@code pm.partiePossibleHumain()}
	 */
	private static Set<String> listeAmisPouvantJouer(Map<String, Joueur> joueurs, Jeu jeu, String joueurActif) throws JoueurNonTrouveException, Joueur2NonHumainException {
		Set<String> amisSansBotsPouvantJouer = listeAmisSansBots(joueurs, joueurActif);
		for(String pseudo : amisSansBotsPouvantJouer) {
			PartieMultijoueurs pm = new PartieMultijoueurs(jeu, joueurs.get(joueurActif), joueurs.get(pseudo)); 
			if(!pm.partiePossibleHumain()) {
				amisSansBotsPouvantJouer.remove(pseudo);
			}
		}
		return amisSansBotsPouvantJouer;
	}
	
	/**
	 * Obtenir tous les bots pr�sents dans l'application.
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 * 
	 * @return la liste de {@code Bot} parmi tous les joueurs
	 */
	private static Set<String> listeBots(Map<String, Joueur> joueurs) {
		Set<String> bots = new HashSet<>();
		for(String pseudo : joueurs.keySet()) {
			if(joueurs.get(pseudo) instanceof Bot) {
				bots.add(pseudo);
			}
		}
		return bots;
	}
	
	/**
	 * Gestionnaire des bots pr�sents dans l'application.<br/>
	 * <br/>
	 * On aura apr�s l'appel de cette fonction 1 seul bot par jeu.<br/>
	 * TODO : ind�pendamment de la console.
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 */
	private static void gestionBots(Map<String, Joueur> joueurs) {
		Set<String> bots = listeBots(joueurs);
		Set<String> botsEnTrop = new HashSet<>();
		if(bots.isEmpty()) {
			
		} else {
			for(String pseudoBot : bots) {
				Set<Jeu> jeux = joueurs.get(pseudoBot).getJeux();
				bots.remove(pseudoBot);
				for(String pseudoAutreBot : bots) {
					for(Jeu jeu : jeux) {
						if(joueurs.get(pseudoAutreBot).getJeux().contains(jeu)) {
							bots.remove(pseudoAutreBot);
							botsEnTrop.add(pseudoAutreBot);
							break;
						}
					}
				}
				bots.add(pseudoBot);
			}
		}
		for(String pseudo : botsEnTrop) {
			joueurs.remove(pseudo);
		}
	}
	
	/**
	 * Gestionnaire d'un partie avec un bot.<br/>
	 * Soit on cr�e un nouveau bot et on le retourne, soit on retourne un bot d�j� existant auquel
	 * on ajoute (si n�cessaire) le jeu demand�.<br/>
	 * <br/>
	 * Hypoth�se : Un bot peut jouer � plusieurs parties du m�me jeu en m�me temps
	 * mais pas 2 parties de jeux diff�rents en m�me temps.
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 * @param jeu : le jeu demand� lors d'une nouvelle partie
	 * 
	 * @return le pseudo du bot avec lequel on peut jouer
	 */
	private static String gestionPartieAvecBots(Map<String, Joueur> joueurs, Jeu jeu) {
		Set<String> bots = listeBots(joueurs);
		String nomBot = Bot.PSEUDO_BOT + Bot.getId();
		if(bots.isEmpty()) {
			joueurs.put(nomBot, new Bot(jeu));
			return nomBot;
		} else {
			for(String pseudoBot : bots) {
				// Le bot ne joue pas
				if(!((Bot)joueurs.get(pseudoBot)).getJoue()) {
					if(!joueurs.get(pseudoBot).getJeux().contains(jeu)) {						
						try {
							joueurs.get(pseudoBot).ajouterJeu(jeu);
						} catch (PlusDePlaceCollectionJeuxException e) {}
					}
					return pseudoBot;
				}
				// Le bot joue au m�me jeu
				if(((Bot)joueurs.get(pseudoBot)).getJoue() && jeu.getNom().equals(((Bot)joueurs.get(pseudoBot)).getJeuEnCours())) {
					return pseudoBot;
				}
			}
			// Tous les bots jouent � un autre jeu
			joueurs.put(nomBot, new Bot(jeu));
			return nomBot;
		}
	}
	
	/**
	 * Retourne la liste des joueurs avec lesquels on peut jouer au jeu pass� en param�tres.<br/>
	 * Les amis ne sont pas exclus lors de la recherche.
	 * <br/>
	 * Hypoth�se : les joueurs sont :
	 * <ul>
	 *   <li>Les joueurs qui poss�dent le plus de jeux en commun et ayant la possibilit� de jouer, c�d ayant la console appropri�e � leur jeu et respectant les conditions d'ajout en tant qu'ami</li>
	 *   <li>Si on ne peut pas d�partager, les joueurs qui poss�dent de la place dans leur liste d'amis</li>
	 * </ul> 
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 * @param jeu : le jeu demand� lors d'une nouvelle partie
	 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
	 * @param max : le nombre maximum de joueurs aptes � jouer � garder 
	 * 
	 * @return la liste des joueurs qui peuvent jouer avec nous au jeu demand�, s'il y en a, sinon
	 * @throws JoueurNonTrouveException : aucun ami trouv�
	 */
	private static Set<String> listeJoueursAdaptesPourJouer(Map<String, Joueur> joueurs, Jeu jeu, String joueurActif, int max) throws JoueurNonTrouveException {
		Set<String> joueursAdaptes = new HashSet<>();
		int c = joueurs.size();
		if(joueurs.isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
			throw new JoueurNonTrouveException();
		}
		while(joueursAdaptes.size() < max && c > 0) {
			for(Joueur j : joueurs.values()) {
				if(j instanceof Humain && !j.getPseudo().equals(joueurs.get(joueurActif).getPseudo())) {
					// Pour chacun des jeux du joueur j
					for(Jeu je : j.getJeux()) {
						// On v�rifie que le nom du jeu entr� en param�tre et le nom du jeu est le m�me
						if(jeu.getNom().equals(je.getNom())) {
							// S'il est le m�me, on v�rifie qu'il poss�de la console appropri�e (� son propre jeu)
							if(((Humain) j).getMachines().contains(je.getPlateforme())) {
								// On regarde s'il peut accepter de nouveaux amis selon la relation entre les 2 joueurs
								if(joueurs.get(joueurActif) instanceof Gold && !(j instanceof Enfant)) {
									if(j instanceof Standard && j.getAmis().size() < ((Standard)j).getAmisMax()) {
										joueursAdaptes.add(j.getPseudo());
									}
									if(j instanceof Gold) {
										joueursAdaptes.add(j.getPseudo());
									}
								}
								if(joueurs.get(joueurActif) instanceof Enfant && j instanceof Enfant) {
									if(j.getAmis().size() < ((Enfant)j).getAmisMax()) {									
										joueursAdaptes.add(j.getPseudo());
									}
								}
							}
						}
					}
				}
				c--;
			}
		}
		if(joueursAdaptes.isEmpty()) {
			System.out.println("Aucun joueur ne correspond et ne peut jouer avec vous � ce jeu pour l'instant...");
			throw new JoueurNonTrouveException();
		}
		return joueursAdaptes;
	}
	
	/**
	 * Recherche les joueurs ayant le plus jou� aujourd'hui et de leur nombre de parties.<br/>
	 * <br/>
	 * Hypoth�se : les bots ne comptent pas, on ne garde que les joueurs de la classe {@code Humain}.
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 * @param max : le nombre maximum de joueurs � garder pour le classement
	 * 
	 * @return une map (tri�e car on les ajoute dans l'ordre) des joueurs ayant le plus jou� aujourd'hui et de leur nombre de parties
	 * @throws JoueurNonTrouveException 
	 */
	@SuppressWarnings("deprecation")
	private static Map<String, Integer> classementJoueursDuJour(Map<String, Joueur> joueurs, int max) throws JoueurNonTrouveException {
		Map<String, Integer> classement = new HashMap<>();
		
		Date now = new Date();
		int year = 1900 + now.getYear();
		int month = 1 + now.getMonth();
		int day = now.getDate();
		String ceJour = year + "/" + month + "/" + day;
		
		Map<String, PartieMultijoueurs> parties = new HashMap<>();
		int c = 0;
		
		// On trouve c le nombre maximum de parties effectu�es aujourd'hui
		for(Joueur j : joueurs.values()) {
			if(j instanceof Humain) {				
				try {
					parties = ((Humain)j).getPartiesJour(ceJour);
				} catch (PartieNonTrouveeException e) {}
			}
			if(parties.size() > c) {
				c = parties.size();
			}
		}
		
		// On ajoute les joueurs dans l'ordre du nombre de parties jou�es aujourd'hui
		while(c > 0 && classement.size() < max) {
			for(Joueur j : joueurs.values()) {
				if(j instanceof Humain) {					
					try {
						parties = ((Humain)j).getPartiesJour(ceJour);
						if(parties.size() == c) {
							classement.put(j.getPseudo(), c);
						}
					} catch (PartieNonTrouveeException e) {}
				}
			}
			c--;
		}
		
		if(classement.isEmpty()) {
			throw new JoueurNonTrouveException();
		}
		return classement;
	}
	
	/**
	 * Recherche les jeux ayant �t� le plus jou� ainsi que le nombre de parties jou�es.<br/>
	 * <br/>
	 * Hypoth�se : On ne prend pas en compte le fait qu'une partie soit jou�e par 2 joueurs (car elle est enregistr�e pour les 2 si ce sont des humains). Un jeu a �t� jou� 1 fois si un joueur {@code Humain} y a jou�.
	 * 
	 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
	 * @param jeux : la collection de jeux de l'application
	 * @param max : le nombre maximum de joueurs � garder pour le classement
	 * 
	 * @return une map (non tri�e) du rang du jeu et du nombre de fois o� on y a jou�
	 * @throws JeuNonTrouveException 
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private static Map<Integer, Integer> classementJeuxLesPlusPopulaires(Map<String, Joueur> joueurs, Collection<Jeu> jeux) throws JeuNonTrouveException {
		Map<Integer, Integer> classement = new HashMap<>();
		
		// On met tous les jeux � 0
		for(Jeu j : jeux) {
			classement.put(j.getRang(), 0);
		}
		
		Date now = new Date();
		int year = 1900 + now.getYear();
		int month = 1 + now.getMonth();
		int day = now.getDate();
		String ceJour = year + "/" + month + "/" + day;
		
		// On ajoute +1 au jeu s'il a �t� jou� lors d'une partie		
		for(Joueur j : joueurs.values()) {
			if(j instanceof Humain) {
				try {
					for(PartieMultijoueurs pm : ((Humain)j).getPartiesJour(ceJour).values()) {
						classement.put(pm.getJeu().getRang(), +1);
						// classement.get(pm.getJeu().getRang() + 1
					}
				} catch (PartieNonTrouveeException e) {}
			}
		}
		
		
		
		if(classement.isEmpty()) {
			throw new JeuNonTrouveException();
		}
		return classement;
	}
	
	/**
	 * Tout ce qui concerne le {@code Joueur} en lui m�me (cr�ation du compte, connexion, accueil du joueur sur son profil, affichage du profil, d�connexion)
	 * 
	 * @author Nicolas Vrignaud
	 * 
	 * @see projet.java.app.Menus
	 */
	public static class Profil {
		
		/**
		 * Cr�ation interactive du compte {@code Joueur} en mode CLI.<br/>
		 * 
		 * Elle passe par les �tapes suivantes
		 * <ul>
		 *   <li>Pseudo : nouveau et plus de 3 caract�res</li>
		 *   <li>Email : mot1@mot2.extension (extension sera soit com, soit fr, soit org)</li>
		 *   <li>Date de naissance : doit �tre un format valide (sourtout concernant le mois et le jour)</li>
		 *   <li>Console personnelle : affichage de la liste des consoles disponibles et choix du num�ro associ�</li>
		 *   <br/>
		 *   <li>R�capitulatif : affichage</li>
		 *   <li>Statut : Standard ou Gold (ce statut sera sauvegard� pour plus tard dans le cas d'un enfant)</li>
		 *   <li>Fin : ajout du joueur / recommencer / quitter / erreur</li>
		 * </ul>
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param estEnfant : le joueur � inscrire est-il un enfant ?
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		private static Pair<Options, String> creationCompte(Map<String, Joueur> joueurs, SortedSet<String> plateformes, boolean estEnfant) {
			// Choix du futur pseudo (ne doit pas d�j� exister et doit faire plus de 3 caract�res)
			System.out.print("Pseudo: ");
			String pseudo = sc.nextLine();
			if(joueurs != null) {
				while(joueurs.containsKey(pseudo) || pseudo.length() < 3) {
					if(joueurs.containsKey(pseudo)) {
						System.out.println("Ce pseudo est d�j� utilis�. Veuillez en choisir un autre");
						System.out.print("Pseudo: ");
						sc = new Scanner(System.in);
						pseudo = sc.nextLine();
					}
					if(pseudo.length() < 3) {
						System.out.println("Ce pseudo est trop court. Veuillez choisir un pseudo de 3 caract�res ou plus.");
						System.out.print("Pseudo: ");
						sc = new Scanner(System.in);
						pseudo = sc.nextLine();
					}
				}
			}
			
			// Choix de l'email (doit respecter quelques crit�res)
			System.out.print("Email: ");
			sc = new Scanner(System.in);
			String email = sc.nextLine();
			while(!email.matches("[\\w'-_]+@[\\w'-_]+.(com|fr|org)")) {
				System.out.println("Cette adresse mail n'est pas correcte. Veuillez entrer une adresse de la forme mot1@mot2.(com/fr/org)");
				System.out.print("Email: ");
				sc = new Scanner(System.in);
				email = sc.nextLine();
			}
			
			// Choix de la date de naissance
			System.out.print("Date de naissance (AAAA/MM/JJ): ");
			sc = new Scanner(System.in);
			String date = sc.nextLine();
			while(!date.matches("^\\d{4}\\/((0[13578]|1[02])\\/([0-2][1-9]|3[01])|(0[469]|11)\\/([0-2][1-9]|30)|02\\/[0-2][1-9])$")) {
				System.out.println("Ce format de date n'est pas valide. Veuillez entrer votre date de naissance correctement.");
				System.out.print("Date de naissance (AAAA/MM/JJ): ");
				sc = new Scanner(System.in);
				date = sc.nextLine();
			}
			Date dateNaissance = null;
			try {
				dateNaissance = new SimpleDateFormat("yyyy/MM/dd").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			// Choix de sa console personnelle (une seule, celle qu'on poss�de � l'inscription)
			Object[] p = plateformes.toArray();
			int nombre = p.length + 1;
			for(int i = 0; i < p.length; i++) {
				System.out.println((i + 1) + ". " + p[i]);
			}
			System.out.print("Une de vos consoles personnelles (choisir le num�ro associ�): ");
			sc = new Scanner(System.in);
			String choix = sc.nextLine();
			while(!choix.matches("\\d+")) {
				System.out.println("Veuillez choisir un nombre.");
				System.out.print("Une de vos consoles personnelles: ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			int indexConsole = Integer.parseInt(choix);
			while(indexConsole <= 0 || indexConsole > nombre) {
				System.out.println("Veuillez choisir un nombre entre 1 et " + nombre + ".");
				System.out.print("Une de vos consoles personnelles: ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
				indexConsole = Integer.parseInt(choix);
			}
			String console = (String) p[indexConsole - 1];
			
			// R�capitulatif
			System.out.print(Menus.SEPARATEUR);
			System.out.println("R�capitulatif...");
			System.out.println("Pseudo: " + pseudo);
			System.out.println("Email: " + email);
			System.out.println(Menus.DATE_FORMAT.format(dateNaissance));
			System.out.println("Console: " + console);
			System.out.println(Menus.SEPARATEUR);
			
			// Choix du statut et ajout du joueur (si possible)
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.println(Menus.CHOIX_RECOMMENCER);
			System.out.println("Choisissez votre statut.");
			if(estEnfant) System.out.println("(ce statut sera actif d�s que vous aurez 18 ans)");
			
			System.out.print("Statut (S=Standard / G=Gold): ");
			String statut = "";
			while(!statut.equals("S") && !statut.equals("G") && !statut.equals("R") && !statut.equals("Q")) {			
				sc = new Scanner(System.in);
				statut = sc.nextLine();
				switch(statut) {
				case "S":
					System.out.println("Cr�ation du compte...");
					int tailleAvant = joueurs.size();
					if(estEnfant) {
						joueurs.put(pseudo, new Enfant(pseudo, email, dateNaissance, console, statut));
					} else {						
						joueurs.put(pseudo, new Standard(pseudo, email, dateNaissance, console));
					}
					int tailleApres = joueurs.size();
					// V�rifier si le joueur a bien �t� ajout� (et s'il n'est pas en double)
					if(tailleApres == tailleAvant + 1) {
						System.out.println("Compte cr�� avec succ�s!");
						resultat.setBoth(Options.AFFICHAGE_PROFIL, pseudo);
						System.out.println("Affichage de votre profil Standard...");						
					} else {
						System.out.println("Erreur dans la cr�ation du compte. Veuillez r�essayer.");
						resultat.setBoth(Options.ACCUEIL, "");
					}
					break;
				case "G":
					System.out.println("Achat du statut Gold...");
					System.out.println("Cr�ation du compte...");
					tailleAvant = joueurs.size();
					if(estEnfant) {
						joueurs.put(pseudo, new Enfant(pseudo, email, dateNaissance, console, statut));
					} else {						
						joueurs.put(pseudo, new Gold(pseudo, email, dateNaissance, console));
					}
					tailleApres = joueurs.size();
					// V�rifier si joueur bien ajout� (et s'il n'est pas en double (exception ?))
					if(tailleApres == tailleAvant + 1) {						
						System.out.println("Compte cr�� avec succ�s!");
						resultat.setBoth(Options.AFFICHAGE_PROFIL, pseudo);
						System.out.println("Affichage de votre profil Gold...");
					} else {
						System.out.println("Erreur dans la cr�ation du compte. Veuillez r�essayer.");
						resultat.setBoth(Options.ACCUEIL, "");
					}
					break;
				case "R":
					System.out.print(Menus.SEPARATEUR);
					resultat.setBoth(Options.ACCUEIL, "");
					break;
				case "Q":
					resultat.setBoth(Options.QUITTER, "");
					break;
				default:
					System.out.println("Ce statut n'est pas disponible. Veuillez choisir un statut existant.");
					break;
				}
			}
			return resultat;
		}
		
		/**
		 * Connexion d'un joueur � l'application.<br/>
		 * (sans gestion de mot de passe pour l'instant)<br/>
		 * <br/>
		 * Si lors de la connexion, un enfant a atteint 18 ans, il change de statut et obtient le futur statut qu'il avait enregistr� � la cr�ation de son compte.
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		private static Pair<Options, String> connexion(Map<String, Joueur> joueurs, SortedSet<String> plateformes) {
			if(joueurs.isEmpty()) {
				System.out.println("\nAucun joueurs inscrits. Retour � l'�cran d'accueil...");
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			System.out.print("Votre pseudo: ");
			sc = new Scanner(System.in);
			String pseudo = sc.nextLine();

			while(!joueurs.containsKey(pseudo)) {
				// Q ne peut pas �tre un pseudo car il doit contenir au moins 3 caract�res
				if(pseudo.equals("Q")) {
					resultat.setBoth(Options.ACCUEIL, "");
					return resultat;
				}
				System.out.println(Menus.CHOIX_QUITTER);
		        System.out.println("Le pseudo est inexistant. Veuillez entrer votre pseudo.");
		        System.out.print("Pseudo: ");
				sc = new Scanner(System.in);
				pseudo = sc.nextLine();
			}
			
			if(joueurs.get(pseudo) instanceof Enfant) {
				if(((Enfant)joueurs.get(pseudo)).aPlusDe18Ans()) {
					Humain adulte = ((Enfant)joueurs.get(pseudo)).devenirAdulte();
					joueurs.replace(pseudo, adulte);
					System.out.println("Vous avez maintenant plus de 18 ans !\nVous pouvez utiliser toutes les fonctionalit�s disponibles !");
				}
			}
			
			resultat.setBoth(Options.AFFICHAGE_PROFIL, pseudo);
			return resultat;
		}
		
		/**
		 * Accueil interactif du joueur.<br/>
		 * (choix entre cr�ation de compte / connexion / quitter l'application)
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> accueilJoueur(Map<String, Joueur> joueurs, SortedSet<String> plateformes) {
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Cr�ation de compte\n2. Connexion\n3. Quitter");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			
			switch(choix) {
			case "1":
				resultat = creationCompte(joueurs, plateformes, false);
				break;
			case "2":
				resultat = connexion(joueurs, plateformes);
				break;
			case "3":
				resultat.setBoth(Options.QUITTER, "");
				break;
			default:
				System.out.println("Veuillez choisir une des options ci-dessous.");
				resultat.setBoth(Options.ACCUEIL, "");
				break;
			}
			return resultat;
		}
		
		/**
		 * Affichage du profil priv� d'un joueur et menus du choix de la prochaine action.<br/>
		 * <br/>
		 * Affichage du titre de chaque {@code Options} (s'il elle est cens� apparaitre dans ce menu)<br/>
		 * Choix par le joueur d'une des options.
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> afficherProfil(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			int choix = 0;
			if(!resultat.getSecond().equals("")) {
				StringBuilder b = new StringBuilder();
				b.append(Menus.SEPARATEUR);
				
				b.append(joueurs.get(joueurActif).toString());
				
				b.append(Menus.SEPARATEUR);
				
				b.append("Options :\n");
				if(joueurs.get(joueurActif) instanceof Enfant) {
					for(Options option : Options.values()) {
						if(option.getEstDansMenu() && option.getEstAutoriseAuxEnfants()) {
							b.append("\t" + option.getNumeroEnfant() + ". " + option.getTitre() + "\n");
						}
					}
				} else {					
					for(Options option : Options.values()) {
						if(option.getEstDansMenu()) {
							b.append("\t" + option.getNumero() + ". " + option.getTitre() + "\n");
						}
					}
				}
				b.append(Menus.SEPARATEUR);
				System.out.println(b.toString());
				String choixStr = "";
				while(!choixStr.matches("\\d+")) {					
					System.out.print("Votre choix : ");
					sc = new Scanner(System.in);
					choixStr = sc.nextLine();
				}
				choix = Integer.parseInt(choixStr);
			}
			// ExceptionOptionNonTrouvee
			if(joueurs.get(joueurActif) instanceof Enfant) {
				resultat.setFirst(Options.findOptionByNumeroEnfant(choix));
			} else {
				resultat.setFirst(Options.findOptionByNumero(choix));
			}
			resultat.setSecond(joueurActif);
			return resultat;
		}

		/**
		 * D�connexion de l'application. Retourne � l'�cran d'accueil si le choix final est oui.
		 * 
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> deconnexion(String joueurActif) {
			System.out.println("Etes-vous s�r(e) de vouloir vous d�connecter ?");
			System.out.println(Menus.CHOIX_OUI);
			System.out.println(Menus.CHOIX_NON);
			System.out.print("Votre choix : ");
			sc = new Scanner(System.in);
			String choix = sc.nextLine();
			switch(choix) {
			case "O":
				resultat.setBoth(Options.ACCUEIL, "");
				break;
			case "N":
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				break;
			default:
				resultat.setBoth(Options.DECONNEXION, joueurActif);
				break;
			}
			return resultat;
		}
	}
	
	/**
	 * Tout ce qui concerne les interactions entre le {@code Joueur} et d'autres joueurs.<br/>
	 * (inscription de son enfant, ajout d'un ami, suppression d'un ami, offre d'un jeu � un ami)
	 * 
	 * @author Nicolas Vrignaud
	 * 
	 * @see projet.java.app.Menus
	 *
	 */
	public static class Interactions {
		
		/**
		 * Inscription interactive par le parent 1 de son {@code Enfant}.<br/>
		 * Ajout r�ciproque � la liste d'amis (si impossible, exception).<br/>
		 * Choix d'un parent 2.<br/>
		 * <br/>
		 * Hypoth�ses :<br/>
		 * On suppose qu'apr�s inscription, sa liste de parents/tuteurs ne peut pas �tre modif�e (elle contient donc soit 1 soit 2 Joueurs).<br/>
		 * On suppose (pour l'instant) qu'un parent ne peut plus mentionner son enfant s'il s'inscrit apr�s lui (cas du 2e parent).
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> inscrireSonEnfant(Map<String, Joueur> joueurs, SortedSet<String> plateformes, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			if(!(joueurs.get(joueurActif) instanceof Enfant)) {
				String parent = joueurActif;
				
				System.out.println("Veillez � �tre pr�sent lors de l'inscription de votre enfant");
				resultat = Menus.Profil.creationCompte(joueurs, plateformes, true);
				
				if(resultat.getFirst() == Options.AFFICHAGE_PROFIL) {
					System.out.println("Ajout r�ciproque dans la liste d'amis...");
					String enfant = resultat.getSecond();
					try {
						((Enfant)joueurs.get(enfant)).inscrire(1, (Gold)joueurs.get(parent));
					} catch (PlusDePlaceListeAmisException e) {
						System.out.println(e.getMessage());
						System.out.println("Faites de la place dans votre liste d'amis et recommencez.");
						resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
						return resultat;
					}
					System.out.println("Ajout r�ussi !");
					
					String choix = "";
					while(joueurs.get(choix) == null && !choix.equals("N")) {
						System.out.println("Votre enfant a-t-il un autre parent/tuteur inscrit ?");
						System.out.print("Si oui, entrez son pseudo. Sinon, entrez N : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
						switch(choix) {
						case "N":
							break;
						default:
							if(joueurs.containsKey(choix)) {
								System.out.println("Ajout r�ciproque dans la liste d'amis...");
								try {
									((Enfant)joueurs.get(enfant)).inscrire(2, (Gold)joueurs.get(choix));
								} catch (PlusDePlaceListeAmisException e) {
									System.out.println(e.getMessage());
									resultat.setBoth(Options.AFFICHAGE_PROFIL, enfant);
									return resultat;
								}
								System.out.println("Ajout r�ussi !");
							} else {
								System.out.println("Ce pseudo n'est pas dans la liste de joueurs inscrits. Veuillez r�essayer.");
							}
							break;
						}
					}
				}
				
			} else {
				System.out.println("Vous ne pouvez pas inscrire d'autres personnes.");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
			}
			return resultat;
		}
		
		/**
		 * Ajout interactive d'un ami dans sa liste d'amis.<br/>
		 * <br/>
		 * Il faut bien s�r que le pseudo existe et que l'ami puisse �tre ajout� (r�ciproquement).<br/>
		 * Un enfant ne peut pas retirer un parent/tuteur de sa liste d'amis (et inversement).<br/>
		 * Une liste d'ami peut �tre compl�te, et donc propager une exception.<br/>
		 * La demande est forc�ment accept�e par le futur ami.<br/>
		 * <br/>
		 * Hypoth�se : Le joueur connait le pseudo de son ami, donc aucune liste ou classement n'est affich� ici (une liste sera affich�e si disponible et si aucun de nos amis ne peut jouer avec nous � un jeu pr�cis).
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> ajouterAmi(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Veuillez indiquer le pseudo de votre futur ami : ");
			sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			switch(pseudo) {
			case "Q":
				System.out.println("Op�ration annul�e.");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				break;
			default:
				if(joueurs.containsKey(pseudo)) {
					boolean estAjoute;
					try {
						estAjoute = joueurs.get(joueurActif).ajouterAmi(joueurs.get(pseudo));
					} catch (PlusDePlaceListeAmisException e) {
						System.out.println(e.getMessage());
						System.out.println("Annulation...");
						resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
						return resultat;
					}
					if(estAjoute) {						
						boolean estAjouteReciproque;
						try {
							estAjouteReciproque = joueurs.get(pseudo).ajouterAmi(joueurs.get(joueurActif));
						} catch (PlusDePlaceListeAmisException e) {
							System.out.println(e.getMessage());
							System.out.println("Annulation...");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						}
						if(estAjouteReciproque) {
							System.out.println("Ami ajout� avec succ�s !");
						} else {
							System.out.println("Annulation...");
							joueurs.get(joueurActif).supprimerAmi(joueurs.get(pseudo));
							System.out.println("Op�ration annul�e.");
						}
					} else {
						System.out.println("Annulation...");
						System.out.println("Op�ration annul�e.");
					}
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				} else {
					System.out.println("Ce n'est pas le pseudo d'un joueur existant.");
					resultat.setBoth(Options.INVITER, joueurActif);
				}
				break;
			}
			return resultat;
		}
		
		/**
		 * Suppression d'un ami de sa liste d'amis.<br/>
		 * <br/>
		 * La suppression est r�ciproque et se fait sans attente d'une confirmation de la part de l'ami.
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> supprimerAmi(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Veuillez indiquer le pseudo de l'ami � supprimer : ");
			sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			switch(pseudo) {
			case "Q":
				System.out.println("Op�ration annul�e.");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				break;
			default:
				if(joueurs.get(joueurActif).getAmis().contains(pseudo)) {
					boolean estSupprime = joueurs.get(joueurActif).supprimerAmi(joueurs.get(pseudo));
					if(estSupprime) {
						boolean estSupprimeReciproque = joueurs.get(pseudo).supprimerAmi(joueurs.get(joueurActif));
						if(estSupprimeReciproque) {
							System.out.println("Ami supprim� avec succ�s !");												
						} else {
							System.out.println("Annulation...");
							try {
								joueurs.get(joueurActif).ajouterAmi(joueurs.get(pseudo));
							} catch (PlusDePlaceListeAmisException e) {
								System.out.println(e.getMessage());
								System.out.println("Annulation...");
								resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
								return resultat;
							}
							System.out.println("Op�ration annul�e.");	
						}
					} else {
						System.out.println("Annulation...");
						System.out.println("Op�ration annul�e.");				
					}
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				} else {
					System.out.println("Ce n'est pas le pseudo de l'un de vos amis.");
					resultat.setBoth(Options.SUPPRIMER, joueurActif);
				}
				break;
			}
			return resultat;
		}
		
		/**
		 * Offre interactive d'un jeu � un ami.<br/>
		 * <br/>
		 * - Affichage des jeux poss�d�s (fonction d�di�e)<br/>
		 * - Affichage des d�tails du jeu choisi (selon son rang)<br/>
		 * - Offre du jeu � un ami ? O/N<br/>
		 * - Offre du jeu (sauf si exception ou refus)
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param genres : le set des genres de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 * 
		 * @see projet.java.app.Menus.CollectionJeux
		 */
		public static Pair<Options, String> offrirJeu(Map<String, Joueur> joueurs, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			if(joueurs.get(joueurActif) instanceof Enfant) {
				System.out.println("Vous ne pouvez pas offrir de jeux !!");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			}
			resultat = Menus.CollectionJeux.afficherListeJeux(joueurs.get(joueurActif).getJeux(), plateformes, genres, joueurActif);
			if(!resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
				if(resultat.getFirst().equals(Options.COLLECTION)) resultat.setFirst(Options.CADEAU);
			} else {
				Pair<Integer, Pair<Options, String>> res = Menus.CollectionJeux.afficherDetailsJeu(joueurs.get(joueurActif).getJeux(), joueurActif);
				int indexJeu = res.getFirst();
				resultat = res.getSecond();
				if(resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
					// Offre du jeu � un ami
					System.out.println(Menus.CHOIX_OUI);
					System.out.println(Menus.CHOIX_NON);
					System.out.print("\nVoulez-vous offrir ce jeu ? : ");
					sc = new Scanner(System.in);
					String choix = sc.nextLine();
					while(!choix.equals("O") && !choix.equals("N")) {
						System.out.println("Veuillez choisir une option disponible.");
						System.out.print("\nVoulez-vous offrir ce jeu ? : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
					}
					switch(choix) {
					case "O":						
						Set<String> amisSansBots = new HashSet<>();
						try {
							amisSansBots = Menus.listeAmisSansBots(joueurs, joueurActif);
						} catch (JoueurNonTrouveException e) {
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						}
						
						for(String pseudo : amisSansBots) {
							System.out.println("- " + pseudo);
						}
						System.out.println(Menus.CHOIX_QUITTER);
						System.out.print("Veuillez entrer le pseudo du joueur auquel offrir le jeu : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
						
						while((joueurs.get(choix) == null || !joueurs.get(joueurActif).getAmis().contains(choix)) && !choix.equals("Q")) {
							if(joueurs.get(choix) == null) {
								System.out.println("Ce pseudo n'est pas valide. Veuillez entrer un pseudo existant.");
							} else {
								System.out.println("Ce joueur n'est pas un ami. Vous ne pouvez pas lui offrir de jeu.");
							}
							System.out.print("Veuillez entrer le pseudo du joueur auquel offrir le jeu : ");
							sc = new Scanner(System.in);
							choix = sc.nextLine();
						}
						
						if(choix.equals("Q")) {
							System.out.println("Op�ration annul�e.");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						}
						
						System.out.println("Offre du jeu � " + choix + "...");
						try {
							boolean estAjoute = joueurs.get(choix).ajouterJeu(Jeu.trouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
							if(estAjoute) {
								boolean estSupprime = joueurs.get(joueurActif).supprimerJeu(Jeu.trouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
								if(estSupprime) {
									System.out.println("Jeu offert � l'ami s�lectionn� !");								
								} else {
									System.out.println("Annulation...");
									joueurs.get(choix).supprimerJeu(Jeu.trouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
									System.out.println("Op�ration annul�e.");
								}
							} else {
								System.out.println("Annulation...");
								System.out.println("Op�ration annul�e.");
							}
						} catch (JeuNonTrouveException e) {
							System.out.println("Le rang du jeu entr� n'�tait pas le bon...");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						} catch (PlusDePlaceCollectionJeuxException e) {
							System.out.println(e.getMessage());
							System.out.println("Annulation...");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						}
						
						resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
						break;
					case "N":
						System.out.println("Retour au menu d'offre de jeu � un ami...");
						resultat.setBoth(Options.CADEAU, joueurActif);
						break;
					default:
						System.out.println("Retour au menu d'offre de jeu � un ami...");
						resultat.setBoth(Options.CADEAU, joueurActif);
						break;
					}
				}
			}
			return resultat;
		}
	}
	
	/**
	 * Tout ce qui concerne les changements de console d'un {@code Joueur}.<br/>
	 * (ajout d'une nouvelle console, suppression d'une console qui ne marche plus)<br/>
	 * Pour plus de facilit�s, ces 2 fonctionalit�s sont inclues dans une 3e fonction qui laisse l'utilisateur choisir entre les 2.
	 * 
	 * @author Nicolas Vrignaud
	 *
	 *@see projet.java.app.Menus
	 */
	public static class Consoles {
		/**
		 * Ajout d'une nouvelle console � celles qu'on poss�de parmi celles disponibles.<br/>
		 * <br/>
		 * - Affichage des consoles disponibles<br/>
		 * - Choix du num�ro associ�<br/>
		 * - Ajout � la liste des consoles<br/>
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		private static Pair<Options, String> ajouterNouvelleConsole(Map<String, Joueur> joueurs, SortedSet<String> plateformes, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			Object[] p = plateformes.toArray();
			int nombre = p.length + 1;
			for(int i = 0; i < p.length; i++) {
				System.out.println((i + 1) + ". " + p[i]);
			}
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Choisir une nouvelle console � ajouter (choisir le num�ro associ�) : ");
			sc = new Scanner(System.in);
			String choix = sc.nextLine();
			while(!choix.matches("\\d+")) {
				if(choix.equals("Q")) {
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					return resultat;
				}
				System.out.println("Veuillez choisir un nombre.");
				System.out.print("Console � ajouter : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			
			int indexConsole = Integer.parseInt(choix);
			while(indexConsole <= 0 || indexConsole > nombre) {
				System.out.println("Veuillez choisir un nombre entre 1 et " + nombre + ".");
				System.out.print("Console � ajouter : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
				if(choix.equals("Q")) {
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					return resultat;
				}
				indexConsole = Integer.parseInt(choix);
			}
			String console = (String) p[indexConsole - 1];
			
			boolean estAjoute = ((Humain) joueurs.get(joueurActif)).ajouterNouvelleConsole(console);
			if(estAjoute) {
				System.out.println("Nouvelle console ajout�e avec succ�s !");
			} else {
				System.out.println("Vous poss�dez d�j� cette console.");
				System.out.println("Annulation...");
				System.out.println("Op�ration annul�e.");
			}
			resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
			return resultat;
		}
		
		/**
		 * Suppression de l'une de ses consoles selon le num�ro associ�.<br/>
		 * (si on n'a pas de consoles, la fonction retourne directement une valeur)
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		private static Pair<Options, String> supprimerConsole(Map<String, Joueur> joueurs, SortedSet<String> plateformes, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			Set<String> m = ((Humain) joueurs.get(joueurActif)).getMachines();
			if(m.isEmpty()) {
				System.out.println("Vous n'avez aucune console, vous ne pouvez pas en supprimer.");
				System.out.println("Annulation...");
				System.out.println("Op�ration annul�e.");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			}
			int nombre = m.size() + 1;
			for(int i = 0; i < m.size(); i++) {
				System.out.println((i + 1) + ". " + m.toArray()[i]);
			}
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Choisir une console � supprimer (choisir le num�ro associ�) : ");
			sc = new Scanner(System.in);
			String choix = sc.nextLine();
			
			while(!choix.matches("\\d+")) {
				if(choix.equals("Q")) {
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					return resultat;
				}
				System.out.println("Veuillez choisir un nombre.");
				System.out.print("Console � supprimer : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			int indexConsole = Integer.parseInt(choix);
			
			while(indexConsole <= 0 || indexConsole > nombre) {
				System.out.println("Veuillez choisir un nombre entre 1 et " + nombre + ".");
				System.out.print("Console � supprimer : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
				if(choix.equals("Q")) {
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					return resultat;
				}
				indexConsole = Integer.parseInt(choix);
			}
			String console = (String) m.toArray()[indexConsole - 1];
			
			boolean estSupprime = ((Humain) joueurs.get(joueurActif)).supprimerConsole(console);
			if(estSupprime) {
				System.out.println("Console supprim�e avec succ�s !");
			} else {
				System.out.println("Vous ne poss�dez pas cette console.");
				System.out.println("Annulation...");
				System.out.println("Op�ration annul�e.");
			}
			resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
			return resultat;
		}
		
		/**
		 * Gestion interactif des consoles du joueur.<br/>
		 * (choix entre ajout / suppression)
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> gestionConsoles(Map<String, Joueur> joueurs, SortedSet<String> plateformes, String joueurActif) {
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Ajouter une console\n2. Supprimer une console");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			
			switch(choix) {
			case "1":
				resultat = ajouterNouvelleConsole(joueurs, plateformes, joueurActif);
				break;
			case "2":
				resultat = supprimerConsole(joueurs, plateformes, joueurActif);
				break;
			default:
				System.out.println("Veuillez choisir une des options ci-dessous.");
				resultat.setBoth(Options.GESTION_CONSOLE, joueurActif);
				break;
			}
			return resultat;
		}
	}
	
	/**
	 * Tout ce qui concerne les collections de {@code Jeu}.<br/>
	 * (affichage d'une liste de jeux et affichage des d�tails sur un jeu)
	 * 
	 * @author Nicolas Vrignaud
	 *
	 * @see projet.java.app.Menus
	 */
	public static class CollectionJeux {
		/**
		 * Affiche la liste des jeux en param�tres.<br/>
		 * (par plateforme ou par genre selon le choix de l'utilisateur)
		 * 
		 * @param jeux : collection de jeux
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param genres : le set des genres de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> afficherListeJeux(Collection<Jeu> jeux, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(jeux.isEmpty()) {
				System.out.println("Aucun jeux dans votre collection...");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			}
			
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Jeux class�s par machine\n2. Jeux class�s par genre\n3. Recherche du titre");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			Collection<Jeu> jeuxTries;
			Object[] arrayJeuxTries;
			
			switch(choix) {
			case "1":
				jeuxTries = Jeu.triParMachine(jeux, plateformes);
				arrayJeuxTries = jeuxTries.toArray();
				for(int i = 0; i < jeuxTries.size(); i++) {
					if(i == 0) {
						System.out.println(((Jeu) arrayJeuxTries[0]).getPlateforme());
					} else {
						if(!((Jeu) arrayJeuxTries[i]).getPlateforme().equals(((Jeu) arrayJeuxTries[i - 1]).getPlateforme())) {
							System.out.println(((Jeu) arrayJeuxTries[i]).getPlateforme());
						}
					}
					System.out.println(((Jeu) arrayJeuxTries[i]).affichageRapide());
				}
				break;
			case "2":
				jeuxTries = Jeu.triParGenre(jeux, genres);
				arrayJeuxTries = jeuxTries.toArray();
				for(int i = 0; i < jeuxTries.size(); i++) {
					if(i == 0) {
						System.out.println(((Jeu) arrayJeuxTries[0]).getGenre());
					} else {
						if(!((Jeu) arrayJeuxTries[i]).getGenre().equals(((Jeu) arrayJeuxTries[i - 1]).getGenre())) {
							System.out.println(((Jeu) arrayJeuxTries[i]).getGenre());
						}
					}
					System.out.println(((Jeu) arrayJeuxTries[i]).affichageRapide());
				}
				break;
			case "3":
				System.out.print("Mot cl� : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
				
				jeuxTries = Jeu.rechercheMotCleTitre(jeux, choix);
				
				arrayJeuxTries = jeuxTries.toArray();
				for(Jeu j : jeuxTries) {
					System.out.println(j.affichageRapide());
				}
				break;
			default:
				System.out.println("Veuillez choisir une des options ci-dessous.");
				resultat.setBoth(Options.COLLECTION, joueurActif);
				return resultat;
			}
			if(jeuxTries.isEmpty()) {
				System.out.println("Aucun jeu ne correspond...");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			}
			resultat.setBoth(Options.DETAILS_JEU_PERSO, joueurActif);
			return resultat;
		}
		
		/**
		 * Affichage des d�tails d'un {@code Jeu} d'une collection selon son rang.<br/>
		 * 
		 * @param jeux : collection de jeux
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Integer, Pair<Options, String>> afficherDetailsJeu(Collection<Jeu> jeux, String joueurActif) {
			if(jeux.isEmpty()) {
				System.out.println("Aucun jeux dans votre collection...");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return new Pair<>(0, resultat);
			}
			
			System.out.println("\n" + Menus.CHOIX_QUITTER);
			System.out.print("Choisissez le rang du jeu pour afficher ses d�tails : ");
			sc = new Scanner(System.in);
			String choix = sc.nextLine();
			
			while(!choix.matches("\\d+") && !choix.equals("Q")) {
				System.out.println("Veuillez choisir une option disponible.");
				System.out.println("\n" + Menus.CHOIX_QUITTER);
				System.out.print("Choisissez le rang du jeu pour afficher ses d�tails : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			
			int indexJeu;
			if(choix.matches("\\d+")) {
				indexJeu = Integer.parseInt(choix);
				try {
					Menus.afficherDetailsJeuSelonRang(jeux, indexJeu);
				} catch (JeuNonTrouveException e) {
					System.out.println("Le rang du jeu entr� n'�tait pas le bon...");
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					return new Pair<>(0, resultat);
				}
				resultat.setBoth(Options.DETAILS_JEU_PERSO, joueurActif);
			} else {
				indexJeu = 0;
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
			}
			return new Pair<>(indexJeu, resultat);
		}
	}
	
	/**
	 * Tout ce qui concerne l'achat d'un {@code Jeu}.<br/>
	 * (achat d'un jeu qui passe par les fonctions d'affichage de jeux)
	 * 
	 * @author Nicolas Vrignaud
	 *
	 * @see projet.java.app.Menus
	 * @see projet.java.app.Menus.CollectionJeux
	 */
	public static class Boutique {
		/**
		 * Achat interactif d'un jeu dans la boutique.<br/>
		 * <br/>
		 * - Affichage des jeux disponibles (fonction d�di�e)<br/>
		 * - Affichage des d�tails du jeu choisi (selon son rang)<br/>
		 * - Achat du jeu ? O/N<br/>
		 * - Achat (sauf si exception ou refus, plus de place dans la collection de jeux par exemple)
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param jeux : collection de jeux
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param genres : le set des genres de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 * 
		 * @see projet.java.app.Menus.CollectionJeux
		 */
		public static Pair<Options, String> acheterJeu(Map<String, Joueur> joueurs, List<Jeu> jeux, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			if(joueurs.get(joueurActif) instanceof Enfant) {
				System.out.println("Vous ne pouvez pas acheter de jeux !!");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			}
			resultat = Menus.CollectionJeux.afficherListeJeux(jeux, plateformes, genres, joueurActif);
			if(!resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
				if(resultat.getFirst().equals(Options.COLLECTION)) resultat.setFirst(Options.BOUTIQUE);
			} else {
				Pair<Integer, Pair<Options, String>> res = Menus.CollectionJeux.afficherDetailsJeu(jeux, joueurActif);
				int indexJeu = res.getFirst();
				resultat = res.getSecond();
				if(resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
					// Achat du jeu
					System.out.println(Menus.CHOIX_OUI);
					System.out.println(Menus.CHOIX_NON);
					System.out.print("\nVoulez-vous acheter ce jeu ? : ");
					sc = new Scanner(System.in);
					String choix = sc.nextLine();
					while(!choix.equals("O") && !choix.equals("N")) {
						System.out.println("Veuillez choisir une option disponible.");
						System.out.print("\nVoulez-vous acheter ce jeu ? : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
					}
					switch(choix) {
					case "O":
						System.out.println("Achat du jeu...");
						boolean estAchete;
						try {
							estAchete = joueurs.get(joueurActif).ajouterJeu(Jeu.trouverJeuSelonRang(jeux, indexJeu));
						} catch (JeuNonTrouveException e) {
							System.out.println("Le rang du jeu entr� n'�tait pas le bon...");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						} catch (PlusDePlaceCollectionJeuxException e) {
							System.out.println(e.getMessage());
							System.out.println("Annulation...");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						}
						if(estAchete) {
							System.out.println("Jeu achet� et ajout� � votre liste !");
						} else {
							System.out.println("Annulation...");
							System.out.println("Op�ration annul�e.");
						}
						resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
						break;
					case "N":
						System.out.println("Retour � la boutique...");
						resultat.setBoth(Options.BOUTIQUE, joueurActif);
						break;
					default:
						System.out.println("Retour � la boutique...");
						resultat.setBoth(Options.BOUTIQUE, joueurActif);
						break;
					}
				}
			}
			return resultat;
		}
		
	}	
	
	/**
	 * Tout ce qui concerne les amis d'un {@code Joueur}.<br/>
	 * (affichage de sa liste d'amis, affichage des d�tails publiques sur un joueur)
	 * 
	 * @author Nicolas Vrignaud
	 *
	 * @see projet.java.app.Menus
	 */
	public static class ListeAmis {
		/**
		 * Affichage de la liste d'amis d'un joueur (seulement le pseudo).
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> afficherListeAmis(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			if(joueurs.get(joueurActif).getAmis().isEmpty()) {
				System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			}
			for(String pseudo : joueurs.get(joueurActif).getAmis()) {
				System.out.print("- " + pseudo);
				if(joueurs.get(joueurActif) instanceof Enfant) {
					if(((Enfant)joueurs.get(joueurActif)).getPseudosParents()[0].equals(pseudo) || ((Enfant)joueurs.get(joueurActif)).getPseudosParents()[1].equals(pseudo) ) {
						System.out.print(" (parent/tuteur)");
					}
				}
				System.out.print("\n");
			}
			System.out.print("\n");
			resultat.setBoth(Options.DETAILS_PUBLIQUES_AMIS, joueurActif);
			return resultat;
		}
		
		/**
		 * Affichage des d�tails publiques de l'un de nos amis.<br/>
		 * (recherche par pseudo)
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> afficherDetailsPubliquesAmis(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			System.out.println("\n" + Menus.CHOIX_QUITTER);
			System.out.print("Choisissez le pseudo du jeu pour afficher ses d�tails publiques : ");
			sc = new Scanner(System.in);
			String choix = sc.nextLine();
			
			while((joueurs.get(choix) == null || !joueurs.get(joueurActif).getAmis().contains(choix)) && !choix.equals("Q")) {
				if(joueurs.get(choix) == null) {								
					System.out.println("Ce pseudo n'est pas valide. Veuillez entrer un pseudo existant.");
				} else {
					System.out.println("Ce joueur n'est pas un ami.");
				}
				System.out.print("Choisissez le pseudo du jeu pour afficher ses d�tails publiques : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			
			if(choix.equals("Q")) {
				System.out.println("Op�ration annul�e.");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			}
			
			System.out.println(Menus.SEPARATEUR);
			System.out.println(joueurs.get(choix).profilPublic());
			System.out.println(Menus.SEPARATEUR);
			
			resultat.setBoth(Options.DETAILS_PUBLIQUES_AMIS, joueurActif);
			return resultat;
		}
	}
	
	/**
	 * Tout ce qui concerne une partie multijoueurs (2 joueurs).<br/>
	 * (jouer � une partie multijoueurs)
	 * 
	 * @author Nicolas Vrignaud
	 *
	 * @see projet.java.app.Menus
	 */
	public static class PartieMulti {
		
		/**
		 * Permet � un utilisateur de jouer en mode multijoueurs (2 joueurs) avec un ami ou un bot, au choix.<br/>
		 * Si aucun ami ou aucun bot ne peut jouer � ce jeu, on affiche la liste des 10 (maximum) joueurs pouvant jouer avec nous � ce jeu et que l'on peut inviter pour jouer.<br/>
		 * Si la partie peut �tre jou�e (suppose qu'on ai encore des parties quotidiennes disponibles), les r�sultats sont affich�s et enregistr�s dans chacune des listes de parties des joueurs (sauf s'il s'agit d'un bot).<br/>
		 * <br/>
		 * Hypoth�se : On suppose qu'un bot n'a pas besoin d'�tre ami avec le joueur pour jouer.<br/>
		 * (ce n'est pas un vrai joueur et cela rendrait la gestion des amis p�nible pour les joueurs ayant un nombre d'amis limit�s)
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param jeux : collection de jeux
		 * @param plateformes : le set des plateformes de jeu disponibles
		 * @param genres : le set des genres de jeu disponibles
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		public static Pair<Options, String> jouer(Map<String, Joueur> joueurs, List<Jeu> jeux, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			// Demander le jeu auquel le joueur veut jouer parmi ses jeux (s'il a un jeu)
			resultat = Menus.CollectionJeux.afficherListeJeux(joueurs.get(joueurActif).getJeux(), plateformes, genres, joueurActif);
			if(!resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
				if(resultat.getFirst().equals(Options.COLLECTION)) resultat.setFirst(Options.JOUER);
			} else {
				Pair<Integer, Pair<Options, String>> res = Menus.CollectionJeux.afficherDetailsJeu(joueurs.get(joueurActif).getJeux(), joueurActif);
				int indexJeu = res.getFirst();
				resultat = res.getSecond();
				if(resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
					// S�lection du jeu auquel jouer
					System.out.println(Menus.CHOIX_OUI);
					System.out.println(Menus.CHOIX_NON);
					System.out.print("\nVoulez-vous jouer � ce jeu ? : ");
					sc = new Scanner(System.in);
					String choix = sc.nextLine();
					
					while(!choix.equals("O") && !choix.equals("N")) {
						System.out.println("Veuillez choisir une option disponible.");
						System.out.print("\nVoulez-vous jouer � ce jeu ? : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
					}
					switch(choix) {
					case "O":
						// Choix du mode de jeu (avec ami ou avec bot)
						System.out.print(Menus.SEPARATEUR);
						System.out.print("1. Jouer avec un ami\n2. Jouer avec un bot");
						System.out.print(Menus.SEPARATEUR);
						System.out.print("Votre choix : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
						
						switch(choix) {
						case "1":
							// Afficher la liste des amis pouvant jouer s'il y en a
							Set<String> amisDispos;
							try {
								amisDispos = Menus.listeAmisPouvantJouer(joueurs, Jeu.trouverJeuSelonRang(jeux, indexJeu), joueurActif);
								if(!amisDispos.isEmpty()) {
									for(String pseudo : amisDispos) {
										System.out.println("- " + pseudo);
									}
									// Choix de l'ami avec qui jouer
									System.out.println(Menus.CHOIX_QUITTER);
									System.out.print("Veuillez entrer le pseudo du joueur avec qui jouer : ");
									sc = new Scanner(System.in);
									choix = sc.nextLine();
									
									while((joueurs.get(choix) == null || !joueurs.get(joueurActif).getAmis().contains(choix)) && !choix.equals("Q")) {
										if(joueurs.get(choix) == null) {
											System.out.println("Ce pseudo n'est pas valide. Veuillez entrer un pseudo existant.");
										} else {
											System.out.println("Ce joueur n'est pas un ami. Vous ne pouvez pas jouer avec lui.");
										}
										System.out.print("Veuillez entrer le pseudo du joueur auquel offrir le jeu : ");
										sc = new Scanner(System.in);
										choix = sc.nextLine();
									}
									
									if(choix.equals("Q")) {
										System.out.println("Op�ration annul�e.");
										resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
										return resultat;
									}
									
									// Partie avec cet ami et enregistrement des r�sultats
									PartieMultijoueurs pm = new PartieMultijoueurs(Jeu.trouverJeuSelonRang(jeux, indexJeu), joueurs.get(joueurActif), joueurs.get(choix));
									boolean partieJouee = pm.resultatsDePartie();
									if(partieJouee) {
										System.out.println("Partie en cours...");
										System.out.println("La partie est termin�e!");
										System.out.print(Menus.SEPARATEUR);
										System.out.println("R�sultats: ");
										System.out.println("    Gagnant : "  + pm.getPseudoGagnant());
										System.out.println("    Perdant : "  + pm.getPseudoPerdant());
										System.out.println("Retrouvez ces r�sultats dans vos statistiques personnelles.");
										System.out.print(Menus.SEPARATEUR);
										System.out.println("Retour � votre profil...");
										resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
										return resultat;
									}
								} else {
									System.out.println("Aucun ami ne peut jouer avec vous � ce jeu...");
								}
							} catch (JoueurNonTrouveException e) {
								System.out.println("Il n'y a pas de joueurs qui peuvent jouer � ce jeu dans votre liste d'amis...");
							} catch (Joueur2NonHumainException e) {
								System.out.println(e.getMessage());
							} catch (PlusDePlaceNombreDePartiesException e) {
								System.out.println(e.getMessage());
								System.out.println("Annulation...");
								resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
								return resultat;
							} catch (JeuNonTrouveException e) {}
							break;
						case "2":
							// Partie avec le bot attribu� et enregistrement des r�sultats
							String nomBot;
							PartieMultijoueurs pm;
							try {
								nomBot = Menus.gestionPartieAvecBots(joueurs, Jeu.trouverJeuSelonRang(jeux, indexJeu));
								pm = new PartieMultijoueurs(Jeu.trouverJeuSelonRang(jeux, indexJeu), joueurs.get(joueurActif), joueurs.get(nomBot));
							} catch (JeuNonTrouveException e1) {
								System.out.println("Erreur dans le rang du jeu.");
								resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
								return resultat;
							}
							boolean partieJouee;
							try {
								partieJouee = pm.resultatsDePartie();
							} catch (PlusDePlaceNombreDePartiesException e) {
								System.out.println(e.getMessage());
								System.out.println("Annulation...");
								resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
								return resultat;
							}
							if(partieJouee) {
								System.out.println("Partie en cours...");
								System.out.println("La partie est termin�e!");
								System.out.print(Menus.SEPARATEUR);
								System.out.println("R�sultats: ");
								System.out.println("    Gagnant : "  + pm.getPseudoGagnant());
								System.out.println("    Perdant : "  + pm.getPseudoPerdant());
								System.out.println("Retrouvez ces r�sultats dans vos statistiques personnelles.");
								System.out.print(Menus.SEPARATEUR);
								System.out.println("Retour � votre profil...");
								resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
								return resultat;
							}
							
							// Suppression des �ventuels bots pour respecter les conditions du cahier des charges (voir la fonction en question)
							Menus.gestionBots(joueurs);
							
							break;
						default:
							System.out.println("Veuillez choisir une des options ci-dessous.");
							resultat.setBoth(Options.JOUER, joueurActif);
							return resultat;
						}
						
						
						// Pas d'ami pouvant jouer / Pas de module d'IA disponible : on propose un choix et on affiche les (10 MAX) joueurs les plus appropri�s � inviter pour jouer
						try {
							Set<String> amisAptes = Menus.listeJoueursAdaptesPourJouer(joueurs, Jeu.trouverJeuSelonRang(jeux, indexJeu), joueurActif, 10);
							System.out.println("Invitez un de ces joueurs en tant qu'ami pour pouvoir jouer avec un vrai joueur.\nSinon, vous pouvez toujours jouer avec un bot.");
							for(String a : amisAptes) {
								System.out.println("- " + joueurs.get(a).getPseudo());
							}
						} catch (JoueurNonTrouveException e) {
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						} catch (JeuNonTrouveException e) {
							System.out.println("Erreur dans le rang du jeu");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						}
						break;
					case "N":
						System.out.println("Retour au menu de choix du jeu...");
						resultat.setBoth(Options.JOUER, joueurActif);
						break;
					default:
						System.out.println("Retour au menu de choix du jeu...");
						resultat.setBoth(Options.JOUER, joueurActif);
						break;
					}
				}
			}
			resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
			return resultat;
		}
	}
	
	/**
	 * Tout ce qui concerne les statistiques.<br/>
	 * (affichage des statistiques d'un {@code Joueur}, affichage du classment des joueurs et des jeux)
	 * 
	 * @author Nicolas Vrignaud
	 *
	 * @see projet.java.app.Menus
	 */
	public static class Statistiques {
		/**
		 * Permet � un joueur de voir ses statistiques personnelles.<br/>
		 * <br/>
		 * - Choix entre ses parties jou�es aujourd'hui, ses parties d'un jour � choisir, et sa proportion de victoires/d�faites.<br/>
		 * - Affichage de la liste des parties avec les d�tails pour chacune (si partie il y a).<br/>
		 * - Respectivement, affichage de la proportion de victoires/d�faites et du pourcentage de victoires.<br/>
		 * - Retour direct au profil du {@code Joueur} apr�s affichage.
		 * 
		 * @param joueurs : la map des joueurs de l'application avec pour cl� leur pseudo
		 * @param joueurActif : le pseudo du joueur actif au moment de l'appel de la fonction
		 * 
		 * @return l'objet {@code Pair<Options, String>} � r�cup�rer pour le bon fonctionnement dans la classe principale
		 */
		@SuppressWarnings("deprecation")
		public static Pair<Options, String> affichageStatistiquesJoueur(Map<String, Joueur> joueurs, String joueurActif) {
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Partie du jour\n2. Partie d'un jour � choisir\n3. Proportion victoires/d�faites");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			
			switch(choix) {
			case "1":
				Map<String, PartieMultijoueurs> parties = new HashMap<>();
				Date now;
				try {
					now = new Date();
					int year = 1900 + now.getYear();
					int month = 1 + now.getMonth();
					int day = now.getDate();
					parties = ((Humain)joueurs.get(joueurActif)).getPartiesJour(year + "/" + month + "/" + day);
				} catch (PartieNonTrouveeException e) {
					System.out.println("Aucune partie n'a �t� jou�e ce jour-ci...");
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					resultat.setSecond(joueurActif);
					return resultat;
				}
				
				System.out.print(Menus.SEPARATEUR);
				System.out.println("Parties jou�es le " + Menus.DATE_FORMAT.format(now) + " :");
				for(PartieMultijoueurs pm : parties.values()) {
					System.out.println("- " + pm.toString());
				}
				System.out.print(Menus.SEPARATEUR);
				break;
			case "2":
				System.out.print("Jour pour lequel afficher les parties (AAAA/MM/JJ): ");
				sc = new Scanner(System.in);
				String jour = sc.nextLine();
				while(!jour.matches("^\\d{4}\\/((0[13578]|1[02])\\/([0-2][1-9]|3[01])|(0[469]|11)\\/([0-2][1-9]|30)|02\\/[0-2][1-9])$")) {
					System.out.println("Ce format de date n'est pas valide. Veuillez entrer votre date de naissance correctement.");
					System.out.print("Jour pour lequel afficher les parties (AAAA/MM/JJ): ");
					sc = new Scanner(System.in);
					jour = sc.nextLine();
				}
				
				Date dateJour = new Date();
				try {
					dateJour = new SimpleDateFormat("yyyy/MM/dd").parse(jour);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				parties = new HashMap<>();
				try {
					parties = ((Humain)joueurs.get(joueurActif)).getPartiesJour(jour);
				} catch (PartieNonTrouveeException e) {
					System.out.println("Aucune partie n'a �t� jou�e ce jour-ci...");
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					return resultat;
				}
				
				System.out.print(Menus.SEPARATEUR);
				System.out.println("Parties jou�es le " + Menus.DATE_FORMAT.format(dateJour) + " :");
				for(PartieMultijoueurs pm : parties.values()) {
					System.out.println("- " + pm.toString());
				}
				System.out.print(Menus.SEPARATEUR);
				break;
			case "3":
				Pair<Integer, Integer> vd = ((Humain)joueurs.get(joueurActif)).proportionVictoiresDefaites();
				double pv = ((Humain)joueurs.get(joueurActif)).pourcentageDeVictoire();
				System.out.print(Menus.SEPARATEUR);
				System.out.println("Proportion victoires/d�faites : " + vd.getFirst() + "/" + vd.getSecond());
				System.out.println("Pourcentage de victoires : " + pv + " %");
				System.out.print(Menus.SEPARATEUR);
				break;
			default:
				System.out.println("Veuillez choisir une des options ci-dessous.");
				resultat.setBoth(Options.STATISTIQUES, joueurActif);
				break;
			}
			resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
			return resultat;
		}
		
		public static Pair<Options, String> affichageStatistiquesGenerales(Map<String, Joueur> joueurs, List<Jeu> jeux, String joueurActif) {
			// Joueurs ayant le plus jou� aujourd'hui
			// Jeux les plus jou�s
			// Correspondrait � l'option CLASSEMENT et serait affich�e dans le menu principal
			
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Classement des joueurs ayant le plus jou� aujourd'hui\n2. Classement des jeux les plus jou�s\n");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			
			switch(choix) {
			case "1":
				Map<String, Integer> classementJoueurs;
				try {
					classementJoueurs = Menus.classementJoueursDuJour(joueurs, 50);
				} catch (JoueurNonTrouveException e) {
					System.out.println("Pas de donn�es � afficher...");
					resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
					return resultat;
				}
				int c = 1;
				for(String pseudo : classementJoueurs.keySet()) {
					System.out.println(c + ". " + pseudo + ", nombre de parties jou�es : " + classementJoueurs.get(pseudo));
					c++;
				}
				break;
			case "2":
				//Map<Integer, Integer> classementJeux;
				System.out.println("Pas de donn�es � afficher ici pour l'instant...");
				resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
				return resultat;
			default:
				System.out.println("Veuillez choisir une des options ci-dessous.");
				resultat.setBoth(Options.CLASSEMENT, joueurActif);
				break;
			}
			resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
			return resultat;
		}
	}
}
