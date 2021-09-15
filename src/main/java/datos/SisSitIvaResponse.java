package datos;

import entidades.SisSitIVA;

/**
 *
 * @author FrancoSili
 */
public class SisSitIvaResponse implements Payload {

    private Integer idSisSitIVA;
    private String descripcion;
    private String descCorta;
    private String letra;
    
    public SisSitIvaResponse(SisSitIVA s) {
        this.descCorta = s.getDesCorta();
        this.descripcion = s.getDescripcion();
        this.idSisSitIVA = s.getIdSisSitIVA();
        this.letra = s.getLetra();
    }

    public Integer getIdSisSitIVA() {
        return idSisSitIVA;
    }

    public void setIdSisSitIVA(Integer idSisSitIVA) {
        this.idSisSitIVA = idSisSitIVA;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }
    

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
