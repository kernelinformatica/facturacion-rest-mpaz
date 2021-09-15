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
public class GrabaFacCompraSybase {

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

  public Boolean grabarFactComprasSybase(FactCab factCab, List<FactDetalle> factDetalle, List<FactFormaPago> factFormaPago, List<FactPie> factPie, Usuario user) {
        System.out.println("::::::::: Ejecuta Clase ----------------------> grabarFactComprasSybase()  -> nroComprobante: " + factCab.getNumero() + " | Tipo SisComp: " + factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes());

        ServicioResponse respuesta = new ServicioResponse();
        //Seteo la fecha de hoy
        Calendar calendario = new GregorianCalendar();
        Date fechaHoy = calendario.getTime();
        // categoria sybase de iva
        List<ModeloDetalle> modeloDetalleArray = new ArrayList<>();
        Padron condiIva = padronFacade.find(factCab.getIdPadron());
        //Contadores para los pases
        Integer paseDetalle = 1;
        //Me fijo si es debe o haber
        BigDecimal signo = new BigDecimal(1);

        String formateada = String.format("%012d", factCab.getNumero());
        String ptoVtaTemp = formateada.substring(0, 4);
        String nroCompTemp = formateada.substring(4, formateada.length());
        Integer ptoVta = Integer.parseInt(ptoVtaTemp);
        Integer nroComp = Integer.parseInt(nroCompTemp);
        Integer cbteTipoCompSybase = 0;
        ptoVtaTemp = "";
        nroCompTemp = "";
        String contabilSn;
        String facturadoSn;
        //  SIGNO 
        if (factCab.getIdCteTipo().getSurenu().equals("D")) {
            signo = signo.negate();
        }

        // SI SON REMITOS VAN ESTAS MARCAS
        if (factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(1) || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(31)) {
            contabilSn = "N";
            facturadoSn = "N";
        } else {
            contabilSn = "S";
            facturadoSn = "S";
        }
        //cuantas veces me vas a negar el signo papá
        /*if (factCab.getIdCteTipo().getSurenu().equals("D")) {
         signo = signo.negate();
         }*/
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
            BigDecimal totalPercep2 = new BigDecimal(0);
            BigDecimal netoIva21 = new BigDecimal(0);
            BigDecimal netoIva105 = new BigDecimal(0);
            BigDecimal netoIva27 = new BigDecimal(0);
            BigDecimal cotizacionDolar = new BigDecimal(1);
            // dolarizo o pesifico 
            if (factCab.getIdCteTipo().getcTipoOperacion() <= 17) {
                // factura
                if (factCab.getIdmoneda().getIdMoneda() > 1) {
                    cotizacionDolar = factCab.getCotDolar();
                    System.out.println("ES EN DOLARES LA DEBO DE PESIFICAR (cotizacion: " + cotizacionDolar + ") ");
                } else {
                    cotizacionDolar = new BigDecimal(1);
                }
                System.out.println("COTIZACION DOLAR: " + cotizacionDolar);

            }
            Integer formaPagoSeleccionada = 0;
            for (FactDetalle det : factDetalle) {
                totalPieFactura = new BigDecimal(0);
                totalDetalleFactura = new BigDecimal(0);
                totalImpuestos = new BigDecimal(0);
                totalPrecioUnitario = new BigDecimal(0);
                totalCantidad = new BigDecimal(0);
                totalIva21 = new BigDecimal(0);
                totalIva105 = new BigDecimal(0);
                totalIva27 = new BigDecimal(0);
                totalPercep1 = new BigDecimal(0);
                totalPercep2 = new BigDecimal(0);
                netoIva21 = new BigDecimal(0);
                netoIva105 = new BigDecimal(0);
                netoIva27 = new BigDecimal(0);
                FacComprasSybase facComprasDetalle = new FacComprasSybase(det.getCodProducto(),
                        Short.valueOf(Integer.toString(det.getIdFactCab().getIdCteTipo().getcTipoOperacion())),
                        det.getIdFactCab().getFechaEmision(),
                        Short.valueOf(Integer.toString(det.getIdFactCab().getIdCteTipo().getcTipoOperacion())),
                        Long.valueOf(nroComp),
                        det.getIdFactCab().getIdPadron(),
                        Short.valueOf(Integer.toString(paseDetalle)),
                        Long.parseLong(det.getIdFactCab().getCuit()),
                        Short.valueOf(Integer.toString(ptoVta)));
                // valores 0 por defecto para que no se graben en nulo
                facComprasDetalle.setCRetencion1(Double.valueOf(0));
                facComprasDetalle.setCRetencion2(Double.valueOf(0));
                facComprasDetalle.setCIvaRi(Double.valueOf(0));
                facComprasDetalle.setCIva105(totalIva105.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                facComprasDetalle.setCIvaRni(Double.valueOf(0));
                facComprasDetalle.setCPercepcion1(Double.valueOf(0));
                facComprasDetalle.setCPercepcion2(Double.valueOf(0));
                facComprasDetalle.setCRetencion2da(Double.valueOf(0));
                facComprasDetalle.setCRetencionMiel(Double.valueOf(0));
                facComprasDetalle.setCSircrebCdba(Double.valueOf(0));
                facComprasDetalle.setCSircrebStafe(Double.valueOf(0));
                facComprasDetalle.setCNombre(det.getIdFactCab().getNombre());
                facComprasDetalle.setCCantidad(det.getCantidad().setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                Double precioUnitario = det.getPrecio().multiply(signo).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
                facComprasDetalle.setCPrecioUnitario(precioUnitario);
                facComprasDetalle.setCFechaVencimiento(det.getIdFactCab().getFechaVto());
                facComprasDetalle.setCFacturadoSn(facturadoSn.charAt(0));
                facComprasDetalle.setCCodigoOperador(user.getUsuarioSybase());
                facComprasDetalle.setCHora(fechaHoy);
                facComprasDetalle.setCFechaContabil(det.getIdFactCab().getFechaConta());
                facComprasDetalle.setBarra(det.getIdFactCab().getCodBarra());

                for (FactFormaPago fp : factFormaPago) {
                    formaPagoSeleccionada = fp.getIdFormaPago().getCodigoSysbase();
                }
                facComprasDetalle.setCFormaPago((Short.valueOf(Integer.toString(formaPagoSeleccionada))));
                // percepciones e impuestos particulares
                for (FactPie pie : factPie) {
                    if (pie.getIdSisTipoModelo().getIdSisTipoModelo().equals(6)) {
                        totalPercep1 = (totalPercep1.add(pie.getImporte())).multiply(cotizacionDolar);
                        totalPercep2 = new BigDecimal(0);
                    } else {
                        totalPercep1 = new BigDecimal(0);
                        totalPercep2 = new BigDecimal(0);
                    };
                }

                if (det.getIvaPorc().equals(new BigDecimal(10.5)) || det.getIvaPorc().equals(new BigDecimal(10.50)) || det.getIvaPorc().equals(new BigDecimal(1050))) {
                    totalIva105 = det.getImporte().multiply(det.getIvaPorc()).divide(new BigDecimal(100));
                    //.multiply(signo).multiply(cotizacionDolar).doubleValue()
                    facComprasDetalle.setCRetencion1(Double.valueOf(0));
                    facComprasDetalle.setCRetencion2(Double.valueOf(0));
                    facComprasDetalle.setCIvaRi(Double.valueOf(0));
                    facComprasDetalle.setCIva105(totalIva105.multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                    facComprasDetalle.setCIvaRni(Double.valueOf(0));
                    facComprasDetalle.setCPercepcion2(Double.valueOf(0));
                    netoIva105 = det.getImporte();
                    totalDetalleFactura = (netoIva105.add(totalIva105).add(totalPercep1)).multiply(cotizacionDolar);

                } else if (det.getIvaPorc().equals(new BigDecimal(21))) {
                    // System.out.println("IVA 21 ivaRi -> " + det.getIvaPorc());
                    totalIva21 = det.getImporte().multiply(det.getIvaPorc()).divide(new BigDecimal(100));
                    //.multiply(signo).multiply(cotizacionDolar).doubleValue()
                    facComprasDetalle.setCIvaRi(totalIva21.multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                    facComprasDetalle.setCIva105(Double.valueOf(0));
                    facComprasDetalle.setCIvaRni(Double.valueOf(0));
                    facComprasDetalle.setCPercepcion2(Double.valueOf(0));
                    facComprasDetalle.setCRetencion1(Double.valueOf(0));
                    facComprasDetalle.setCRetencion2(Double.valueOf(0));

                    netoIva21 = det.getImporte();
                    totalDetalleFactura = (netoIva21.add(totalIva21).add(totalPercep1)).multiply(cotizacionDolar);

                } else if (det.getIvaPorc().equals(new BigDecimal(27))) {
                    // System.out.println("IVA 27 c_percepcion2 -> " + det.getIvaPorc());
                    facComprasDetalle.setCIvaRi(Double.valueOf(0));
                    facComprasDetalle.setCIva105(Double.valueOf(0));
                    totalIva27 = det.getImporte().multiply(det.getIvaPorc()).divide(new BigDecimal(100));
                    facComprasDetalle.setCPercepcion2(totalIva27.multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                    facComprasDetalle.setCIvaRni(Double.valueOf(0));
                    facComprasDetalle.setCRetencion1(Double.valueOf(0));
                    facComprasDetalle.setCRetencion2(Double.valueOf(0));

                    netoIva27 = det.getImporte();
                    totalDetalleFactura = (netoIva27.add(totalIva27).add(totalPercep1)).multiply(cotizacionDolar);

                } else if (det.getIvaPorc().equals(0)) {
                    facComprasDetalle.setCIvaRi(Double.valueOf(0));
                    facComprasDetalle.setCIva105(Double.valueOf(0));
                    facComprasDetalle.setCIvaRni(Double.valueOf(0));
                    facComprasDetalle.setCPercepcion2(Double.valueOf(0));
                    facComprasDetalle.setCRetencion1(Double.valueOf(0));
                    facComprasDetalle.setCRetencion2(Double.valueOf(0));

                    totalPercep1 = new BigDecimal(0);
                    totalDetalleFactura = new BigDecimal(0);
                }

                facComprasDetalle.setCBonificacion(totalDetalleFactura.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                totalDetalleFactura = new BigDecimal(0);
                facComprasDetalle.setCCondicionIva(Short.valueOf(Integer.toString(condiIva.getCondIva().getCondiva())));
                facComprasDetalle.setCDeposito(det.getIdDepositos().getCodigoDep());

                //Valores que van en 0
                facComprasDetalle.setCDescuento(Double.valueOf(0));
                facComprasDetalle.setCFormaPago((Short.valueOf(Integer.toString(0))));
                facComprasDetalle.setCImpuestoInterno(Double.valueOf(0));
                facComprasDetalle.setCOtroImpuesto(Double.valueOf(0));
                facComprasDetalle.setCCodigoRelacion(0);
                facComprasDetalle.setCTipoComprobanteAsoc(Short.valueOf(Integer.toString(0)));
                facComprasDetalle.setCNumeroComprobanteAsoc(Long.parseLong("0"));
                facComprasDetalle.setCContabil(contabilSn);
                facComprasDetalle.setCRetencionMiel(Double.valueOf(0));
                facComprasDetalle.setCRetencion2da(Double.valueOf(0));
                facComprasDetalle.setCanjeSn("N");
                facComprasDetalle.setCanjeNroCto("N");
                facComprasDetalle.setCSircrebStafe(Double.valueOf(0));
                facComprasDetalle.setCSircrebCdba(Double.valueOf(0));
                facComprasDetalle.setCDescripcion("N");
                facComprasDetalle.setcDolar(Double.valueOf(0));
                // si es factura no se graba detalle en facCompras Sybase y hacemos la persistencia
                if (det.getIdFactCab().getIdCteTipo().getcTipoOperacion() >= 17) {
                    if (det.getIdFactCab().getIdCteTipo().getcTipoOperacion().equals(17)) {
                        // si son remitos  grabo la cotizacion del dolar
                        facComprasDetalle.setcDolar(factCab.getCotDolar().doubleValue());
                    } else {
                        facComprasDetalle.setcDolar(Double.valueOf(0));
                    }
                    boolean transaccionFacC;
                    transaccionFacC = factComprasSybaseFacade.setFacComprasSybaseNuevo(facComprasDetalle);
                    //si la transaccion fallo devuelvo el mensaje
                    if (!transaccionFacC) {
                        return false;
                    }
                    paseDetalle++;
                }
            }
            /* Movimiento 0 cierre */
            System.out.println("-------------> Movimiento 0 CIERRE L50 " + (cotizacionDolar));
            FacComprasSybase movCierre = new FacComprasSybase("CIERRE L50",
                    Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())),
                    factCab.getFechaEmision(),
                    Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())),
                    Long.valueOf(nroComp),
                    factCab.getIdPadron(),
                    Short.valueOf(Integer.toString(0)),
                    Long.parseLong(factCab.getCuit()),
                    Short.valueOf(Integer.toString(ptoVta)));
            totalPieFactura = new BigDecimal(0);
            totalDetalleFactura = new BigDecimal(0);
            totalImpuestos = new BigDecimal(0);
            totalPrecioUnitario = new BigDecimal(0);
            totalCantidad = new BigDecimal(0);
            totalIva21 = new BigDecimal(0);
            totalIva105 = new BigDecimal(0);
            totalIva27 = new BigDecimal(0);
            totalPercep1 = new BigDecimal(0);
            totalPercep2 = new BigDecimal(0);
            netoIva21 = new BigDecimal(0);
            netoIva105 = new BigDecimal(0);
            netoIva27 = new BigDecimal(0);

            // VALORES QUE VAN EN 0 ////////////////////////////////////////////////////
            movCierre.setCDescuento(Double.valueOf(0));
            movCierre.setCImpuestoInterno(Double.valueOf(0));
            movCierre.setCOtroImpuesto(Double.valueOf(0));
            movCierre.setCCodigoRelacion(0);
            movCierre.setCTipoComprobanteAsoc(Short.valueOf(Integer.toString(0)));
            movCierre.setCNumeroComprobanteAsoc(Long.parseLong("0"));
            movCierre.setCContabil(contabilSn);
            movCierre.setCRetencionMiel(Double.valueOf(0));
            movCierre.setCRetencion2da(Double.valueOf(0));
            movCierre.setCRetencion2(Double.valueOf(0));
            movCierre.setCanjeSn("N");
            movCierre.setCanjeNroCto("N");
            movCierre.setCSircrebStafe(Double.valueOf(0));
            movCierre.setCSircrebCdba(Double.valueOf(0));
            movCierre.setCIvaRni(Double.valueOf(0));
            movCierre.setCIvaRi(Double.valueOf(0));
            movCierre.setCIva105(Double.valueOf(0));
            movCierre.setCDescuento(Double.valueOf(0));
            movCierre.setCDescripcion("N");
            // si son remitos  grabo la cotizacion del dolar getcTipoOperacion = 17 
            if (factCab.getIdCteTipo().getcTipoOperacion().equals(17)) {
                movCierre.setcDolar(factCab.getCotDolar().doubleValue());
            } else {
                movCierre.setcDolar(Double.valueOf(0));
            }

            BigDecimal totalFactDetalle = new BigDecimal(0);
            BigDecimal totalFactPie = new BigDecimal(0);

            // FIN VALORES EN 0 ///////////////////////////////////////////////////////
            for (FactPie pie : factPie) {
                // Percepciones
                if (pie.getIdSisTipoModelo().getIdSisTipoModelo().equals(6)) {
                    totalPercep1 = totalPercep1.add(pie.getImporte());
                    totalPercep2 = new BigDecimal(0);
                } else {
                    totalPercep1 = new BigDecimal(0);
                    totalPercep2 = new BigDecimal(0);
                };
                // ivas
                if (pie.getIdSisTipoModelo().getIdSisTipoModelo().equals(2)) {
                    System.out.println("-------------> ES IVA() " + (pie.getIdSisTipoModelo().getIdSisTipoModelo()));
                    if (pie.getPorcentaje().equals(new BigDecimal(10.5))) {
                        totalIva105 = totalIva105.add(pie.getImporte());
                        movCierre.setCIva105(totalIva105.multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                        System.out.println("-------------> ES IVA 10.5 " + (totalIva105.multiply(cotizacionDolar).doubleValue()));
                    } else if (pie.getPorcentaje().equals(new BigDecimal(21))) {
                        totalIva21 = totalIva21.add(pie.getImporte());
                        movCierre.setCIvaRi(totalIva21.multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                        System.out.println("-------------> ES IVA 21 " + (totalIva21.multiply(cotizacionDolar).doubleValue()));
                    } else if (pie.getPorcentaje().equals(new BigDecimal(27))) {
                        totalIva27 = totalIva27.add(pie.getImporte());
                        movCierre.setCPercepcion2(totalIva27.multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
                        System.out.println("-------------> ES IVA 27 " + (totalIva27.multiply(cotizacionDolar).doubleValue()));
                    }
                }
                totalFactPie = totalFactPie.add(pie.getImporte());
            }
            for (FactDetalle det : factDetalle) {
                totalPrecioUnitario = totalPrecioUnitario.add(det.getImporte().multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN));
                totalCantidad = new BigDecimal(0); //totalCantidad.add(det.getCantidad());
                movCierre.setCDeposito(det.getIdDepositos().getCodigoDep());
                totalFactDetalle = totalFactDetalle.add(det.getImporte());
            }
            for (FactFormaPago fp : factFormaPago) {
                formaPagoSeleccionada = fp.getIdFormaPago().getCodigoSysbase();

            }
            movCierre.setCFormaPago((Short.valueOf(Integer.toString(formaPagoSeleccionada))));
            totalPieFactura = totalFactDetalle.add(totalFactPie).multiply(cotizacionDolar);
            ///////////////////////////////////////////////
            movCierre.setCNombre(factCab.getNombre());
            movCierre.setCDescripcion("N");
            movCierre.setCFechaVencimiento(factCab.getFechaVto());
            movCierre.setCFacturadoSn(facturadoSn.charAt(0));
            movCierre.setCCodigoOperador(user.getUsuarioSybase());
            movCierre.setCHora(fechaHoy);
            movCierre.setCFechaContabil(factCab.getFechaConta());
            movCierre.setBarra("");
            movCierre.setCCondicionIva(Short.valueOf(Integer.toString(condiIva.getCondIva().getCondiva())));
            //
            movCierre.setCPercepcion1(totalPercep1.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
            movCierre.setCBonificacion(totalPieFactura.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
            movCierre.setCPrecioUnitario(totalPrecioUnitario.doubleValue());
            movCierre.setCCantidad(totalCantidad.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
            movCierre.setCRetencion1(totalPercep1.add(totalPercep2).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
            movCierre.setCPercepcion1(Double.valueOf(0));
            movCierre.setCPercepcion2(Double.valueOf(0));

            boolean transaccion0;
            transaccion0 = factComprasSybaseFacade.setFacComprasSybaseNuevo(movCierre);
            //si la trnsaccion fallo devuelvo el mensaje
            if (!transaccion0) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con la imputacion: ");
                return false;
            }

            // fin movimiento 0  cierre
        } catch (Exception ex) {
            System.out.println(AppCodigo.ERROR + " | FacCompras Sybase():::::::::::::::::: ----> " + ex.toString());
            return false;
        }
        System.out.println("::::::::: FIN  ----------------------> FacCompras Sybase() :: Stock pasado exitosamente !!! > ");
        return true;
    }

   
}
