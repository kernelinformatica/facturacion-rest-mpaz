package datos;

import entidades.PadronClientes;

/**
 *
 * @author FrancoSili
 */
public class ClienteResponse implements Payload{
    private Integer idCliente;
    private PadronGralResponse padronGral;
    private VendedorResponse vendedor;

    public ClienteResponse(PadronClientes c) {
        this.idCliente = c.getIdCliente();
        this.padronGral = new PadronGralResponse(c.getIdPadronGral());
        this.vendedor = new VendedorResponse(c.getIdVendedor());
    }   
    
    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public PadronGralResponse getPadronGral() {
        return padronGral;
    }

    public void setPadronGral(PadronGralResponse padronGral) {
        this.padronGral = padronGral;
    }

    public VendedorResponse getVendedor() {
        return vendedor;
    }

    public void setVendedor(VendedorResponse vendedor) {
        this.vendedor = vendedor;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
