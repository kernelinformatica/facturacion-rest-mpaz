package datos;

import entidades.Marca;

/**
 *
 * @author FrancoSili
 */
public class MarcaResponse implements Payload {
    private Integer idMarcas;
    private String descripcion;

    public MarcaResponse(Marca m) {
        this.idMarcas = m.getIdMarcas();
        this.descripcion = m.getDescripcion();
    }

    public MarcaResponse(Integer idMarcas, String descripcion) {
        this.idMarcas = idMarcas;
        this.descripcion = descripcion;
    }
    
    public Integer getIdMarcas() {
        return idMarcas;
    }

    public void setIdMarcas(Integer idMarcas) {
        this.idMarcas = idMarcas;
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
