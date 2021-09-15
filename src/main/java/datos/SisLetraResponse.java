package datos;

import entidades.SisLetra;

/**
 *
 * @author Franco Sili
 */

public class SisLetraResponse implements Payload{
    private int idSisLetra;
    private String letra;

    public SisLetraResponse(SisLetra s) {
        this.idSisLetra = s.getIdSisLetra();
        this.letra = s.getLetra();
    }

    
    public int getIdSisLetra() {
        return idSisLetra;
    }

    public void setIdSisLetra(int idSisLetra) {
        this.idSisLetra = idSisLetra;
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
