package datos;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */
public class LoteResponse implements Payload {
    private Integer idLote;
    private String nroLote;
    private int item;
    private String serie;
    private Date fechaElab;
    private Date fechaVto;
    private boolean vigencia;
    private String comprobante;
    private BigDecimal numero;
    private String codProducto;
    private String descripcionProd;
    private BigDecimal stock;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private String receta;
    private int cantidad;
    private BigDecimal stockNegativo;
    private Integer idProducto;

    public LoteResponse(Integer idLote, String nroLote, int item, String serie, Date fechaElab, Date fechaVto, boolean vigencia, String comprobante, BigDecimal numero, String codProducto, String descripcionProd, BigDecimal stock, BigDecimal ingresos, BigDecimal egresos, BigDecimal stockNegativo, Integer idProducto) {
        this.idLote = idLote;
        this.nroLote = nroLote;
        this.item = item;
        this.serie = serie;
        this.fechaElab = fechaElab;
        this.fechaVto = fechaVto;
        this.vigencia = vigencia;
        this.comprobante = comprobante;
        this.numero = numero;
        this.codProducto = codProducto;
        this.descripcionProd = descripcionProd;
        this.stock = stock;
        this.ingresos = ingresos;
        this.egresos = egresos;
        this.receta = "";
        this.cantidad = 0;
        this.stockNegativo = stockNegativo;
        this.idProducto = idProducto;
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    
    public String getNroLote() {
        return nroLote;
    }

    public void setNroLote(String nroLote) {
        this.nroLote = nroLote;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Date getFechaElab() {
        return fechaElab;
    }

    public void setFechaElab(Date fechaElab) {
        this.fechaElab = fechaElab;
    }

    public Date getFechaVto() {
        return fechaVto;
    }

    public void setFechaVto(Date fechaVto) {
        this.fechaVto = fechaVto;
    }

    public boolean isVigencia() {
        return vigencia;
    }

    public void setVigencia(boolean vigencia) {
        this.vigencia = vigencia;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public BigDecimal getNumero() {
        return numero;
    }

    public void setNumero(BigDecimal numero) {
        this.numero = numero;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getDescripcionProd() {
        return descripcionProd;
    }

    public void setDescripcionProd(String descripcionProd) {
        this.descripcionProd = descripcionProd;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public String getReceta() {
        return receta;
    }

    public void setReceta(String receta) {
        this.receta = receta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getStockNegativo() {
        return stockNegativo;
    }

    public void setStockNegativo(BigDecimal stockNegativo) {
        this.stockNegativo = stockNegativo;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
