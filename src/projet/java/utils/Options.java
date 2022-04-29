package projet.java.utils;

public enum Options {
	ACCUEIL("accueil", false, true),
	AFFICHAGE_PROFIL("affichage de votre profil", false, true),
	JOUER("Jouer"),
	COLLECTION("Votre collection de jeux"),
	INVITER("Inviter un ami"),
	SUPPRIMER("Supprimer un ami"),
	BOUTIQUE("Boutique", true, false),
	CADEAU("Offrir un jeu", true, false),
	INSCRIRE_ENFANT("Inscrire votre enfant", true, false),
	GESTION_CONSOLE("Gestionnaire de vos consoles"),
	DECONNEXION("Déconnexion"),
	QUITTER("quitter l'application", false, true);
	
	// AFFICHAGE_AMIS
	
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
