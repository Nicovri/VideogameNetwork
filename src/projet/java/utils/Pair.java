package projet.java.utils;

import java.io.Serializable;

public class Pair<F, S> implements Serializable {
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
