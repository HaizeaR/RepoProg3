package T3.ejemplo6;

import java.util.ArrayList;

public class Producto {
	private int id; 
	private String text; 
	private int precio; 
	private ArrayList<Compra> listaCompras;
	
	public Producto(int id, String text, int precio, ArrayList<Compra> listaCompras) {
		super();
		this.id = id;
		this.text = text;
		this.precio = precio;
		this.listaCompras = listaCompras;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public ArrayList<Compra> getListaCompras() {
		return listaCompras;
	}

	public void setListaCompras(ArrayList<Compra> listaCompras) {
		this.listaCompras = listaCompras;
	}
	
	
	
	
	

}
