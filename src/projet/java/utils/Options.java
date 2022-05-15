package projet.java.utils;

/**
 * Enumération qui récapitule les fonctionnalités les plus importantes de l'application.<br/>
 * Utilisée principalement dans {@code Menus} et {@code App}, pour la liaison entre les 2 classes.<br/>
 * <br/>
 * Paramètres:<br/>
 * <ul>
 *   <li>String titre : s'affiche dans le menu principal du joueur selon la valeur de {@code estDansMenu}</li>
 *   <li>int numero : le numéro qui s'affiche dans le menu principal et permet le choix d'une option</li>
 *   <li>int numeroEnfant : idem mais pour les joueurs {@code Enfant}</li>
 *   <li>boolean estDansMenu : indique si l'option doit apparaitre dans le menu principal ou non</li>
 *   <li>boolean estAutoriseAuxEnfants : indique si l'option de menu doit apparaitre pour un enfant ou non</li>
 * </ul>
 * 
 * <br/>
 * L'énum possède aussi:<br/>
 * - 2 classes internes statiques qui font office de compteur (respectivement pour les adultes et les enfants).<br/>
 * - 2 constructeurs, avec en paramètres juste un titre si l'option apparait dans le menu et est autorisée aux enfants. Sinon, on spécifie ces 2 variables en paramètres.<br/>
 * - 2 fonctions pour trouver l'option en fonction de l'index (respectivement pour les adultes et les enfants).<br/>
 * - les getters et setters appropriés.<br/>
 * <br/>
 * 
 * @author Nicolas Vrignaud
 *
 * @see projet.java.app.App
 * @see projet.java.app.Menus
 */
public enum Options {
	ACCUEIL("accueil", false, true),
	AFFICHAGE_PROFIL("affichage de votre profil", false, true),
	JOUER("Jouer"),
	COLLECTION("Votre collection de jeux"),
	DETAILS_JEU_PERSO("détails du jeu", false, true),
	AFFICHAGE_AMIS("Votre liste d'amis"),
	DETAILS_PUBLIQUES_AMIS("détails publiques de vos amis", false, true),
	STATISTIQUES("Vos statistiques personnelles"),
	CLASSEMENT("Classement général"),
	INVITER("Inviter un ami"),
	SUPPRIMER("Supprimer un ami"),
	BOUTIQUE("Boutique", true, false),
	CADEAU("Offrir un jeu", true, false),
	INSCRIRE_ENFANT("Inscrire votre enfant", true, false),
	GESTION_CONSOLE("Gestionnaire de vos consoles"),
	DECONNEXION("Déconnexion"),
	QUITTER("quitter l'application", false, true);
	
	private static class Compteur {
		private static int index = 0;
	}
	private static class CompteurEnfant {
		private static int index = 0;
	}
	
	private String titre;
	private int numero = 0;
	private int numeroEnfant = 0;
	private boolean estDansMenu;
	private boolean estAutoriseAuxEnfants;
	
	private Options(String t) {
		this.titre = t;
		this.estDansMenu = true;
		this.estAutoriseAuxEnfants = true;
		this.numero = ++Compteur.index;
		this.numeroEnfant = ++CompteurEnfant.index;
	}
	
	private Options(String t, boolean menu, boolean enfants) {
		this.estDansMenu = menu;
		this.estAutoriseAuxEnfants = enfants;		
		this.titre = t;
		
		if(this.estDansMenu) {
			this.numero = ++Compteur.index;
			if(this.estAutoriseAuxEnfants) {
				this.numeroEnfant = ++CompteurEnfant.index;
			}
		}
	}
	
	public String getTitre() { return this.titre; }
	public int getNumero() { return this.numero; }
	public int getNumeroEnfant() { return this.numeroEnfant; }
	public boolean getEstDansMenu() { return this.estDansMenu; }
	public boolean getEstAutoriseAuxEnfants() { return this.estAutoriseAuxEnfants; }
	
	public static Options findOptionByNumero(int n) {
		for(Options option : values()) {
			if(option.getNumero() == n) {
				return option;
			}
		}
		return null;
	};
	
	public static Options findOptionByNumeroEnfant(int n) {
		for(Options option : values()) {
			if(option.getNumeroEnfant() == n) {
				return option;
			}
		}
		return null;
	};
}
