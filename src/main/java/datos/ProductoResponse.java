package datos;

import entidades.ProdCultivo;
import entidades.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author FrancoSili
 */
public class ProductoResponse implements Payload {
    private Integer idProductos;
    private String codProducto;
    private String codigoBarra;
    private String descripcionCorta;
    private String descripcion;
    private boolean aptoCanje;
    private boolean stock;
    private boolean trazable;
    private boolean traReceta;
    private boolean traInforma;
    private String gtin;
    private BigDecimal puntoPedido;
    private BigDecimal costoReposicion;
    private BigDecimal precioVentaProv;
    private String observaciones;
    private SisIVAResponse IVA;
    private SubRubroResponse subRubro;
    private SisUnidadResponse unidadCompra;
    private SisUnidadResponse unidadVenta;
    private boolean editar; 
    private ModeloCabResponse modeloCab;
    private MarcaResponse marca;
    private SisMonedasResponse moneda;
    private List<CultivoResponse> cultivos;   

    public ProductoResponse(Producto p) {
        this.idProductos = p.getIdProductos();
        this.codProducto = p.getCodProducto();
        this.codigoBarra = p.getCodigoBarra();
        this.descripcionCorta = p.getDescripcionCorta();
        this.descripcion = p.getDescripcion();
        this.aptoCanje = p.getAptoCanje();
        this.stock = p.getStock();
        this.trazable = p.getTrazable();
        this.traReceta = p.getTraReceta();
        this.traInforma = p.getTraInforma();
        this.gtin = p.getGtin();
        this.puntoPedido = p.getPuntoPedido();
        this.costoReposicion = p.getCostoReposicion();
        this.precioVentaProv = p.getPrecioVentaProv();
        this.observaciones = p.getObservaciones();
        this.IVA = new SisIVAResponse(p.getIdIVA());
        this.subRubro = new SubRubroResponse(p.getIdSubRubros());
        this.unidadCompra = new SisUnidadResponse(p.getUnidadCompra());
        this.unidadVenta = new SisUnidadResponse(p.getUnidadVenta());
        this.editar = true;
        this.modeloCab = new ModeloCabResponse(p.getIdModeloCab());
        this.marca = new MarcaResponse(p.getIdMarca());
        this.moneda = new SisMonedasResponse(p.getIdMoneda());
        this.cultivos = new ArrayList<>();
    }

    public ProductoResponse(Integer idProductos, String descripcion, String codProducto) {
        this.idProductos = idProductos;
        this.descripcion = descripcion;
        this.codProducto = codProducto;
    }
    
    public Integer getIdProductos() {
        return idProductos;
    }

    public void setIdProductos(Integer idProductos) {
        this.idProductos = idProductos;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isAptoCanje() {
        return aptoCanje;
    }

    public void setAptoCanje(boolean aptoCanje) {
        this.aptoCanje = aptoCanje;
    }


    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

    public boolean isTrazable() {
        return trazable;
    }

    public void setTrazable(boolean trazable) {
        this.trazable = trazable;
    }

    public boolean isTraReceta() {
        return traReceta;
    }

    public void setTraReceta(boolean traReceta) {
        this.traReceta = traReceta;
    }

    public boolean isTraInforma() {
        return traInforma;
    }

    public void setTraInforma(boolean traInforma) {
        this.traInforma = traInforma;
    }

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public BigDecimal getPuntoPedido() {
        return puntoPedido;
    }

    public void setPuntoPedido(BigDecimal puntoPedido) {
        this.puntoPedido = puntoPedido;
    }

    public BigDecimal getCostoReposicion() {
        return costoReposicion;
    }

    public void setCostoReposicion(BigDecimal costoReposicion) {
        this.costoReposicion = costoReposicion;
    }

    public BigDecimal getPrecioVentaProv() {
        return precioVentaProv;
    }

    public void setPrecioVentaProv(BigDecimal precioVentaProv) {
        this.precioVentaProv = precioVentaProv;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public SisIVAResponse getIVA() {
        return IVA;
    }

    public void setIVA(SisIVAResponse idIVA) {
        this.IVA = idIVA;
    }

    public SubRubroResponse getSubRubros() {
        return subRubro;
    }

    public void setSubRubros(SubRubroResponse idSubRubros) {
        this.subRubro = idSubRubros;
    }

    public SisUnidadResponse getUnidadCompra() {
        return unidadCompra;
    }

    public void setUnidadCompra(SisUnidadResponse unidadCompra) {
        this.unidadCompra = unidadCompra;
    }

    public SisUnidadResponse getUnidadVenta() {
        return unidadVenta;
    }

    public void setUnidadVenta(SisUnidadResponse unidadVenta) {
        this.unidadVenta = unidadVenta;
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public SubRubroResponse getSubRubro() {
        return subRubro;
    }

    public void setSubRubro(SubRubroResponse subRubro) {
        this.subRubro = subRubro;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public ModeloCabResponse getModeloCab() {
        return modeloCab;
    }

    public void setModeloCab(ModeloCabResponse modeloCab) {
        this.modeloCab = modeloCab;
    }

    public MarcaResponse getMarca() {
        return marca;
    }

    public void setMarca(MarcaResponse marca) {
        this.marca = marca;
    }

    public SisMonedasResponse getMoneda() {
        return moneda;
    }

    public void setMoneda(SisMonedasResponse moneda) {
        this.moneda = moneda;
    }

    public List<CultivoResponse> getCultivos() {
        return cultivos;
    }

    public void setCultivos(List<CultivoResponse> cultivos) {
        this.cultivos = cultivos;
    }
    
    public void agregarCultivos(Collection<ProdCultivo> prodCultivo) {
        for(ProdCultivo p : prodCultivo) {
            CultivoResponse cr = new CultivoResponse(p.getIdCultivo());
            this.cultivos.add(cr);
        }
    }
    
    
}
