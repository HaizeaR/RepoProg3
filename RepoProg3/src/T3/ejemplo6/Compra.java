package T3.ejemplo6;

public class Compra {
	private int id; 
	private long fecha;  
	private int cantidad; 
	private Producto producto;
	public Compra(int id, long fecha, int cantidad, Producto producto) {
	
		this.id = id;
		this.fecha = fecha;
		this.cantidad = cantidad;
		this.producto = producto;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getFecha() {
		return fecha;
	}
	public void setFecha(long fecha) {
		this.fecha = fecha;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	} 
	
	
	

}
