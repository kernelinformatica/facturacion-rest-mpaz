package datos;

import entidades.PadronProveedor;
import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */

public class ProveedorResponse implements Payload {
    
    private Integer idPadronProveedor;
    private BigDecimal iibbRet;
    private BigDecimal iibbPer;
    private PadronGralResponse padronGral;

    public ProveedorResponse(PadronProveedor p) {
        this.idPadronProveedor = p.getIdPadronProveedor();
        this.iibbRet = p.getIibbRet();
        this.iibbPer = p.getIibbPer();
        this.padronGral = new PadronGralResponse(p.getIdPadronGral());
    }

    public Integer getIdPadronProveedor() {
        return idPadronProveedor;
    }

    public void setIdPadronProveedor(Integer idPadronProveedor) {
        this.idPadronProveedor = idPadronProveedor;
    }

    public BigDecimal getIibbRet() {
        return iibbRet;
    }

    public void setIibbRet(BigDecimal iibbRet) {
        this.iibbRet = iibbRet;
    }

    public BigDecimal getIibbPer() {
        return iibbPer;
    }

    public void setIibbPer(BigDecimal iibbPer) {
        this.iibbPer = iibbPer;
    }

    public PadronGralResponse getPadronGral() {
        return padronGral;
    }

    public void setPadronGral(PadronGralResponse padronGral) {
        this.padronGral = padronGral;
    }
    
    

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
