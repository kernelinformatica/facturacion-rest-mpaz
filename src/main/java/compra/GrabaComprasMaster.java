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
import persistencia.FacComprasSybaseFacade;
import persistencia.ParametrosFacSybaseFacade;
  
import utils.Utils;

/**
 *
 * @author Kernel informatica
 */
public class GrabaComprasMaster {

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
    FacComprasSybaseFacade factComprasSybaseFacade;
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
    @Inject
    MasterFacade masterFacade;

 public Response grabarMaster(FactCab factCab, List<FactDetalle> factDetalle, List<FactFormaPago> factFormaPago, List<FactPie> factPie, Usuario user) {
        System.out.println("::::::::: Ejecuta  ----------------------> GrabaMaster()-> Nro Comprobante: " + factCab.getNumero());
        Integer codigoConceptoFac = 96;
        ServicioResponse respuesta = new ServicioResponse();
        //Seteo la fecha de hoy
        Calendar calendario = new GregorianCalendar();
        Date fechaHoy = calendario.getTime();
        BigDecimal cotizacionDolar = new BigDecimal(1);
        //Busco el proximo numero del asiento
        Integer masAsiento = 0;
        Integer idMaster = masterFacade.findProximoByEmpresa(factCab.getIdCteTipo().getIdEmpresa());
        if (idMaster != null) {
            Master master = masterFacade.find(idMaster);
            if (master != null) {
                masAsiento = master.getMAsiento();
            }
        }
        if (factCab.getIdCteTipo().getcTipoOperacion() < 17) {
            // factura
            if (factCab.getIdmoneda().getIdMoneda() > 1) {
                cotizacionDolar = factCab.getCotDolar();
                // System.out.println("ES EN DOLARES LA DEBO DE PESIFICAR (cotizacion: " + cotizacionDolar + ") ");
            } else {
                cotizacionDolar = new BigDecimal(1);
            }

        }
        masAsiento = masAsiento + 1;
        //Contadores para los pases
        Integer paseDetalle = 1;
        // busco el padron
        Padron pad = padronFacade.getPadronByCodigo(factCab.getIdPadron());
        //Me fijo si es debe o haber
        BigDecimal signo = new BigDecimal(1);
        BigDecimal subTotal = new BigDecimal(0);
        if (factCab.getIdCteTipo().getSurenu().equals("D")) {
            signo = signo.negate();
        }
        try {

            for (FactDetalle det : factDetalle) {

                Master masterDetalle = new Master();
                masterDetalle.setIdFactCab(factCab);
                masterDetalle.setCodigoLibro(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos())));
                masterDetalle.setCotizacion(factCab.getCotDolar());
                masterDetalle.setFechayhora(fechaHoy);
                masterDetalle.setIdEmpresa(factCab.getIdCteTipo().getIdEmpresa().getIdEmpresa());
                masterDetalle.setMAsiento(masAsiento);
                if ((pad.getPadronApelli() + " " + pad.getPadronNombre()).length() > 30) {
                    masterDetalle.setMDetalle((pad.getPadronApelli() + " <>" + pad.getPadronNombre()).substring(0, 30));
                } else {
                    masterDetalle.setMDetalle(pad.getPadronApelli() + " " + pad.getPadronNombre());
                }
                masterDetalle.setMFechaEmi(factCab.getFechaEmision());

                masterDetalle.setMImporte((det.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN));
                subTotal = subTotal.add((det.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN));

                masterDetalle.setMIngreso(factCab.getFechaConta());
                masterDetalle.setMPase(Short.valueOf(Integer.toString(paseDetalle)));

                //si es canje
                if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                    masterDetalle.setMVence(factCab.getFechaEmision());
                } else {
                    masterDetalle.setMVence(factCab.getFechaVto());
                }

                masterDetalle.setNroComp(factCab.getNumero());
                masterDetalle.setPadronCodigo(factCab.getIdPadron());
                masterDetalle.setPlanCuentas(det.getImputacion());
                masterDetalle.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdCteTipo())));
                masterDetalle.setMColumIva(Short.valueOf(Integer.toString(det.getIdLibro())));

                //Parametros que van en 0
                masterDetalle.setAutorizaCodigo(Short.valueOf("0"));
                masterDetalle.setTipoCompAsoc(Short.valueOf("0"));
                masterDetalle.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                masterDetalle.setCondGan(Short.valueOf("0"));
                masterDetalle.setCondIva(Short.valueOf("0"));
                masterDetalle.setMUnidades(BigDecimal.ZERO);
                masterDetalle.setNroCompAsoc(Long.valueOf("0"));
                masterDetalle.setNroCompPreimp(Long.valueOf("0"));
                masterDetalle.setCodActividad(Long.valueOf("0"));
                masterDetalle.setMMinuta(Long.valueOf("0"));
                masterDetalle.setMCtacte("N");
                masterDetalle.setOperadorCodigo(user.getUsuario());
                masterDetalle.setMAsientoRub(0);

                boolean transaccionMasterDetalle;
                transaccionMasterDetalle = masterFacade.setMasterNuevo(masterDetalle);
                //si la trnsaccion fallo devuelvo el mensaje
                if (!transaccionMasterDetalle) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con el detalle: " + det.getDetalle());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Sumo uno al contador de pases
                paseDetalle++;
            }

            for (FactFormaPago fp : factFormaPago) {
                Master masterFormaPago = new Master();
                masterFormaPago.setIdFactCab(factCab);
                masterFormaPago.setCodigoLibro(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos())));
                masterFormaPago.setCotizacion(factCab.getCotDolar());
                masterFormaPago.setFechayhora(fechaHoy);
                masterFormaPago.setIdEmpresa(factCab.getIdCteTipo().getIdEmpresa().getIdEmpresa());
                masterFormaPago.setMAsiento(masAsiento);
                masterFormaPago.setMFechaEmi(factCab.getFechaEmision());
                masterFormaPago.setMImporte((fp.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).negate());

                masterFormaPago.setMIngreso(factCab.getFechaConta());
                masterFormaPago.setMPase(Short.valueOf(Integer.toString(paseDetalle)));

                //si es canje
                if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                    masterFormaPago.setMVence(factCab.getFechaEmision());
                } else {
                    masterFormaPago.setMVence(factCab.getFechaVto());
                }
                masterFormaPago.setNroComp(factCab.getNumero());
                masterFormaPago.setPadronCodigo(factCab.getIdPadron());

                masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdCteTipo())));

                if (fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(1) || fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(6)) {
                    if ((pad.getPadronApelli() + " " + pad.getPadronNombre()).length() > 30) {
                        masterFormaPago.setMDetalle((pad.getPadronApelli() + " " + pad.getPadronNombre()).substring(0, 30));
                    } else {
                        masterFormaPago.setMDetalle(pad.getPadronApelli() + " " + pad.getPadronNombre());
                    }
                    masterFormaPago.setMCtacte("N");
                    masterFormaPago.setPlanCuentas(fp.getCtaContable());

                } else {
                    if (factCab.getIdmoneda().getIdMoneda() > 1) {
                        masterFormaPago.setMDetalle(factCab.getIdCteTipo().getDescripcion() + " | U$S" + factCab.getCotDolar());
                    } else {
                        masterFormaPago.setMDetalle(factCab.getIdCteTipo().getDescripcion() + " | Pesos");
                    }

                    masterFormaPago.setMCtacte("S");
                    // Si es Compras a Cuenta Corriente busco la cuenta contable en la categoria del padron
                    if (fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(2)) {
                        CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                        masterFormaPago.setPlanCuentas(Integer.toString(ctacteCatego.getPlanCuentas()));
                    } else {
                        if (fp.getIdFormaPago().getTipo().getIdSIsModulo().getIdSisModulos().equals(2)) {
                            CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                            masterFormaPago.setPlanCuentas(Integer.toString(ctacteCatego.getPlanCuentas()));
                        } else {
                            masterFormaPago.setPlanCuentas(fp.getCtaContable());
                        }

                    }
                }

                //Parametros que van en 0
                masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                masterFormaPago.setCondGan(Short.valueOf("0"));
                masterFormaPago.setCondIva(Short.valueOf("0"));
                masterFormaPago.setMColumIva(Short.valueOf("0"));
                masterFormaPago.setMUnidades(BigDecimal.ZERO);
                masterFormaPago.setNroCompAsoc(Long.valueOf("0"));
                masterFormaPago.setNroCompPreimp(Long.valueOf("0"));
                masterFormaPago.setCodActividad(Long.valueOf("0"));
                masterFormaPago.setMMinuta(Long.valueOf("0"));
                masterFormaPago.setOperadorCodigo(user.getUsuario());
                masterFormaPago.setMAsientoRub(0);

                boolean transaccionMasterFormaPago;
                transaccionMasterFormaPago = masterFacade.setMasterNuevo(masterFormaPago);
                //si la trnsaccion fallo devuelvo el mensaje
                if (!transaccionMasterFormaPago) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con la forma de pago: " + fp.getDetalle());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Sumo uno al contador de pases
                paseDetalle++;
            }

            for (FactPie fi : factPie) {
                if (fi.getImporte().equals(BigDecimal.ZERO)) {
                    continue;
                }
                Master masterImputa = new Master();
                masterImputa.setIdFactCab(factCab);
                masterImputa.setCodigoLibro(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos())));
                masterImputa.setCotizacion(factCab.getCotDolar());
                masterImputa.setFechayhora(fechaHoy);
                masterImputa.setIdEmpresa(factCab.getIdCteTipo().getIdEmpresa().getIdEmpresa());
                masterImputa.setMAsiento(masAsiento);
                if ((pad.getPadronApelli() + " " + pad.getPadronNombre()).length() > 30) {
                    masterImputa.setMDetalle((pad.getPadronApelli() + " " + pad.getPadronNombre()).substring(0, 30));
                } else {
                    masterImputa.setMDetalle(pad.getPadronApelli() + " " + pad.getPadronNombre());
                }
                masterImputa.setMFechaEmi(factCab.getFechaEmision());
                masterImputa.setMImporte(fi.getImporte().multiply(signo));
                masterImputa.setMImporte((fi.getImporte().multiply(signo)).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN));
                masterImputa.setMIngreso(factCab.getFechaConta());
                masterImputa.setMPase(Short.valueOf(Integer.toString(paseDetalle)));
                //si es canje
                if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                    masterImputa.setMVence(factCab.getFechaEmision());
                } else {
                    masterImputa.setMVence(factCab.getFechaVto());
                }

                masterImputa.setNroComp(factCab.getNumero());
                masterImputa.setPadronCodigo(factCab.getIdPadron());
                masterImputa.setPlanCuentas(fi.getCtaContable());
                masterImputa.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdCteTipo())));
                masterImputa.setMColumIva(Short.valueOf(Integer.toString(fi.getIdLibro())));
                masterImputa.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));

                //Parametros que van en 0
                masterImputa.setAutorizaCodigo(Short.valueOf("0"));
                masterImputa.setTipoCompAsoc(Short.valueOf("0"));
                masterImputa.setCondGan(Short.valueOf("0"));
                masterImputa.setCondIva(Short.valueOf("0"));
                masterImputa.setMUnidades(BigDecimal.ZERO);
                masterImputa.setNroCompAsoc(Long.valueOf("0"));
                masterImputa.setNroCompPreimp(Long.valueOf("0"));
                masterImputa.setCodActividad(Long.valueOf("0"));
                masterImputa.setMMinuta(Long.valueOf("0"));
                masterImputa.setMCtacte("N");
                masterImputa.setOperadorCodigo(user.getUsuario());
                masterImputa.setMAsientoRub(0);
                boolean transaccionMaster2;
                transaccionMaster2 = masterFacade.setMasterNuevo(masterImputa);
                //si la trnsaccion fallo devuelvo el mensaje
                if (!transaccionMaster2) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con la imputacion: " + fi.getDetalle());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Sumo uno al contador de pases
                paseDetalle++;
            }

            /*
            
            
             CANJE: 
             SI ES VENTAS Y ES CANJE
                
             si es canjeInsumos es true y si tipo operacion 5 y modulo ventas = 2
             */
            if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) && factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {

                // CANJE CREDITO PASE 1  MASTER
                for (FactFormaPago fp : factFormaPago) {
                    //fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(7) 
                    // busco la cuenta contable para el cereal que viene en la factcab
                    CanjesContratosCereales objContratosCereales = canjesContratosCerealesFacade.findCuentaPorCereal(factCab.getIdCteTipo().getIdEmpresa(), factCab.getCerealCanje().getCerealCodigo());

                    Master masterFormaPago = new Master();
                    masterFormaPago.setIdFactCab(factCab);
                    masterFormaPago.setCodigoLibro(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos())));
                    masterFormaPago.setCotizacion(factCab.getCotDolar());
                    masterFormaPago.setFechayhora(fechaHoy);
                    masterFormaPago.setIdEmpresa(factCab.getIdCteTipo().getIdEmpresa().getIdEmpresa());
                    masterFormaPago.setMAsiento(masAsiento);
                    masterFormaPago.setMFechaEmi(factCab.getFechaEmision());

                    if (factCab.getDiferidoVto().equals(true)) {
                        masterFormaPago.setMImporte((fp.getImporte().multiply(signo).multiply(cotizacionDolar)).setScale(2, RoundingMode.HALF_EVEN));

                    } else {
                        masterFormaPago.setMImporte((subTotal.multiply(signo)).setScale(2, RoundingMode.HALF_EVEN));
                    }
                    // si la marca diferiere al vencimiento sn = TRUE informo el TOTAL, caso contrario el SUBTOTAL
                    masterFormaPago.setMIngreso(factCab.getFechaConta());
                    masterFormaPago.setMPase(Short.valueOf(Integer.toString(paseDetalle)));
                    //si es canje
                    if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                        masterFormaPago.setMVence(factCab.getFechaEmision());
                    } else {
                        masterFormaPago.setMVence(factCab.getFechaVto());
                    }
                    masterFormaPago.setNroComp(factCab.getNumero());
                    masterFormaPago.setPadronCodigo(factCab.getIdPadron());
                    masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdCteTipo())));
                    masterFormaPago.setMDetalle("CREDITO CTA CEREAL");
                    masterFormaPago.setMCtacte("S");
                    if (objContratosCereales != null) {
                        masterFormaPago.setPlanCuentas(objContratosCereales.getCtaContable());
                    } else {
                        masterFormaPago.setPlanCuentas(fp.getCtaContable());
                    }

                    //Parametros que van en 0
                    masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                    masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                    masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                    masterFormaPago.setCondGan(Short.valueOf("0"));
                    masterFormaPago.setCondIva(Short.valueOf("0"));
                    masterFormaPago.setMColumIva(Short.valueOf("0"));
                    masterFormaPago.setMUnidades(BigDecimal.ZERO);
                    masterFormaPago.setNroCompAsoc(Long.valueOf("0"));
                    masterFormaPago.setNroCompPreimp(Long.valueOf("0"));
                    masterFormaPago.setCodActividad(Long.valueOf("0"));
                    masterFormaPago.setMMinuta(Long.valueOf("0"));
                    masterFormaPago.setOperadorCodigo(user.getUsuario());
                    masterFormaPago.setMAsientoRub(0);

                    boolean transaccionMasterFormaPagoPase1;
                    transaccionMasterFormaPagoPase1 = masterFacade.setMasterNuevo(masterFormaPago);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccionMasterFormaPagoPase1) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con la forma de pago pase 1: " + fp.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Sumo uno al contador de pases
                    paseDetalle++;
                }
                // CANJE CREDITO PASE 2 MASTER
                for (FactFormaPago fp : factFormaPago) {
                    //fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(7) 
                    Master masterFormaPago = new Master();
                    masterFormaPago.setIdFactCab(factCab);
                    masterFormaPago.setCodigoLibro(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos())));
                    masterFormaPago.setCotizacion(factCab.getCotDolar());
                    masterFormaPago.setFechayhora(fechaHoy);
                    masterFormaPago.setIdEmpresa(factCab.getIdCteTipo().getIdEmpresa().getIdEmpresa());
                    masterFormaPago.setMAsiento(masAsiento);
                    masterFormaPago.setMFechaEmi(factCab.getFechaEmision());
                    if (factCab.getDiferidoVto().equals(true)) {
                        masterFormaPago.setMImporte((fp.getImporte().multiply(signo).multiply(cotizacionDolar)).setScale(2, RoundingMode.HALF_EVEN).negate());

                    } else {
                        masterFormaPago.setMImporte((subTotal.multiply(signo)).setScale(2, RoundingMode.HALF_EVEN).negate());
                    }
                    masterFormaPago.setMIngreso(factCab.getFechaConta());
                    masterFormaPago.setMPase(Short.valueOf(Integer.toString(paseDetalle)));
                    //si es canje
                    if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion().equals(5) || factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                        masterFormaPago.setMVence(factCab.getFechaEmision());
                    } else {
                        masterFormaPago.setMVence(factCab.getFechaVto());
                    }
                    masterFormaPago.setNroComp(factCab.getNumero());
                    masterFormaPago.setPadronCodigo(factCab.getIdPadron());
                    masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdCteTipo())));
                    masterFormaPago.setMDetalle("CREDITO CTACTE");
                    masterFormaPago.setMCtacte("N");
                    // Si es Compras a Cuenta Corriente busco la cuenta contable en la categoria del padron
                    if (fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(2)) {
                        CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                        masterFormaPago.setPlanCuentas(Integer.toString(ctacteCatego.getPlanCuentas()));
                    } else {
                        CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                        masterFormaPago.setPlanCuentas(Integer.toString(ctacteCatego.getPlanCuentas()));
                        //masterFormaPago.setPlanCuentas(fp.getCtaContable());
                    }

                    //Parametros que van en 0
                    masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                    masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                    masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                    masterFormaPago.setCondGan(Short.valueOf("0"));
                    masterFormaPago.setCondIva(Short.valueOf("0"));
                    masterFormaPago.setMColumIva(Short.valueOf("0"));
                    masterFormaPago.setMUnidades(BigDecimal.ZERO);
                    masterFormaPago.setNroCompAsoc(Long.valueOf("0"));
                    masterFormaPago.setNroCompPreimp(Long.valueOf("0"));
                    masterFormaPago.setCodActividad(Long.valueOf("0"));
                    masterFormaPago.setMMinuta(Long.valueOf("0"));
                    masterFormaPago.setOperadorCodigo(user.getUsuario());
                    masterFormaPago.setMAsientoRub(0);

                    boolean transaccionMasterFormaPagoPase2;
                    transaccionMasterFormaPagoPase2 = masterFacade.setMasterNuevo(masterFormaPago);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccionMasterFormaPagoPase2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con la forma de pago pase 2: " + fp.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Sumo uno al contador de pases
                    paseDetalle++;
                }

            }

            respuesta.setControl(AppCodigo.CREADO, "Comprobante creado con exito, con detalles");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }

    }
}
