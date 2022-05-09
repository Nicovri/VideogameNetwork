package projet.java.app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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
 * Classe secondaire de l'application
 * @author Nicolas Vrignaud
 * 
 * Les sous-classes sont directement li�es � la class projet.java.app.App et repr�sentent les fonctionnalit�s disponibles.
 *
 */
public class Menus {
	private static Pair<Options, String> resultat = new Pair<>();
	
	private final static String SEPARATEUR = "\n".concat("=====".repeat(15)).concat("\n");
	private final static DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRENCH);
	
	private final static String CHOIX_QUITTER = "(Pour quitter, entrez Q)";
	private final static String CHOIX_RECOMMENCER = "(Pour recommencer, entrez R)";
	private final static String CHOIX_OUI = "(Pour valider, entrez O)";
	private final static String CHOIX_NON = "(Pour refuser, entrez N)";
	
	private static Jeu trouverJeuSelonRang(Collection<Jeu> jeux, int rang) throws JeuNonTrouveException {
		for(Jeu jeu : jeux) {
			if(jeu.getRang() == rang) {
				return jeu;
			}
		}
		throw new JeuNonTrouveException();
	}
	
	private static void afficherDetailsJeuSelonRang(Collection<Jeu> jeux, int rang) throws JeuNonTrouveException {
		Jeu jeu = trouverJeuSelonRang(jeux, rang);
		System.out.println(Menus.SEPARATEUR);
		System.out.println(jeu.toString());
		System.out.println(Menus.SEPARATEUR);
	}
	
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
	
	private static Set<String> listeBots(Map<String, Joueur> joueurs) {
		Set<String> bots = new HashSet<>();
		for(String pseudo : joueurs.keySet()) {
			if(joueurs.get(pseudo) instanceof Bot) {
				bots.add(pseudo);
			}
		}
		return bots;
	}
	
	// Au plus 1 bot par jeu (TODO: ind�pendemment de la console)
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
	
	// Hypoth�se : Un bot peut jouer � plusieurs parties du m�me jeu en m�me temps
	// Mais pas 2 parties de jeux diff�rents en m�me temps
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
	
	// 10 joueurs au maximum retourn�s
	// Joueurs qui poss�dent le plus de jeux en commun (et ayant la possibilit� de jouer, c�d la console appropri�e � leur jeu)
	// Si on ne peut pas d�partager, joueurs qui poss�dent de la place dans leur liste d'amis (avec les conditions des Enfants et des Adultes)
	private static Set<String> listeJoueursAdaptesPourJouer(Map<String, Joueur> joueurs, Jeu jeu, String joueurActif) throws JoueurNonTrouveException {
		Set<String> joueursAdaptes = new HashSet<>();
		int c = joueurs.size();
		if(joueurs.isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
			throw new JoueurNonTrouveException();
		}
		while(joueursAdaptes.size() < 10 || c == 0) {
			for(Joueur j : joueurs.values()) {
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
				c--;
			}
		}
		if(joueursAdaptes.isEmpty()) {
			System.out.println("Aucun joueur ne correspond et ne peut jouer avec vous � ce jeu pour l'instant...");
			throw new JoueurNonTrouveException();
		}
		return joueursAdaptes;
	}
	
	public static class Profil {
		
		private static Pair<Options, String> creationCompte(Map<String, Joueur> joueurs, SortedSet<String> plateformes, boolean estEnfant) {
			System.out.print("Pseudo: ");
			Scanner sc = new Scanner(System.in);
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
			
			System.out.print("Email: ");
			sc = new Scanner(System.in);
			String email = sc.nextLine();
			while(!email.matches("[\\w'-_]+@[\\w'-_]+.(com|fr|org)")) {
				System.out.println("Cette adresse mail n'est pas correcte. Veuillez entrer une adresse de la forme mot1@mot2.(com/fr/org)");
				System.out.print("Email: ");
				sc = new Scanner(System.in);
				email = sc.nextLine();
			}
			
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
			
			System.out.print(Menus.SEPARATEUR);
			System.out.println("R�capitulatif...");
			System.out.println("Pseudo: " + pseudo);
			System.out.println("Email: " + email);
			System.out.println(Menus.DATE_FORMAT.format(dateNaissance));
			System.out.println("Console: " + console);
			System.out.println(Menus.SEPARATEUR);
			
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
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						resultat.setSecond(pseudo);
						System.out.println("Affichage de votre profil Standard...");						
					} else {
						System.out.println("Erreur dans la cr�ation du compte. Veuillez r�essayer.");
						resultat.setFirst(Options.ACCUEIL);
						resultat.setSecond("");
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
						// V�rifier si joueur bien ajout� (et s'il n'est pas en double (exception ?))
					}
					tailleApres = joueurs.size();
					if(tailleApres == tailleAvant + 1) {						
						System.out.println("Compte cr�� avec succ�s!");
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						resultat.setSecond(pseudo);
						System.out.println("Affichage de votre profil Gold...");
					} else {
						System.out.println("Erreur dans la cr�ation du compte. Veuillez r�essayer.");
						resultat.setFirst(Options.ACCUEIL);
						resultat.setSecond("");
					}
					break;
				case "R":
					System.out.print(Menus.SEPARATEUR);
					resultat.setFirst(Options.ACCUEIL);
					resultat.setSecond("");
					break;
				case "Q":
					resultat.setFirst(Options.QUITTER);
					resultat.setSecond("");
					break;
				default:
					System.out.println("Ce statut n'est pas disponible. Veuillez choisir un statut existant.");
					break;
				}
			}
			return resultat;
		}
		
		// Connexion (sans gestion de mot de passe pour l'instant)
		private static Pair<Options, String> connexion(Map<String, Joueur> joueurs, SortedSet<String> plateformes) {
			System.out.print("Votre pseudo: ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			if(!joueurs.isEmpty()) {
				while(joueurs.get(pseudo) == null) {
					// Q et R ne peuvent pas �tre un pseudo car il doit contenir au moins 3 caract�res
					System.out.println(Menus.CHOIX_QUITTER);
					System.out.println(Menus.CHOIX_RECOMMENCER);
			        System.out.println("Le pseudo est inexistant. Veuillez entrer votre pseudo.");
			        System.out.print("Pseudo: ");
					sc = new Scanner(System.in);
					pseudo = sc.nextLine();
				}
				switch(pseudo) {
				case "R":
					resultat.setFirst(Options.ACCUEIL);
					resultat.setSecond("");
					break;
				case "Q":
					resultat.setFirst(Options.QUITTER);
					resultat.setSecond("");
					break;
				default:
					resultat.setFirst(Options.AFFICHAGE_PROFIL);
					resultat.setSecond(pseudo);
					break;
				}
			} else {
				System.out.println("\nAucun joueurs inscrits. Retour � l'�cran d'accueil...");
				resultat.setFirst(Options.ACCUEIL);
				resultat.setSecond("");
			}
			return resultat;
		}
		
		public static Pair<Options, String> accueilJoueur(Map<String, Joueur> joueurs, SortedSet<String> plateformes) {
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Cr�ation de compte\n2. Connexion\n3. Quitter");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			Scanner sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			
			switch(choix) {
			case "1":
				resultat = creationCompte(joueurs, plateformes, false);
				break;
			case "2":
				resultat = connexion(joueurs, plateformes);
				break;
			case "3":
				resultat.setFirst(Options.QUITTER);
				resultat.setSecond("");
				break;
			default:
				System.out.println("Veuillez choisir une des options ci-dessous.");
				resultat.setFirst(Options.ACCUEIL);
				resultat.setSecond("");
				break;
			}
			return resultat;
		}
		
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
					Scanner sc = new Scanner(System.in);
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

		public static Pair<Options, String> deconnexion(String joueurActif) {
			System.out.println("Etes-vous s�r(e) de vouloir vous d�connecter ?");
			System.out.println(Menus.CHOIX_OUI);
			System.out.println(Menus.CHOIX_NON);
			System.out.print("Votre choix : ");
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			switch(choix) {
			case "O":
				resultat.setFirst(Options.ACCUEIL);
				resultat.setSecond("");
				break;
			case "N":
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				break;
			default:
				resultat.setFirst(Options.DECONNEXION);
				resultat.setSecond(joueurActif);
				break;
			}
			return resultat;
		}
	}
	
	public static class Interactions {
		
		// On suppose qu'apr�s inscription, sa liste de parents/tuteurs ne peut pas �tre modif�e (elle contient donc soit 1 soit 2 Joueurs)
		// On suppose (pour l'instant) qu'un parent ne peut plus mentionner son enfant s'il s'inscrit apr�s lui (cas du 2e parent)
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
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						resultat.setSecond(joueurActif);
						return resultat;
					}
					System.out.println("Ajout r�ussi !");
					
					String choix = "";
					while(joueurs.get(choix) == null && !choix.equals("N")) {
						System.out.println("Votre enfant a-t-il un autre parent/tuteur inscrit ?");
						System.out.print("Si oui, entrez son pseudo. Sinon, entrez N : ");
						Scanner sc = new Scanner(System.in);
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
									resultat.setFirst(Options.AFFICHAGE_PROFIL);
									resultat.setSecond(enfant);
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
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
			}
			return resultat;
		}
		
		public static Pair<Options, String> ajouterAmi(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Veuillez indiquer le pseudo de votre futur ami : ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			switch(pseudo) {
			case "Q":
				System.out.println("Op�ration annul�e.");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
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
					resultat.setFirst(Options.AFFICHAGE_PROFIL);
					resultat.setSecond(joueurActif);
				} else {
					System.out.println("Ce n'est pas le pseudo d'un joueur existant.");
					resultat.setFirst(Options.INVITER);
					resultat.setSecond(joueurActif);
				}
				break;
			}
			return resultat;
		}
		
		public static Pair<Options, String> supprimerAmi(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Veuillez indiquer le pseudo de l'ami � supprimer : ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			switch(pseudo) {
			case "Q":
				System.out.println("Op�ration annul�e.");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
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
					resultat.setFirst(Options.AFFICHAGE_PROFIL);
					resultat.setSecond(joueurActif);
				} else {
					System.out.println("Ce n'est pas le pseudo de l'un de vos amis.");
					resultat.setFirst(Options.SUPPRIMER);
					resultat.setSecond(joueurActif);
				}
				break;
			}
			return resultat;
		}
		
		public static Pair<Options, String> offrirJeu(Map<String, Joueur> joueurs, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			if(joueurs.get(joueurActif) instanceof Enfant) {
				System.out.println("Vous ne pouvez pas offrir de jeux !!");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
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
					Scanner sc = new Scanner(System.in);
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
							resultat.setFirst(Options.AFFICHAGE_PROFIL);
							resultat.setSecond(joueurActif);
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
							resultat.setFirst(Options.AFFICHAGE_PROFIL);
							resultat.setSecond(joueurActif);
							return resultat;
						}
						
						System.out.println("Offre du jeu � " + choix + "...");
						try {
							boolean estAjoute = joueurs.get(choix).ajouterJeu(Menus.trouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
							if(estAjoute) {
								boolean estSupprime = joueurs.get(joueurActif).supprimerJeu(Menus.trouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
								if(estSupprime) {
									System.out.println("Jeu offert � l'ami s�lectionn� !");								
								} else {
									System.out.println("Annulation...");
									joueurs.get(choix).supprimerJeu(Menus.trouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
									System.out.println("Op�ration annul�e.");
								}
							} else {
								System.out.println("Annulation...");
								System.out.println("Op�ration annul�e.");
							}
						} catch (JeuNonTrouveException e) {
							System.out.println("Le rang du jeu entr� n'�tait pas le bon...");
							resultat.setFirst(Options.AFFICHAGE_PROFIL);
							resultat.setSecond(joueurActif);
							return resultat;
						} catch (PlusDePlaceCollectionJeuxException e) {
							System.out.println(e.getMessage());
							System.out.println("Annulation...");
							resultat.setBoth(Options.AFFICHAGE_PROFIL, joueurActif);
							return resultat;
						}
						
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						resultat.setSecond(joueurActif);
						break;
					case "N":
						System.out.println("Retour au menu d'offre de jeu � un ami...");
						resultat.setFirst(Options.CADEAU);
						resultat.setSecond(joueurActif);
						break;
					default:
						System.out.println("Retour au menu d'offre de jeu � un ami...");
						resultat.setFirst(Options.CADEAU);
						resultat.setSecond(joueurActif);
						break;
					}
				}
			}
			return resultat;
		}
	}
	
	public static class Consoles {
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
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			while(!choix.matches("\\d+")) {
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
			resultat.setFirst(Options.AFFICHAGE_PROFIL);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
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
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				return resultat;
			}
			int nombre = m.size() + 1;
			for(int i = 0; i < m.size(); i++) {
				System.out.println((i + 1) + ". " + m.toArray()[i]);
			}
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Choisir une console � supprimer (choisir le num�ro associ�) : ");
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			while(!choix.matches("\\d+")) {
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
			resultat.setFirst(Options.AFFICHAGE_PROFIL);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
		public static Pair<Options, String> gestionConsoles(Map<String, Joueur> joueurs, SortedSet<String> plateformes, String joueurActif) {
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Ajouter une console\n2. Supprimer une console");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			Scanner sc = new Scanner(System.in);
			
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
				resultat.setFirst(Options.GESTION_CONSOLE);
				resultat.setSecond(joueurActif);
				break;
			}
			return resultat;
		}
	}
	
	public static class CollectionJeux {
		public static Pair<Options, String> afficherListeJeux(Collection<Jeu> jeux, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(jeux.isEmpty()) {
				System.out.println("Aucun jeux dans votre collection...");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				return resultat;
			}
			
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Jeux class�s par machine\n2. Jeux class�s par genre");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			Scanner sc = new Scanner(System.in);
			
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
			default:
				System.out.println("Veuillez choisir une des options ci-dessous.");
				resultat.setFirst(Options.COLLECTION);
				resultat.setSecond(joueurActif);
				return resultat;
			}
			resultat.setFirst(Options.DETAILS_JEU_PERSO);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
		public static Pair<Integer, Pair<Options, String>> afficherDetailsJeu(Collection<Jeu> jeux, String joueurActif) {
			if(jeux.isEmpty()) {
				System.out.println("Aucun jeux dans votre collection...");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				return new Pair<>(0, resultat);
			}
			
			System.out.println("\n" + Menus.CHOIX_QUITTER);
			System.out.print("Choisissez le rang du jeu pour afficher ses d�tails : ");
			Scanner sc = new Scanner(System.in);
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
					resultat.setFirst(Options.AFFICHAGE_PROFIL);
					resultat.setSecond(joueurActif);
					return new Pair<>(0, resultat);
				}
				resultat.setFirst(Options.DETAILS_JEU_PERSO);
				resultat.setSecond(joueurActif);
			} else {
				indexJeu = 0;
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
			}
			return new Pair<>(indexJeu, resultat);
		}
	}
	
	public static class Boutique {
		public static Pair<Options, String> acheterJeu(Map<String, Joueur> joueurs, List<Jeu> jeux, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			if(joueurs.get(joueurActif) instanceof Enfant) {
				System.out.println("Vous ne pouvez pas acheter de jeux !!");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
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
					Scanner sc = new Scanner(System.in);
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
							estAchete = joueurs.get(joueurActif).ajouterJeu(Menus.trouverJeuSelonRang(jeux, indexJeu));
						} catch (JeuNonTrouveException e) {
							System.out.println("Le rang du jeu entr� n'�tait pas le bon...");
							resultat.setFirst(Options.AFFICHAGE_PROFIL);
							resultat.setSecond(joueurActif);
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
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						break;
					case "N":
						System.out.println("Retour � la boutique...");
						resultat.setFirst(Options.BOUTIQUE);
						resultat.setSecond(joueurActif);
						break;
					default:
						System.out.println("Retour � la boutique...");
						resultat.setFirst(Options.BOUTIQUE);
						resultat.setSecond(joueurActif);
						break;
					}
				}
			}
			return resultat;
		}
		
	}	
	
	public static class ListeAmis {
		public static Pair<Options, String> afficherListeAmis(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			if(joueurs.get(joueurActif).getAmis().isEmpty()) {
				System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				return resultat;
			}
			for(String pseudo : joueurs.get(joueurActif).getAmis()) {
				System.out.println("- " + pseudo);
			}
			System.out.print("\n");
			resultat.setFirst(Options.DETAILS_PUBLIQUES_AMIS);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
		public static Pair<Options, String> afficherDetailsPubliquesAmis(Map<String, Joueur> joueurs, String joueurActif) {
			if(joueurs.isEmpty()) {
				resultat.setBoth(Options.ACCUEIL, "");
				return resultat;
			}
			
			System.out.println("\n" + Menus.CHOIX_QUITTER);
			System.out.print("Choisissez le pseudo du jeu pour afficher ses d�tails publiques : ");
			Scanner sc = new Scanner(System.in);
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
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				return resultat;
			}
			
			System.out.println(Menus.SEPARATEUR);
			System.out.println(joueurs.get(choix).profilPublic());
			System.out.println(Menus.SEPARATEUR);
			
			resultat.setFirst(Options.DETAILS_PUBLIQUES_AMIS);
			resultat.setSecond(joueurActif);
			return resultat;
		}
	}
	
	public static class PartieMulti {
		
		// On suppose qu'un bot n'a pas besoin d'�tre ami avec le joueur pour jouer
		// (ce n'est pas un vrai joueur et cela rendrait la gestion des amis p�nible pour les joueurs ayant un nombre d'amis limit�s)
		public static Pair<Options, String> jouer(Map<String, Joueur> joueurs, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
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
					Scanner sc = new Scanner(System.in);
					String choix = sc.nextLine();
					
					while(!choix.equals("O") && !choix.equals("N")) {
						System.out.println("Veuillez choisir une option disponible.");
						System.out.print("\nVoulez-vous jouer � ce jeu ? : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
					}
					switch(choix) {
					case "O":
						// TODO: Il reste � compl�ter ce switch case pour cette fonction jouer
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
								amisDispos = Menus.listeAmisPouvantJouer(joueurs, (Jeu)joueurs.get(joueurActif).getJeux().toArray()[0], joueurActif);
								if(!amisDispos.isEmpty()) {
									for(String pseudo : amisDispos) {
										System.out.println(pseudo);
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
										resultat.setFirst(Options.AFFICHAGE_PROFIL);
										resultat.setSecond(joueurActif);
										return resultat;
									}
									
									// Partie avec cet ami et enregistrement des r�sultats
									PartieMultijoueurs pm = new PartieMultijoueurs((Jeu)joueurs.get(joueurActif).getJeux().toArray()[indexJeu], joueurs.get(joueurActif), joueurs.get(choix));
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
										resultat.setFirst(Options.AFFICHAGE_PROFIL);
										return resultat;
									}
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
							}
							break;
						case "2":
							String nomBot = Menus.gestionPartieAvecBots(joueurs, (Jeu)joueurs.get(joueurActif).getJeux().toArray()[indexJeu]);
							// Partie avec le bot attribu� et enregistrement des r�sultats
							PartieMultijoueurs pm = new PartieMultijoueurs((Jeu)joueurs.get(joueurActif).getJeux().toArray()[indexJeu], joueurs.get(joueurActif), joueurs.get(nomBot));
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
								resultat.setFirst(Options.AFFICHAGE_PROFIL);
								return resultat;
							}
							
							// Suppression des �ventuels bots pour respecter les conditions du cahier des charges (voir la fonction en question)
							//Menus.gestionBots(joueurs);
							
							break;
						default:
							System.out.println("Veuillez choisir une des options ci-dessous.");
							resultat.setFirst(Options.JOUER);
							resultat.setSecond(joueurActif);
							return resultat;
						}
						
						// Pas d'ami pouvant jouer / Pas de module d'IA disponible : on propose un choix et on affiche les (10 MAX) joueurs les plus appropri�s � inviter pour jouer
						System.out.println("Invitez un de ces joueurs en tant qu'ami pour pouvoir jouer avec un vrai joueur. Sinon, vous pouvez toujours jouer avec un bot.");
						
						try {
							Menus.listeJoueursAdaptesPourJouer(joueurs, (Jeu)joueurs.get(joueurActif).getJeux().toArray()[indexJeu], joueurActif);
						} catch (JoueurNonTrouveException e) {
							// TODO Auto-generated catch block
						}
						
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						resultat.setSecond(joueurActif);
						return resultat;
					case "N":
						System.out.println("Retour au menu de choix du jeu...");
						resultat.setFirst(Options.JOUER);
						resultat.setSecond(joueurActif);
						break;
					default:
						System.out.println("Retour au menu de choix du jeu...");
						resultat.setFirst(Options.JOUER);
						resultat.setSecond(joueurActif);
						break;
					}
				}
			}
			return resultat;
			// Si on introduit un bot, modifier son statut boolean joue et jeuEnCours
			
		}
	}
	
	public static class Statistiques {}
}
