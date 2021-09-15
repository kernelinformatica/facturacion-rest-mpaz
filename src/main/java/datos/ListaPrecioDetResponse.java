package datos;

import entidades.ListaPrecioDet;
import entidades.Producto;
import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class ListaPrecioDetResponse implements Payload {

    private Integer idListaPrecioDet;    
    private BigDecimal precio;   
    private BigDecimal cotaInf;   
    private BigDecimal cotaSup;    
    private String observaciones;      
    private BigDecimal cotaInfPorce;   
    private BigDecimal cotaSupPorce;
    private ProductoResponse idProducto;

    public ListaPrecioDetResponse(ListaPrecioDet l) {
        this.idListaPrecioDet = l.getIdListaPreciosDet();
        this.precio = l.getPrecio();
        this.cotaInf = l.getCotaInf();
        this.cotaSup = l.getCotaSup();
        this.observaciones = l.getObservaciones();
        this.cotaInfPorce = l.getCotaInfPorc();
        this.cotaSupPorce = l.getCotaSupPorc();
        this.idProducto = new ProductoResponse(l.getIdProductos());
    }
    
    public ListaPrecioDetResponse(BigDecimal precio, BigDecimal cotaInf, BigDecimal cotaSup, Producto p) {
        this.precio = precio;
        this.cotaInf = cotaInf;
        this.cotaSup = cotaSup;
        this.observaciones = null;
        this.idProducto = new ProductoResponse(p);
    }
    
    public Integer getIdListaPrecioDet() {
        return idListaPrecioDet;
    }

    public void setIdListaPrecioDet(Integer idListaPrecioDet) {
        this.idListaPrecioDet = idListaPrecioDet;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getCotaInf() {
        return cotaInf;
    }

    public void setCotaInf(BigDecimal cotaInf) {
        this.cotaInf = cotaInf;
    }

    public BigDecimal getCotaSup() {
        return cotaSup;
    }

    public void setCotaSup(BigDecimal cotaSup) {
        this.cotaSup = cotaSup;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public ProductoResponse getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(ProductoResponse idProducto) {
        this.idProducto = idProducto;
    }

    public BigDecimal getCotaInfPorce() {
        return cotaInfPorce;
    }

    public void setCotaInfPorce(BigDecimal cotaInfPorce) {
        this.cotaInfPorce = cotaInfPorce;
    }

    public BigDecimal getCotaSupPorce() {
        return cotaSupPorce;
    }

    public void setCotaSupPorce(BigDecimal cotaSupPorce) {
        this.cotaSupPorce = cotaSupPorce;
    }

    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
