package datos;

import entidades.CteTipo;
import entidades.CteTipoSisLetra;
import entidades.Sucursal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author FrancoSili
 */
public class CteTipoResponse implements Payload {
    Integer idCteTipo; 
    int codigoComp; 
    String descCorta;
    String descripcion; 
    boolean cursoLegal;  
    String surenu;
    String observaciones;
    boolean requiereFormaPago;
    SisComprobanteResponse comprobante;
    List<SisLetraSisCodAfipResponse> letrasCodigos;
    
    
    public CteTipoResponse(CteTipo c) {
        this.idCteTipo = c.getIdCteTipo();
        this.codigoComp = c.getCodigoComp();
        this.descCorta = c.getDescCorta();
        this.descripcion = c.getDescripcion();
        this.cursoLegal = c.getCursoLegal();
        this.surenu = c.getSurenu();
        this.observaciones = c.getObservaciones();
        this.comprobante = new SisComprobanteResponse(c.getIdSisComprobante());
        this.requiereFormaPago = c.getRequiereFormaPago();
        this.letrasCodigos = new ArrayList<>();
    }

    public Integer getIdCteTipo() {
        return idCteTipo;
    }

    public void setIdCteTipo(Integer idCteTipo) {
        this.idCteTipo = idCteTipo;
    }

    public int getCodigoComp() {
        return codigoComp;
    }

    public void setCodigoComp(int codigoComp) {
        this.codigoComp = codigoComp;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isCursoLegal() {
        return cursoLegal;
    }

    public void setCursoLegal(boolean cursoLegal) {
        this.cursoLegal = cursoLegal;
    }

    public String getSurenu() {
        return surenu;
    }

    public void setSurenu(String surenu) {
        this.surenu = surenu;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    
    public SisComprobanteResponse getComprobante() {
        return comprobante;
    }

    public void setComprobante(SisComprobanteResponse comprobante) {
        this.comprobante = comprobante;
    }
    
    public void agregarLetrasCodigos(Collection<CteTipoSisLetra> letras, Sucursal sucursal) {
        for(CteTipoSisLetra l : letras) {
            SisLetraSisCodAfipResponse slr = new SisLetraSisCodAfipResponse(l);
            if(!l.getCteNumeradorCollection().isEmpty()) {
               slr.agregarNumeradores(l.getCteNumeradorCollection(), sucursal); 
            }
           
            this.letrasCodigos.add(slr);
        }
    }

    public boolean isRequiereFormaPago() {
        return requiereFormaPago;
    }

    public void setRequiereFormaPago(boolean requiereFormaPago) {
        this.requiereFormaPago = requiereFormaPago;
    }

    public List<SisLetraSisCodAfipResponse> getLetrasCodigos() {
        return letrasCodigos;
    }

    public void setLetrasCodigos(List<SisLetraSisCodAfipResponse> letrasCodigos) {
        this.letrasCodigos = letrasCodigos;
    }
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 
}
