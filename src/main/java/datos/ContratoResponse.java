package datos;

import entidades.Contrato;
import entidades.ContratoDet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author FrancoSili
 */
public class ContratoResponse implements Payload {
    private Integer idContratos;   
    private String contratoNro;
    private Integer idPadron;
    private String padronApelli;
    private String padronNombre;
    private Date fechaNacimiento;
    private String nacionalidad;
    private String profesion;
    private String documento;
    private String padre;
    private String madre;
    private Integer kilos;
    private Integer kilosCumplidos;
    private Integer cosecha;
    private String observaciones;
    private SisCanjeResponse sisCanje;
    private Date fechaVto;
    private List<ContratoDetResponse> contratoDet;

    public ContratoResponse(Contrato c) {
        this.idContratos = c.getIdContratos();
        this.contratoNro = c.getContratoNro();
        this.idPadron = c.getIdPadron();
        this.padronApelli = c.getApellidoCliente();
        this.padronNombre = c.getNombreCliente();
        this.fechaNacimiento = c.getFechaNacimiento();
        this.nacionalidad = c.getNacionalidad();
        this.profesion = c.getProfesion();
        this.documento = c.getDocumento();
        this.padre = c.getPadre();
        this.madre = c.getMadre();
        this.kilos = c.getKilos();
        this.cosecha = c.getCosecha();
        this.observaciones = c.getObservaciones();
        this.sisCanje = new SisCanjeResponse(c.getIdSisCanje());
        this.contratoDet = new ArrayList<>();
        this.fechaVto = c.getFechaVto();
        this.kilosCumplidos = 0;
    }    

    public Integer getIdContratos() {
        return idContratos;
    }

    public void setIdContratos(Integer idContratos) {
        this.idContratos = idContratos;
    }

    public String getContratoNro() {
        return contratoNro;
    }

    public void setContratoNro(String contratoNro) {
        this.contratoNro = contratoNro;
    }

    public Integer getIdPadron() {
        return idPadron;
    }

    public void setIdPadron(Integer idPadron) {
        this.idPadron = idPadron;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getPadre() {
        return padre;
    }

    public void setPadre(String padre) {
        this.padre = padre;
    }

    public String getMadre() {
        return madre;
    }

    public void setMadre(String madre) {
        this.madre = madre;
    }

    public Integer getKilos() {
        return kilos;
    }

    public void setKilos(Integer kilos) {
        this.kilos = kilos;
    }

    public Integer getCosecha() {
        return cosecha;
    }

    public void setCosecha(Integer cosecha) {
        this.cosecha = cosecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public SisCanjeResponse getSisCanje() {
        return sisCanje;
    }

    public void setSisCanje(SisCanjeResponse sisCanje) {
        this.sisCanje = sisCanje;
    }

    public List<ContratoDetResponse> getContratoDet() {
        return contratoDet;
    }

    public void setContratoDet(List<ContratoDetResponse> contratoDet) {
        this.contratoDet = contratoDet;
    }

    public Date getFechaVto() {
        return fechaVto;
    }

    public void setFechaVto(Date fechaVto) {
        this.fechaVto = fechaVto;
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

    public Integer getKilosCumplidos() {
        return kilosCumplidos;
    }

    public void setKilosCumplidos(Integer kilosCumplidos) {
        this.kilosCumplidos = kilosCumplidos;
    }
    
   
    public void agregarDetalles(Collection<ContratoDet> det) {
        for(ContratoDet d : det) {
            ContratoDetResponse dr = new ContratoDetResponse(d);
            this.kilosCumplidos = this.kilosCumplidos + d.getKilos();
            this.contratoDet.add(dr);
        }
    }
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
