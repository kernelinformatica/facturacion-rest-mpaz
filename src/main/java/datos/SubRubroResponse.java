package datos;

import entidades.SubRubro;

/**
 *
 * @author Franco Sili
 */
public class SubRubroResponse implements Payload {


    private int idSubRubro;
    private String descripcion;
    private RubroResponse rubro;
    private int codigoSubRubro;


    public SubRubroResponse(SubRubro subRubro) {
        this.idSubRubro = subRubro.getIdSubRubros();
        this.descripcion = subRubro.getDescripcion();
        this.rubro = new RubroResponse(subRubro.getIdRubros());
        this.codigoSubRubro = subRubro.getCodSubRubros();
    }

    public int getIdSubRubro() {
        return idSubRubro;
    }

    public void setIdSubRubro(int idSubRubro) {
        this.idSubRubro = idSubRubro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public RubroResponse getRubro() {
        return rubro;
    }

    public void setRubro(RubroResponse rubro) {
        this.rubro = rubro;
    }
    
    public int getCodigoSubRubro() {
        return codigoSubRubro;
    }

    public void setCodigoSubRubro(int codigoSubRubro) {
        this.codigoSubRubro = codigoSubRubro;
    }
    

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
