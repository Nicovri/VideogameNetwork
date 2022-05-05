package projet.java.app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;

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

public class Menus {
	private static Pair<Options, String> resultat = new Pair<>();
	
	private final static String SEPARATEUR = "\n".concat("=====".repeat(15)).concat("\n");
	private final static DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRENCH);
	
	private final static String CHOIX_QUITTER = "(Pour quitter, entrez Q)";
	private final static String CHOIX_RECOMMENCER = "(Pour recommencer, entrez R)";
	private final static String CHOIX_OUI = "(Pour valider, entrez O)";
	private final static String CHOIX_NON = "(Pour refuser, entrez N)";
	
	private static void afficherDetailsJeuSelonRang(Collection<Jeu> jeux, int rang) {
		for(Jeu jeu : jeux) {
			if(jeu.getRang() == rang) {
				System.out.println(Menus.SEPARATEUR);
				System.out.println(jeu.toString());
				System.out.println(Menus.SEPARATEUR);
			}
		}
	}
	
	private static Jeu TrouverJeuSelonRang(Collection<Jeu> jeux, int rang) {
		for(Jeu jeu : jeux) {
			if(jeu.getRang() == rang) {
				return jeu;
			}
		}
		return null; // throw ExceptionJeuNonTrouve
	}
	
	// EcxceptionListeAmisVide
	private static Set<String> listeAmisSansBots(Map<String, Joueur> joueurs, String joueurActif) {
		Set<String> amisSansBots = new HashSet<>();
		int c = 0;
		if(joueurs.get(joueurActif).getAmis().isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
		}
		for(String pseudo : joueurs.get(joueurActif).getAmis()) {
			if(!(joueurs.get(pseudo) instanceof Bot)) {
				amisSansBots.add(pseudo);
				c++;
			}
		}
		if(c == 0 && !joueurs.get(joueurActif).getAmis().isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis non bots dans votre liste.");
		}
		return amisSansBots;
	}
	
	private static Set<String> listeAmisPouvantJouer(Map<String, Joueur> joueurs, Jeu jeu, String joueurActif) {
		Set<String> amisSansBotsPouvantJouer = listeAmisSansBots(joueurs, joueurActif);
		// Gestion set vide
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
	
	// Au plus 1 bot par jeu (TODO: indépendemment de la console)
	private static boolean gestionBots(Map<String, Joueur> joueurs) {
		Set<String> bots = listeBots(joueurs);
		Set<String> botsEnTrop = new HashSet<>();
		if(bots.isEmpty()) {
			return true;
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
		return true;
	}
	
	// ExceptionListeVide
	// Hypothèse : Un bot peut jouer à plusieurs parties du même jeu en même temps
	// Mais pas 2 parties de jeux différents en même temps
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
						joueurs.get(pseudoBot).ajouterJeu(jeu);
					}
					return pseudoBot;
				}
				// Le bot joue au même jeu
				if(((Bot)joueurs.get(pseudoBot)).getJoue() && jeu.getNom().equals(((Bot)joueurs.get(pseudoBot)).getJeuEnCours())) {
					return pseudoBot;
				}
			}
			// Tous les bots jouent à un autre jeu
			joueurs.put(nomBot, new Bot(jeu));
			return nomBot;
		}
	}
	
	public static class Profil {
		
		private static Pair<Options, String> creationCompte(Map<String, Joueur> joueurs, SortedSet<String> plateformes, boolean estEnfant) {
			System.out.print("Pseudo: ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			if(joueurs != null) {
				while(joueurs.containsKey(pseudo) || pseudo.length() < 3) {
					if(joueurs.containsKey(pseudo)) {
						System.out.println("Ce pseudo est déjà utilisé. Veuillez en choisir un autre");
						System.out.print("Pseudo: ");
						sc = new Scanner(System.in);
						pseudo = sc.nextLine();						
					}
					if(pseudo.length() < 3) {
						System.out.println("Ce pseudo est trop court. Veuillez choisir un pseudo de 3 caractères ou plus.");
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
			System.out.print("Une de vos consoles personnelles (choisir le numéro associé): ");
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
			System.out.println("Récapitulatif...");
			System.out.println("Pseudo: " + pseudo);
			System.out.println("Email: " + email);
			System.out.println(Menus.DATE_FORMAT.format(dateNaissance));
			System.out.println("Console: " + console);
			System.out.println(Menus.SEPARATEUR);
			
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.println(Menus.CHOIX_RECOMMENCER);
			System.out.println("Choisissez votre statut.");
			if(estEnfant) System.out.println("(ce statut sera actif dès que vous aurez 18 ans)");
			
			System.out.print("Statut (S=Standard / G=Gold): ");
			String statut = "";
			while(!statut.equals("S") && !statut.equals("G") && !statut.equals("R") && !statut.equals("Q")) {			
				sc = new Scanner(System.in);
				statut = sc.nextLine();
				switch(statut) {
				case "S":
					System.out.println("Création du compte...");
					if(estEnfant) {
						joueurs.put(pseudo, new Enfant(pseudo, email, dateNaissance, console, statut));
					} else {						
						joueurs.put(pseudo, new Standard(pseudo, email, dateNaissance, console));
						// Vérifier si joueur bien ajouté (et s'il n'est pas en double (exception ?))
					}
					System.out.println("Compte créé avec succès!");
					resultat.setFirst(Options.AFFICHAGE_PROFIL);
					resultat.setSecond(pseudo);
					System.out.println("Affichage de votre profil Standard...");
					break;
				case "G":
					System.out.println("Achat du statut Gold...");
					System.out.println("Création du compte...");
					if(estEnfant) {
						joueurs.put(pseudo, new Enfant(pseudo, email, dateNaissance, console, statut));
					} else {						
						joueurs.put(pseudo, new Gold(pseudo, email, dateNaissance, console));
						// Vérifier si joueur bien ajouté (et s'il n'est pas en double (exception ?))
					}
					System.out.println("Compte créé avec succès!");
					resultat.setFirst(Options.AFFICHAGE_PROFIL);
					resultat.setSecond(pseudo);
					System.out.println("Affichage de votre profil Gold...");
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
		
		private static Pair<Options, String> connexion(Map<String, Joueur> joueurs, SortedSet<String> plateformes) {
			// Connexion (sans gestion du mot de passe pour l'instant)
			System.out.print("Votre pseudo: ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			if(!joueurs.isEmpty()) {
				while(joueurs.get(pseudo) == null) {
					// Q et R ne peuvent pas être un pseudo car il doit contenir au moins 3 caractères
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
				System.out.println("\nAucun joueurs inscrits. Retour à l'écran d'accueil...");
				resultat.setFirst(Options.ACCUEIL);
				resultat.setSecond("");
			}
			return resultat;
		}
		
		public static Pair<Options, String> accueilJoueur(Map<String, Joueur> joueurs, SortedSet<String> plateformes) {
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Création de compte\n2. Connexion\n3. Quitter");
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
			int choix = 0;
			if(!joueurActif.equals("")) {
				StringBuilder b = new StringBuilder();
				b.append(Menus.SEPARATEUR);
				
				Joueur j = joueurs.get(joueurActif);
				b.append(j.toString());
				
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
			System.out.println("Etes-vous sûr(e) de vouloir vous déconnecter ?");
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
		
		// On suppose qu'après inscription, sa liste de parents/tuteurs ne peut pas être modifée (elle contient donc soit 1 soit 2 Joueurs)
		// On suppose (pour l'instant) qu'un parent ne peut plus mentionner son enfant s'il s'inscrit après lui (cas du 2e parent)
		public static Pair<Options, String> inscrireSonEnfant(Map<String, Joueur> joueurs, SortedSet<String> plateformes, String joueurActif) {
			if(!(joueurs.get(joueurActif) instanceof Enfant)) {
				String parent = joueurActif;
				
				System.out.println("Veillez à être présent lors de l'inscription de votre enfant");
				resultat = Menus.Profil.creationCompte(joueurs, plateformes, true);
				
				if(resultat.getFirst() == Options.AFFICHAGE_PROFIL) {
					System.out.println("Ajout réciproque dans la liste d'amis...");
					String enfant = resultat.getSecond();
					((Enfant) joueurs.get(enfant)).setPseudoParent1(joueurs.get(parent).getPseudo());
					joueurs.get(parent).ajouterAmi(joueurs.get(enfant));
					joueurs.get(enfant).ajouterAmi(joueurs.get(parent));
					System.out.println("Ajout réussi !");
					
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
								System.out.println("Ajout réciproque dans la liste d'amis...");
								((Enfant) joueurs.get(enfant)).setPseudoParent2(choix);
								joueurs.get(choix).ajouterAmi(joueurs.get(enfant));
								joueurs.get(enfant).ajouterAmi(joueurs.get(choix));
								System.out.println("Ajout réussi !");
							} else {
								System.out.println("Ce pseudo n'est pas dans la liste de joueurs inscrits. Veuillez réessayer");
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
		
		// ExceptionAmiNonTrouve extends ExceptionJoueurNonTrouve
		public static Pair<Options, String> ajouterAmi(Map<String, Joueur> joueurs, String joueurActif) /*throws ExceptionAmiNonTrouve*/ {
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Veuillez indiquer le pseudo de votre futur ami : ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			switch(pseudo) {
			case "Q":
				System.out.println("Opération annulée.");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				break;
			default:
				if(joueurs.containsKey(pseudo)) {
					boolean estAjoute = joueurs.get(joueurActif).ajouterAmi(joueurs.get(pseudo));
					if(estAjoute) {						
						boolean estAjouteReciproque = joueurs.get(pseudo).ajouterAmi(joueurs.get(joueurActif));
						if(estAjouteReciproque) {
							System.out.println("Ami ajouté avec succès !");
						} else {
							System.out.println("Annulation...");
							joueurs.get(joueurActif).supprimerAmi(joueurs.get(pseudo));
							System.out.println("Opération annulée.");
						}
					} else {
						System.out.println("Annulation...");
						System.out.println("Opération annulée.");
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
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Veuillez indiquer le pseudo de l'ami à supprimer : ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			switch(pseudo) {
			case "Q":
				System.out.println("Opération annulée.");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				break;
			default:
				if(joueurs.get(joueurActif).getAmis().contains(pseudo)) {
					boolean estSupprime = joueurs.get(joueurActif).supprimerAmi(joueurs.get(pseudo));
					if(estSupprime) {
						boolean estSupprimeReciproque = joueurs.get(pseudo).supprimerAmi(joueurs.get(joueurActif));
						if(estSupprimeReciproque) {
							System.out.println("Ami supprimé avec succès !");												
						} else {
							System.out.println("Annulation...");
							joueurs.get(joueurActif).ajouterAmi(joueurs.get(pseudo));
							System.out.println("Opération annulée.");	
						}
					} else {
						System.out.println("Annulation...");
						System.out.println("Opération annulée.");				
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
					// Offre du jeu à un ami
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
						System.out.println(Menus.CHOIX_QUITTER);
						Set<String> amisSansBots = Menus.listeAmisSansBots(joueurs, joueurActif);
						for(String pseudo : amisSansBots) {
							System.out.println("- " + pseudo);							
						}
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
							System.out.println("Opération annulée.");
							resultat.setFirst(Options.AFFICHAGE_PROFIL);
							resultat.setSecond(joueurActif);
							return resultat;
						}
						
						System.out.println("Offre du jeu à " + choix + "...");
						boolean estAjoute = joueurs.get(choix).ajouterJeu(Menus.TrouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
						if(estAjoute) {
							boolean estSupprime = joueurs.get(joueurActif).supprimerJeu(Menus.TrouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
							if(estSupprime) {
								System.out.println("Jeu offert à l'ami sélectionné !");								
							} else {
								System.out.println("Annulation...");
								joueurs.get(choix).supprimerJeu(Menus.TrouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
								System.out.println("Opération annulée.");
							}
						} else {
							System.out.println("Annulation...");
							System.out.println("Opération annulée.");
						}
						
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						resultat.setSecond(joueurActif);
						break;
					case "N":
						System.out.println("Retour au menu d'offre de jeu à un ami...");
						resultat.setFirst(Options.CADEAU);
						resultat.setSecond(joueurActif);
						break;
					default:
						System.out.println("Retour au menu d'offre de jeu à un ami...");
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
			Object[] p = plateformes.toArray();
			int nombre = p.length + 1;
			for(int i = 0; i < p.length; i++) {
				System.out.println((i + 1) + ". " + p[i]);
			}
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Choisir une nouvelle console à ajouter (choisir le numéro associé) : ");
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			while(!choix.matches("\\d+")) {
				System.out.println("Veuillez choisir un nombre.");
				System.out.print("Console à ajouter : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			int indexConsole = Integer.parseInt(choix);
			while(indexConsole <= 0 || indexConsole > nombre) {
				System.out.println("Veuillez choisir un nombre entre 1 et " + nombre + ".");
				System.out.print("Console à ajouter : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
				indexConsole = Integer.parseInt(choix);
			}
			String console = (String) p[indexConsole - 1];
			
			boolean estAjoute = ((Humain) joueurs.get(joueurActif)).ajouterNouvelleConsole(console);
			if(estAjoute) {
				System.out.println("Nouvelle console ajoutée avec succès !");
			} else {
				System.out.println("Vous possédez dèjà cette console.");
				System.out.println("Annulation...");
				System.out.println("Opération annulée.");
			}
			resultat.setFirst(Options.AFFICHAGE_PROFIL);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
		private static Pair<Options, String> supprimerConsole(Map<String, Joueur> joueurs, SortedSet<String> plateformes, String joueurActif) {
			Set<String> m = ((Humain) joueurs.get(joueurActif)).getMachines();
			if(m.isEmpty()) {
				System.out.println("Vous n'avez aucune console, vous ne pouvez pas en supprimer.");
				System.out.println("Annulation...");
				System.out.println("Opération annulée.");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				return resultat;
			}
			int nombre = m.size() + 1;
			for(int i = 0; i < m.size(); i++) {
				System.out.println((i + 1) + ". " + m.toArray()[i]);
			}
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Choisir une console à supprimer (choisir le numéro associé) : ");
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			while(!choix.matches("\\d+")) {
				System.out.println("Veuillez choisir un nombre.");
				System.out.print("Console à supprimer : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			int indexConsole = Integer.parseInt(choix);
			
			while(indexConsole <= 0 || indexConsole > nombre) {
				System.out.println("Veuillez choisir un nombre entre 1 et " + nombre + ".");
				System.out.print("Console à supprimer : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
				indexConsole = Integer.parseInt(choix);
			}
			String console = (String) m.toArray()[indexConsole - 1];
			
			boolean estSupprime = ((Humain) joueurs.get(joueurActif)).supprimerConsole(console);
			if(estSupprime) {
				System.out.println("Console supprimée avec succès !");
			} else {
				System.out.println("Vous ne possédez pas cette console.");
				System.out.println("Annulation...");
				System.out.println("Opération annulée.");
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
		// ExceptionPasDeDonneesPlateformes / Genres (si null)
		public static Pair<Options, String> afficherListeJeux(Collection<Jeu> jeux, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Jeux classés par machine\n2. Jeux classés par genre");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			Scanner sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			Collection<Jeu> jeuxTries;
			Object[] arrayJeuxTries;
			
			if(jeux.isEmpty()) {
				System.out.println("Aucun jeux dans votre collection...");
				resultat.setFirst(Options.AFFICHAGE_PROFIL);
				resultat.setSecond(joueurActif);
				return resultat;
			}
			
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
			System.out.println("\n" + Menus.CHOIX_QUITTER);
			System.out.print("Choisissez le rang du jeu pour afficher ses détails : ");
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			
			while(!choix.matches("\\d+") && !choix.equals("Q")) {
				System.out.println("Veuillez choisir une option disponible.");
				System.out.println("\n" + Menus.CHOIX_QUITTER);
				System.out.print("Choisissez le rang du jeu pour afficher ses détails : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			
			int indexJeu;
			if(choix.matches("\\d+")) {
				indexJeu = Integer.parseInt(choix);
				Menus.afficherDetailsJeuSelonRang(jeux, indexJeu);
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
						boolean estAchete = joueurs.get(joueurActif).ajouterJeu(Menus.TrouverJeuSelonRang(jeux, indexJeu));
						if(estAchete) {							
							System.out.println("Jeu acheté et ajouté à votre liste !");
						} else {
							System.out.println("Annulation...");
							System.out.println("Opération annulée.");
						}
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						break;
					case "N":
						System.out.println("Retour à la boutique...");
						resultat.setFirst(Options.BOUTIQUE);
						resultat.setSecond(joueurActif);
						break;
					default:
						System.out.println("Retour à la boutique...");
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
			System.out.println("\n" + Menus.CHOIX_QUITTER);
			System.out.print("Choisissez le pseudo du jeu pour afficher ses détails publiques : ");
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			
			while((joueurs.get(choix) == null || !joueurs.get(joueurActif).getAmis().contains(choix)) && !choix.equals("Q")) {
				if(joueurs.get(choix) == null) {								
					System.out.println("Ce pseudo n'est pas valide. Veuillez entrer un pseudo existant.");
				} else {
					System.out.println("Ce joueur n'est pas un ami.");
				}
				System.out.print("Choisissez le pseudo du jeu pour afficher ses détails publiques : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
			}
			
			if(choix.equals("Q")) {
				System.out.println("Opération annulée.");
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
		// On suppose que le joueur va essayer de jouer en premier lieu avec un autre joueur ami avant d'essayer de jouer avec un bot
		// On suppose qu'un bot n'a pas besoin d'être ami avec le joueur pour jouer (ce n'est pas un vrai joueur, cela rendrait la gestion des amis pénible pour les joueurs ayant un nombre d'amis limités)
		public static Pair<Options, String> jouer(Map<String, Joueur> joueurs, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			// Demander le jeu auquel on veut jouer parmi ses jeux (s'il a un jeu)
			resultat = Menus.CollectionJeux.afficherListeJeux(joueurs.get(joueurActif).getJeux(), plateformes, genres, joueurActif);
			if(!resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
				if(resultat.getFirst().equals(Options.COLLECTION)) resultat.setFirst(Options.JOUER);
			} else {
				Pair<Integer, Pair<Options, String>> res = Menus.CollectionJeux.afficherDetailsJeu(joueurs.get(joueurActif).getJeux(), joueurActif);
				int indexJeu = res.getFirst();
				resultat = res.getSecond();
				if(resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
					// Sélection du jeu auquel jouer
					System.out.println(Menus.CHOIX_OUI);
					System.out.println(Menus.CHOIX_NON);
					System.out.print("\nVoulez-vous jouer à ce jeu ? : ");
					Scanner sc = new Scanner(System.in);
					String choix = sc.nextLine();
					
					while(!choix.equals("O") && !choix.equals("N")) {
						System.out.println("Veuillez choisir une option disponible.");
						System.out.print("\nVoulez-vous jouer à ce jeu ? : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
					}
					switch(choix) {
					case "O":
						// TODO: Il reste à compléter ce switch case pour cette fonction jouer
						// Choix du mode de jeu (avec ami ou avec bot)
						System.out.print(Menus.SEPARATEUR);
						System.out.print("1. Jouer avec un ami\n2. Jouer avec un bot");
						System.out.print(Menus.SEPARATEUR);
						System.out.print("Votre choix : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
						
						Set<String> amisDispos = Menus.listeAmisPouvantJouer(joueurs, (Jeu)joueurs.get(joueurActif).getJeux().toArray()[0], joueurActif);
						switch(choix) {
						case "1":
							// Afficher la liste des amis pouvant jouer s'il y en a
							if(!amisDispos.isEmpty()) {
								for(String pseudo : amisDispos) {
									System.out.println(pseudo);
								}
								// Choix de l'ami avec qui jouer
							}
							break;
						case "2":
							if(Integer.parseInt(((Jeu)joueurs.get(joueurActif).getJeux().toArray()[0]).getAnnee()) >= Integer.parseInt(Jeu.DATE_IA)) {
								
							}
							break;
						default:
							System.out.println("Veuillez choisir une des options ci-dessous.");
							resultat.setFirst(Options.JOUER);
							resultat.setSecond(joueurActif);
							return resultat;
						}
						
						/*// Pas d'ami pouvant jouer / Pas de module d'IA disponible : on propose un choix et on affiche les (10 MAX) joueurs les plus appropriés à inviter pour jouer
						System.out.println("Invitez un de ces joueurs en tant qu'ami pour pouvoir jouer avec un vrai joueur. Sinon, vous pouvez toujours jouer avec un bot.");
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						resultat.setSecond(joueurActif);
						return resultat;*/
						
						break;
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

			// Vérifier PARTIES_MAX dans joueur
			
			// Si on introduit un bot, modifier son statut boolean joue et jeuEnCours
			// fonction gestionBots (pour enlever ceux en trop)
		}
	}
	
	public static class Statistiques {}
}
