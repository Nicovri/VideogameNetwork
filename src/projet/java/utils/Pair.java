package projet.java.utils;

import java.io.Serializable;

/**
 * Paire utilisée pour la liaison entre les 2 classes Menus et App.<br/>
 * <br/>
 * Plusieurs fonctions qui permettent d'obtenir les valeur, et de les modifier, soit une par une, soit toutes les deux.
 * 
 * @author Nicolas Vrignaud
 *
 * @param <F> : premier type générique
 * @param <S> : deuxième type générique
 * 
 * @see projet.java.app.App
 * @see projet.java.app.Menus
 */
public class Pair<F, S> implements Serializable {
	private static final long serialVersionUID = -7038461186541031443L;
	
	F first;
	S second;
	
	public Pair() {
		this.first = null;
		this.second = null;
	}
	
	public Pair(F f, S s) {
		this.first = f;
		this.second = s;
	}
	
	public F getFirst() { return this.first; }
	public S getSecond() { return this.second; }
	
	public void setFirst(F f) { this.first = f; }
	public void setSecond(S s) { this.second = s; }
	
	public void setBoth(F f, S s) { this.first = f; this.second = s; }
}
