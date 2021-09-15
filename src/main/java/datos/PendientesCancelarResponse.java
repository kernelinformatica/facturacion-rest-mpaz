package datos;

import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */
public class PendientesCancelarResponse implements Payload{
    private String comprobante;
    private String numero;   
    private BigDecimal original;
    private BigDecimal pendiente;
    private BigDecimal precio;
    private BigDecimal dolar;
    private String moneda;
    private BigDecimal porCalc;
    private BigDecimal ivaPorc;
    private Integer deposito;
    private Integer idFactDetalleImputa;
    private Integer idFactCabImputada;
    private BigDecimal descuento;
    private String tipoDescuento;
    private BigDecimal cantBultos;
    private String despacho;
    private String observaciones;
    private Integer itemImputada;
    private BigDecimal importe;
    private ProductoResponse producto;
    private Integer idListaPrecio;
    private String codigoListaPrecio;
    private String letra;
    private Integer idFactDetalle;
    private BigDecimal diferenciaPrecio;
    private BigDecimal recargo;
    private BigDecimal recargoTotal;
    private Integer diasLibres;
    private Integer diasResultantes;
    private BigDecimal cotaInferior;
    private BigDecimal cotaSuperior;
    
    public PendientesCancelarResponse(String comprobante, String numero, BigDecimal original, BigDecimal pendiente, BigDecimal precio, BigDecimal dolar, String moneda, BigDecimal porCalc, BigDecimal ivaPorc, Integer deposito, Integer idFactDetalleImputa, Integer idFactCabImputada, BigDecimal descuento, String tipoDescuento, BigDecimal cantBultos, String despacho, String observaciones, Integer itemImputada,BigDecimal importe,Integer idListaPrecio, String codigoListaPrecio, String letra,Integer idFactDetalle, ProductoResponse p) {
        this.comprobante = comprobante;
        this.numero = numero;
        this.original = original;
        this.pendiente = pendiente;
        this.precio = precio;
        this.dolar = dolar;
        this.moneda = moneda;
        this.porCalc = porCalc;
        this.ivaPorc = ivaPorc;
        this.deposito = deposito;
        this.idFactDetalleImputa = idFactDetalleImputa;
        this.idFactCabImputada = idFactCabImputada;
        this.descuento = descuento;
        this.tipoDescuento = tipoDescuento;
        this.cantBultos = cantBultos;
        this.despacho = despacho;
        this.observaciones = observaciones;
        this.itemImputada = itemImputada;
        this.producto = p;
        this.importe = importe;
        this.idListaPrecio = idListaPrecio;
        this.codigoListaPrecio = codigoListaPrecio;
        this.letra = letra;
        this.idFactDetalle = idFactDetalle;
        this.diferenciaPrecio = BigDecimal.ZERO;
        this.recargo = BigDecimal.ZERO;
        this.recargoTotal = BigDecimal.ZERO;
        this.diasLibres = 0;
        this.diasResultantes = 0;
        this.cotaInferior = null;
        this.cotaSuperior = null;
    }
    public PendientesCancelarResponse(ProductoResponse p) {
        this.comprobante = "";
        this.numero = "0";
        this.original = new BigDecimal(0);
        this.pendiente = new BigDecimal(0);
        this.precio = p.getPrecioVentaProv();
        this.dolar = new BigDecimal(0);
        this.moneda = " ";
        this.porCalc = new BigDecimal(0);
        this.ivaPorc = p.getIVA().getPorcIVA();
        this.deposito = 0;
        this.idFactDetalleImputa = null;
        this.idFactCabImputada = null;
        this.descuento = new BigDecimal(0);
        this.tipoDescuento = "%";
        this.cantBultos = new BigDecimal(0);;
        this.despacho = "";
        this.observaciones = "";
        this.itemImputada = 0;
        this.producto = p;
        this.diferenciaPrecio = BigDecimal.ZERO;
        this.recargo = BigDecimal.ZERO;
        this.recargoTotal = BigDecimal.ZERO;
        this.diasLibres = 0;
        this.diasResultantes = 0;
        this.cotaInferior = null;
        this.cotaSuperior = null;
    }



    public ProductoResponse getProducto() {
        return producto;
    }

    public void setProducto(ProductoResponse producto) {
        this.producto = producto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getIvaPorc() {
        return ivaPorc;
    }

    public void setIvaPorc(BigDecimal ivaPorc) {
        this.ivaPorc = ivaPorc;
    }

    public Integer getDeposito() {
        return deposito;
    }

    public void setDeposito(Integer deposito) {
        this.deposito = deposito;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public Integer getIdFactDetalleImputa() {
        return idFactDetalleImputa;
    }

    public void setIdFactDetalleImputa(Integer idFactDetalleImputa) {
        this.idFactDetalleImputa = idFactDetalleImputa;
    }

    public Integer getIdFactCabImputada() {
        return idFactCabImputada;
    }

    public void setIdFactCabImputada(Integer idFactCabImputada) {
        this.idFactCabImputada = idFactCabImputada;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getCantBultos() {
        return cantBultos;
    }

    public void setCantBultos(BigDecimal cantBultos) {
        this.cantBultos = cantBultos;
    }

    public String getDespacho() {
        return despacho;
    }

    public void setDespacho(String despacho) {
        this.despacho = despacho;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public Integer getItemImputada() {
        return itemImputada;
    }

    public void setItemImputada(Integer itemImputada) {
        this.itemImputada = itemImputada;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Integer getIdListaPrecio() {
        return idListaPrecio;
    }

    public void setIdListaPrecio(Integer idListaPrecio) {
        this.idListaPrecio = idListaPrecio;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getCodigoListaPrecio() {
        return codigoListaPrecio;
    }

    public void setCodigoListaPrecio(String codigoListaPrecio) {
        this.codigoListaPrecio = codigoListaPrecio;
    }

    public Integer getIdFactDetalle() {
        return idFactDetalle;
    }

    public void setIdFactDetalle(Integer idFactDetalle) {
        this.idFactDetalle = idFactDetalle;
    }
    
    
    public BigDecimal getDiferenciaPrecio() {
        return diferenciaPrecio;
    }
    
    public void setDiferenciaPrecio(BigDecimal diferenciaPrecio) {
        this.diferenciaPrecio = diferenciaPrecio;
    }
    
    public BigDecimal getRecargo() {
        return recargo;
    }
    
    public void setRecargo(BigDecimal recargo) {
        this.recargo = recargo;
    }
    
    public BigDecimal getRecargoTotal() {
        return recargoTotal;
    }
    
    public void setRecargoTotal(BigDecimal recargoTotal) {
        this.recargoTotal = recargoTotal;
    }
    
    public Integer getDiasLibres() {
        return diasLibres;
    }
    
    public void setDiasLibres(Integer diasLibres) {
        this.diasLibres = diasLibres;
    }
    
    public Integer getDiasResultantes() {
        return diasResultantes;
    }
    
    public void setDiasResultantes(Integer diasResultantes) {
        this.diasResultantes = diasResultantes;
    }
    
    public BigDecimal getCotaInferior() {
        return cotaInferior;
    }
    
    public void setCotaInferior(BigDecimal cotaInferior) {
        this.cotaInferior = cotaInferior;
    }
    
    public BigDecimal getCotaSuperior() {
        return cotaSuperior;
    }
    
    public void setCotaSuperior(BigDecimal cotaSuperior) {
        this.cotaSuperior = cotaSuperior;
    }
    

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
