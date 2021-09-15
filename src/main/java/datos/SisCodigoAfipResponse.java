package datos;

import entidades.SisCodigoAfip;

/**
 *
 * @author FrancoSili
 */

public class SisCodigoAfipResponse implements Payload {
    private Integer idSisCodigoAfip;
    private int codigoAfip;
    private String descripcion;

    public SisCodigoAfipResponse(SisCodigoAfip s) {
        this.idSisCodigoAfip = s.getIdSisCodigoAfip();
        this.codigoAfip = s.getCodigoAfip();
        this.descripcion = s.getDescripcion();
    }
    
    public Integer getIdSisCodigoAfip() {
        return idSisCodigoAfip;
    }

    public void setIdSisCodigoAfip(Integer idSisCodigoAfip) {
        this.idSisCodigoAfip = idSisCodigoAfip;
    }

    public int getCodigoAfip() {
        return codigoAfip;
    }

    public void setCodigoAfip(int codigoAfip) {
        this.codigoAfip = codigoAfip;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }  

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
