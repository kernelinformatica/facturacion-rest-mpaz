package datos;

import entidades.CteNumerador;
import java.util.Date;

/**
 *
 * @author FrancoSili
 */
public class CteNumeradorResponse implements Payload {
    private Integer idCteNumerador;
    private Integer numerador;
    private String descripcion;
    private Date fechaApertura;
    private Date fechaCierre;
    private String cai;
    private Date vtoCai;
    private Boolean electronico;
    private PtoVentaResponse ptoVenta;
    private SisLetraSisCodAfipResponse letrasCodigos;

    public CteNumeradorResponse(CteNumerador c) {
        this.idCteNumerador = c.getIdCteNumerador();
        this.numerador = c.getNumerador();
        this.descripcion = c.getDescripcion();
        this.fechaApertura = c.getFechaApertura();
        this.fechaCierre = c.getFechaCierre();
        this.cai = c.getCai();
        this.vtoCai = c.getVtoCai();
        this.electronico = c.getElectronico();
        this.ptoVenta = new PtoVentaResponse(c.getIdPtoVenta());
        this.letrasCodigos = new SisLetraSisCodAfipResponse(c.getIdCteTipoSisLetra());
    }
    
    public Integer getIdCteNumerador() {
        return idCteNumerador;
    }

    public void setIdCteNumerador(Integer idCteNumerador) {
        this.idCteNumerador = idCteNumerador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public PtoVentaResponse getPtoVenta() {
        return ptoVenta;
    }

    public void setPtoVenta(PtoVentaResponse ptoVenta) {
        this.ptoVenta = ptoVenta;
    }
  
    public Date getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Integer getNumerador() {
        return numerador;
    }

    public void setNumerador(Integer numerador) {
        this.numerador = numerador;
    }

    public SisLetraSisCodAfipResponse getLetrasCodigos() {
        return letrasCodigos;
    }

    public void setLetrasCodigos(SisLetraSisCodAfipResponse letrasCodigos) {
        this.letrasCodigos = letrasCodigos;
    }

    public String getCai() {
        return cai;
    }

    public void setCai(String cai) {
        this.cai = cai;
    }

    public Date getVtoCai() {
        return vtoCai;
    }

    public void setVtoCai(Date vtoCai) {
        this.vtoCai = vtoCai;
    }

    public Boolean getElectronico() {
        return electronico;
    }

    public void setElectronico(Boolean electronico) {
        this.electronico = electronico;
    }
    
           
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
