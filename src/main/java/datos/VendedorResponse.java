package datos;

import entidades.PadronVendedor;
import java.math.BigDecimal;

/**
 *
 * @author FrancoSili
 */

public class VendedorResponse implements Payload {
    private Integer idVendedor;
    private BigDecimal porcentaje;
    private PadronGralResponse padronGral;

    public VendedorResponse(PadronVendedor v) {
        this.idVendedor = v.getIdVendedor();
        this.porcentaje = v.getPorcentaje();
        this.padronGral = new PadronGralResponse(v.getIdPadronGral());
    }
    
    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
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
