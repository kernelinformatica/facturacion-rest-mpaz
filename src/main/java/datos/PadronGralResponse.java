package datos;

import entidades.PadronGral;

/**
 *
 * @author FrancoSili
 */

public class PadronGralResponse implements Payload {
    private Integer idPadronGral;
    private String nombre;
    private String apellido;
    private String cuit;
    private String domicilio;
    private String nro;
    private String localidad;
    private CategoriaResponse categoria;
    private SisSitIvaResponse sisSitIVA;

    public PadronGralResponse(PadronGral p) {
        this.idPadronGral = p.getIdPadronGral();
        this.nombre = p.getNombre();
        this.apellido = p.getApellido();
        this.cuit = p.getCuit();
        this.domicilio = p.getDomicilio();
        this.nro = p.getNro();
        this.localidad = p.getLocalidad();
        this.categoria = new CategoriaResponse(p.getIdCategoria());
        this.sisSitIVA = new SisSitIvaResponse(p.getIdSisSitIVA());
    }
       
    public Integer getIdPadronGral() {
        return idPadronGral;
    }

    public void setIdPadronGral(Integer idPadronGral) {
        this.idPadronGral = idPadronGral;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getNro() {
        return nro;
    }

    public void setNro(String nro) {
        this.nro = nro;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public CategoriaResponse getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponse categoria) {
        this.categoria = categoria;
    }

    public SisSitIvaResponse getSisSitIVA() {
        return sisSitIVA;
    }

    public void setSisSitIVA(SisSitIvaResponse sisSitIVA) {
        this.sisSitIVA = sisSitIVA;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
