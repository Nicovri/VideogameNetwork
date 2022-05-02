package projet.java.app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;

import projet.java.jeux.Jeu;
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
	
	// Plut�t la retourner que l'afficher
	private static boolean afficherListeAmisSansBots(Map<String, Joueur> joueurs, String joueurActif) {
		int c = 0;
		if(joueurs.get(joueurActif).getAmis().isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
			return false;
		}
		for(String pseudo : joueurs.get(joueurActif).getAmis()) {
			if(!(joueurs.get(pseudo) instanceof Bot)) {				
				System.out.println("- " + pseudo);
				c++;
			}
		}
		if(c == 0 && !joueurs.get(joueurActif).getAmis().isEmpty()) {
			System.out.println("Vous n'avez pas encore d'amis non bots dans votre liste.");
			return false;
		}
		return true;
	}
	
	// utiliser la fonction au dessus, puis afficher la liste
	// Si la liste est vide, voir si des bots sont disponibles pour jouer
	private static boolean afficherListeAmisPouvantJouer() {
		
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
					if(estEnfant) {
						joueurs.put(pseudo, new Enfant(pseudo, email, dateNaissance, console, statut));
					} else {						
						joueurs.put(pseudo, new Standard(pseudo, email, dateNaissance, console));
						// V�rifier si joueur bien ajout� (et s'il n'est pas en double (exception ?))
					}
					System.out.println("Compte cr�� avec succ�s!");
					resultat.setFirst(Options.AFFICHAGE_PROFIL);
					resultat.setSecond(pseudo);
					System.out.println("Affichage de votre profil Standard...");
					break;
				case "G":
					System.out.println("Achat du statut Gold...");
					System.out.println("Cr�ation du compte...");
					if(estEnfant) {
						joueurs.put(pseudo, new Enfant(pseudo, email, dateNaissance, console, statut));
					} else {						
						joueurs.put(pseudo, new Gold(pseudo, email, dateNaissance, console));
						// V�rifier si joueur bien ajout� (et s'il n'est pas en double (exception ?))
					}
					System.out.println("Compte cr�� avec succ�s!");
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
				return new Pair<>(Options.findOptionByNumeroEnfant(choix), joueurActif);
			} else {
				return new Pair<>(Options.findOptionByNumero(choix), joueurActif);
			}
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
			if(!(joueurs.get(joueurActif) instanceof Enfant)) {
				String parent = joueurActif;
				
				System.out.println("Veillez � �tre pr�sent lors de l'inscription de votre enfant");
				resultat = Menus.Profil.creationCompte(joueurs, plateformes, true);
				
				if(resultat.getFirst() == Options.AFFICHAGE_PROFIL) {
					System.out.println("Ajout r�ciproque dans la liste d'amis...");
					String enfant = resultat.getSecond();
					((Enfant) joueurs.get(enfant)).setPseudoParent1(joueurs.get(parent).getPseudo());
					joueurs.get(parent).ajouterAmi(joueurs.get(enfant));
					joueurs.get(enfant).ajouterAmi(joueurs.get(parent));
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
								((Enfant) joueurs.get(enfant)).setPseudoParent2(choix);
								joueurs.get(choix).ajouterAmi(joueurs.get(enfant));
								joueurs.get(enfant).ajouterAmi(joueurs.get(choix));
								System.out.println("Ajout r�ussi !");
							} else {
								System.out.println("Ce pseudo n'est pas dans la liste de joueurs inscrits. Veuillez r�essayer");
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
				System.out.println("Op�ration annul�e.");
				break;
			default:
				if(joueurs.containsKey(pseudo)) {
					boolean estAjoute = joueurs.get(joueurActif).ajouterAmi(joueurs.get(pseudo));
					boolean estAjouteReciproque = joueurs.get(pseudo).ajouterAmi(joueurs.get(joueurActif));
					if(estAjoute && estAjouteReciproque) {
						System.out.println("Ami ajout� avec succ�s !");					
					} else if(!estAjoute || !estAjouteReciproque) {
						if(estAjoute && !estAjouteReciproque) joueurs.get(joueurActif).supprimerAmi(joueurs.get(pseudo));
						if(!estAjoute && estAjouteReciproque) joueurs.get(pseudo).supprimerAmi(joueurs.get(joueurActif));
						System.out.println("Annulation...");
						System.out.println("Op�ration annul�e.");
					}
				}
				break;
			}
			resultat.setFirst(Options.AFFICHAGE_PROFIL);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
		public static Pair<Options, String> supprimerAmi(Map<String, Joueur> joueurs, String joueurActif) {
			System.out.println(Menus.CHOIX_QUITTER);
			System.out.print("Veuillez indiquer le pseudo de l'ami � supprimer : ");
			Scanner sc = new Scanner(System.in);
			String pseudo = sc.nextLine();
			
			switch(pseudo) {
			case "Q":
				System.out.println("Op�ration annul�e.");
				break;
			default:
				if(joueurs.containsKey(pseudo)) {
					boolean estSupprime = joueurs.get(joueurActif).supprimerAmi(joueurs.get(pseudo));
					boolean estSupprimeReciproque = joueurs.get(pseudo).supprimerAmi(joueurs.get(joueurActif));
					if(estSupprime && estSupprimeReciproque) {
						System.out.println("Ami supprim� avec succ�s !");					
					} else if(!estSupprime || !estSupprimeReciproque) {
						if(estSupprime && !estSupprimeReciproque) joueurs.get(joueurActif).ajouterAmi(joueurs.get(pseudo));
						if(!estSupprime && estSupprimeReciproque) joueurs.get(pseudo).ajouterAmi(joueurs.get(joueurActif));
						System.out.println("Annulation...");
						System.out.println("Op�ration annul�e.");
					}
				}
				break;
			}
			resultat.setFirst(Options.AFFICHAGE_PROFIL);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
		public static Pair<Options, String> offrirJeu(Map<String, Joueur> joueurs, SortedSet<String> plateformes, SortedSet<String> genres, String joueurActif) {
			if(joueurs.get(joueurActif) instanceof Enfant) {
				System.out.println("Vous ne pouvez pas offrir de jeux !!");
				return new Pair<>(Options.AFFICHAGE_PROFIL, joueurActif);
			}
			resultat = Menus.CollectionJeux.afficherListeJeux(joueurs.get(joueurActif).getJeux(), plateformes, genres, joueurActif);
			if(!resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
				if(resultat.getFirst().equals(Options.COLLECTION)) resultat.setFirst(Options.CADEAU);
			} else {
				Pair<Integer, Pair<Options, String>> res = Menus.CollectionJeux.afficherDetailsJeu(joueurs.get(joueurActif).getJeux(), joueurActif);
				int indexJeu = res.getFirst();
				resultat = res.getSecond();
				if(resultat.getFirst().equals(Options.DETAILS_JEU_PERSO)) {
					// Offre du jeu (� un ami non enfant sauf si on est le parent)
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
						Menus.afficherListeAmisSansBots(joueurs, joueurActif);
						System.out.print("Veuillez entrer le pseudo du joueur auquel offrir le jeu : ");
						sc = new Scanner(System.in);
						choix = sc.nextLine();
						while(joueurs.get(choix) == null && !joueurs.get(joueurActif).getAmis().contains(choix)) {
							if(joueurs.get(choix) == null) {								
								System.out.println("Ce pseudo n'est pas valide. Veuillez entrer un pseudo existant.");
							} else {
								System.out.println("Ce joueur n'est pas un ami. Vous ne pouvez pas lui offrir de jeu.");
							}
							System.out.print("Veuillez entrer le pseudo du joueur auquel offrir le jeu : ");
							sc = new Scanner(System.in);
							choix = sc.nextLine();
						}
						
						System.out.println("Offre du jeu � " + choix + "...");
						boolean estAjoute = joueurs.get(choix).ajouterJeu(Menus.TrouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
						boolean estSupprime = joueurs.get(joueurActif).supprimerJeu(Menus.TrouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
						if(estAjoute && estSupprime) {
							System.out.println("Jeu offert � l'ami s�lectionn� !");
						} else if(!estAjoute || !estSupprime) {
							if(estAjoute && !estSupprime) joueurs.get(choix).supprimerJeu(Menus.TrouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
							if(!estAjoute && estSupprime) joueurs.get(joueurActif).ajouterJeu(Menus.TrouverJeuSelonRang(joueurs.get(joueurActif).getJeux(), indexJeu));
							System.out.println("Annulation...");
							System.out.println("Op�ration annul�e.");
						}
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						break;
					case "N":
						System.out.println("Retour au profil...");
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
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
			Set<String> m = ((Humain) joueurs.get(joueurActif)).getMachines();
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
			System.out.print(Menus.SEPARATEUR);
			System.out.print("1. Jeux class�s par machine\n2. Jeux class�s par genre");
			System.out.print(Menus.SEPARATEUR);
			System.out.print("Votre choix : ");
			Scanner sc = new Scanner(System.in);
			
			String choix = sc.nextLine();
			Collection<Jeu> jeuxTries;
			Object[] arrayJeuxTries;
			
			if(jeux.isEmpty()) {
				System.out.println("Aucun jeux dans votre collection...");
				return new Pair<>(Options.AFFICHAGE_PROFIL, joueurActif);
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
				return new Pair<>(Options.COLLECTION, joueurActif);
			}
			resultat.setFirst(Options.DETAILS_JEU_PERSO);
			resultat.setSecond(joueurActif);
			return resultat;
		}
		
		public static Pair<Integer, Pair<Options, String>> afficherDetailsJeu(Collection<Jeu> jeux, String joueurActif) {
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
				return new Pair<>(Options.AFFICHAGE_PROFIL, joueurActif);
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
							System.out.println("Jeu achet� et ajout� � votre liste !");
						} else {
							System.out.println("Annulation...");
							System.out.println("Op�ration annul�e.");
						}
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
						break;
					case "N":
						System.out.println("Retour au profil...");
						resultat.setFirst(Options.AFFICHAGE_PROFIL);
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
			if(joueurs.get(joueurActif).getAmis().isEmpty()) {
				System.out.println("Vous n'avez pas encore d'amis dans votre liste.");
				return new Pair<>(Options.AFFICHAGE_PROFIL, joueurActif);
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
			System.out.print("Choisissez le pseudo du jeu pour afficher ses d�tails publiques : ");
			Scanner sc = new Scanner(System.in);
			String choix = sc.nextLine();
			
			while(joueurs.get(choix) == null && !joueurs.get(joueurActif).getAmis().contains(choix)) {
				if(joueurs.get(choix) == null) {								
					System.out.println("Ce pseudo n'est pas valide. Veuillez entrer un pseudo existant.");
				} else {
					System.out.println("Ce joueur n'est pas un ami.");
				}
				System.out.print("Choisissez le pseudo du jeu pour afficher ses d�tails publiques : ");
				sc = new Scanner(System.in);
				choix = sc.nextLine();
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
	}
	
	public static class Statistiques {}
}
