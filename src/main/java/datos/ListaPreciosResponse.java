package datos;

import entidades.FormaPago;
import entidades.ListaPrecio;
import entidades.ListaPrecioDet;
import entidades.ListaPrecioFormaPago;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author FrancoSili
 */
public class ListaPreciosResponse implements Payload {
  
    private Integer idListaPrecio;   
    private int codigoLista;    
    private Date fechaAlta;   
    private Date vigenciaDesde;   
    private Date vigenciaHasta;    
    private boolean activa;    
    private int idPadronCliente;    
    private int idPadronRepresentante;    
    private BigDecimal porc1;    
    private String condiciones;  
    private SisMonedasResponse idMoneda;
    private List<FormaPagoResponse> formasPago;
    private List<ListaPrecioDetResponse> listaPrecioDetCollection;


    public ListaPreciosResponse(ListaPrecio l) {
        this.idListaPrecio = l.getIdListaPrecios();
        this.codigoLista = l.getCodigoLista();
        this.fechaAlta = l.getFechaAlta();
        this.vigenciaDesde = l.getVigenciaDesde();
        this.vigenciaHasta = l.getVigenciaHasta();
        this.activa = l.getActiva();
        this.idPadronCliente = l.getIdPadronCliente();
        this.porc1 = l.getPorc1();
        this.condiciones = l.getCondiciones();
        this.idMoneda = new SisMonedasResponse(l.getIdMoneda());
        this.formasPago = new ArrayList<>();
        this.listaPrecioDetCollection = new ArrayList<>();
    }

    public Integer getIdListaPrecio() {
        return idListaPrecio;
    }

    public void setIdListaPrecio(Integer idListaPrecio) {
        this.idListaPrecio = idListaPrecio;
    }

    public int getCodigoLista() {
        return codigoLista;
    }

    public void setCodigoLista(int codigoLista) {
        this.codigoLista = codigoLista;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getVigenciaDesde() {
        return vigenciaDesde;
    }

    public void setVigenciaDesde(Date vigenciaDesde) {
        this.vigenciaDesde = vigenciaDesde;
    }

    public Date getVigenciaHasta() {
        return vigenciaHasta;
    }

    public void setVigenciaHasta(Date vigenciaHasta) {
        this.vigenciaHasta = vigenciaHasta;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public int getIdPadronCliente() {
        return idPadronCliente;
    }

    public void setIdPadronCliente(int idPadronCliente) {
        this.idPadronCliente = idPadronCliente;
    }

    public int getIdPadronRepresentante() {
        return idPadronRepresentante;
    }

    public void setIdPadronRepresentante(int idPadronRepresentante) {
        this.idPadronRepresentante = idPadronRepresentante;
    }

    public BigDecimal getPorc1() {
        return porc1;
    }

    public void setPorc1(BigDecimal porc1) {
        this.porc1 = porc1;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public List<ListaPrecioDetResponse> getListaPrecioDetCollection() {
        return listaPrecioDetCollection;
    }

    public void setListaPrecioDetCollection(List<ListaPrecioDetResponse> listaPrecioDetCollection) {
        this.listaPrecioDetCollection = listaPrecioDetCollection;
    }

    public SisMonedasResponse getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(SisMonedasResponse idMoneda) {
        this.idMoneda = idMoneda;
    }

    public List<FormaPagoResponse> getFormasPago() {
        return formasPago;
    }

    public void setFormasPago(List<FormaPagoResponse> formasPago) {
        this.formasPago = formasPago;
    }
    
    public void agregarFormasPago(Collection<ListaPrecioFormaPago> formasPago) {
        for(ListaPrecioFormaPago l : formasPago) {
            FormaPagoResponse lr = new FormaPagoResponse(l.getIdFormaPago());
            if(l.getIdFormaPago().getFormaPagoDetCollection() != null && !l.getIdFormaPago().getFormaPagoDetCollection().isEmpty()) {
                lr.agregarDetalles(l.getIdFormaPago().getFormaPagoDetCollection());
            }
            ListaPreciosResponse lpr = new ListaPreciosResponse(l.getIdListaPrecio());
            lpr.agregarListaPrecioDet(l.getIdListaPrecio().getListaPrecioDetCollection());
            lr.setListaPrecio(lpr);
            this.formasPago.add(lr);
        }
    }
    
    public void agregarListaPrecioDet(Collection<ListaPrecioDet> lista) {
        for(ListaPrecioDet l : lista) {
            ListaPrecioDetResponse lr = new ListaPrecioDetResponse(l);
            this.listaPrecioDetCollection.add(lr);
        }
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
