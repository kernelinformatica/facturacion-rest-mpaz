package datos;

import entidades.Rubro;

/**
 *
 * @author Franco Sili
 */
public class RubroResponse implements Payload{
    private int idRubro;
    private EmpresaResponse empresa;
    private String descripcion;
    private int codigoRubro;


    public RubroResponse(Rubro rubro) {
        this.idRubro = rubro.getIdRubros();
        this.empresa = new EmpresaResponse(rubro.getIdEmpresa());
        this.descripcion = rubro.getDescripcion();
        this.codigoRubro = rubro.getCodRubro();
    }
    
    public int getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(int idRubro) {
        this.idRubro = idRubro;
    }

    public EmpresaResponse getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaResponse empresa) {
        this.empresa = empresa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    public int getCodigoRubro() {
        return codigoRubro;
    }

    public void setCodigoRubro(int codigoRubro) {
        this.codigoRubro = codigoRubro;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}


