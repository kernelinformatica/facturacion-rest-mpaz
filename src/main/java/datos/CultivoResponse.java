package datos;

import entidades.Cultivo;

/**
 *
 * @author FrancoSili
 */

public class CultivoResponse implements Payload {
    private Integer idCultivo;
    private String descripcion;
    private String cosecha;

    public CultivoResponse(Cultivo c) {
        this.idCultivo = c.getIdCultivo();
        this.descripcion = c.getDescripcion();
        this.cosecha = c.getCosecha();
    }

    public Integer getIdCultivo() {
        return idCultivo;
    }

    public void setIdCultivo(Integer idCultivo) {
        this.idCultivo = idCultivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCosecha() {
        return cosecha;
    }

    public void setCosecha(String cosecha) {
        this.cosecha = cosecha;
    }
      
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }   
}
