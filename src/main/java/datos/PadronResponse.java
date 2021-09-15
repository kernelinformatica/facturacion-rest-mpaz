package datos;

import entidades.Padron;

/**
 *
 * @author FrancoSili
 */
public class PadronResponse implements Payload{
    private Integer padronCodigo;
    private String padronApelli;
    private String padronNombre;
    private String padronDomicilio;
    private String padronNro;
    private Integer codigoPostal;
    private Long cuit;
    private CondIvaResponse condIva;

    public PadronResponse(Padron p) {
        this.padronCodigo = p.getPadronCodigo();
        this.padronApelli = p.getPadronApelli();
        this.padronNombre = p.getPadronNombre();
        this.codigoPostal = p.getCodigoPostal();
        this.cuit = p.getPadronCuit11();
        this.padronNro = p.getPadronDomnro();
        this.padronDomicilio = p.getPadronDomici();
        
        this.condIva = new CondIvaResponse(p.getCondIva());
    }
    
    public Integer getPadronCodigo() {
        return padronCodigo;
    }

    public void setPadronCodigo(Integer padronCodigo) {
        this.padronCodigo = padronCodigo;
    }

    public String getPadronApelli() {
        return padronApelli;
    }

    public void setPadronApelli(String padronApelli) {
        this.padronApelli = padronApelli;
    }

    public String getPadronNombre() {
        return padronNombre;
    }

    public void setPadronNombre(String padronNombre) {
        this.padronNombre = padronNombre;
    }

    public Integer getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(Integer codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public CondIvaResponse getCondIva() {
        return condIva;
    }

    public void setCondIva(CondIvaResponse condIva) {
        this.condIva = condIva;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public String getPadronDomicilio() {
        return padronDomicilio;
    }

    public void setPadronDomicilio(String padronDomicilio) {
        this.padronDomicilio = padronDomicilio;
    }

    public String getPadronNro() {
        return padronNro;
    }

    public void setPadronNro(String padronNro) {
        this.padronNro = padronNro;
    }
    
    
    
    

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
