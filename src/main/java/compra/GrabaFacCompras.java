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
import entidades.FacCompras;
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
 import persistencia.FacComprasFacade; 
import utils.Utils;

/**
 *
 * @author Kernel informatica
 */
public class GrabaFacCompras {

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
    FacComprasFacade facComprasFacade;

 public Boolean grabarFactCompras(FactCab factCab, List<FactDetalle> factDetalle, List<FactFormaPago> factFormaPago, List<FactPie> factPie, Usuario user) {
        System.out.println("::::::::: Ejecuta ----------------------> FacCompras() ");
        ServicioResponse respuesta = new ServicioResponse();
        //Seteo la fecha de hoy
        Calendar calendario = new GregorianCalendar();
        Date fechaHoy = calendario.getTime();
        // categoria sybase de iva
        List<ModeloDetalle> modeloDetalleArray = new ArrayList<>();
        Padron condiIva = padronFacade.find(factCab.getIdPadron());
        Integer idFacCompras = facComprasFacade.findProximoIdByEmpresa(factCab.getIdCteTipo().getIdEmpresa());
        if (idFacCompras != null) {
            idFacCompras = idFacCompras + 1;
        } else {
            idFacCompras = 1;
        }

        //Contadores para los pases
        Integer paseDetalle = 1;
        //Me fijo si es debe o haber
        BigDecimal signo = new BigDecimal(1);
        String formateada = String.format("%012d", factCab.getNumero());
        String ptoVtaTemp = formateada.substring(0, 4);
        String nroCompTemp = formateada.substring(4, formateada.length());
        String facturadoSn = "N";
        String contabilSn = "N";
        Integer ptoVta = Integer.parseInt(ptoVtaTemp);
        Integer nroComp = Integer.parseInt(nroCompTemp);
        ptoVtaTemp = "";
        nroCompTemp = "";

        // Verifico si son remitos o otro tipo de comprobante
        if (factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(1) || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(31)) {
            facturadoSn = "N";
            contabilSn = "N";
        } else {
            facturadoSn = "S";
            contabilSn = "S";
        }

        if (factCab.getIdCteTipo().getSurenu().equals("D")) {
            signo = signo.negate();
        }
        try {

// arranco desde el moviemnto 1 
            BigDecimal totalPieFactura = new BigDecimal(0);
            BigDecimal totalDetalleFactura = new BigDecimal(0);
            BigDecimal totalImpuestos = new BigDecimal(0);
            BigDecimal totalPrecioUnitario = new BigDecimal(0);
            BigDecimal totalCantidad = new BigDecimal(0);
            BigDecimal totalIva21 = new BigDecimal(0);
            BigDecimal totalIva105 = new BigDecimal(0);
            BigDecimal totalIva27 = new BigDecimal(0);
            BigDecimal totalPercep1 = new BigDecimal(0);
            BigDecimal netoIva21 = new BigDecimal(0);
            BigDecimal netoIva105 = new BigDecimal(0);
            BigDecimal netoIva27 = new BigDecimal(0);
            for (FactDetalle det : factDetalle) {

                FacCompras facComprasDetalle = new FacCompras(idFacCompras,
                        det.getCodProducto(),
                        Short.valueOf(Integer.toString(det.getIdFactCab().getIdCteTipo().getcTipoOperacion())),
                        det.getIdFactCab().getFechaEmision(),
                        Short.valueOf(Integer.toString(det.getIdFactCab().getIdCteTipo().getcTipoOperacion())),
                        Long.valueOf(nroComp),
                        det.getIdFactCab().getIdPadron(),
                        Short.valueOf(Integer.toString(paseDetalle)),
                        Long.parseLong(det.getIdFactCab().getCuit()),
                        Short.valueOf(Integer.toString(ptoVta)));

                facComprasDetalle.setIdEmpresa(factCab.getIdCteTipo().getIdEmpresa().getIdEmpresa());
                facComprasDetalle.setCFormaPago(Short.valueOf(Integer.toString(0)));
                facComprasDetalle.setCNombre(det.getIdFactCab().getNombre());
                facComprasDetalle.setCCantidad(det.getCantidad());
                facComprasDetalle.setCDescripcion(det.getDetalle());
                facComprasDetalle.setCPrecioUnitario(det.getPrecio());
                facComprasDetalle.setCFechaVencimiento(det.getIdFactCab().getFechaVto());
                facComprasDetalle.setCFacturadoSn(facturadoSn.charAt(0));
                facComprasDetalle.setCCodigoOperador(user.getUsuario());
                facComprasDetalle.setCHora(fechaHoy);
                facComprasDetalle.setCFechaContabil(det.getIdFactCab().getFechaConta());
                facComprasDetalle.setBarra(det.getIdFactCab().getCodBarra());

                // percepciones e impuestos particulares
                for (FactPie pie : factPie) {
                    List<ModeloDetalle> modelosDetalle = modeloDetalleFacade.getBuscaModeloDetallePorLibro(pie.getIdLibro());
                    for (ModeloDetalle modeloDetalle : modelosDetalle) {
                        if (modeloDetalle == null) {
                            facComprasDetalle.setCPercepcion1(BigDecimal.ZERO);
                            totalPercep1 = new BigDecimal(0);
                        } else {
                            // Percepciones
                            if (modeloDetalle.getIdLibro().getPosicion().equals("D") && modeloDetalle.getDescripcion().equals(pie.getDetalle())) {
                                totalPercep1 = totalPercep1.add(pie.getImporte());

                            } else {
                            }

                        }
                    }

                }
                facComprasDetalle.setCPercepcion1(totalPercep1);
                if (det.getIvaPorc().equals(new BigDecimal(10.5)) || det.getIvaPorc().equals(new BigDecimal(10.50)) || det.getIvaPorc().equals(new BigDecimal(1050))) {
                    totalIva105 = det.getImporte().multiply(new BigDecimal(10.5)).divide(new BigDecimal(100));
                    facComprasDetalle.setCIvaRi(BigDecimal.ZERO);
                    facComprasDetalle.setCIva105(totalIva105);
                    facComprasDetalle.setCIvaRni(BigDecimal.ZERO);
                    facComprasDetalle.setCPercepcion2(BigDecimal.ZERO);
                    netoIva105 = det.getImporte();
                    totalDetalleFactura = netoIva105.add(totalIva105).add(totalPercep1);

                } else if (det.getIvaPorc().equals(new BigDecimal(21))) {
                    // System.out.println("IVA 21 ivaRi -> " + det.getIvaPorc());
                    totalIva21 = det.getImporte().multiply(new BigDecimal(21)).divide(new BigDecimal(100));
                    facComprasDetalle.setCIvaRi(totalIva21);
                    facComprasDetalle.setCIva105(BigDecimal.ZERO);
                    facComprasDetalle.setCIvaRni(BigDecimal.ZERO);
                    facComprasDetalle.setCPercepcion2(BigDecimal.ZERO);

                    netoIva21 = det.getImporte();
                    totalDetalleFactura = netoIva21.add(totalIva21).add(totalPercep1);

                } else if (det.getIvaPorc().equals(new BigDecimal(27))) {
                    // System.out.println("IVA 27 c_percepcion2 -> " + det.getIvaPorc());
                    facComprasDetalle.setCIvaRi(BigDecimal.ZERO);
                    facComprasDetalle.setCIva105(BigDecimal.ZERO);
                    totalIva27 = det.getImporte().multiply(new BigDecimal(27)).divide(new BigDecimal(100));
                    facComprasDetalle.setCPercepcion2(totalIva27);
                    facComprasDetalle.setCIvaRni(BigDecimal.ZERO);
                    netoIva27 = det.getImporte();
                    totalDetalleFactura = netoIva27.add(totalIva27).add(totalPercep1);

                } else if (det.getIvaPorc().equals(0)) {
                    //Exento
                    // System.out.println("IVA PROCEJANTE ES 0 PUEDE SER EXENTO -> " + det.getIvaPorc());
                    facComprasDetalle.setCIvaRi(BigDecimal.ZERO);
                    facComprasDetalle.setCIva105(BigDecimal.ZERO);
                    facComprasDetalle.setCIvaRni(BigDecimal.ZERO);
                    facComprasDetalle.setCPercepcion2(BigDecimal.ZERO);
                    totalPercep1 = new BigDecimal(0);
                    totalDetalleFactura = new BigDecimal(0);
                }

                facComprasDetalle.setCBonificacion(totalDetalleFactura);
                totalDetalleFactura = new BigDecimal(0);
                facComprasDetalle.setCCondicionIva(Short.valueOf(Integer.toString(condiIva.getCondIva().getCondiva())));
                facComprasDetalle.setCDeposito(det.getIdDepositos().getCodigoDep());

                //Valores que van en 0
                facComprasDetalle.setCDescuento(new BigDecimal(0));
                facComprasDetalle.setCFormaPago((Short.valueOf(Integer.toString(0))));
                facComprasDetalle.setCImpuestoInterno(BigDecimal.ZERO);
                facComprasDetalle.setCOtroImpuesto(BigDecimal.ZERO);
                facComprasDetalle.setCCodigoRelacion(0);
                facComprasDetalle.setCTipoComprobanteAsoc(Short.valueOf(Integer.toString(0)));
                facComprasDetalle.setCNumeroComprobanteAsoc(Long.parseLong("0"));
                facComprasDetalle.setCContabil(contabilSn);
                facComprasDetalle.setCRetencionMiel(BigDecimal.ZERO);
                facComprasDetalle.setCRetencion2da(BigDecimal.ZERO);
                facComprasDetalle.setCanjeSn("N");
                facComprasDetalle.setCanjeNroCto("N");
                facComprasDetalle.setCSircrebStafe(BigDecimal.ZERO);
                facComprasDetalle.setCSircrebCdba(BigDecimal.ZERO);
                facComprasDetalle.setcDolar(BigDecimal.ZERO);
                // si es factura no se graba detalle en facCompras Sybase y hacemos la persistencia
                if (det.getIdFactCab().getIdCteTipo().getcTipoOperacion() >= 17) {
                    if (det.getIdFactCab().getIdCteTipo().getcTipoOperacion().equals(17)) {
                        // si son remitos  grabo la cotizacion del dolar
                        facComprasDetalle.setcDolar(factCab.getCotDolar());
                    } else {
                        facComprasDetalle.setcDolar(BigDecimal.ZERO);
                    }

                    boolean transaccionFacC;
                    transaccionFacC = facComprasFacade.setFacComprasNuevo(facComprasDetalle);
                    //si la transaccion fallo devuelvo el mensaje
                    if (!transaccionFacC) {
                        return false;
                    }

                    //Sumo uno al contador de pases
                    paseDetalle++;

                }

            }

            // Movimiento cierre = 0
            FacCompras movCierre = new FacCompras(idFacCompras,
                    "CIERRE",
                    Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())),
                    factCab.getFechaEmision(),
                    Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())),
                    Long.valueOf(nroComp),
                    factCab.getIdPadron(),
                    Short.valueOf(Integer.toString(0)),
                    Long.parseLong(factCab.getCuit()),
                    Short.valueOf(Integer.toString(ptoVta)));

            for (FactPie pie : factPie) {
                List<ModeloDetalle> modelosDetalle = modeloDetalleFacade.getBuscaModeloDetallePorLibro(pie.getIdLibro());
                for (ModeloDetalle modeloDetalle : modelosDetalle) {
                    if (modeloDetalle == null) {
                        movCierre.setCPercepcion1(BigDecimal.ZERO);
                        totalPercep1 = new BigDecimal(0);
                    } else {
                        // Percepciones
                        if (modeloDetalle.getIdLibro().getPosicion().equals("D") && modeloDetalle.getDescripcion().equals(pie.getDetalle())) {

                            totalPercep1 = totalPercep1.add(pie.getImporte());

                        } else {
                            movCierre.setCPercepcion1(BigDecimal.ZERO);
                        }
                        // grago los iva en el mov 0
                        if (pie.getPorcentaje().equals(new BigDecimal(10.5)) || pie.getPorcentaje().equals(new BigDecimal(10.50)) || pie.getPorcentaje().equals(new BigDecimal(1050))) {
                            movCierre.setCIva105(pie.getImporte());
                            totalIva105 = pie.getImporte();

                        } else if (pie.getPorcentaje().equals(new BigDecimal(21)) || pie.getPorcentaje().equals(new BigDecimal(21.00))) {

                            totalIva21 = pie.getImporte();
                            movCierre.setCIvaRi(pie.getImporte());

                        } else if (pie.getPorcentaje().equals(new BigDecimal(27))) {
                            totalIva27 = pie.getImporte();
                            movCierre.setCPercepcion2(pie.getImporte());

                        }

                    }

                    totalPieFactura = pie.getBaseImponible().add(pie.getImporte()).add(totalIva21).add(totalIva27).add(totalIva105).add(totalPercep1);
                }

            }
            for (FactDetalle det : factDetalle) {

                totalPrecioUnitario = totalPrecioUnitario.add(det.getPrecio());
                totalCantidad = totalCantidad.add(det.getCantidad());
                movCierre.setCDeposito(det.getIdDepositos().getCodigoDep());

                /*
                
                 aca agregar los totalizadores de iva recorre fac detalle y suma
                
                 */
            }

            // aca van los totalizados del iva
            movCierre.setCPrecioUnitario(totalPrecioUnitario);
            movCierre.setCCantidad(totalCantidad);
            movCierre.setCBonificacion(totalPieFactura);
            ///////////////////////////////////////////////
            movCierre.setIdEmpresa(factCab.getIdCteTipo().getIdEmpresa().getIdEmpresa());
            movCierre.setCFormaPago(Short.valueOf(Integer.toString(0)));
            movCierre.setCNombre(factCab.getNombre());
            movCierre.setCDescripcion(factCab.getObservaciones());
            movCierre.setCPercepcion1(totalPercep1);
            movCierre.setCFechaVencimiento(factCab.getFechaVto());
            movCierre.setCFacturadoSn(facturadoSn.charAt(0));
            movCierre.setCCodigoOperador(user.getUsuario());
            movCierre.setCHora(fechaHoy);
            movCierre.setCFechaContabil(factCab.getFechaConta());
            movCierre.setBarra("");
            movCierre.setCCondicionIva(Short.valueOf(Integer.toString(condiIva.getCondIva().getCondiva())));
            //Valores que van en 0
            movCierre.setCIvaRni(BigDecimal.ZERO);
            movCierre.setCPercepcion2(BigDecimal.ZERO);
            movCierre.setCDescuento(new BigDecimal(0));
            movCierre.setCFormaPago((Short.valueOf(Integer.toString(0))));

            movCierre.setCImpuestoInterno(BigDecimal.ZERO);
            movCierre.setCOtroImpuesto(BigDecimal.ZERO);

            movCierre.setCCodigoRelacion(0);
            movCierre.setCTipoComprobanteAsoc(Short.valueOf(Integer.toString(0)));
            movCierre.setCNumeroComprobanteAsoc(Long.parseLong("0"));

            movCierre.setCContabil(contabilSn);
            movCierre.setCRetencionMiel(BigDecimal.ZERO);
            movCierre.setCRetencion2da(BigDecimal.ZERO);
            movCierre.setCanjeSn("N");
            movCierre.setCanjeNroCto("N");
            movCierre.setCSircrebStafe(BigDecimal.ZERO);
            movCierre.setCSircrebCdba(BigDecimal.ZERO);
            movCierre.setZfacFecha(fechaHoy);
            movCierre.setZfacPasado(true);
            movCierre.setZfacNovedad(fechaHoy);
            boolean transaccion0;
            transaccion0 = facComprasFacade.setFacComprasNuevo(movCierre);
            //si la trnsaccion fallo devuelvo el mensaje
            if (!transaccion0) {
                return false;
            }

            // fin movimiento 0 
        } catch (Exception ex) {
            System.out.println(AppCodigo.ERROR + " :::::::::::::::::: ----> " + ex.getMessage());
            return false;
        }
        System.out.println("::::::::: FIN  ----------------------> FacCompras() :: Stock pasado exitosamente !!!");
        return true;
    }

}
