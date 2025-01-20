package es.florida.blackJack;

public class Carta {
	String palo;
	int puntos;
	
	public Carta() {}

	public Carta(String palo, int puntos) {
		this.palo = palo;
		this.puntos = puntos;
	}

	public String getPalo() {
		return palo;
	}

	public void setPalo(String palo) {
		this.palo = palo;
	}

	public int getPuntos() {
		return puntos;
	}

	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}

	@Override
	public String toString() {
		return "Carta [palo=" + palo + ", puntos=" + puntos + "]";
	}
}
