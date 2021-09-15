package datos;

import entidades.SisTipoModelo;

/**
 *
 * @author FrancoSili
 */
public class SisTipoModeloResponse implements Payload{
    private int idTipoModelo;
    private String descripcion;
    private Integer orden;

    public SisTipoModeloResponse(SisTipoModelo s) {
        this.idTipoModelo = s.getIdSisTipoModelo();
        this.descripcion = s.getTipo();
        this.orden = s.getOrden();
    }
    
    public int getIdTipoModelo() {
        return idTipoModelo;
    }

    public void setIdTipoModelo(int idTipoModelo) {
        this.idTipoModelo = idTipoModelo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }



    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
