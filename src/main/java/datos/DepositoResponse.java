package datos;

import entidades.Deposito;

/**
 *
 * @author Dario Quiroga
 */
public class DepositoResponse implements Payload {
    private Integer idDeposito;    
    private Integer codigoDep;   
    private String descripcion;    
    private String domicilio;   
    private String codigoPostal;

    public DepositoResponse (Integer aInt, Integer aInt0, String string, String string0, String string1){
      this.idDeposito = aInt;
      this.codigoDep =aInt0;
      this.descripcion = string;
      this.codigoPostal = string0;
      this.domicilio = string1;
    }
  
    
    public Integer getIdDeposito() {
        return idDeposito;
    }

    public void setIdDeposito(Integer idDeposito) {
        this.idDeposito = idDeposito;
    }

    public Integer getCodigoDep() {
        return codigoDep;
    }

    public void setCodigoDep(Integer codigoDep) {
        this.codigoDep = codigoDep;
    }

   

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Operación no compatible. (getClassName)"); //To change body of generated methods, choose Tools | Templates.
    }

}
