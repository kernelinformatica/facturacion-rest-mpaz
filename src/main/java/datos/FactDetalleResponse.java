package datos;

import entidades.FactDetalle;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Dario
 */
public class FactDetalleResponse implements Payload{
    private String comprobante;
    private long numero;
    private Date fechaEmision;
    private String codProducto;
    private String articulo;
    private BigDecimal original;
    private BigDecimal pendiente;
    private BigDecimal precio;
    private BigDecimal dolar;
    private String moneda;
    private BigDecimal porCalc;
    private BigDecimal ivaPorc;
    private int deposito;
    private BigDecimal importe;
    private int factCab;
    private String vendedor;
    private String descuento;
    private BigDecimal precioDesc;
    private String unidadDescuento;
    

    public FactDetalleResponse(String comprobante, long numero, Date fechaEmision, String codProducto, String articulo, BigDecimal original, BigDecimal pendiente, BigDecimal precio, BigDecimal dolar, String moneda, BigDecimal porCalc, BigDecimal ivaPorc, int deposito, BigDecimal importe, int idFactCab, String vendedor,String descuento, BigDecimal precioDesc, String unidadDescuento) {
        this.comprobante = comprobante;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.codProducto = codProducto;
        this.articulo = articulo;
        this.original = original;
        this.pendiente = pendiente;
        this.precio = precio;
        this.dolar = dolar;
        this.moneda = moneda;
        this.porCalc = porCalc;
        this.ivaPorc = ivaPorc;
        this.deposito = deposito;
        this.importe = importe;
        this.factCab = idFactCab;
        this.vendedor = vendedor;
        this.descuento = descuento;
        this.precioDesc = precioDesc;
        this.unidadDescuento = unidadDescuento;
    }
    
    public FactDetalleResponse(FactDetalle d) {
        this.comprobante = d.getIdFactCab().getIdCteTipo().getDescripcion();
        this.numero = d.getIdFactCab().getNumero();
        this.fechaEmision = d.getIdFactCab().getFechaEmision();
        this.codProducto = d.getCodProducto();
        this.articulo = d.getDetalle();
        this.original = d.getCantidad();
        this.pendiente = BigDecimal.ZERO;
        this.precio = d.getPrecio();
        this.dolar = d.getIdFactCab().getCotDolar();
        this.moneda = d.getIdFactCab().getIdmoneda().getDescripcion();
        this.porCalc = d.getPorcCalc();
        this.ivaPorc = d.getIvaPorc();
        this.deposito = d.getIdDepositos().getCodigoDep();
        this.importe = d.getImporte();
        this.factCab = d.getIdFactCab().getIdFactCab();
        this.vendedor = "";
    }

    
    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public BigDecimal getOriginal() {
        return original;
    }

    public void setOriginal(BigDecimal original) {
        this.original = original;
    }

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getDolar() {
        return dolar;
    }

    public void setDolar(BigDecimal dolar) {
        this.dolar = dolar;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getPorCalc() {
        return porCalc;
    }

    public void setPorCalc(BigDecimal porCalc) {
        this.porCalc = porCalc;
    }

    public BigDecimal getIvaPorc() {
        return ivaPorc;
    }

    public void setIvaPorc(BigDecimal ivaPorc) {
        this.ivaPorc = ivaPorc;
    }

    public int getDeposito() {
        return deposito;
    }

    public void setDeposito(int deposito) {
        this.deposito = deposito;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public int getFactCab() {
        return factCab;
    }

    public void setFactCab(int factCab) {
        this.factCab = factCab;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getPrecioDesc() {
        return precioDesc;
    }

    public void setPrecioDesc(BigDecimal precioDesc) {
        this.precioDesc = precioDesc;
    }

    public String getUnidadDescuento() {
        return unidadDescuento;
    }

    public void setUnidadDescuento(String unidadDescuento) {
        this.unidadDescuento = unidadDescuento;
    }
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
