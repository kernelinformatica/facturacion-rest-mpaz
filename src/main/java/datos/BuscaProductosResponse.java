package datos;

import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class BuscaProductosResponse implements Payload {
    private String codProducto;
    private String descripcionCorta;
    private String descripcion;
    private BigDecimal precio;
    private boolean trazable;
    private BigDecimal IVA;
    private String rubro;
    private String subRubro;

    public BuscaProductosResponse(String codProducto, String descripcionCorta, String descripcion, BigDecimal precio, boolean trazable, BigDecimal IVA, String rubro, String subRubro) {
        this.codProducto = codProducto;
        this.descripcionCorta = descripcionCorta;
        this.descripcion = descripcion;
        this.precio = precio;
        this.trazable = trazable;
        this.IVA = IVA;
        this.rubro = rubro;
        this.subRubro = subRubro;
    }
    
    

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isTrazable() {
        return trazable;
    }

    public void setTrazable(boolean trazable) {
        this.trazable = trazable;
    }

    public BigDecimal getIVA() {
        return IVA;
    }

    public void setIVA(BigDecimal IVA) {
        this.IVA = IVA;
    }

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public String getSubRubro() {
        return subRubro;
    }

    public void setSubRubro(String subRubro) {
        this.subRubro = subRubro;
    }

    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
