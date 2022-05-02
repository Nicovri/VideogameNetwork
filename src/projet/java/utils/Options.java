package projet.java.utils;

public enum Options {
	ACCUEIL("accueil", false, true),
	AFFICHAGE_PROFIL("affichage de votre profil", false, true),
	JOUER("Jouer"),
	COLLECTION("Votre collection de jeux"),
	DETAILS_JEU_PERSO("d�tails du jeu", false, true),
	AFFICHAGE_AMIS("Votre liste d'amis"),
	DETAILS_PUBLIQUES_AMIS("d�tails publiques de vos amis", false, true),
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
