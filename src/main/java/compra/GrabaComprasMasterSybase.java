package compra;

/*import com.google.gson.JsonElement;
import com.google.gson.JsonObject;*/
import datos.AppCodigo;
import datos.DatosResponse;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CerealSisaSybase;
import entidades.CerealSisaAlicuotasSybase;
import entidades.Contrato;
import entidades.ContratoDet;
import entidades.CteNumerador;
import entidades.CteTipo;
import entidades.Deposito;
import entidades.FactCab;
import entidades.FactDetalle;
import entidades.FactFormaPago;
import entidades.FactImputa;
import entidades.FactPie;
import entidades.FormaPagoDet;
import entidades.ListaPrecioDet;
import entidades.Lote;
import entidades.Master;
import entidades.CanjesContratosCereales;
import entidades.CanjesDocumentoSybase;
import entidades.CanjesDocumentoSybasePK;
import entidades.Cereales;
import entidades.MasterSybase;
import entidades.Padron;
import entidades.Producto;
import entidades.Produmo;
import entidades.RelacionesCanje;
import entidades.SisCotDolar;
import entidades.SisMonedas;
import entidades.SisOperacionComprobante;
import entidades.SisTipoModelo;
import entidades.SisTipoOperacion;
import entidades.Usuario;
import entidades.ModeloDetalle;
import entidades.CtacteCategoria;
import entidades.FacComprasSybase;
import entidades.ParametrosFacSybase;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import persistencia.AccesoFacade;
import persistencia.CerealSisaSybaseFacade;
import persistencia.ContratoDetFacade;
import persistencia.ContratoFacade;
import persistencia.CteNumeradorFacade;
import persistencia.CteTipoFacade;
import persistencia.DepositoFacade;
import persistencia.FactCabFacade;
import persistencia.FactDetalleFacade;
import persistencia.FactFormaPagoFacade;
import persistencia.FactImputaFacade;
import persistencia.FactPieFacade;
import persistencia.FormaPagoDetFacade;
import persistencia.FormaPagoFacade;
import persistencia.ListaPrecioFacade;
import persistencia.LoteFacade;
import persistencia.MasterFacade;
import persistencia.ModeloDetalleFacade;
import persistencia.MasterSybaseFacade;
import persistencia.CanjesDocumentoSybaseFacade;
import persistencia.CanjesContratosCerealesFacade;
import persistencia.CerealesFacade;
import persistencia.PadronFacade;
import persistencia.ProductoFacade;
import persistencia.ProdumoFacade;
import persistencia.RelacionesCanjeFacade;
import persistencia.SisCotDolarFacade;
import persistencia.SisMonedasFacade;
import persistencia.SisOperacionComprobanteFacade;
import persistencia.SisTipoModeloFacade;
import persistencia.SisTipoOperacionFacade;
import persistencia.UsuarioFacade;
import persistencia.ParametroGeneralFacade;
import persistencia.MasterSybaseFacade;
import persistencia.CtacteCategoriaFacade;

import persistencia.ParametrosFacSybaseFacade;
  
import utils.Utils;

/**
 *
 * @author Kernel informatica
 */
public class GrabaComprasMasterSybase {

    @Inject
    Utils utilidadesFacade;
    @Inject
    UsuarioFacade usuarioFacade;
    @Inject
    AccesoFacade accesoFacade;
    @Inject
    SisMonedasFacade sisMonedasFacade;
    @Inject
    CteTipoFacade cteTipoFacade;
    @Inject
    FormaPagoFacade formaPagoFacade;
    @Inject
    FactCabFacade factCabFacade;
    @Inject
    DepositoFacade depositoFacade;
    @Inject
    FactDetalleFacade factDetalleFacade;
    @Inject
    FactImputaFacade factImputaFacade;
    @Inject
    FactPieFacade factPieFacade;
    @Inject
    ProductoFacade productoFacade;
    @Inject
    ProdumoFacade produmoFacade;
    @Inject
    LoteFacade loteFacade;
    @Inject
    FactFormaPagoFacade factFormaPagoFacade;
    @Inject
    FormaPagoDetFacade formaPagoDetFacade;
    @Inject
    SisTipoOperacionFacade sisTipoOperacionFacade;
    @Inject
    CteNumeradorFacade cteNumeradorFacade;
    @Inject
    SisTipoModeloFacade sisTipoModeloFacade;
    @Inject
    ModeloDetalleFacade modeloDetalleFacade;
    @Inject
    CtacteCategoriaFacade ctaCteCategoriaFacade;
    

    @Inject
    SisOperacionComprobanteFacade sisOperacionComprobanteFacade;
    @Inject
    ListaPrecioFacade listaPrecioFacade;
    @Inject
    SisCotDolarFacade sisCotDolarFacade;
    @Inject
    PadronFacade padronFacade;
    @Inject
    ContratoFacade contratoFacade;
    @Inject
    ContratoDetFacade contratoDetFacade;
    @Inject
    RelacionesCanjeFacade relacionesCanjeFacade;
    @Inject
    ParametroGeneralFacade parametro;
    @Inject
    CanjesContratosCerealesFacade canjesContratosCereales;
    @Inject
    MasterSybaseFacade masterSybaseFacade;
    
    @Inject
    ParametrosFacSybaseFacade parametrosFacSybaseFacade;
    @Inject
    CerealSisaSybaseFacade cerealSisaSybaseFacade;
    @Inject
    CanjesDocumentoSybaseFacade canjesDocumentosSybaseFacade;
    @Inject
    CerealesFacade cerealesFacade;
    @Inject
    CanjesContratosCerealesFacade canjesContratosCerealesFacade;
   

 public Boolean grabarMasterSybase(FactCab factCab,
            List<FactDetalle> factDetalle,
            List<FactFormaPago> factFormaPago,
            List<FactPie> factPie,
            Usuario user) {
        System.out.println("::::::::: Master Sybase  ----------------------> GrabaMasterSybase()-> Nro Comprobante: " + factCab.getNumero() + " | tipo operacion: " + factCab.getIdCteTipo().getcTipoOperacion());
        ServicioResponse respuesta = new ServicioResponse();
        //Seteo la fecha de hoy
        Calendar calendario = new GregorianCalendar();
        Date fechaHoy = calendario.getTime();
        Integer libroCodigo = 50;//sisOperacionComprobante.getLibroCodigo();
        Integer masAsiento = masterSybaseFacade.findProximoNroAsiento(libroCodigo);
        masAsiento = masAsiento + 1;
        String detalleCorto = "";
        Integer paseDetalle = 0;
        BigDecimal cotizacionDolar = new BigDecimal(1);
        //Me fijo si es debe o haber
        BigDecimal signo = new BigDecimal(1);
        cotizacionDolar = new BigDecimal(1);

        Integer codigoConceptoFac = 96;
        // busco el padron
        Padron pad = padronFacade.getPadronByCodigo(factCab.getIdPadron());

        if (factCab.getIdCteTipo().getcTipoOperacion() < 17) {
            // factura
            if (factCab.getIdmoneda().getIdMoneda() > 1) {
                cotizacionDolar = factCab.getCotDolar();
            } else {
                cotizacionDolar = new BigDecimal(1);
            }

        }
        BigDecimal subTotal = new BigDecimal(0);

        //Sumo uno al contador de pases
        if (factCab.getIdCteTipo().getSurenu().equals("D")) {
            signo = signo.negate();
            System.out.println("::::::::: Master Sybase  ----------------------> SIGNO NEGATIVO factCab.getIdCteTipo().getSurenu() " + factCab.getIdCteTipo().getSurenu());

        }
        try {

            for (FactDetalle det : factDetalle) {
                paseDetalle = paseDetalle + 1;
                String detalleCompleto = pad.getPadronApelli() + " " + pad.getPadronNombre();
                if (detalleCompleto.length() > 30) {
                    detalleCorto = (pad.getPadronApelli() + " " + pad.getPadronNombre()).substring(0, 29);

                } else {
                    detalleCorto = detalleCompleto;
                }
                System.out.println(paseDetalle + "::::::::: Master Sybase  ----------------------> GrabaMasterSybase()-> Cotizacion Dolar: setMImporte: " + det.getImporte().multiply(signo).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                MasterSybase masterDetalle = new MasterSybase(factCab.getFechaConta(), masAsiento, Short.valueOf(Integer.toString(paseDetalle)), Short.valueOf(Integer.toString(libroCodigo)));
                masterDetalle.setCotizacion(factCab.getCotDolar().setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                masterDetalle.setFechayhora(fechaHoy);
                masterDetalle.setMDetalle(detalleCorto);
                masterDetalle.setMFechaEmi(factCab.getFechaEmision());

                //si es canje
                if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                    masterDetalle.setMVence(factCab.getFechaEmision());
                } else {
                    masterDetalle.setMVence(factCab.getFechaVto());
                }

                masterDetalle.setMImporte((det.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                subTotal = subTotal.add((det.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN));

                masterDetalle.setNroComp(factCab.getNumero());
                masterDetalle.setPadronCodigo(factCab.getIdPadron());
                masterDetalle.setPlanCuentas(Integer.parseInt(det.getImputacion()));
                masterDetalle.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())));
                masterDetalle.setMColumIva(Short.valueOf(Integer.toString(det.getIdLibro())));
                masterDetalle.setAutorizaCodigo(Short.valueOf("0"));
                masterDetalle.setTipoCompAsoc(Short.valueOf("0"));
                masterDetalle.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                masterDetalle.setCondGan(Short.valueOf("0"));
                masterDetalle.setCondIva(Short.valueOf("0"));
                masterDetalle.setMUnidades(det.getCantidad().doubleValue());
                masterDetalle.setNroCompAsoc(Long.valueOf("0"));
                masterDetalle.setNroCompPreimp(Long.valueOf("0"));
                masterDetalle.setCodActividad(Long.valueOf("0"));
                masterDetalle.setMMinuta(Long.valueOf(masAsiento));
                masterDetalle.setMCtacte("N");
                masterDetalle.setOperadorCodigo(user.getUsuarioSybase());
                masterDetalle.setMAsientoRub(0);
                boolean transaccionSybase1;
                transaccionSybase1 = masterSybaseFacade.masterSybaseNuevo(masterDetalle);
                //si la trnsaccion fallo devuelvo el mensaje
                if (!transaccionSybase1) {
                    return false;
                }

            }
            for (FactFormaPago fp : factFormaPago) {

                //Sumo uno al contador de pases
                paseDetalle = paseDetalle + 1;
                MasterSybase masterFormaPago = new MasterSybase(factCab.getFechaConta(), masAsiento, Short.valueOf(Integer.toString(paseDetalle)), Short.valueOf(Integer.toString(libroCodigo)));
                masterFormaPago.setCotizacion(factCab.getCotDolar().setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                masterFormaPago.setFechayhora(fechaHoy);

                masterFormaPago.setMFechaEmi(factCab.getFechaEmision());
                //si es canje
                if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                    masterFormaPago.setMVence(factCab.getFechaEmision());
                } else {
                    masterFormaPago.setMVence(factCab.getFechaVto());
                }
                masterFormaPago.setNroComp(factCab.getNumero());
                masterFormaPago.setPadronCodigo(factCab.getIdPadron());

                masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())));
                if (fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(1)
                        || fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(6)) {
                    masterFormaPago.setMCtacte("N");
                    if ((pad.getPadronApelli() + " " + pad.getPadronNombre()).length() > 30) {
                        masterFormaPago.setMDetalle((pad.getPadronApelli() + " " + pad.getPadronNombre()).substring(0, 30));
                    } else {
                        masterFormaPago.setMDetalle(pad.getPadronApelli() + " " + pad.getPadronNombre());
                    }
                    masterFormaPago.setPlanCuentas(Integer.parseInt(fp.getCtaContable()));
                    masterFormaPago.setMImporte((fp.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                } else {
                    masterFormaPago.setMCtacte("S");
                    // debe ir el signo negativo
                    masterFormaPago.setMImporte((fp.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue() * -1);

                    if (factCab.getIdmoneda().getIdMoneda() > 1) {
                        masterFormaPago.setMDetalle(factCab.getIdCteTipo().getDescripcion() + " | U$S " + factCab.getCotDolar());
                    } else {
                        masterFormaPago.setMDetalle(factCab.getIdCteTipo().getDescripcion() + " | Pesos ");
                    }

                    if (fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(2)) {
                        // Si es COMPRAS  a Cuenta Corriente busco la cuenta contable en la categoria del padron
                        CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                        masterFormaPago.setPlanCuentas(Integer.parseInt(Integer.toString(ctacteCatego.getPlanCuentas())));
                    } else {
                        // so es ventas
                        if (fp.getIdFormaPago().getTipo().getIdSIsModulo().getIdSisModulos().equals(2)) {
                            CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                            masterFormaPago.setPlanCuentas(Integer.parseInt(Integer.toString(ctacteCatego.getPlanCuentas())));
                        } else {
                            masterFormaPago.setPlanCuentas(Integer.parseInt(fp.getCtaContable()));
                        }

                    }
                }
                System.out.println(paseDetalle + "::::::::: Master Sybase  ----------------------> GrabaMasterSybase()-> Forma pago: setMImporte: " + fp.getImporte().multiply(signo).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                //Parametros que van en 0
                masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                masterFormaPago.setCondGan(Short.valueOf("0"));
                masterFormaPago.setCondIva(Short.valueOf("0"));
                masterFormaPago.setMColumIva(Short.valueOf("0"));
                masterFormaPago.setMUnidades(Double.valueOf(0));
                masterFormaPago.setNroCompAsoc(Long.valueOf("0"));
                masterFormaPago.setNroCompPreimp(Long.valueOf("0"));
                masterFormaPago.setCodActividad(Long.valueOf("0"));
                masterFormaPago.setMMinuta(Long.valueOf(masAsiento));
                masterFormaPago.setOperadorCodigo(user.getUsuarioSybase());
                masterFormaPago.setMAsientoRub(0);

                boolean transaccionSybase2;
                transaccionSybase2 = masterSybaseFacade.masterSybaseNuevo(masterFormaPago);
                //si la trnsaccion fallo devuelvo el mensaje
                if (!transaccionSybase2) {
                    return false;
                }

            }

            for (FactPie fi : factPie) {
                if (fi.getImporte().equals(BigDecimal.ZERO)) {
                    continue;
                }
                //Sumo uno al contador de pases
                paseDetalle = paseDetalle + 1;
                MasterSybase masterImputa = new MasterSybase(factCab.getFechaConta(), masAsiento, Short.valueOf(Integer.toString(paseDetalle)), Short.valueOf(Integer.toString(libroCodigo)));
                masterImputa.setCotizacion(factCab.getCotDolar().setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                masterImputa.setFechayhora(factCab.getFechaConta());
                if ((pad.getPadronApelli() + " " + pad.getPadronNombre()).length() > 30) {
                    masterImputa.setMDetalle((pad.getPadronApelli() + " " + pad.getPadronNombre()).substring(0, 30));
                } else {
                    masterImputa.setMDetalle(pad.getPadronApelli() + " " + pad.getPadronNombre());
                }
                masterImputa.setMFechaEmi(factCab.getFechaEmision());
                //si es canje
                if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                    masterImputa.setMVence(factCab.getFechaEmision());
                } else {
                    masterImputa.setMVence(factCab.getFechaVto());
                }
                masterImputa.setMImporte((fi.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                masterImputa.setNroComp(factCab.getNumero());
                masterImputa.setPadronCodigo(factCab.getIdPadron());
                masterImputa.setPlanCuentas(Integer.parseInt(fi.getCtaContable()));
                masterImputa.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())));
                masterImputa.setMColumIva(Short.valueOf(Integer.toString(fi.getIdLibro())));
                //Parametros que van en 0
                masterImputa.setAutorizaCodigo(Short.valueOf("0"));
                masterImputa.setTipoCompAsoc(Short.valueOf("0"));
                masterImputa.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                masterImputa.setCondGan(Short.valueOf("0"));
                masterImputa.setCondIva(Short.valueOf("0"));
                masterImputa.setMUnidades(Double.valueOf(0));
                masterImputa.setNroCompAsoc(Long.valueOf("0"));
                masterImputa.setNroCompPreimp(Long.valueOf("0"));
                masterImputa.setCodActividad(Long.valueOf("0"));
                masterImputa.setMMinuta(Long.valueOf(masAsiento));
                masterImputa.setMCtacte("N");
                masterImputa.setOperadorCodigo(user.getUsuarioSybase());
                masterImputa.setMAsientoRub(0);
                boolean transaccionSybase3;
                transaccionSybase3 = masterSybaseFacade.masterSybaseNuevo(masterImputa);
                //si la transaccion fallo devuelvo el mensaje
                if (!transaccionSybase3) {
                    return false;
                }

            }

            /*
            
            
             CANJE: 
             SI ES VENTAS Y ES CANJE
                
            
             */
            if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) && factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                BigDecimal subTotalCanje = new   BigDecimal(0);
                // CANJE CREDITO PASE 1 MASTER SYBASE
                for (FactFormaPago fp : factFormaPago) {
                    //fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(7) 
                    //Sumo uno al contador de pases
                    paseDetalle = paseDetalle + 1;
                    MasterSybase masterFormaPago = new MasterSybase(factCab.getFechaConta(), masAsiento, Short.valueOf(Integer.toString(paseDetalle)), Short.valueOf(Integer.toString(libroCodigo)));
                    masterFormaPago.setCotizacion(factCab.getCotDolar().setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                    masterFormaPago.setFechayhora(fechaHoy);

                    masterFormaPago.setMFechaEmi(factCab.getFechaEmision());
                    masterFormaPago.setMVence(factCab.getFechaEmision());
                    masterFormaPago.setNroComp(factCab.getNumero());
                    masterFormaPago.setPadronCodigo(factCab.getIdPadron());

                    masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())));
                    masterFormaPago.setMCtacte("S");
                    // debe ir el signo negativo

                    if (factCab.getDiferidoVto().equals(true)) {
                        masterFormaPago.setMImporte((fp.getImporte().multiply(signo).multiply(cotizacionDolar)).setScale(2, RoundingMode.HALF_EVEN).doubleValue() * -1);
                    } else {
                        masterFormaPago.setMImporte((subTotal.multiply(signo)).setScale(2, RoundingMode.HALF_EVEN).doubleValue() * -1);

                    }
                    masterFormaPago.setMDetalle("CREDITO CTACTE");
                    if (fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(2)) {
                        CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                        masterFormaPago.setPlanCuentas(ctacteCatego.getPlanCuentas());
                    } else {
                        CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                        masterFormaPago.setPlanCuentas(ctacteCatego.getPlanCuentas());
                        //masterFormaPago.setPlanCuentas(Integer.valueOf(fp.getCtaContable()));
                    }

                    System.out.println(paseDetalle + "::::::::: Master Sybase  ----------------------> GrabaMasterSybase()-> Forma pago: setMImporte: " + fp.getImporte().multiply(signo).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                    //Parametros que van en 0
                    masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                    masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                    masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                    masterFormaPago.setCondGan(Short.valueOf("0"));
                    masterFormaPago.setCondIva(Short.valueOf("0"));
                    masterFormaPago.setMColumIva(Short.valueOf("0"));
                    masterFormaPago.setMUnidades(Double.valueOf(0));
                    masterFormaPago.setNroCompAsoc(Long.valueOf("0"));
                    masterFormaPago.setNroCompPreimp(Long.valueOf("0"));
                    masterFormaPago.setCodActividad(Long.valueOf("0"));
                    masterFormaPago.setMMinuta(Long.valueOf(masAsiento));
                    masterFormaPago.setOperadorCodigo(user.getUsuarioSybase());
                    masterFormaPago.setMAsientoRub(0);

                    boolean transaccionSybasePase1;
                    transaccionSybasePase1 = masterSybaseFacade.masterSybaseNuevo(masterFormaPago);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccionSybasePase1) {
                        return false;
                    }

                }
                // CANJE CREDITO PASE 2 MASER SYBASE
                // calculo el subtotal

                for (FactFormaPago fp : factFormaPago) {
                    //fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(7)
                    // busco la cuenta contable para el cereal que viene en la factcab
                    CanjesContratosCereales objContratosCereales = canjesContratosCerealesFacade.findCuentaPorCereal(factCab.getIdCteTipo().getIdEmpresa(), factCab.getCerealCanje().getCerealCodigo());

                    paseDetalle = paseDetalle + 1;
                    MasterSybase masterFormaPago = new MasterSybase(factCab.getFechaConta(), masAsiento, Short.valueOf(Integer.toString(paseDetalle)), Short.valueOf(Integer.toString(libroCodigo)));
                    masterFormaPago.setCotizacion(factCab.getCotDolar().setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                    masterFormaPago.setFechayhora(fechaHoy);
                    masterFormaPago.setMFechaEmi(factCab.getFechaEmision());
                    masterFormaPago.setMVence(factCab.getFechaEmision());
                    masterFormaPago.setNroComp(factCab.getNumero());
                    masterFormaPago.setPadronCodigo(factCab.getIdPadron());
                    masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())));
                    masterFormaPago.setMCtacte("N");
                    // debe ir el signo negativo
                    if (factCab.getDiferidoVto().equals(true)) {
                        masterFormaPago.setMImporte((fp.getImporte().multiply(signo).multiply(cotizacionDolar)).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                    } else {
                        masterFormaPago.setMImporte((subTotal.multiply(signo)).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                    }
                    masterFormaPago.setMDetalle("CREDITO CEREALES");
                    if (objContratosCereales != null) {
                        masterFormaPago.setPlanCuentas(Integer.valueOf(objContratosCereales.getCtaContable()));
                    } else {
                        masterFormaPago.setPlanCuentas(Integer.valueOf(fp.getCtaContable()));
                    }

                    //Parametros que van en 0
                    masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                    masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                    masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                    masterFormaPago.setCondGan(Short.valueOf("0"));
                    masterFormaPago.setCondIva(Short.valueOf("0"));
                    masterFormaPago.setMColumIva(Short.valueOf("0"));
                    masterFormaPago.setMUnidades(Double.valueOf(0));
                    masterFormaPago.setNroCompAsoc(Long.valueOf("0"));
                    masterFormaPago.setNroCompPreimp(Long.valueOf("0"));
                    masterFormaPago.setCodActividad(Long.valueOf("0"));
                    masterFormaPago.setMMinuta(Long.valueOf(masAsiento));
                    masterFormaPago.setOperadorCodigo(user.getUsuarioSybase());
                    masterFormaPago.setMAsientoRub(0);

                    boolean transaccionSybasePase2;
                    transaccionSybasePase2 = masterSybaseFacade.masterSybaseNuevo(masterFormaPago);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccionSybasePase2) {
                        return false;
                    }

                }
                //  GRABA TABLA DOCUMENTO SYBASE
                // dario
               
               /* for (FactDetalle det : factDetalle) {
                    if (factCab.getIdmoneda().getIdMoneda() > 1) {
                        subTotalCanje = subTotalCanje.add(det.getImporte());
                    } else {
                        subTotalCanje = subTotalCanje.add((det.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN));
                    }
                }*/

                for (FactFormaPago fp : factFormaPago) {
                    System.out.println(paseDetalle + "::::::::: DOCUMENTO SYBASE  ----------------------> grabaDocumentoSybase()-> Moneda: "+factCab.getIdmoneda().getIdMoneda()+", Diferido: "+factCab.getDiferidoVto()+", SubTotal: "+subTotal);

                    String numeroCompTemp = String.valueOf(factCab.getNumero());
                    String numeroComp = numeroCompTemp.substring(numeroCompTemp.length() - 8, numeroCompTemp.length());

                    CanjesDocumentoSybase documentoCanje = new CanjesDocumentoSybase(factCab.getIdPadron(), (short) factCab.getIdCteNumerador().getIdPtoVenta().getPtoVenta(), Integer.parseInt(numeroComp));
                    if (factCab.getIdmoneda().getIdMoneda() > 1) {
                        if (factCab.getDiferidoVto().equals(true)) {
                            documentoCanje.setImporte(fp.getImporte().doubleValue());
                        } else {
                             documentoCanje.setImporte(subTotal.multiply(signo).divide(factCab.getCotDolar(), 2, RoundingMode.HALF_UP).doubleValue());
                        }

                    } else { 
                        if (factCab.getDiferidoVto().equals(true)) {
                            documentoCanje.setImporte(fp.getImporte().divide(factCab.getCotDolar(), 2, RoundingMode.HALF_UP).doubleValue());
                        } else {
                            documentoCanje.setImporte(subTotal.multiply(signo).divide(factCab.getCotDolar(), 2, RoundingMode.HALF_UP).doubleValue());

                        }

                    }
                    // Non-terminating decimal expansion; no exact representable decimal result.
                    documentoCanje.setVencimiento(factCab.getFechaVto());
                    documentoCanje.setEmision(factCab.getFechaEmision());
                    documentoCanje.setCanje("S");
                    documentoCanje.setCereal(Short.parseShort(factCab.getCerealCanje().getCerealCodigo()));
                    documentoCanje.setCantidad(new BigDecimal(0));
                    documentoCanje.setDolar(factCab.getCotDolar().doubleValue());
                    Cereales cereal = cerealesFacade.findCerealPorCodigo(factCab.getCerealCanje().getCerealCodigo());
                    documentoCanje.setCosecha(cereal.getNombre());
                    documentoCanje.setFactura(Long.valueOf(0));

                    boolean transaccionCanjeDoc;
                    transaccionCanjeDoc = canjesDocumentosSybaseFacade.setCanjeDocsNuevo(documentoCanje);

                    if (!transaccionCanjeDoc) {
                        return false;
                    }
                }
                // AGREGO LA PERCEP A LA TABLA DOCUMENTOS

            }

        } catch (Exception ex) {
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            //return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
        System.out.println("::::::::: FIN  ----------------------> GrabaMasterSybase() :: Comprobante contabilizado (" + paseDetalle + " pases) (Sybase) con exito ");
        return true;

    }
}