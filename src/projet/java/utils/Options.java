package projet.java.utils;

/**
 * Enum�ration qui r�capitule les fonctionnalit�s les plus importantes de l'application.<br/>
 * Utilis�e principalement dans {@code Menus} et {@code App}, pour la liaison entre les 2 classes.<br/>
 * <br/>
 * Param�tres:<br/>
 * <ul>
 *   <li>String titre : s'affiche dans le menu principal du joueur selon la valeur de {@code estDansMenu}</li>
 *   <li>int numero : le num�ro qui s'affiche dans le menu principal et permet le choix d'une option</li>
 *   <li>int numeroEnfant : idem mais pour les joueurs {@code Enfant}</li>
 *   <li>boolean estDansMenu : indique si l'option doit apparaitre dans le menu principal ou non</li>
 *   <li>boolean estAutoriseAuxEnfants : indique si l'option de menu doit apparaitre pour un enfant ou non</li>
 * </ul>
 * 
 * <br/>
 * L'�num poss�de aussi:<br/>
 * - 2 classes internes statiques qui font office de compteur (respectivement pour les adultes et les enfants).<br/>
 * - 2 constructeurs, avec en param�tres juste un titre si l'option apparait dans le menu et est autoris�e aux enfants. Sinon, on sp�cifie ces 2 variables en param�tres.<br/>
 * - 2 fonctions pour trouver l'option en fonction de l'index (respectivement pour les adultes et les enfants).<br/>
 * - les getters et setters appropri�s.<br/>
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
	DETAILS_JEU_PERSO("d�tails du jeu", false, true),
	AFFICHAGE_AMIS("Votre liste d'amis"),
	DETAILS_PUBLIQUES_AMIS("d�tails publiques de vos amis", false, true),
	STATISTIQUES("Vos statistiques personnelles"),
	CLASSEMENT("Classement g�n�ral"),
	INVITER("Inviter un ami"),
	SUPPRIMER("Supprimer un ami"),
	BOUTIQUE("Boutique", true, false),
	CADEAU("Offrir un jeu", true, false),
	INSCRIRE_ENFANT("Inscrire votre enfant", true, false),
	GESTION_CONSOLE("Gestionnaire de vos consoles"),
	DECONNEXION("D�connexion"),
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
