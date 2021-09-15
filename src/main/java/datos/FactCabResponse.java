package datos;

import entidades.FactCab;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *
 * @author Dario Quiroga
 */
public class FactCabResponse implements Payload{
    private Integer idFactCab;
//    private String letra;
      private long numero;
//    private Date fechaEmision;
//    private Date fechaVto;
//    private Date fechaConta;
//    private String cai;
//    private Date caiVto;
//    private String codBarra;    
    private Integer idPadron;    
    private String nombre;    
    private String cuit;       
//    private String codigoPostal;
//    private String idListaPrecios;
    private BigDecimal cotDolar;
//    private Date fechaDolar;
//    private Integer idDepositos;
//    private Object observaciones;
//    private String sitIVA;
//    private CteTipoResponse cteTipo;
    private String modulo;
    private String comprobante;
    private String moneda;
    
    private String imputada;
    private Date fechaEmi;
    private Date fechaVence;
    private String vendedor;
    private Integer idCteTipo;
    private BigDecimal importeNeto;
    private BigDecimal importeTotal;
 
    private String tipoOperacion;
    private String autorizada;
    private String permiteBorrado;
    private Integer kilosCanje;
    private Boolean pesificado;
    private Boolean dolarizadoAlVto;
    private BigDecimal interesMensualCompra;
    private Boolean canjeInsumos;
    private String tipoCambio;
    private Boolean diferidoVto;
   
    private List<FactDetalleResponse> detalle;
    private List<FactImputaResponse> imputa;
    private List<FactPieResponse> pie;
    private List<FactFormaPagoResponse> formaPago;
    private List<MasterResponse> master;
    private List<SisMonedasResponse> monedas;
    
    
    public FactCabResponse(FactCab f) {
        this.idFactCab = f.getIdFactCab();
        this.comprobante = f.getIdCteTipo().getDescripcion();
        this.numero = f.getNumero();
        this.fechaEmi = f.getFechaEmision();
        this.fechaVence = f.getFechaVto();
        this.idPadron = f.getIdPadron();
        this.nombre = f.getNombre();
        this.cuit = f.getCuit();
        this.cotDolar = f.getCotDolar();       
        this.moneda = f.getIdmoneda().getDescripcion();
        
        this.modulo = f.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getDescripcion();
        this.vendedor = "";       
        this.importeNeto = BigDecimal.ZERO;
        this.importeTotal = BigDecimal.ZERO;
       
        this.tipoOperacion = f.getIdSisTipoOperacion().getDescripcion();
        this.autorizada = " ";
        this.pesificado = f.getPesificado();
        this.dolarizadoAlVto = f.getDolarizadoAlVto();
        this.interesMensualCompra = f.getInteresMensualCompra();
        this.canjeInsumos = f.getCanjeInsumos();
        this.tipoCambio = f.getTipoCambio();
        this.diferidoVto = f.getDiferidoVto();
        
        this.detalle = new ArrayList<>();
        this.formaPago =  new ArrayList<>();
        this.pie =  new ArrayList<>();;
        this.imputa = new ArrayList<>();
        this.master = new ArrayList<>();
        this.monedas = new ArrayList<>();
    }

    public FactCabResponse(Integer idFactCab, String comprobante,long numero, Date fechaEmision, Date fechaVence, Integer idPadron, String nombre, String cuit, BigDecimal cotDolar, String moneda, String imputada, String modulo, String vendedor, Integer idCteTipo, BigDecimal importeNeto, BigDecimal importeTotal, String tipoOperacion, String autorizada, String permiteBorrado, Integer kilosCanje, Boolean pesificado, Boolean dolarizadoAlVto, BigDecimal interesMensualCompra, Boolean canjeInsumos, String tipoCambio) {
        this.idFactCab = idFactCab;
        this.comprobante = comprobante;
        this.numero = numero;
        this.fechaEmi = fechaEmision;
        this.fechaVence = fechaVence;
        this.idPadron = idPadron;
        this.nombre = nombre;
        this.cuit = cuit;
        this.cotDolar = cotDolar;
        this.moneda = moneda;
        
        this.imputada = imputada;
        this.modulo = modulo;
        this.vendedor = vendedor;
        this.idCteTipo = idCteTipo;
        this.importeNeto = importeNeto;
        this.importeTotal = importeTotal;
        
        this.tipoOperacion = tipoOperacion;
        this.autorizada = autorizada;
        this.permiteBorrado = permiteBorrado;
        this.kilosCanje = kilosCanje;
        this.pesificado = pesificado;
        this.dolarizadoAlVto = dolarizadoAlVto;
        this.interesMensualCompra = interesMensualCompra;
        this.canjeInsumos = canjeInsumos;
        this.tipoCambio = tipoCambio;
        
        this.detalle = new ArrayList<>();
        this.imputa = new ArrayList<>();
        this.formaPago = new ArrayList();
        this.pie = new ArrayList<>();       
        this.master = new ArrayList<>();
        this.monedas = new ArrayList<>();

    }

    public FactCabResponse(Integer idFactCab) {
        this.idFactCab = idFactCab;
    }
    
    public List<FactFormaPagoResponse> getFormaPago() {
        return formaPago;
    }

    

//    public String getLetra() {
//        return letra;
//    }
//
//    public void setLetra(String letra) {
//        this.letra = letra; 
//    }
    public void setFormaPago(List<FactFormaPagoResponse> formaPago) {
        this.formaPago = formaPago;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }
    
    public void setPesificado(Boolean pesificado) {
        this.pesificado = pesificado;
    }

  
    
    public void setDolarizadoAlVto(Boolean dolarizadoAlVto) {
        this.dolarizadoAlVto = dolarizadoAlVto;
    }
    
    public void setInteresMensualCompra(BigDecimal interesMensualCompra) {
        this.interesMensualCompra = interesMensualCompra;
    }
    
    public void setCanjeInsumos(Boolean canjeInsumos) {
        this.canjeInsumos = canjeInsumos;
    }
    
    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public Date getFechaVence() {
        return fechaVence;
    }

    public void setFechaVence(Date fechaVence) {
        this.fechaVence = fechaVence;
    }
    public int getIdPadron() {
        return idPadron;
    }

    public void setIdPadron(int idPadron) {
        this.idPadron = idPadron;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

//    public String getCodigoPostal() {
//        return codigoPostal;
//    }
//
//    public void setCodigoPostal(String codigoPostal) {
//        this.codigoPostal = codigoPostal;
//    }
//
//    public String getIdListaPrecios() {
//        return idListaPrecios;
//    }
//
//    public void setIdListaPrecios(String idListaPrecios) {
//        this.idListaPrecios = idListaPrecios;
//    }
//
//    public CteTipoResponse getCteTipo() {
//        return cteTipo;
//    }
//
//    public void setCteTipo(CteTipoResponse cteTipo) {
//        this.cteTipo = cteTipo;
//    }
    
    public SisMonedasResponse geMonedas() {
        return (SisMonedasResponse) monedas;
    }

    public void setMonedas(SisMonedasResponse monedas) {
        this.monedas = (List<SisMonedasResponse>) monedas;
    }
    
    public Integer getIdFactCab() {
        return idFactCab;
    }

    public void setIdFactCab(Integer idFactCab) {
        this.idFactCab = idFactCab;
    }

//    public Date getFechaConta() {
//        return fechaConta;
//    }
//
//    public void setFechaConta(Date fechaConta) {
//        this.fechaConta = fechaConta;
//    }

    public BigDecimal getCotDolar() {
        return cotDolar;
    }

    public void setCotDolar(BigDecimal cotDolar) {
        this.cotDolar = cotDolar;
    }

//    public Object getObservaciones() {
//        return observaciones;
//    }
//
//    public void setObservaciones(Object observaciones) {
//        this.observaciones = observaciones;
//    }
//
//    public Date getFechaDolar() {
//        return fechaDolar;
//    }
//
//    public void setFechaDolar(Date fechaDolar) {
//        this.fechaDolar = fechaDolar;
//    }
//
//    public Integer getIdDepositos() {
//        return idDepositos;
//    }
//
//    public void setIdDepositos(Integer idDepositos) {
//        this.idDepositos = idDepositos;
//    }
//
//    public String getSitIVA() {
//        return sitIVA;
//    }
//
//    public void setSitIVA(String sitIVA) {
//        this.sitIVA = sitIVA;
//    }

    public List<FactDetalleResponse> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<FactDetalleResponse> detalle) {
        this.detalle = detalle;
    }

    public List<SisMonedasResponse> getMonedas() {
        return monedas;
    }

    public void setMonedas(List<SisMonedasResponse> monedas) {
        this.monedas = monedas;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public Date getFechaEmi() {
        return fechaEmi;
    }

    public void setFechaEmi(Date fechaEmi) {
        this.fechaEmi = fechaEmi;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    
    public String getImputada() {
        return imputada;
    }

    public void setImputada(String imputada) {
        this.imputada = imputada;
    }
    
    
    public void agregarMonedas(List<SisMonedasResponse> m){
        for(SisMonedasResponse mo: m){
            this.monedas.add(mo);
        }
    }
    public void agregarDetalles(List<FactDetalleResponse> f) {
        for(FactDetalleResponse fd: f) {
            this.detalle.add(fd);
        }
    }
    
    public void agregarMaster(List<MasterResponse> f) {
        for(MasterResponse fd: f) {
            this.master.add(fd);
        }
    }

    public List<FactPieResponse> getPie() {
        return pie;
    }

    public void setPie(List<FactPieResponse> pie) {
        this.pie = pie;
    }

    public List<FactImputaResponse> getImputa() {
        return imputa;
    }

    public void setImputa(List<FactImputaResponse> imputa) {
        this.imputa = imputa;
    }
    
    public List<MasterResponse> getMaster() {
        return master;
    }

    public void setMaster(List<MasterResponse> master) {
        this.master = master;
    }
    
    
    
    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}