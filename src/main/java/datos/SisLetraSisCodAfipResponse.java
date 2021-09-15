package datos;

import entidades.CteNumerador;
import entidades.CteTipoSisLetra;
import entidades.Sucursal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author FrancoSili
 */
public class SisLetraSisCodAfipResponse implements Payload{

private Integer idCteTipoSisLetra;
private SisLetraResponse letra;
private SisCodigoAfipResponse codigoAfip;
private List<CteNumeradorResponse> numeradores;
private CteTipoResponse cteTipo;

    public SisLetraSisCodAfipResponse(CteTipoSisLetra l) {
        this.idCteTipoSisLetra = l.getIdCteTipoSisLetra();
        this.letra = new SisLetraResponse(l.getIdSisLetra());
        this.codigoAfip = new SisCodigoAfipResponse(l.getIdSisCodigoAfip());
        this.cteTipo = new CteTipoResponse(l.getIdCteTipo());
        this.numeradores = new ArrayList<>();
    }

    public SisLetraResponse getLetra() {
        return letra;
    }

    public void setLetra(SisLetraResponse letra) {
        this.letra = letra;
    }

    public SisCodigoAfipResponse getCodigoAfip() {
        return codigoAfip;
    }

    public void setCodigoAfip(SisCodigoAfipResponse codigoAfip) {
        this.codigoAfip = codigoAfip;
    }

    public List<CteNumeradorResponse> getNumeradores() {
        return numeradores;
    }

    public void setNumeradores(List<CteNumeradorResponse> numeradores) {
        this.numeradores = numeradores;
    }

    public CteTipoResponse getCteTipo() {
        return cteTipo;
    }

    public void setCteTipo(CteTipoResponse cteTipo) {
        this.cteTipo = cteTipo;
    }

    public Integer getIdCteTipoSisLetra() {
        return idCteTipoSisLetra;
    }

    public void setIdCteTipoSisLetra(Integer idCteTipoSisLetra) {
        this.idCteTipoSisLetra = idCteTipoSisLetra;
    }
 
   
    
    public void agregarNumeradores(Collection<CteNumerador> numeradores, Sucursal sucursal) {
        for(CteNumerador p : numeradores) {
            if(p.getIdPtoVenta().getIdSucursal().equals(sucursal)) {
                CteNumeradorResponse cnr = new CteNumeradorResponse(p);
                this.numeradores.add(cnr);
            }
        }
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void agregarNumeradores(Collection<CteNumerador> numeradores, Integer idPtoVenta) {
        for(CteNumerador p : numeradores) {
            if(Objects.equals(p.getIdPtoVenta().getIdPtoVenta(), idPtoVenta)) {
                CteNumeradorResponse cnr = new CteNumeradorResponse(p);
                this.numeradores.add(cnr);
            }
        }
    }
}
