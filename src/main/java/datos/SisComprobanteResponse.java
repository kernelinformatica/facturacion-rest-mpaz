package datos;

import entidades.SisComprobante;
import entidades.SisMonedas;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dario Quiroga
 */
public class SisComprobanteResponse implements Payload{
    private Integer idSisComprobantes;
    private String descripcion;
    private String imputacion;
    private Integer orden;
    private SisModuloResponse modulo;
    private Boolean incluyeNeto;
    private Boolean incluyeIva;
    private Boolean relacionadosMultiples;
    private String referencia;
    private Boolean difCotizacion;
    private Integer idSisOperacionComprobante;
    private Boolean admiteRelacionMultiple;
    private Boolean usaContrato;
    private Boolean permiteImporteCero;
    private Boolean usaRelacion;
    private Boolean relacionadoObligatorio;
    private String observaciones;
    private List<SisMonedasResponse> monedas = new ArrayList<>();
    
    public SisComprobanteResponse(SisComprobante s) {
       this.idSisComprobantes = s.getIdSisComprobantes();
       this.descripcion = s.getDescripcion();
       this.imputacion = s.getImputacion();
       this.modulo = new SisModuloResponse(s.getIdSisModulos());
       
       
    }

    public Boolean getRelacionadoObligatorio() {
        return relacionadoObligatorio;
    }

    public void setRelacionadoObligatorio(Boolean relacionadoObligatorio) {
        this.relacionadoObligatorio = relacionadoObligatorio;
    }
    
    public Integer getIdSisComprobantes() {
        return idSisComprobantes;
    }

    public void setIdSisComprobantes(Integer idSisComprobantes) {
        this.idSisComprobantes = idSisComprobantes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImputacion() {
        return imputacion;
    }

    public void setImputacion(String imputacion) {
        this.imputacion = imputacion;
    }

    public SisModuloResponse getModulo() {
        return modulo;
    }

    public void setModulo(SisModuloResponse modulo) {
        this.modulo = modulo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getIncluyeNeto() {
        return incluyeNeto;
    }

    public void setIncluyeNeto(Boolean incluyeNeto) {
        this.incluyeNeto = incluyeNeto;
    }

    public Boolean getIncluyeIva() {
        return incluyeIva;
    }

    public void setIncluyeIva(Boolean incluyeIva) {
        this.incluyeIva = incluyeIva;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Boolean getDifCotizacion() {
        return difCotizacion;
    }

    public void setDifCotizacion(Boolean difCotizacion) {
        this.difCotizacion = difCotizacion;
    }

    public Integer getIdSisOperacionComprobante() {
        return idSisOperacionComprobante;
    }

    public void setIdSisOperacionComprobante(Integer idSisOperacionComprobante) {
        this.idSisOperacionComprobante = idSisOperacionComprobante;
    }

    public Boolean getRelacionadosMultiples() {
        return relacionadosMultiples;
    }

    public void setRelacionadosMultiples(Boolean relacionadosMultiples) {
        this.relacionadosMultiples = relacionadosMultiples;
    }

    public Boolean getAdmiteRelacionMultiple() {
        return admiteRelacionMultiple;
    }

    public void setAdmiteRelacionMultiple(Boolean admiteRelacionMultiple) {
        this.admiteRelacionMultiple = admiteRelacionMultiple;
    }

    public Boolean getUsaContrato() {
        return usaContrato;
    }

    public void setUsaContrato(Boolean usaContrato) {
        this.usaContrato = usaContrato;
    }

    public Boolean getPermiteImporteCero() {
        return permiteImporteCero;
    }

    public void setPermiteImporteCero(Boolean permiteImporteCero) {
        this.permiteImporteCero = permiteImporteCero;
    }

    public List<SisMonedasResponse> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<SisMonedasResponse> monedas) {
        this.monedas = monedas;
    }

    public Boolean getUsaRelacion() {
        return usaRelacion;
    }

    public void setUsaRelacion(Boolean usaRelacion) {
        this.usaRelacion = usaRelacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    
    
    public void agregarMonedas(List<SisMonedas> todas) {
        for(SisMonedas t : todas) {
            SisMonedasResponse sr = new SisMonedasResponse(t);
            this.getMonedas().add(sr);
        }
    }
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
