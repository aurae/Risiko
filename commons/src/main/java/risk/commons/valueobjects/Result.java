package risk.commons.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Diese Klasse dient als Container-Klasse für Rückgabewerte für verschiedenste
 * Anwendungsbereiche. Meistens wird ein Result-Objekt von den unteren Schichten
 * an die UI weitergeleitet, dort entpackt und mit den beinhaltenden
 * Werten weitergearbeitet.
 * @author Marcel
 */
public class Result implements Serializable {
	private static final long serialVersionUID = -1552970383552353349L;
	/** Liste von Objekten, die als Container dient */
	private ArrayList<Object> objects;
	
	/** Konstruktor, der eine leere Objektliste anlegt */
	public Result() { objects = new ArrayList<Object>(); }
	
	/** Einfügen eines neuen Objektes in die Liste
	 * @param o Objekt */
	public void push(Object o) { objects.add(o); }
	
	/** Herausziehen des ersten Elementes in der Objektliste
	 * @return Objekt, oder null, wenn die Liste leer ist */
	public Object pop() {
		if (!objects.isEmpty()) {
			Object o = objects.get(0);
			objects.remove(0);
			return o;
		}
		return null;
	}
	
	/** Gib' die Anzahl der Elemente in der Objektliste zurück */
	public int size() {	return objects.size(); }
}
