package datos;

import entidades.SisUnidad;

/**
 *
 * @author Franco Sili
 */
public class SisUnidadResponse implements Payload {
    private Integer idUnidad;
    private String descripcion;

    public SisUnidadResponse(SisUnidad s) {
        this.idUnidad = s.getIdUnidad();
        this.descripcion = s.getDescripcion();
    }
    
    public Integer getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripción) {
        this.descripcion = descripcion;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
