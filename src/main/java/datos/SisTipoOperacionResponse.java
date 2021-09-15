package datos;

import entidades.SisTipoOperacion;

/**
 *
 * @author FrancoSili
 */
public class SisTipoOperacionResponse implements Payload {
    private Integer idSisTipoOperacion;
    private String descripcion;
    private boolean canje;
    private SisModuloResponse modulo;
    private Boolean depositoOrigen;
    private Boolean depositoDestino;    
    
    public SisTipoOperacionResponse(SisTipoOperacion s) {
        this.idSisTipoOperacion = s.getIdSisTipoOperacion();
        this.descripcion = s.getDescripcion();
        this.canje = s.getCanje();
        this.depositoOrigen = s.getDepositoOrigen();
        this.depositoDestino = s.getDepositoDestino();
        this.modulo = new SisModuloResponse(s.getIdSisModulos());
    }
    
    public Integer getIdSisTipoOperacion() {
        return idSisTipoOperacion;
    }

    public void setIdSisTipoOperacion(Integer idSisTipoOperacion) {
        this.idSisTipoOperacion = idSisTipoOperacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public SisModuloResponse getModulo() {
        return modulo;
    }

    public void setModulo(SisModuloResponse modulo) {
        this.modulo = modulo;
    }

    public boolean isCanje() {
        return canje;
    }

    public void setCanje(boolean canje) {
        this.canje = canje;
    }

    public Boolean getDepositoOrigen() {
        return depositoOrigen;
    }

    public void setDepositoOrigen(Boolean depositoOrigen) {
        this.depositoOrigen = depositoOrigen;
    }

    public Boolean getDepositoDestino() {
        return depositoDestino;
    }

    public void setDepositoDestino(Boolean depositoDestino) {
        this.depositoDestino = depositoDestino;
    }
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
