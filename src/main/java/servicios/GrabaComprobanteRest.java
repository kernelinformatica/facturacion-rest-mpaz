package servicios;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.SortHelper;
import compra.GrabaFacCompraSybase;
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
import entidades.SisOperacionComprobante;
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
import entidades.FacCompras;
import entidades.FacComprasSybase;
import entidades.FacVentasSybase;
import entidades.FacVentasSybasePK;
import entidades.FacVentasDolarSybase;
import entidades.FacVentasDolarSybasePK;
import entidades.ModeloDetalle;
import entidades.CtacteCategoria;
import entidades.FitoStockSybase;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import persistencia.FacVentasSybaseFacade;
import persistencia.FacVentasDolarSybaseFacade;
import persistencia.FacComprasFacade;
import persistencia.FacComprasSybaseFacade;
import persistencia.CtacteCategoriaFacade;
import persistencia.ParametrosFacSybaseFacade;
import persistencia.FitoStockSybaseFacade;
import utils.Utils;

/**
 *
 * @author Kernel informatica
 */
@Stateless
@Path("grabaComprobante")
public class GrabaComprobanteRest {

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
    MasterFacade masterFacade;
    @Inject
    ModeloDetalleFacade modeloDetalleFacade;
    @Inject
    CtacteCategoriaFacade ctaCteCategoriaFacade;
    @Inject
    FacComprasFacade facComprasFacade;

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
    FacComprasSybaseFacade factComprasSybaseFacade;
    @Inject
    ParametrosFacSybaseFacade parametrosFacSybaseFacade;
    @Inject
    CerealSisaSybaseFacade cerealSisaSybaseFacade;
    @Inject
    FacVentasSybaseFacade facVentasSybaseFacade;
    @Inject
    CanjesDocumentoSybaseFacade canjesDocumentosSybaseFacade;
    @Inject
    CanjesDocumentoSybaseFacade documentoSybaseFacade;
    @Inject
    CerealesFacade cerealesFacade;
    @Inject
    CanjesContratosCerealesFacade canjesContratosCerealesFacade;
    @Inject
    FitoStockSybaseFacade fitoStockSybaseFacade;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkHttp(@Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        respuesta.setControl(AppCodigo.OK, "Funciona graba comprobante");
        return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setComprobante(
            @HeaderParam("token") String token,
            @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            System.out.println("<----- grabaComprobanteRest ----->");
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            int conta;
            //Obtengo los atributos del body
            //Datos de FactCab
            Integer idCteTipo = (Integer) Utils.getKeyFromJsonObject("idCteTipo", jsonBody, "Integer");
            String letra = (String) Utils.getKeyFromJsonObject("letra", jsonBody, "String");
            BigDecimal numero = (BigDecimal) Utils.getKeyFromJsonObject("numero", jsonBody, "BigDecimal");
            Date fechaEmision = (Date) Utils.getKeyFromJsonObject("fechaEmision", jsonBody, "Date");
            Date fechaVencimiento = (Date) Utils.getKeyFromJsonObject("fechaVencimiento", jsonBody, "Date");
            Date fechaConta = (Date) Utils.getKeyFromJsonObject("fechaConta", jsonBody, "Date");
            String cai = (String) Utils.getKeyFromJsonObject("cai", jsonBody, "String");
            Date caiVto = (Date) Utils.getKeyFromJsonObject("caiVto", jsonBody, "Date");
            String codBarra = (String) Utils.getKeyFromJsonObject("codBarra", jsonBody, "String");
            Integer idPadron = (Integer) Utils.getKeyFromJsonObject("idPadron", jsonBody, "Integer");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            String nombre = (String) Utils.getKeyFromJsonObject("nombre", jsonBody, "String");
            String cuit = (String) Utils.getKeyFromJsonObject("cuit", jsonBody, "String");
            String sisSitIva = (String) Utils.getKeyFromJsonObject("sisSitIva", jsonBody, "String");
            String codigoPostal = (String) Utils.getKeyFromJsonObject("codigoPostal", jsonBody, "String");
            String listaPrecio = (String) Utils.getKeyFromJsonObject("listaPrecio", jsonBody, "String");
            BigDecimal cotDolar = (BigDecimal) Utils.getKeyFromJsonObject("cotDolar", jsonBody, "BigDecimal");
            Date fechaDolar = (Date) Utils.getKeyFromJsonObject("fechaDolar", jsonBody, "Date");
            String observaciones = (String) Utils.getKeyFromJsonObject("observaciones", jsonBody, "String");
            Integer idSisTipoOperacion = (Integer) Utils.getKeyFromJsonObject("idSisTipoOperacion", jsonBody, "Integer");
            Integer idFactCab = (Integer) Utils.getKeyFromJsonObject("idFactCab", jsonBody, "Integer");
            Integer idNumero = (Integer) Utils.getKeyFromJsonObject("idNumero", jsonBody, "Integer");
            Integer idVendedor = (Integer) Utils.getKeyFromJsonObject("idVendedor", jsonBody, "Integer");
            Integer codigoAfip = (Integer) Utils.getKeyFromJsonObject("codigoAfip", jsonBody, "Integer");
            Integer idSisOperacionComprobante = (Integer) Utils.getKeyFromJsonObject("idSisOperacionComprobante", jsonBody, "Integer");
            Integer idContrato = (Integer) Utils.getKeyFromJsonObject("idContrato", jsonBody, "Integer");
            String direccion = (String) Utils.getKeyFromJsonObject("direccion", jsonBody, "String");
            String codigoCereal = (String) Utils.getKeyFromJsonObject("cereal", jsonBody, "String");
            Boolean diferidoVto = (Boolean) Utils.getKeyFromJsonObject("diferidoVto", jsonBody, "boolean");
            /// Para canje /////////
            String productoCanje = (String) Utils.getKeyFromJsonObject("productoCanje", jsonBody, "String");
            BigDecimal precioReferenciaCanje = (BigDecimal) Utils.getKeyFromJsonObject("precioReferenciaCanje", jsonBody, "BigDecimal");
            BigDecimal interesCanje = (BigDecimal) Utils.getKeyFromJsonObject("interesCanje", jsonBody, "BigDecimal");
            Integer kilosCanje = (Integer) Utils.getKeyFromJsonObject("kilosCanje", jsonBody, "Integer");
            String observacionesCanje = (String) Utils.getKeyFromJsonObject("observacionesCanje", jsonBody, "String");
            Integer idRelacionSisCanje = (Integer) Utils.getKeyFromJsonObject("idRelacionSisCanje", jsonBody, "Integer");
            Boolean pesificado = (Boolean) Utils.getKeyFromJsonObject("pesificado", jsonBody, "boolean");
            Boolean dolarizadoAlVto = (Boolean) Utils.getKeyFromJsonObject("dolarizadoAlVto", jsonBody, "boolean");
            Boolean canjeInsumos = (Boolean) Utils.getKeyFromJsonObject("canjeInsumos", jsonBody, "boolean");
            String tipoCambio = (String) Utils.getKeyFromJsonObject("tipoCambio", jsonBody, "String");
            BigDecimal interesMensualCompra = (BigDecimal) Utils.getKeyFromJsonObject("interesMensualCompra", jsonBody, "BigDecimal");
            //Deposito de destino para el movimiento de remitos internos
            Integer idDepositoDestino = (Integer) Utils.getKeyFromJsonObject("idDepositoDestino", jsonBody, "Integer");

            //Booleanos para ver que se guarda y que no.
            boolean factCabecera = (Boolean) Utils.getKeyFromJsonObject("factCabecera", jsonBody, "boolean");
            boolean factDet = (Boolean) Utils.getKeyFromJsonObject("factDet", jsonBody, "boolean");
            boolean factImputa = (Boolean) Utils.getKeyFromJsonObject("factImputa", jsonBody, "boolean");
            boolean factPie = (Boolean) Utils.getKeyFromJsonObject("factPie", jsonBody, "boolean");
            boolean produmo = (Boolean) Utils.getKeyFromJsonObject("produmo", jsonBody, "boolean");
            boolean factFormaPago = (Boolean) Utils.getKeyFromJsonObject("factFormaPago", jsonBody, "boolean");
            boolean lote = (Boolean) Utils.getKeyFromJsonObject("lote", jsonBody, "boolean");
            boolean grabaFactura = (Boolean) Utils.getKeyFromJsonObject("grabaFactura", jsonBody, "boolean");

            boolean pesificadoSn = (Boolean) Utils.getKeyFromJsonObject("pesificado", jsonBody, "boolean");
            Boolean esPesificado = (Boolean) Utils.getKeyFromJsonObject("marcaPesificado", jsonBody, "boolean");
            Boolean esPesificadoPersisteSn = (Boolean) Utils.getKeyFromJsonObject("pesificadoPersisteSn", jsonBody, "boolean");
            BigDecimal nroCompPesificado = (BigDecimal) Utils.getKeyFromJsonObject("nroCompPesificado", jsonBody, "BigDecimal");

            //Datos de la factura relacionada a un remito
            Integer tipoFact = (Integer) Utils.getKeyFromJsonObject("tipoFact", jsonBody, "Integer");
            String letraFact = (String) Utils.getKeyFromJsonObject("letraFact", jsonBody, "String");
            BigDecimal numeroFact = (BigDecimal) Utils.getKeyFromJsonObject("numeroFact", jsonBody, "BigDecimal");
            Date fechaVencimientoFact = (Date) Utils.getKeyFromJsonObject("fechaVencimientoFact", jsonBody, "Date");
            Date fechaContaFact = (Date) Utils.getKeyFromJsonObject("fechaContaFact", jsonBody, "Date");
            Integer idNumeroFact = (Integer) Utils.getKeyFromJsonObject("idNumeroFact", jsonBody, "Integer");
            Integer codigoAfipFact = (Integer) Utils.getKeyFromJsonObject("codigoAfipFact", jsonBody, "Integer");

            //Grillas de articulos, subTotales, elementos trazables y formas de pago
            List<JsonElement> grillaArticulos = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("grillaArticulos", jsonBody, "ArrayList");
            List<JsonElement> grillaSubTotales = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("grillaSubTotales", jsonBody, "ArrayList");
            List<JsonElement> grillaTrazabilidad = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("grillaTrazabilidad", jsonBody, "ArrayList");
            List<JsonElement> grillaFormaPago = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("grillaFormaPago", jsonBody, "ArrayList");

            //
            // marca para saber si tengho que enviar mail o no 
            boolean enviaMail = false;
            //valido que token no sea null
            if (token == null || token.trim().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Error, token vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (pesificadoSn == false) {
                pesificadoSn = false;
            } else {
                pesificadoSn = true;
            }
            //Busco el token
            Acceso userToken = accesoFacade.findByToken(token);

            //valido que Acceso no sea null
            if (userToken == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Acceso nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Busco el usuario
            Usuario user = usuarioFacade.getByToken(userToken);

            //valido que el Usuario no sea null
            if (user == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Usuario nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();

            }

            //valido vencimiento token
            if (!accesoFacade.validarToken(userToken, user)) {
                respuesta.setControl(AppCodigo.ERROR, "Credenciales incorrectas");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }
            if (esPesificado == null) {
                esPesificado = false;
            }
            if (esPesificadoPersisteSn == null) {
                esPesificadoPersisteSn = false;
            }
            //Me fijo que  los campos que tienen el atributo NotNull no sean nulos
            if (idCteTipo == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, tipo de comprobante nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (letra == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, letra nula");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (numero == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, numero nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (fechaEmision == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, fecha emision nula");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (fechaVencimiento == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, fecha vencimiento nula");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (idPadron == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, padron nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (productoCanje == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, producto canje nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (precioReferenciaCanje == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, precio referencia canje nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (interesCanje == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, interes canje nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (idMoneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, moneda en nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (nombre == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, nombre nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (cuit == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, cuit nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (sisSitIva == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, sisSitIva nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (codigoPostal == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, codigo postal nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (cotDolar == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, cot dolar nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (idSisTipoOperacion == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, sisTipoOperacion nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (codigoAfip == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, codigo Afip nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //----------Metodos de busquedas de clases por id----------//
            CteTipo cteTipo = cteTipoFacade.find(idCteTipo);
            //Pregunto si existe el CteTipo
            if (cteTipo == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el tipo de Comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            SisMonedas sisMonedas = sisMonedasFacade.find(idMoneda);
            //Pregunto si existe SisMonedas
            if (sisMonedas == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Moneda");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            SisTipoOperacion sisTipoOperacion = sisTipoOperacionFacade.find(idSisTipoOperacion);
            //Pregunto si existe sisTipoOperacion
            if (sisTipoOperacion == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el tipo de operacion");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Pregunto si la grilla de articulos no esta vacia
            if (grillaArticulos == null) {
                respuesta.setControl(AppCodigo.ERROR, "Lista de Articulos Vacia");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Hago la sumatoria para comprobar que pendientes sea distinto de cero para continuar
            BigDecimal sumatoriaPendientes = new BigDecimal(0);
            for (JsonElement j : grillaArticulos) {
                BigDecimal pendiente = (BigDecimal) Utils.getKeyFromJsonObject("pendiente", j.getAsJsonObject(), "BigDecimal");
                sumatoriaPendientes = sumatoriaPendientes.add(pendiente);
            }

            //Pregunto si la sumatoria de pendientes es igual a 0
            if (sumatoriaPendientes.compareTo(BigDecimal.ZERO) == 0) {
                respuesta.setControl(AppCodigo.ERROR, "La cantidad no puede ser nula");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Compruebo si es ventas y si viene la lista de precios nula
            if (cteTipo.getIdSisComprobante().getIdSisModulos().getIdSisModulos() == 2 && listaPrecio == null) {
                respuesta.setControl(AppCodigo.ERROR, "Debe seleccionar una lista de precios");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Esta condicion solamente aplica para los emitidos por el sistema
            if (cteTipo.getIdSisComprobante().getPropio() == 1 && idNumero != null) {
                CteNumerador cteNumerador = cteNumeradorFacade.find(idNumero);
                if (cteNumerador == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar la factura, no existe el numero de comprobante");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                String ptoVenta = String.valueOf(cteNumerador.getIdPtoVenta().getPtoVenta());

                //Valido que la fecha de emision no sea menor a la de un comprobante con el mismo tipo dado de alta.
                List<FactCab> posteriores = factCabFacade.getByFechaEmpresaCompLetra(fechaEmision, cteTipo, user.getIdPerfil().getIdSucursal().getIdEmpresa(), letra);

                if (posteriores != null && !posteriores.isEmpty() && posteriores.size() > 0) {
                    List<FactCab> filtrados = new ArrayList<>();
                    for (FactCab p : posteriores) {
                        String num = String.valueOf(p.getNumero());
                        String pto = num.substring(0, num.length() - 8);
                        if (pto.equals(ptoVenta)) {
                            filtrados.add(p);
                        }
                    }
                    if (filtrados.size() > 1) {
                        Collections.sort(filtrados, (o1, o2) -> o1.getFechaEmision().compareTo(o2.getFechaEmision()));
                    }
                    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yy");
                    respuesta.setControl(AppCodigo.ERROR, "Error al dar de alta el Comprobante, la fecha debe ser mayor a: " + formateador.format(filtrados.get(filtrados.size() - 1).getFechaEmision()));
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }

//            String dir = "";
            String codPostal = codigoPostal;
            if (idPadron != null && idPadron != 0) {
                Padron padron = padronFacade.find(idPadron);

                if (padron != null) {
                    if (padron.getPadronDomici() != null) {
                        direccion = padron.getPadronDomici();
                    }
                    if (padron.getPadronDomnro() != null) {
                        direccion = direccion + " " + padron.getPadronDomnro();
                    }
                    if (padron.getPadronDompis() != null) {
                        direccion = direccion + " " + padron.getPadronDompis();
                    }
                    if (padron.getPadronDomdto() != null) {
                        direccion = direccion + " " + padron.getPadronDomdto();
                    }
                }
                if (padron.getCodigoPostal() != null) {
                    codPostal = padron.getCodigoPostal().toString();
                }
            }
            Cereales cereal = null;
            if (codigoCereal != null) {
                cereal = cerealesFacade.findCerealPorCodigo(codigoCereal);
            }

            FactCab factCab = new FactCab();
            //Pregunto si se guarda una cabecera
            if (factCabecera) {
                factCab.setCerealCanje(cereal);
                factCab.setDiferidoVto(diferidoVto);
                factCab.setCai(cai);
                factCab.setCaiVto(caiVto);
                factCab.setCodBarra(codBarra);
                factCab.setCodigoPostal(codPostal);
                factCab.setCotDolar(cotDolar);
                factCab.setCuit(cuit);
                factCab.setFechaConta(fechaConta);
                factCab.setFechaDolar(fechaDolar);
                factCab.setFechaEmision(fechaEmision);
                factCab.setFechaVto(fechaVencimiento);
                factCab.setIdCteTipo(cteTipo);
                factCab.setIdListaPrecios(listaPrecio);
                factCab.setIdPadron(idPadron);
                factCab.setIdProductoCanje(productoCanje);
                factCab.setIdmoneda(sisMonedas);
                factCab.setInteresCanje(interesCanje);
                factCab.setLetra(letra);
                factCab.setNombre(nombre);
                factCab.setNumero(numero.longValue());
                factCab.setObservaciones(observaciones);
                factCab.setPrecioReferenciaCanje(precioReferenciaCanje);
                factCab.setSitIVA(sisSitIva);
                factCab.setIdSisTipoOperacion(sisTipoOperacion);
                factCab.setIdVendedor(idVendedor);
                factCab.setCodigoAfip(codigoAfip);
                factCab.setIdSisOperacionComprobantes(idSisOperacionComprobante);
                factCab.setDomicilio(direccion);
                factCab.setPesificado(pesificado);
                factCab.setDolarizadoAlVto(dolarizadoAlVto);
                factCab.setInteresMensualCompra(interesMensualCompra);
                factCab.setCanjeInsumos(canjeInsumos);
                factCab.setTipoCambio(tipoCambio);
            } else {
                if (idFactCab == null) {
                    respuesta.setControl(AppCodigo.ERROR, "El parametro de cabecera no puede ser nulo");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                //busco fact cab
                factCab = factCabFacade.find(idFactCab);
                if (factCab == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe Fact Cab");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }

            //le seteo el deposito de destino
            if (idDepositoDestino != null) {
                Deposito deposito = depositoFacade.find(idDepositoDestino);
                if (deposito == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, el deposito con id " + idDepositoDestino + " no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                factCab.setIdDepositoDestino(deposito);
            }

            //Busco el contrato si es que lleva
            ContratoDet contratoDet = new ContratoDet();
            if (idContrato != null && kilosCanje != null) {
                Contrato contrato = contratoFacade.find(idContrato);
                if (contrato == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no xiste el contrato seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                if (kilosCanje > contrato.getKilos()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, los kilos sobrepasan a lo estipulado en el contrato");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                if (!contrato.getContratoDetCollection().isEmpty()) {
                    Integer sumatoriaCantidad = 0;
                    for (ContratoDet d : contrato.getContratoDetCollection()) {
                        sumatoriaCantidad = sumatoriaCantidad + d.getKilos();
                    }
                    sumatoriaCantidad = kilosCanje + sumatoriaCantidad;
                    if (sumatoriaCantidad > contrato.getKilos()) {
                        respuesta.setControl(AppCodigo.ERROR, "Error, los kilos sobrepasan a lo estipulado en el contrato");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }

                contratoDet.setIdContratos(contrato);
                contratoDet.setIdFactCab(factCab);
                contratoDet.setImporte(precioReferenciaCanje);
                contratoDet.setKilos(kilosCanje);
                contratoDet.setObservaciones(observacionesCanje);
            }

            if (idRelacionSisCanje != null) {
                RelacionesCanje relacion = relacionesCanjeFacade.find(idRelacionSisCanje);
                if (relacion == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, la relacion seleccionada no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                factCab.setIdRelacionCanje(relacion);
                factCab.setKilosCanje(kilosCanje);
            }

            //Le agrego el numero a numero afip si es compra
            if (cteTipo.getIdSisComprobante().getIdSisModulos().getIdSisModulos() == 1) {
                factCab.setNumeroAfip(numero.longValue());
            }

            //Pregunto si guarda Detalles
            if (factDet) {
                //Armo la listas de Fact par luego parsearlas todas juntas
                List<FactDetalle> listaDetalles = new ArrayList<>();
                List<FactImputa> listaImputa = new ArrayList<>();
                List<FactImputa> listaImputaExtra = new ArrayList<>();
                List<Produmo> listaProdumo = new ArrayList<>();
                List<Produmo> listaProdumo2 = new ArrayList<>();
                List<FactPie> listaPie = new ArrayList<>();
                List<Lote> listaLotes = new ArrayList<>();
                List<FactFormaPago> listaFormaPago = new ArrayList<>();
                List<BigDecimal> listaComRel = new ArrayList<>();
                //Contador para factDetalle
                int item = 0;
                //Recorro el array de grillaArticulos y creo facDetalle para cada articulo

                for (JsonElement j : grillaArticulos) {
                    //Obtengo los atributos del body
                    Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", j.getAsJsonObject(), "Integer");
                    String articulo = (String) Utils.getKeyFromJsonObject("articulo", j.getAsJsonObject(), "String");
                    Integer posicionArticulo = (Integer) Utils.getKeyFromJsonObject("prodIndice", j.getAsJsonObject(), "Integer");
                    BigDecimal pendiente = (BigDecimal) Utils.getKeyFromJsonObject("pendiente", j.getAsJsonObject(), "BigDecimal");
                    BigDecimal precio = (BigDecimal) Utils.getKeyFromJsonObject("precio", j.getAsJsonObject(), "BigDecimal");
                    BigDecimal porCalc = (BigDecimal) Utils.getKeyFromJsonObject("porCalc", j.getAsJsonObject(), "BigDecimal");
                    String descuento = (String) Utils.getKeyFromJsonObject("descuento", j.getAsJsonObject(), "String");
                    BigDecimal ivaPorc = (BigDecimal) Utils.getKeyFromJsonObject("ivaPorc", j.getAsJsonObject(), "BigDecimal");
                    Integer cantidadBulto = (Integer) Utils.getKeyFromJsonObject("cantidadBulto", j.getAsJsonObject(), "Integer");
                    String despacho = (String) Utils.getKeyFromJsonObject("despacho", j.getAsJsonObject(), "String");
                    Boolean trazable = (Boolean) Utils.getKeyFromJsonObject("trazable", j.getAsJsonObject(), "boolean");
                    Integer idDeposito = (Integer) Utils.getKeyFromJsonObject("idDeposito", j.getAsJsonObject(), "Integer");
                    String observacionDetalle = (String) Utils.getKeyFromJsonObject("observacionDetalle", j.getAsJsonObject(), "String");
                    String imputacion = (String) Utils.getKeyFromJsonObject("imputacion", j.getAsJsonObject(), "String");
                    Integer idFactDetalleImputa = (Integer) Utils.getKeyFromJsonObject("idFactDetalleImputa", j.getAsJsonObject(), "Integer");
                    BigDecimal importe = (BigDecimal) Utils.getKeyFromJsonObject("importe", j.getAsJsonObject(), "BigDecimal");

                    if (importe.intValue() == 0) {
                        System.out.println("da 0 esto " + importe.intValue());
                    } else {
                        System.out.println("no da 0 esto " + importe.intValue());
                    }
                    BigDecimal precioDesc = (BigDecimal) Utils.getKeyFromJsonObject("precioDesc", j.getAsJsonObject(), "BigDecimal");
                    String unidadDescuento = (String) Utils.getKeyFromJsonObject("unidadDescuento", j.getAsJsonObject(), "String");
                    BigDecimal comprobanteRel = (BigDecimal) Utils.getKeyFromJsonObject("comprobanteRel", j.getAsJsonObject(), "BigDecimal");
                    Integer idLibro = (Integer) Utils.getKeyFromJsonObject("idLibro", j.getAsJsonObject(), "Integer");

                    if (idLibro == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el detalle de la factura, idLibro nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Pregunto por los campos que son NOTNULL
                    if (idProducto == null || articulo == null || pendiente == null || precio == null || porCalc == null
                            || ivaPorc == null || cantidadBulto == null || despacho == null || trazable == null || idDeposito == null
                            || observacionDetalle == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Busco el deposito por id, si no encuentro alguno desarmo la transaccion.
                    Deposito deposito = depositoFacade.find(idDeposito);
                    if (deposito == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, el deposito con id " + idDeposito + " no existe");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Busco el producto 
                    Producto producto = productoFacade.find(idProducto);
                    if (producto == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, el producto con id " + idProducto + " no existe");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Actualizo el precio del aritulo si es compra, si es distinto de 0 y si el precio es distinto. 
                    // Ademas lo convierto de acuerdo al tipo de moneda
                    if (!precio.equals(producto.getCostoReposicion()) && cteTipo.getIdSisComprobante().getIdSisModulos().getIdSisModulos().equals(1) && precio.compareTo(BigDecimal.ZERO) != 0) {
                        SisCotDolar sisCotDolar = sisCotDolarFacade.getLastCotizacion();
                        BigDecimal precioAtualizado = new BigDecimal(0);
                        //Me fijo la moneda seleccionada y la del producto
                        if (producto.getDescripcion().equals("$AR") && sisMonedas.getDescripcion().equals("u$s")) {
                            precioAtualizado = producto.getCostoReposicion().multiply(sisCotDolar.getCotizacion());
                        } else if ((producto.getDescripcion().equals("u$s") && sisMonedas.getDescripcion().equals("$AR"))) {
                            precioAtualizado = producto.getCostoReposicion().divide(sisCotDolar.getCotizacion(), 2, RoundingMode.HALF_UP);
                        }
                        //Si si tiene la marca de actualizar costo y el precio convertido es distinto al que tiene el producto lo cambio
                        SisOperacionComprobante sisOperacionComprobante = sisOperacionComprobanteFacade.find(idSisOperacionComprobante);
                        if (sisOperacionComprobante.getActualizaCosto().equals(true)) {
                            if (!precioAtualizado.equals(producto.getCostoReposicion())) {
                                productoFacade.editProducto(producto);
                            }
                        }

                    }

                    //Si la cantidad es igual a 0 no guarda ese articulo
                    if (pendiente.compareTo(BigDecimal.ZERO) == 0) {
                        continue;
                    }

                    if (idSisOperacionComprobante == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura, Comprobante no encontrado");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    SisOperacionComprobante sisOperacionComprobante = sisOperacionComprobanteFacade.find(idSisOperacionComprobante);
                    System.out.println("sisOperacionComprobante: " + sisOperacionComprobante);
                    if (sisOperacionComprobante == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura, Comprobante no encontrado");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    if (!sisOperacionComprobante.getAdmiteRelacionMultiple() && comprobanteRel != null) {
                        listaComRel.add(comprobanteRel);
                    }

                    if (!sisOperacionComprobante.getIncluyeNeto()) {
                        importe = BigDecimal.ZERO;
                    }

                    if (sisOperacionComprobante.getDifCotizacion() && importe.compareTo(BigDecimal.ZERO) < 0 && sisOperacionComprobante.getIdSisComprobantes().getIdSisComprobantes().equals(8)) {
                        respuesta.setControl(AppCodigo.AVISO, "Debe emitir otro tipo de comprobante");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    /////////////////////////////////////////////////////////
                    //Creo el factDetalle nuevo y seteo los valores
                    FactDetalle factDetalle = new FactDetalle();
                    factDetalle.setDetalle(articulo);
                    factDetalle.setCantBultos(cantidadBulto);
                    factDetalle.setCantidad(pendiente);
                    factDetalle.setDescuento(descuento);
                    factDetalle.setDespacho(despacho);
                    factDetalle.setIdDepositos(deposito);
                    factDetalle.setIdFactCab(factCab);
                    factDetalle.setIdProducto(idProducto);
                    factDetalle.setImputacion(imputacion);
                    factDetalle.setItem(item);
                    factDetalle.setIvaPorc(ivaPorc);
                    factDetalle.setObservaciones(observacionDetalle);
                    factDetalle.setPorcCalc(porCalc);
                    factDetalle.setPrecio(precio);
                    factDetalle.setTrazable(trazable);
                    factDetalle.setImporte(importe);
                    factDetalle.setCodProducto(producto.getCodProducto());
                    factDetalle.setUnidadDescuento(unidadDescuento);
                    factDetalle.setPrecioDesc(precioDesc);
                    factDetalle.setIdLibro(idLibro);
                    // calculo el proximo factDetalle 

                    // Busco listaPrecioDet
                    if (listaPrecio != null) {
                        ListaPrecioDet detalleProd = listaPrecioFacade.findByIdProductoAndIdLista(listaPrecio, idProducto);
                        factDetalle.setAuxListaPrecioDet(detalleProd);
                    }
                    listaDetalles.add(factDetalle);
                    //Empiezo la transaccion para la grabacion de FactImputa
                    if (factImputa && idFactDetalleImputa != null) {
                        // id facdetalleimputa ya esta en el remito original ! existe
                        FactDetalle imputa = factDetalleFacade.find(idFactDetalleImputa);

                        if (imputa == null) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar Imputacion, la factura con id " + idFactDetalleImputa + " no existe");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        FactImputa facturaImputa = new FactImputa();
                        facturaImputa.setCantidadImputada(pendiente);
                        facturaImputa.setIdFactDetalle(imputa);
                        facturaImputa.setIdFactDetalleImputa(factDetalle);
                        facturaImputa.setImporteImputado(pendiente.multiply(porCalc).multiply(precio));
                        facturaImputa.setMasAsiento(0);
                        facturaImputa.setMasAsientoImputado(0);
                        listaImputa.add(facturaImputa);

                    }

                    //Empiezo la transaccion para la grabacion de FactImputaExtra IMPUTA EXTRA
                    if (factImputa && idFactDetalleImputa != null) {
                        // id facdetalleimputa ya esta en el remito original ! existe
                        if (pendiente.compareTo(BigDecimal.ZERO) == 0) {
                            // no es pendiente
                        } else {

                            FactDetalle imputa = factDetalleFacade.find(idFactDetalleImputa);

                            if (factDetalle.getIdFactCab().getNumero() != imputa.getIdFactCab().getNumero()) {
                                //if(imputa.getIdFactDetalle() != idFactDetalleImputa){
                                FactImputa facturaImputaExtra = new FactImputa();
                                facturaImputaExtra.setCantidadImputada(pendiente);
                                facturaImputaExtra.setIdFactDetalleImputa(factDetalle);
                                facturaImputaExtra.setIdFactDetalle(imputa);
                                facturaImputaExtra.setImporteImputado(pendiente.multiply(porCalc).multiply(precio));
                                facturaImputaExtra.setMasAsiento(0);
                                facturaImputaExtra.setMasAsientoImputado(0);
                                listaImputaExtra.add(facturaImputaExtra);
                                // }else{
                                //  System.out.print(imputa.getIdFactDetalle() +"FactImputaExtra: Son Iguales no se graba ::::  <- imputa.getIdFactDetalle()--------------idFactDetalleImputa ->"+ idFactDetalleImputa);
                                // }

                            }
                        }
                    }

                    //Pregunto si se graba produmo y empiezo con la transaccion
                    if (produmo && (cteTipo.getIdSisComprobante().getStock().equals(1) || cteTipo.getIdSisComprobante().getStock().equals(2))) {
                        //SisOperacionComprobante soc = sisOperacionComprobanteFacade.findByIdSisComp(user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa(), factCab.getIdSisOperacionComprobantes());
                        // if (soc.getStock().equals(1) || soc.getStock().equals(2)) {
                        Produmo prod = new Produmo();
                        if (cteTipo.getSurenu().equals("D")) {
                            prod.setCantidad(pendiente.negate());
                        } else {
                            prod.setCantidad(pendiente);
                        }
                        prod.setDetalle(articulo);
                        prod.setIdCteTipo(cteTipo);
                        prod.setIdDepositos(deposito);
                        prod.setIdFactDetalle(factDetalle.getIdFactDetalle());
                        prod.setIdProductos(producto);
                        if (cteTipo.getIdCteTipo().equals(69)) {
                            // Si es remito de compra grabo la psoicion de la grilla  
                            // para poder relacionarlos 
                            if (posicionArticulo == null) {
                                posicionArticulo = 0;
                            }
                            prod.setItem(posicionArticulo);
                        } else {
                            prod.setItem(item);
                        }
                        CteNumerador cteNumerador = null;
                        if (idNumero != null) {
                            cteNumerador = cteNumeradorFacade.find(idNumero);
                            if (cteNumerador == null) {
                                respuesta.setControl(AppCodigo.ERROR, "Error al cargar la factura, no existe el numero de comprobante");
                                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                            }
                            String ptoVenta = String.valueOf(cteNumerador.getIdPtoVenta().getPtoVenta());
                            String numeroVentaFormat = String.format("%08d", cteNumerador.getNumerador());
                            String concatenado = ptoVenta.concat(numeroVentaFormat);
                            prod.setNumero(Long.parseLong(concatenado, 10));
                        } else {
                            prod.setNumero(numero.longValue());
                        }
                        prod.setFecha(fechaEmision);
                        prod.setStock(cteTipo.getIdSisComprobante().getStock());
                        //prod.setIdLotes(item);

                        listaProdumo.add(prod);
                        // verifico si debo enviar el mail, notificando que se dio de alta un comprobante ////////////

                        //}
                    }

                    //Le sumo uno al contador de items
                    item++;
                }

                //Voy a fijarme si hay comprobantes relacionados y son distintos de acuerdo a la condicion de si permite relacionados multiples
                if (!listaComRel.isEmpty() && listaComRel.size() > 0) {
                    //Ordeno la lista
                    Collections.sort(listaComRel, (o1, o2) -> o1.compareTo(o2));
                    for (int i = 0; i < listaComRel.size(); i++) {
                        //Me fijo si es distinto al anterior
                        if (i != 0 && !listaComRel.get(i).equals(listaComRel.get(i - 1))) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar el comprobante, este tipo de operacion no admite la relacion de distintos comprobantes");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                    }
                }

                //Termina el recorrido de la Grilla de articulos y empiezo con la de factFormaPago
                System.out.println("::::::::: grillaFormaPago ::::::::::::: -> " + grillaFormaPago);
                if (factFormaPago && grillaFormaPago != null) {

                    for (JsonElement je : grillaFormaPago) {
                        Integer plazo = (Integer) Utils.getKeyFromJsonObject("plazo", je.getAsJsonObject(), "Integer");
                        BigDecimal interes = (BigDecimal) Utils.getKeyFromJsonObject("interes", je.getAsJsonObject(), "BigDecimal");
                        BigDecimal monto = (BigDecimal) Utils.getKeyFromJsonObject("monto", je.getAsJsonObject(), "BigDecimal");
                        String detalle = (String) Utils.getKeyFromJsonObject("detalle", je.getAsJsonObject(), "String");
                        String observacionesFormaPago = (String) Utils.getKeyFromJsonObject("observaciones", je.getAsJsonObject(), "String");
                        String cuentaContable = (String) Utils.getKeyFromJsonObject("cuentaContable", je.getAsJsonObject(), "String");
                        Integer idFormaPagoDet = (Integer) Utils.getKeyFromJsonObject("idFormaPagoDet", je.getAsJsonObject(), "Integer");
                        //Pregunto si son nulos 
                        if (observacionesFormaPago == null || monto == null || interes == null || plazo == null || idFormaPagoDet == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta Forma de Pago, algun campo de la grilla es nulo");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        if (monto.compareTo(BigDecimal.ZERO) == 0) {
                            continue;
                        }

                        FormaPagoDet formaPagoDet = formaPagoDetFacade.getByidFormaPagoDet(idFormaPagoDet);

                        if (formaPagoDet == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta Forma de Pago, la forma de pago no existe");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        //Creo FacForma de pago
                        FactFormaPago factFPago = new FactFormaPago();
                        factFPago.setDetalle(observacionesFormaPago);
                        factFPago.setDiasPago(plazo);
                        //creo la fecha con la cantidad de dias de plazo
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(fechaEmision);
                        calendar.add(Calendar.DAY_OF_YEAR, plazo);
                        //seteo la fecha 
                        if (factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos() > 1) {
                            factFPago.setFechaPago(factCab.getFechaVto());
                        } else {
                            factFPago.setFechaPago(calendar.getTime());
                        }
                        factFPago.setIdFormaPago(formaPagoDet.getIdFormaPago());
                        factFPago.setImporte(monto);
                        factFPago.setPorcentaje(interes);
                        factFPago.setCtaContable(cuentaContable);
                        factFPago.setIdFactCab(factCab);
                        listaFormaPago.add(factFPago);
                    }
                }

                //Aca filtro por las cotas en la lista de precios seleccionada y el precio ingresado
                if (listaPrecio != null) {
                    List<FactDetalle> detallesCotas = new ArrayList<>();
                    for (FactDetalle det : listaDetalles) {
                        BigDecimal precio = det.getPrecioDesc();
                        if (sisMonedas.getDescripcion().equals("u$s") && det.getAuxListaPrecioDet().getIdListaPrecios().getIdMoneda().getDescripcion().equals("$AR")) {
                            SisCotDolar sisCotDolar = sisCotDolarFacade.getLastCotizacion();
                            precio = precio.multiply(sisCotDolar.getCotizacion());
                        } else if (sisMonedas.getDescripcion().equals("$AR") && det.getAuxListaPrecioDet().getIdListaPrecios().getIdMoneda().getDescripcion().equals("u$s")) {
                            SisCotDolar sisCotDolar = sisCotDolarFacade.getLastCotizacion();
                            precio = precio.divide(sisCotDolar.getCotizacion(), 2, RoundingMode.HALF_UP);
                        }
                        /*Boolean usaCota = true;
                         if(listaFormaPago != null && listaFormaPago.size() == 1 && (listaFormaPago.get(0).getIdFormaPago().getIdFormaPago() == 12 || listaFormaPago.get(0).getIdFormaPago().getIdFormaPago() == 13 || listaFormaPago.get(0).getIdFormaPago().getIdFormaPago() == 14)) {
                         usaCota = false;
                         } else {
                         usaCota = true;
                         }
                         if (usaCota && (precio.compareTo(det.getAuxListaPrecioDet().getCotaInf()) < 0
                         || precio.compareTo(det.getAuxListaPrecioDet().getCotaSup()) > 0)
                         && precio.compareTo(BigDecimal.ZERO) != 0) {
                         detallesCotas.add(det);
                         }*/
                    }
                    String prod = "";
                    if (detallesCotas.size() > 0) {
                        for (FactDetalle d : detallesCotas) {
                            prod = prod.concat(d.getDetalle()).concat(", ");
                        }
                        respuesta.setControl(AppCodigo.ERROR, "Los productos: " + prod + ", deben estar entre las cotas indicadas en la lista de precios");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }

                //Empiezo con la grilla de SubTotales para grabar FactPie
                if (factPie) {
                    for (JsonElement je : grillaSubTotales) {
                        String cuenta = (String) Utils.getKeyFromJsonObject("cuenta", je.getAsJsonObject(), "String");
                        String descripcionPie = (String) Utils.getKeyFromJsonObject("descripcionPie", je.getAsJsonObject(), "String");
                        BigDecimal importe = (BigDecimal) Utils.getKeyFromJsonObject("importe", je.getAsJsonObject(), "BigDecimal");
                        BigDecimal totalComprobante = (BigDecimal) Utils.getKeyFromJsonObject("totalComprobante", je.getAsJsonObject(), "BigDecimal");
                        BigDecimal porcentaje = (BigDecimal) Utils.getKeyFromJsonObject("porcentaje", je.getAsJsonObject(), "BigDecimal");
                        Integer idSisTipoModelo = (Integer) Utils.getKeyFromJsonObject("idSisTipoModelo", je.getAsJsonObject(), "Integer");
                        BigDecimal baseImponible = (BigDecimal) Utils.getKeyFromJsonObject("baseImponible", je.getAsJsonObject(), "BigDecimal");
                        String operador = (String) Utils.getKeyFromJsonObject("operador", je.getAsJsonObject(), "String");
                        Integer idLibro = (Integer) Utils.getKeyFromJsonObject("idLibro", je.getAsJsonObject(), "Integer");
                        if (importe.equals(0)) {
                            System.out.println("Error al llevar el importe, venia en 0, " + descripcionPie);
                        }
                        //Pregunto por los que no pueden ser Null
                        if (cuenta == null || descripcionPie == null || totalComprobante == null || idSisTipoModelo == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura, algun campo de la grilla es nulo");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        if (idLibro == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura, idLibro nulo");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        SisTipoModelo sisTipoModelo = sisTipoModeloFacade.find(idSisTipoModelo);
                        if (sisTipoModelo == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura, sisTipoOperacion no encontrado");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        if (idSisOperacionComprobante == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura, Comprobante no encontrado");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        SisOperacionComprobante sisOperacionComprobante = sisOperacionComprobanteFacade.find(idSisOperacionComprobante);
                        if (sisOperacionComprobante == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura, Comprobante no encontrado");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                        System.out.println("valor importe fact pie ---> " + importe + " - " + sisOperacionComprobante.getIncluyeIva());

                        if (!sisOperacionComprobante.getIncluyeIva()) {
                            System.out.println("entre");
                            importe = BigDecimal.ZERO;
                        }
                        //Creo el pie 
                        FactPie facturacionPie = new FactPie();
                        facturacionPie.setCtaContable(cuenta);
                        facturacionPie.setDetalle(descripcionPie);
                        facturacionPie.setImporte(importe);
                        facturacionPie.setIdFactCab(factCab);
                        facturacionPie.setPorcentaje(porcentaje);
                        facturacionPie.setIdSisTipoModelo(sisTipoModelo);
                        facturacionPie.setBaseImponible(baseImponible);
                        facturacionPie.setOperador(operador);
                        facturacionPie.setIdLibro(idLibro);
                        listaPie.add(facturacionPie);

                    }
                }

                // envio de mail 
                SisOperacionComprobante sisOperacionComprobante = sisOperacionComprobanteFacade.find(idSisOperacionComprobante);
                if (Boolean.TRUE.equals(sisOperacionComprobante.getEnviaMail())) {
                    enviaMail = true;
                 } else if (Boolean.FALSE.equals(sisOperacionComprobante.getEnviaMail())) {
                    enviaMail = false;
                }
                System.out.println("Verifico permiso para envio de mail (Alta de Comprobante) = " + sisOperacionComprobante.getEnviaMail());

                //Termina la griila de sub totales y empieza la de trasabilidad
                if (lote && cteTipo.getIdSisComprobante().getStock().equals(1) && grillaTrazabilidad != null && cteTipo.getIdSisComprobante().getIdSisModulos().getIdSisModulos() == 1) {
                    int itemTrazabilidad = 0;
                    int cant = 0;
                    List temp;
                    for (JsonElement gt : grillaTrazabilidad) {

                        //Obtengo los atributos del body
                        String serie = (String) Utils.getKeyFromJsonObject("serie", gt.getAsJsonObject(), "String");
                        Integer posicion = (Integer) Utils.getKeyFromJsonObject("posItem", gt.getAsJsonObject(), "Integer");
                        Date fechaElab = (Date) Utils.getKeyFromJsonObject("fechaElab", gt.getAsJsonObject(), "Date");
                        Date fechaVto = (Date) Utils.getKeyFromJsonObject("fechaVto", gt.getAsJsonObject(), "Date");
                        Boolean vigencia = (Boolean) Utils.getKeyFromJsonObject("vigencia", gt.getAsJsonObject(), "boolean");
                        Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", gt.getAsJsonObject(), "Integer");
                        String nroLote = (String) Utils.getKeyFromJsonObject("nroLote", gt.getAsJsonObject(), "String");
                        Integer glnProovedor = (Integer) Utils.getKeyFromJsonObject("gln", gt.getAsJsonObject(), "Integer");
                        //Pregunto por los que no pueden ser null
                        if (nroLote == null || serie == null || vigencia == null || idProducto == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el lote de la factura, algun campo de la grilla es nulo");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                        if (glnProovedor == null) {
                            glnProovedor = 0;
                        }
                        //Busco el producto por id
                        Producto prod = productoFacade.find(idProducto);
                        if (prod == null) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar lote, el producto con id " + idProducto + " no existe");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        //Armo el lote
                        Lote loteNuevo = new Lote();
                        if (loteFacade.findByNroEmpresaProducto(nroLote, user.getIdPerfil().getIdSucursal().getIdEmpresa(), prod) != null) {
                            loteNuevo = loteFacade.findByNroEmpresaProducto(nroLote, user.getIdPerfil().getIdSucursal().getIdEmpresa(), prod);
                        }

                        loteNuevo.setFechaElab(fechaElab);
                        loteNuevo.setFechaVto(fechaVto);
                        loteNuevo.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                        loteNuevo.setIdproductos(prod);
                        //loteNuevo.setItem(itemTrazabilidad);
                        loteNuevo.setNroLote(nroLote);
                        loteNuevo.setSerie(serie);
                        loteNuevo.setVigencia(vigencia);
                        //loteNuevo.setGlnProovedor(glnProovedor);
                        for (Produmo p : listaProdumo) {
                            if (p.getIdProductos().equals(prod)) {
                                // item de produmo == a la posicion de angular
                                loteNuevo.setItem(posicion);
                                if (p.getItem() == posicion && prod.getTrazable() == true) {
                                    p.setNroLote(nroLote);
                                    p.setGlnProovedor(glnProovedor);

                                }

                            }
                        }

                        //Recorro produmo y si es el mismo producto le agreego el lote y la geolocalizacion del proovedor
                        /*for (Produmo p : listaProdumo) {
                         // p.getItem() cambia cuando hay un elemento no trazable ver de reacomodar para este caso lsitaProdumo
                         if (p.getIdProductos().equals(prod) && p.getItem() == posicion ){
                         if ( prod.getTrazable() == true){
                         p.setNroLote(nroLote);
                         p.setGlnProovedor(glnProovedor);   
                         }

                                   
                         }
                         }*/
                        listaLotes.add(loteNuevo);

                        //Le sumo uno al item
                        itemTrazabilidad++;
                    }

                } else if (lote && cteTipo.getIdSisComprobante().getStock().equals(1) && grillaTrazabilidad != null && cteTipo.getIdSisComprobante().getIdSisModulos().getIdSisModulos() == 2) {
                    int itemTrazabilidad = 0;
                    //No guardo el lote pero si lo agrego a la tabla produmo
                    for (JsonElement gt : grillaTrazabilidad) {
                        //Obtengo los atributos del body
                        String nroLote = (String) Utils.getKeyFromJsonObject("nroLote", gt.getAsJsonObject(), "String");
                        String serie = (String) Utils.getKeyFromJsonObject("serie", gt.getAsJsonObject(), "String");
                        Integer posicion = (Integer) Utils.getKeyFromJsonObject("posItem", gt.getAsJsonObject(), "Integer");
                        Date fechaElab = (Date) Utils.getKeyFromJsonObject("fechaElab", gt.getAsJsonObject(), "Date");
                        Date fechaVto = (Date) Utils.getKeyFromJsonObject("fechaVto", gt.getAsJsonObject(), "Date");
                        Boolean vigencia = (Boolean) Utils.getKeyFromJsonObject("vigencia", gt.getAsJsonObject(), "boolean");
                        Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", gt.getAsJsonObject(), "Integer");
                        Integer glnProovedor = (Integer) Utils.getKeyFromJsonObject("glnProovedor", gt.getAsJsonObject(), "Integer");
                        //Pregunto por los que no pueden ser null
                        if (nroLote == null || serie == null || vigencia == null || idProducto == null) {
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el lote de la factura, algun campo de la grilla es nulo");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        //Busco el producto por id
                        Producto prod = productoFacade.find(idProducto);
                        if (prod == null) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar lote, el producto con id " + idProducto + " no existe");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        //Armo el lote
                        Lote loteNuevo = new Lote();

                        if (loteFacade.findByNroEmpresaProducto(nroLote, user.getIdPerfil().getIdSucursal().getIdEmpresa(), prod) != null) {
                            loteNuevo = loteFacade.findByNroEmpresaProducto(nroLote, user.getIdPerfil().getIdSucursal().getIdEmpresa(), prod);
                        }

                        loteNuevo.setFechaElab(fechaElab);
                        loteNuevo.setFechaVto(fechaVto);
                        loteNuevo.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                        loteNuevo.setIdproductos(prod);
                        loteNuevo.setItem(itemTrazabilidad);
                        loteNuevo.setNroLote(nroLote);
                        loteNuevo.setSerie(serie);
                        loteNuevo.setVigencia(vigencia);

                        //Recorro produmo y si es el mismo producto le agreego el lote y la geolicalizacion del proovedor
                        for (Produmo p : listaProdumo) {
                            if (p.getIdProductos().equals(prod) && posicion == itemTrazabilidad && prod.getTrazable() == true) {

                                p.setNroLote(nroLote);
                                p.setGlnProovedor(glnProovedor);

                            }
                        }
                        //Le sumo uno al item
                        itemTrazabilidad++;
                    }
                }

                //Me fijo si guarda la factura del remito asociado
                if (!grabaFactura) {
                    CteNumerador cteNumerador = null;
                    if (idNumero != null) {
                        cteNumerador = cteNumeradorFacade.find(idNumero);
                        if (cteNumerador == null) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar la factura, no existe el numero de comprobante");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                        String ptoVenta = String.valueOf(cteNumerador.getIdPtoVenta().getPtoVenta());
                        String numeroVentaFormat = String.format("%08d", cteNumerador.getNumerador());
                        String concatenado = ptoVenta.concat(numeroVentaFormat);
                        factCab.setNumero(Long.parseLong(concatenado, 10));
                        factCab.setIdCteNumerador(cteNumerador);
                        //Guardo cai en factCab si tiene el numerador
                        if (cteNumerador.getCai() != null) {
                            factCab.setCai(cteNumerador.getCai());
                        }
                        //Guardo vtoCai en factCab si tiene el numerador
                        if (cteNumerador.getVtoCai() != null) {
                            factCab.setCaiVto(cteNumerador.getVtoCai());
                        }
                    }
                   
                    return this.persistirObjetos(factCab, contratoDet, listaDetalles, listaImputa, listaImputaExtra, listaProdumo, listaPie, listaLotes, listaFormaPago, cteNumerador, user, esPesificado, esPesificadoPersisteSn, nroCompPesificado);
                } else if (tipoFact != null || letraFact != null || numeroFact != null || fechaVencimientoFact != null || fechaContaFact != null) {
                    CteNumerador cteNumerador = null;
                    if (idNumero != null) {
                        cteNumerador = cteNumeradorFacade.find(idNumero);
                        if (cteNumerador == null) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar la factura, no existe el numero de comprobante");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                        String ptoVenta = String.valueOf(cteNumerador.getIdPtoVenta().getPtoVenta());
                        String numeroVentaFormat = String.format("%08d", cteNumerador.getNumerador());
                        String concatenado = ptoVenta.concat(numeroVentaFormat);
                        factCab.setNumero(Long.parseLong(concatenado, 10));
                        factCab.setIdCteNumerador(cteNumerador);
                        //Guardo cai en factCab si tiene el numerador
                        if (cteNumerador.getCai() != null) {
                            factCab.setCai(cteNumerador.getCai());
                        }
                        //Guardo vtoCai en factCab si tiene el numerador
                        if (cteNumerador.getVtoCai() != null) {
                            factCab.setCaiVto(cteNumerador.getVtoCai());
                        }
                    }
                    if (esPesificado == false) {
                        esPesificado = false;
                    } else {
                        esPesificado = true;
                    }
                    if (esPesificadoPersisteSn == false) {
                        esPesificadoPersisteSn = false;
                    } else {
                        esPesificadoPersisteSn = true;
                    }

                    //Persisto Primero los objetos del remito
                    this.persistirObjetos(factCab, contratoDet, listaDetalles, listaImputa, listaImputaExtra, listaProdumo, listaPie, listaLotes, listaFormaPago, cteNumerador, user, esPesificado, esPesificadoPersisteSn, nroCompPesificado);

                    // -> Voy a actualizar en la base  el FactImputa extra que se agrego  EN LISTA IMPUTA
                    //Luego empiezo con los datos de la factura relacionada
                    CteTipo cteTipoFac = cteTipoFacade.find(tipoFact);
                    if (cteTipoFac == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error al cargar la factura, no existe el tipo de comprobante");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Creo la nueva FactCab
                    FactCab fc = new FactCab();
                    //Busco el numerador del relacionado                        
                    CteNumerador cteNumeradorRel = null;
                    if (idNumeroFact != null) {
                        cteNumeradorRel = cteNumeradorFacade.find(idNumeroFact);
                        if (cteNumeradorRel == null) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar la factura, no existe el numero de comprobante");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                        String ptoVenta = String.valueOf(cteNumeradorRel.getIdPtoVenta().getPtoVenta());
                        String numeroVentaFormat = String.format("%08d", cteNumeradorRel.getNumerador());
                        String concatenado = ptoVenta.concat(numeroVentaFormat);
                        fc.setNumero(Long.parseLong(concatenado, 10));
                        fc.setIdCteNumerador(cteNumerador);
                        //Guardo cai en factCab si tiene el numerador
                        if (cteNumerador.getCai() != null) {
                            fc.setCai(cteNumerador.getCai());
                        }
                        //Guardo vtoCai en factCab si tiene el numerador
                        if (cteNumerador.getVtoCai() != null) {
                            fc.setCaiVto(cteNumerador.getVtoCai());
                        }
                    } else {
                        fc.setNumero(numeroFact.longValue());
                    }
                    fc.setCodigoAfip(codigoAfipFact);
                    fc.setCai(cai);
                    fc.setCaiVto(caiVto);
                    fc.setCodBarra(codBarra);
                    fc.setCodigoPostal(codigoPostal);
                    fc.setCotDolar(cotDolar);
                    fc.setCuit(cuit);
                    fc.setFechaConta(fechaContaFact);
                    fc.setFechaDolar(fechaDolar);
                    fc.setFechaEmision(fechaEmision);
                    fc.setFechaVto(fechaVencimientoFact);
                    System.out.println("fechaVencimientoFact -> " + fechaVencimientoFact.toString());
                    fc.setIdCteTipo(cteTipoFac);
                    fc.setIdListaPrecios(listaPrecio);
                    fc.setIdPadron(idPadron);
                    fc.setIdProductoCanje(productoCanje);
                    fc.setIdmoneda(sisMonedas);
                    fc.setInteresCanje(interesCanje);
                    fc.setLetra(letraFact);
                    fc.setNombre(nombre);
                    fc.setObservaciones(observaciones);
                    fc.setPrecioReferenciaCanje(precioReferenciaCanje);
                    fc.setSitIVA(sisSitIva);
                    fc.setIdSisTipoOperacion(sisTipoOperacion);
                    fc.setIdVendedor(idVendedor);

                    return this.generarFacturaRelacionada(fc, contratoDet, listaDetalles, listaImputa, listaProdumo, listaPie, listaLotes, listaFormaPago, cteNumeradorRel, user, request, esPesificado, esPesificadoPersisteSn, nroCompPesificado);
                } else {
                    respuesta.setControl(AppCodigo.ERROR, "No pudo grabar la factura asociada, algun campo no es valido");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            } else {
                respuesta.setControl(AppCodigo.ERROR, "No se graban detalles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }

    public Response persistirObjetos(FactCab factCab,
            ContratoDet contratoDet,
            List<FactDetalle> factDetalle,
            List<FactImputa> factImputa,
            List<FactImputa> factImputaExtra,
            List<Produmo> produmo,
            List<FactPie> factPie,
            List<Lote> listaLotes,
            List<FactFormaPago> factFormaPago,
            CteNumerador cteNumerador,
            Usuario user,
            Boolean marcaPesificado,
            Boolean pesificadoPersisteSn,
            BigDecimal nroCompPesificado
    ) {
        ServicioResponse respuesta = new ServicioResponse();
        try {

            FactCab transaccion;

            try {

                transaccion = factCabFacade.setFactCabNuevo(factCab);
                if (transaccion == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la Cabecera, clave primaria repetida");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                } else {

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error: " + ex.getMessage());
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la Cabecera, clave primaria repetida");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Comienzo con la transaccion de contratoDet si existe
            if (contratoDet != null && contratoDet.getIdContratos() != null) {
                boolean transaccionContrato;
                transaccionContrato = contratoDetFacade.setContratoDetNuevo(contratoDet);
                if (!transaccionContrato) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo dar de alta el detalle del contrato");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }

            //Comienzo con la transaccion de FacDetalle
            Integer idFactImputaNuevo = null;
            if (!factDetalle.isEmpty()) {
                for (FactDetalle d : factDetalle) {
                    boolean transaccion2;
                    transaccion2 = factDetalleFacade.setFactDetalleNuevo(d);

                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Detalle con el articulo: " + d.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            //Comienzo con la transaccion de FactImputa
            if (!factImputa.isEmpty()) {
                for (FactImputa i : factImputa) {
                    boolean transaccion3;
                    transaccion3 = factImputaFacade.setFactImputaNuevo(i);
                    idFactImputaNuevo = factImputa.get(0).getIdFactDetalleImputa().getIdFactDetalle();
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion3) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la imputacion con el articulo: " + i.getIdFactDetalle().getCodProducto());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                }

            }

            //Comienzo con la transaccion de FactImputaExtra
            if (!factImputaExtra.isEmpty()) {

                for (FactImputa ie : factImputaExtra) {
                    //   List<FactDetalle> fd =   factDetalleFacade.getProximoIdDetalle();
                    /*FactDetalle detalleImputa = factDetalleFacade.getFactDetalle(idFactImputaNuevo);
                    FactDetalle detalle = factDetalleFacade.getFactDetalle(factImputaExtra.get(0).getIdFactDetalle().getIdFactDetalle());
                    System.out.println("-----------> idFactImputaNuevo -------------------> " + idFactImputaNuevo);
                    ie.setIdFactDetalle(detalleImputa);
                    ie.setIdFactDetalleImputa(detalle);
                    ie.setCantidadImputada(detalle.getCantidad());
                    boolean transaccion33;
                    transaccion33 = factImputaFacade.setFactImputaNuevo(ie);
                    if (!transaccion33) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la imputacion extra con el articulo: " + ie.getIdFactDetalle().getCodProducto());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }*/

                }

            }

            //Comienzo con la transaccion de FactPie
            if (!factPie.isEmpty()) {
                for (FactPie p : factPie) {
                    boolean transaccion4;
                    transaccion4 = factPieFacade.setFactPieNuevo(p);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion4) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la factura con la cuenta nro:: " + p.getCtaContable());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            if (!produmo.isEmpty()) {
                //Comienzo con la transaccion de produmo
                /* for (Lote l : listaLotes) {
                 if (){
                        
                 }
                 }*/

                for (Produmo pr : produmo) {
                    boolean transaccion5;

                    transaccion5 = produmoFacade.setProdumoNuevo(pr);
                    //si la trnsaccion fallo devuelvo el mensaje

                    if (!transaccion5) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta produmo con el articulo: " + pr.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            if (!listaLotes.isEmpty()) {
                //Comienzo con la transaccion de Lotes
                for (Lote l : listaLotes) {
                    boolean transaccion6;
                    if (l.getIdLotes() == null) {
                        transaccion6 = loteFacade.setLoteNuevo(l);
                    } else {
                        transaccion6 = loteFacade.editLote(l);
                    }

                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion6) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el lote con el articulo: " + l.getIdproductos().getDescripcion());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            if (!factFormaPago.isEmpty()) {

                //Comienzo con la transaccion de Lotes
                for (FactFormaPago f : factFormaPago) {
                    boolean transaccion7;
                    transaccion7 = factFormaPagoFacade.setFactFormaPagoNuevo(f);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion7) {

                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la forma de pago: " + f.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }

            // SOLO SI ES COMPRAS
            // Verifico si son remitos y paso solamente a facComprasSybase //
            if (factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(1)
                    || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(28)
                    || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(29)
                    || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(31)) {
                /*GrabaFacCompraSybase fcSybase = new GrabaFacCompraSybase();
                 fcSybase.grabarFactComprasSybase(factCab, factDetalle, factFormaPago, factPie, user);*/
                this.grabarFactComprasSybase(factCab, factDetalle, factFormaPago, factPie, user);
                this.grabarFitoStockSybase(factCab, factDetalle, factFormaPago, factPie, user, produmo, listaLotes, user);
            } else {

                if (factCab.getIdCteTipo().getCursoLegal()) {

                    Response respGrabarMaster = grabarMaster(factCab, factDetalle, factFormaPago, factPie, user, marcaPesificado);

                    if (respGrabarMaster.getStatusInfo().equals(Response.Status.CREATED) || respGrabarMaster.getStatusInfo().equals(Response.Status.BAD_REQUEST)) {
                        Boolean respGrabaMasterSybase = this.grabarMasterSybase(factCab, factDetalle, factFormaPago, factPie, user, marcaPesificado, nroCompPesificado);
                        if (respGrabaMasterSybase == true) {
                            if (factCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos() == 1) {
                                this.grabarFactComprasSybase(factCab, factDetalle, factFormaPago, factPie, user);
                            } else if (factCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos() == 2) {
                                // ventas
                                this.grabarFactVentasSybase(factCab, factDetalle, factFormaPago, factPie, user);

                            }
                        }
                    }

                }
            }
            // FIN SOLO SI ES COMPRAS

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
        try {
            //Edito produmo para agregarle la el idDetalle y el lote
            if (!produmo.isEmpty() && !factDetalle.isEmpty()) {
                //Comienzo con la transaccion de produmo para agregarle el idFactDetalle
                int i = 0;
                for (Produmo pr : produmo) {
                    boolean transaccion7;
                    pr.setIdFactDetalle(factDetalle.get(i).getIdFactDetalle());
                    transaccion7 = produmoFacade.editProdumo(pr);
                    //Si la trnsaccion fallo devuelvo el mensaje 
                    if (!transaccion7) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta produmo con el articulo: " + pr.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    i++;
                }
            }
            //Edito el numero si es distinto de null
            if (cteNumerador != null) {
                cteNumerador.setNumerador(cteNumerador.getNumerador() + 1);
                cteNumeradorFacade.edit(cteNumerador);
            }
            if (factCab.getIdFactCab() != null) {
                DatosResponse r = new DatosResponse(factCab.getIdFactCab());
                respuesta.setDatos(r);
            }

            respuesta.setControl(AppCodigo.CREADO, "Comprobante creado con exito, con detalles (" + factCab.getIdFactCab() + ")");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }

    public Response generarFacturaRelacionada(FactCab factCab, ContratoDet contratoDet, List<FactDetalle> factDetalle, List<FactImputa> factImputa, List<Produmo> produmo, List<FactPie> factPie, List<Lote> listaLotes, List<FactFormaPago> factFormaPago, CteNumerador cteNumerador, Usuario user, HttpServletRequest request, Boolean marcaPesificado, Boolean pesificadoPersisteSn, BigDecimal nroCompPesificado) {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            List<FactDetalle> listaDetalles = new ArrayList<>();
            List<FactImputa> listaImputa = new ArrayList<>();
            List<FactImputa> listaImputaExtra = new ArrayList<>();
            List<FactPie> listaPie = new ArrayList<>();
            List<FactFormaPago> listaFormaPago = new ArrayList<>();

            //Le asigno el nuevo FactCab a la lista de detalles
            for (FactDetalle d : factDetalle) {
                FactDetalle factDet = new FactDetalle();
                factDet.setCantBultos(d.getCantBultos());
                factDet.setCantidad(d.getCantidad());
                factDet.setCodProducto(d.getCodProducto());
                factDet.setDescuento(d.getDescuento());
                factDet.setDespacho(d.getDespacho());
                factDet.setDetalle(d.getDetalle());
                factDet.setIdDepositos(d.getIdDepositos());
                factDet.setIdFactCab(factCab);
                factDet.setIdProducto(d.getIdProducto());
                factDet.setImporte(d.getImporte());
                factDet.setImputacion(d.getImputacion());
                factDet.setItem(d.getItem());
                factDet.setIvaPorc(d.getIvaPorc());
                factDet.setObservaciones(d.getObservaciones());
                factDet.setPorcCalc(d.getPorcCalc());
                factDet.setPrecio(d.getPrecio());
                factDet.setTrazable(d.getTrazable());
                factDet.setUnidadDescuento(d.getUnidadDescuento());
                factDet.setPrecioDesc(d.getPrecioDesc());
                listaDetalles.add(factDet);
            }

            //Le asigno el remito y el nuevo factCab en imputa
            if (!factImputa.isEmpty()) {
                for (FactImputa l : factImputa) {
                    FactImputa factImp = new FactImputa();
                    factImp.setCantidadImputada(l.getCantidadImputada());
                    for (FactDetalle d : listaDetalles) {
                        if (d.getCodProducto().equals(l.getIdFactDetalle().getCodProducto())) {
                            //Factura
                            factImp.setIdFactDetalleImputa(d);
                        } else {
                            continue;
                        }
                    }
                    //Remito
                    factImp.setIdFactDetalle(l.getIdFactDetalle());
                    factImp.setImporteImputado(l.getImporteImputado());
                    factImp.setMasAsiento(l.getMasAsiento());
                    factImp.setMasAsientoImputado(l.getMasAsientoImputado());
                    listaImputa.add(factImp);
                }
            } else {
                for (FactDetalle d : factDetalle) {
                    FactImputa facturaImputa = new FactImputa();
                    facturaImputa.setCantidadImputada(d.getCantidad());
                    for (FactDetalle det : listaDetalles) {
                        if (d.getCodProducto().equals(det.getCodProducto())) {
                            //Factura
                            facturaImputa.setIdFactDetalleImputa(det);
                        } else {
                            continue;
                        }
                    }
                    facturaImputa.setIdFactDetalle(d);
                    facturaImputa.setImporteImputado(d.getCantidad().multiply(d.getPorcCalc()).multiply(d.getPrecio()));
                    facturaImputa.setMasAsiento(0);
                    facturaImputa.setMasAsientoImputado(0);
                    listaImputa.add(facturaImputa);
                }
            }
            //Limpio la coleccion de produmo porque no se guarda
            produmo.clear();

            //Guardo el nuevo factCab en factPie
            for (FactPie p : factPie) {
                FactPie faPie = new FactPie();
                faPie.setCtaContable(p.getCtaContable());
                faPie.setDetalle(p.getDetalle());
                faPie.setIdConceptos(p.getIdConceptos());
                faPie.setIdFactCab(factCab);
                faPie.setImporte(p.getImporte());
                faPie.setPorcentaje(p.getPorcentaje());
                faPie.setIdSisTipoModelo(p.getIdSisTipoModelo());
                listaPie.add(faPie);
            }

            //Limpio la lista de lotes porque no se guardan
            listaLotes.clear();

            //Guardo el nuevo FactCab en la forma de pago
            for (FactFormaPago fp : factFormaPago) {
                FactFormaPago ffp = new FactFormaPago();
                ffp.setCtaContable(fp.getCtaContable());
                ffp.setDetalle(fp.getDetalle());
                ffp.setDiasPago(fp.getDiasPago());
                ffp.setFechaPago(fp.getFechaPago());
                ffp.setIdFactCab(factCab);
                ffp.setIdFormaPago(fp.getIdFormaPago());
                ffp.setImporte(fp.getImporte());
                ffp.setPorcentaje(fp.getPorcentaje());
                listaFormaPago.add(ffp);
            }

            //Persisto los objetos y devuelvo la respuesta
            return this.persistirObjetos(factCab, contratoDet, listaDetalles, listaImputa, listaImputaExtra, produmo, listaPie, listaLotes, listaFormaPago, cteNumerador, user, marcaPesificado, pesificadoPersisteSn, nroCompPesificado);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }

    public Response grabarMaster(FactCab factCab, List<FactDetalle> factDetalle, List<FactFormaPago> factFormaPago, List<FactPie> factPie, Usuario user, Boolean marcaPesificado) {
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

            /* 
            
             INICIO DE PESIFICACION GRABARMASTER MYSQL
            
             */
            if (marcaPesificado.equals(true)) {

                if ((factCab.getIdCteTipo().getDescCorta().equals("NC") || factCab.getIdCteTipo().getDescCorta().equals("ND")) && factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {
                    /* PASE 1 */
                    // CANJE CREDITO PASE 98  MASTER
                    for (FactFormaPago fp : factFormaPago) {
                        //fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(7) 
                        // busco la cuenta contable para el cereal que viene en la factcab
                        // CanjesContratosCereales objContratosCereales = canjesContratosCerealesFacade.findCuentaPorCereal(factCab.getIdCteTipo().getIdEmpresa(), factCab.getCerealCanje().getCerealCodigo());

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
                        masterFormaPago.setMIngreso(factCab.getFechaConta());
                        masterFormaPago.setMPase(Short.valueOf(Integer.toString(98)));
                        masterFormaPago.setMVence(factCab.getFechaVto());
                        masterFormaPago.setNroComp(factCab.getNumero());
                        masterFormaPago.setPadronCodigo(factCab.getIdPadron());
                        masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdCteTipo())));
                        masterFormaPago.setMDetalle("PESIFICADO F " + factCab.getNumero());
                        masterFormaPago.setMCtacte("S");
                        masterFormaPago.setPlanCuentas(fp.getCtaContable());

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

                    // FIN PASE 98  
                    // PASE 99 
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
                        masterFormaPago.setMPase(Short.valueOf(Integer.toString(99)));
                        masterFormaPago.setMVence(factCab.getFechaVto());
                        masterFormaPago.setNroComp(factCab.getNumero());
                        masterFormaPago.setPadronCodigo(factCab.getIdPadron());
                        masterFormaPago.setTipoComp(Short.valueOf(Integer.toString(factCab.getIdCteTipo().getIdCteTipo())));
                        masterFormaPago.setMDetalle("PESIFICADO F. " + factCab.getNumero());
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
                            respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con la forma de pago pase 99: " + fp.getDetalle());
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }

                        //Sumo uno al contador de pases
                        paseDetalle++;
                    }

                    // FIN PASE 99 PESIFICACION
                }

            }
            /*
            
             FIN PESIFICACION
            
             */

            respuesta.setControl(AppCodigo.CREADO, "Comprobante creado con exito, con detalles");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }

    }

    /*
    
     Fin graba a Master
     A Continuacion ... Grabo  en fac_compras !!! STOCK
    
     */
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

    /*
 
     Fin proceso graba FacCompras
     */
 /*
     Graba a Master de Sybase
     Author: Dario
    
     */
    public Boolean grabarFactComprasSybase(FactCab factCab, List<FactDetalle> factDetalle, List<FactFormaPago> factFormaPago, List<FactPie> factPie, Usuario user) {
        System.out.println("::::::::: Ejecuta ----------------------> grabarFactComprasSybase()  -> nroComprobante: " + factCab.getNumero() + " | Tipo SisComp: " + factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes());

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
        //cuantas veces me vas a negar el signo papÃ¡
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
                        if (factCab.getIdmoneda().getIdMoneda().equals(2)) {
                            facComprasDetalle.setcDolar(factCab.getCotDolar().doubleValue());
                        } else {

                            facComprasDetalle.setcDolar(Double.valueOf(1));
                        }

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
            System.out.println("-------------> FAC COMPRAS Movimiento 0 CIERRE L50 " + (cotizacionDolar));
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
                if (factCab.getIdmoneda().getIdMoneda().equals(1)) {
                    movCierre.setcDolar(Double.valueOf(1));
                } else {
                    movCierre.setcDolar(factCab.getCotDolar().doubleValue());
                }

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

    // fin factCompras Sybase
    public Boolean grabarMasterSybase(FactCab factCab,
            List<FactDetalle> factDetalle,
            List<FactFormaPago> factFormaPago,
            List<FactPie> factPie,
            Usuario user, Boolean marcaPesificado, BigDecimal nroCompPesificado) {
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
                masterFormaPago.setMUnidades(Double.valueOf(1));
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
                masterImputa.setMUnidades(Double.valueOf(1));
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
                BigDecimal subTotalCanje = new BigDecimal(0);
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
                    masterFormaPago.setMUnidades(Double.valueOf(1));
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
                    masterFormaPago.setMUnidades(Double.valueOf(1));
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
                    System.out.println(paseDetalle + "::::::::: DOCUMENTO SYBASE  ----------------------> grabaDocumentoSybase()-> Moneda: " + factCab.getIdmoneda().getIdMoneda() + ", Diferido: " + factCab.getDiferidoVto() + ", SubTotal: " + subTotal);

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

            /*
             PESIFICADO MASTER SYBASE
             */
            if (marcaPesificado.equals(true)) {

                if ((factCab.getIdCteTipo().getDescCorta().equals("NC") || factCab.getIdCteTipo().getDescCorta().equals("ND")) && factCab.getIdSisTipoOperacion().getIdSisModulos().getIdSisModulos().equals(2)) {

                    BigDecimal subTotalCanje = new BigDecimal(0);
                    for (FactFormaPago fp : factFormaPago) {
                        //fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(7) 
                        //Sumo uno al contador de pases
                        paseDetalle = paseDetalle + 1;
                        MasterSybase masterFormaPago = new MasterSybase(factCab.getFechaConta(), masAsiento, Short.valueOf(Integer.toString(98)), Short.valueOf(Integer.toString(libroCodigo)));
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
                        masterFormaPago.setMDetalle("1-PESIFICACION FAC. " + factCab.getNumero());
                        if (fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(2)) {
                            CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                            masterFormaPago.setPlanCuentas(ctacteCatego.getPlanCuentas());
                        } else {
                            CtacteCategoria ctacteCatego = ctaCteCategoriaFacade.getCategoriaByCodigo(pad.getPadronCatego());
                            masterFormaPago.setPlanCuentas(ctacteCatego.getPlanCuentas());
                            //masterFormaPago.setPlanCuentas(Integer.valueOf(fp.getCtaContable()));
                        }

                        System.out.println("98 ::::::::: Master Sybase pase 98 ----------------------> GrabaMasterSybase()-> Forma pago: setMImporte: " + fp.getImporte().multiply(signo).multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_EVEN).doubleValue());

                        //Parametros que van en 0
                        masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                        masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                        masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                        masterFormaPago.setCondGan(Short.valueOf("0"));
                        masterFormaPago.setCondIva(Short.valueOf("0"));
                        masterFormaPago.setMColumIva(Short.valueOf("0"));
                        masterFormaPago.setMUnidades(Double.valueOf(1));
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
                    // PESIFICACION  PASE 2 MASER SYBASE
                    // calculo el subtotal

                    for (FactFormaPago fp : factFormaPago) {
                        //fp.getIdFormaPago().getTipo().getIdSisFormaPago().equals(7)
                        // busco la cuenta contable para el cereal que viene en la factcab
                        //CanjesContratosCereales objContratosCereales = canjesContratosCerealesFacade.findCuentaPorCereal(factCab.getIdCteTipo().getIdEmpresa(), factCab.getCerealCanje().getCerealCodigo());

                        paseDetalle = paseDetalle + 1;
                        MasterSybase masterFormaPago = new MasterSybase(factCab.getFechaConta(), masAsiento, Short.valueOf(Integer.toString(99)), Short.valueOf(Integer.toString(libroCodigo)));
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
                        masterFormaPago.setMDetalle("2-PESIFICACION FAC. " + factCab.getNumero());
                        masterFormaPago.setPlanCuentas(Integer.valueOf(fp.getCtaContable()));
                        /*if (objContratosCereales != null) {
                         masterFormaPago.setPlanCuentas(Integer.valueOf(objContratosCereales.getCtaContable()));
                         } else {
                         masterFormaPago.setPlanCuentas(Integer.valueOf(fp.getCtaContable()));
                         }*/

                        //Parametros que van en 0
                        masterFormaPago.setAutorizaCodigo(Short.valueOf("0"));
                        masterFormaPago.setTipoCompAsoc(Short.valueOf("0"));
                        masterFormaPago.setConceptoCodigo(Short.valueOf(Integer.toString(codigoConceptoFac)));
                        masterFormaPago.setCondGan(Short.valueOf("0"));
                        masterFormaPago.setCondIva(Short.valueOf("0"));
                        masterFormaPago.setMColumIva(Short.valueOf("0"));
                        masterFormaPago.setMUnidades(Double.valueOf(1));
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
                    //el 137 es el afip el que viene en nroCompPÃ«sificado es el interno

                    if (nroCompPesificado != null) {
                        String puntoVentaTemp = String.valueOf(nroCompPesificado);
                        String puntoVenta = puntoVentaTemp.substring(0, puntoVentaTemp.length() - 8);
                        String numeroCompTemp = String.valueOf(nroCompPesificado);
                        String numeroComp = numeroCompTemp.substring(puntoVentaTemp.length() - 8, puntoVentaTemp.length());
                        CanjesDocumentoSybase documento = documentoSybaseFacade.buscaDocumento(factCab.getIdPadron(), Integer.parseInt(puntoVenta), Integer.parseInt(numeroComp));
                        if (documento != null) {
                            documento.setFactura(factCab.getNumero());
                            boolean transaccionCanjeDoc;
                            transaccionCanjeDoc = canjesDocumentosSybaseFacade.setCanjeDocsNuevo(documento);
                        } else {
                            return false;
                        }
                    }

                }

            }

            /*PESIFICADO MASTER SYBASE  FIN  */
        } catch (Exception ex) {
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            // return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
        System.out.println("::::::::: FIN  ----------------------> GrabaMasterSybase() :: Comprobante contabilizado (" + paseDetalle + " pases) (Sybase) con exito ");
        return true;
        //respuesta.setControl(AppCodigo.CREADO, "Comprobante contabilizado (Sybase) con exito , con detalles");
        //return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
    }

    /*
    
     Trazabilidad: Graba en Fito_Stock en Sybase
    
     */
    public Boolean grabarFitoStockSybase(FactCab factCab, List<FactDetalle> factDetalle, List<FactFormaPago> factFormaPago, List<FactPie> factPie, Usuario user, List<Produmo> produmo, List<Lote> lote, Usuario usuario) {
        System.out.println("::::::::: Ejecuta ----------------------> grabarFitoStockSybase()  -> nroComprobante: " + factCab.getNumero() + " | Tipo SisComp: " + factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes());
        if (factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(1) || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(31)) {
            ServicioResponse respuesta = new ServicioResponse();
            //Seteo la fecha de hoy
            Calendar calendario = new GregorianCalendar();
            Date fechaHoy = calendario.getTime();
            // categoria sybase de iva
            //Contadores para los pases
            Integer paseFitoStock = 1;
            //Me fijo si es debe o haber
            BigDecimal signo = new BigDecimal(1);
            // numero de comprobante 
            String formateada = String.format("%012d", factCab.getNumero());
            String ptoVtaTemp = formateada.substring(0, 4);
            String nroCompTemp = formateada.substring(4, formateada.length());
            long nroCompTempCompleto = factCab.getNumero();
            long nroComp = nroCompTempCompleto;
            Integer tipoOperacion = 0;
            Integer tipoOp = factCab.getIdCteTipo().getIdCteTipo();
            if (tipoOp == 69) {
                //si es remito de compra
                tipoOperacion = 1;
            } else if (tipoOp == 81) {
                // si es remito devolucion de compra
                tipoOperacion = 2;
            } else {
                tipoOperacion = factCab.getIdCteTipo().getcTipoOperacion();
            }
            tipoOp = 0;
            Date fechaOperacion = factCab.getFechaEmision();
            Integer padronCodigo = 0;
            Integer padronProveedor = factCab.getIdPadron();
            String observaciones = "";
            String informaSn = "N";
            String pendienteSn = "S";

            for (Produmo prod : produmo) {

                if (prod.getNroLote() == null) {

                } else {

                    FitoStockSybase fitoStockSybase = new FitoStockSybase(tipoOperacion.shortValue(), fechaOperacion, (Long) nroComp, prod.getItem());
                    fitoStockSybase.setArtCodigo(prod.getIdProductos().getCodProducto());
                    fitoStockSybase.setPadronCodigo(padronCodigo);
                    fitoStockSybase.setFitoGtin(prod.getIdProductos().getGtin());
                    fitoStockSybase.setEvId(Short.valueOf("0"));
                    if (prod.getGlnProovedor() == null) {
                        fitoStockSybase.setFitoGlnOrigen(BigDecimal.ZERO.longValue());
                    } else {
                        fitoStockSybase.setFitoGlnOrigen(prod.getGlnProovedor().longValue());
                    }

                    fitoStockSybase.setFitoGlnDestino(BigDecimal.ZERO.longValue());
                    fitoStockSybase.setMotivoId(Short.valueOf("0"));
                    fitoStockSybase.setFitoMotivoDevolucion("");
                    fitoStockSybase.setFitoNroTransaccion(BigDecimal.ZERO.longValue());
                    fitoStockSybase.setObservaciones(observaciones);
                    fitoStockSybase.setOperadorCodigo(user.getUsuarioSybase());
                    fitoStockSybase.setDeposito(prod.getIdDepositos().getCodigoDep());
                    fitoStockSybase.setCosecha(Short.valueOf("0"));
                    fitoStockSybase.setPadronProveedor(padronProveedor);
                    fitoStockSybase.setNCantidad(prod.getCantidad());

                    for (Lote lot : lote) {
                        Producto producto = productoFacade.find(prod.getIdProductos().getIdProductos());
                        if (producto == null) {
                            respuesta.setControl(AppCodigo.ERROR, "Error al cargar lote, el producto con id " + prod.getIdProductos().getIdProductos() + " no existe");
                        }
                        if (lot.getIdproductos().equals(producto) && producto.getTrazable() == true) {
                            // item de produmo == a la posicion de angular
                            //lot.getItem() == prod.getItem() 
                            if (producto.getTrazable() == true && prod.getItem() == lot.getItem()) {
                                fitoStockSybase.setFitoFeElaboracion(lot.getFechaElab());
                                fitoStockSybase.setFitoFeVto(lot.getFechaVto());
                                fitoStockSybase.setFitoNroLote(lot.getNroLote());
                                fitoStockSybase.setFitoNroSerie(lot.getSerie());

                            }

                        } else {

                        }
                        System.out.println(prod.getItem() + " <- produmo | lote -> " + lot.getItem() + " ----------> " + prod.getProdCodigo() + " | Lote: " + lot.getNroLote() + ", Serie: " + lot.getSerie());
                    }

                    fitoStockSybase.setInformarSn(informaSn.charAt(0));
                    fitoStockSybase.setPendienteSn(pendienteSn.charAt(0));
                    paseFitoStock = paseFitoStock + 1;
                    boolean transaccionSybaseFitoStock;
                    transaccionSybaseFitoStock = fitoStockSybaseFacade.fitoStockSybaseNuevo(fitoStockSybase);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccionSybaseFitoStock) {
                        return false;
                    }

                }
            }
            // SI SON REMITOS VAN ESTAS MARCAS
            System.out.println("::::::::: FIN  ----------------------> FitoStock Sybase() :: FitoStock pasado exitosamente !!! > ");
            return true;
        } else {
            System.out.println("::::::::: FIN  ----------------------> FitoStock Sybase() :: Error el tipo de comprobante es invÃ¡lido !!!  ");
            return false;
        }
    }

    /*
    
     Fin Trazabilidad Fito_Stock
    
     */
    public void mandarMailPdf(Boolean enviaMail,
            FactCab factCab,
            HttpServletRequest request,
            Usuario user,
            String token,
            SisOperacionComprobante sisOperacionComprobante,
            CteTipo cteTipo,
            List<FactPie> listaPie,
            Integer idNumero,
            ServicioResponse respuesta,
            BigDecimal numero,
            List<FactFormaPago> listaFormaPago) {
        if (enviaMail == true) {
            byte[] bytes = null;
            try {
                String nombreReporte = factCab.getIdCteTipo().getIdReportes().getNombre();
                String codigoVerificador = "";
                if (factCab.getNumeroAfip() != null && factCab.getIdCteTipo().getCursoLegal() && !" ".equals(factCab.getCai())) {
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd");
                    Formatter obj = new Formatter();
                    String txtCuit = factCab.getCuit();
                    String txtCodComp = String.valueOf(obj.format("%02d", factCab.getCodigoAfip()));
                    String numero2 = String.valueOf(factCab.getNumero());
                    String ptoVenta = numero2.substring(0, numero2.length() - 8);
                    String txtPtoVta = String.valueOf(obj.format("%04d", Integer.parseInt(ptoVenta)));
                    String txtCae = factCab.getCai();
                    String txtVtoCae = formatoFecha.format(factCab.getFechaVto());
                    codigoVerificador = utilidadesFacade.calculoDigitoVerificador(txtCuit, txtCodComp, txtPtoVta, txtCae, txtVtoCae);
                }
                HashMap hm = new HashMap();
                hm.put("idFactCab", factCab.getIdFactCab());
                hm.put("codigoVerificador", codigoVerificador);
                hm.put("prefijoEmpresa", "05");
                System.out.println(factCab.getIdFactCab() + " - " + codigoVerificador + " - " + factCab.getIdCteTipo().getIdCteTipo());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bytes = utilidadesFacade.generateJasperReportPDF(request, nombreReporte, hm, user, outputStream, 1, false, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error: " + ex.getMessage());
            }

            Integer idEmpresa = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa();
            String nombreEmpresa = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getDescripcion();
            String nombreSucursal = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getNombre();
            String emailOrigen = parametro.get("KERNEL_SMTP_USER");
            // Destinatarios de los mails
            String emailDestino = sisOperacionComprobante.getMail1();
            String nombreDestino = sisOperacionComprobante.getNombreApellidoParaMail1();
            // Fin mails  Destinatarios
            String asunto = "Sistema de FacturaciÃ³n: Alta de " + cteTipo.getDescripcion();
            String detallePieComprobante = "";
            BigDecimal baseImponible = new BigDecimal(0);
            BigDecimal totalComprobante = new BigDecimal(0);
            BigDecimal porcentaje = new BigDecimal(0);
            for (FactPie pie : listaPie) {
                detallePieComprobante = pie.getDetalle();
                baseImponible = pie.getBaseImponible();

            }
            // armo nro de comprobante
            CteNumerador cteNumerador = null;
            Produmo nroComp = new Produmo();
            if (idNumero != null) {
                cteNumerador = cteNumeradorFacade.find(idNumero);
                if (cteNumerador == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar la factura, no existe el numero de comprobante");
                }

                String ptoVenta = String.valueOf(cteNumerador.getIdPtoVenta().getPtoVenta());
                String numeroVentaFormat = String.format("%08d", cteNumerador.getNumerador());
                String concatenado = ptoVenta.concat(numeroVentaFormat);
                nroComp.setNumero(Long.parseLong(concatenado, 10));
            } else {
                nroComp.setNumero(numero.longValue());
            }
            String formaPagoString = "		<li>Forma/s de Pago: ";
            if (!listaFormaPago.isEmpty()) {
                for (Integer i = 0; i < listaFormaPago.size(); i++) {
                    if (i != (listaFormaPago.size() - 1)) {
                        formaPagoString = formaPagoString + listaFormaPago.get(i).getIdFormaPago().getDescripcion() + ", ";
                    } else if (i == (listaFormaPago.size() - 1)) {
                        formaPagoString = formaPagoString + listaFormaPago.get(i).getIdFormaPago().getDescripcion() + ".";
                    }
                }
            }
            String nroCompString = Long.toString(nroComp.getNumero());
            // Fechas:
            String fechaEmi = new SimpleDateFormat("dd-MM-yyyy").format(factCab.getFechaEmision());
            String fechaVence = new SimpleDateFormat("dd-MM-yyyy").format(factCab.getFechaVto());
            // Armo el cuerpo del mail 
            String contenido = "<!doctype html>\n"
                    + "<html>\n"
                    + "<head>\n"
                    + "<meta charset=\"utf-8\">\n"
                    + "<title>Sistema FacturaciÃ³n</title>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "<div  style='font-size:14px;'>\n"
                    + "<hr>\n"
                    + "<div><strong>" + accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getDescripcion() + "</strong></div>\n"
                    + "<div> Sucursal: " + nombreSucursal + "</div>"
                    + "<div>Asunto: " + asunto + "</div>"
                    + "<div>Para: " + nombreDestino + "</div>\n"
                    + "<hr>\n"
                    + "<div  style='font-size:12px; padding: 20px;'>"
                    + "	<div><strong>Detalle del Comprobante Cargado</strong></div>\n"
                    + "	<div>\n"
                    + "		<li>Comprobante emitido a: " + factCab.getNombre() + " (" + factCab.getCuit() + ")" + "</li>\n"
                    + "		<li>Nro Cuenta Corriente: " + factCab.getIdPadron() + "</li>\n"
                    + "		<li>Tipo Comprobante: " + cteTipo.getDescripcion() + "\n"
                    + "		<li>Nro Comprobante: " + nroCompString + " </li>\n"
                    + "		<li>Fecha EmsiÃ³n: " + fechaEmi + " </li>\n"
                    + "		<li>Fecha Vencimiento: " + fechaVence + " </li>\n"
                    + "		<li>Importe Neto: $" + baseImponible + " </li>\n"
                    + "		<li>Detalle: " + detallePieComprobante + " </li>\n"
                    + formaPagoString + " </li>\n"
                    + "		<br><li>Observaciones: <br><i>" + factCab.getObservaciones() + "</i><br><strong>"
                    + "		<br><i>Operador que emitio el comprobante: " + accesoFacade.findByToken(token).getIdUsuario().getNombre() + " " + accesoFacade.findByToken(token).getIdUsuario().getApellido() + "</i>\n"
                    + "		\n"
                    + "	</div>\n"
                    + "</div>\n"
                    + "</div>\n"
                    + "\n"
                    + "</body>\n"
                    + "</html>";

            try {
                // fin armado del cuerpo
                // String contenido = asunto+ " | Se ha agregado un nuevo comprobante | Emision: "+fechaEmision+" - Cuit: "+cuit+" -  Comprobante Nro: "+numeroFact+" - TC: "+tipoFact;
            utilidadesFacade.enviarMailPdf(emailOrigen, nombreEmpresa + " : " + nombreSucursal, emailDestino, contenido, asunto, nombreDestino, bytes, sisOperacionComprobante);

            } catch (Exception ex) {
                Logger.getLogger(GrabaComprobanteRest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No se pudo enviar los emails debido a un error inesperado !!!");
        }
    }

    /*
    
     Fin graba a Master Sybase
     A Continuacion ... Grabo  en fac_compras Sybase !!!
    
     */
    public Boolean grabarFactVentasSybase(FactCab factCab, List<FactDetalle> factDetalle, List<FactFormaPago> factFormaPago, List<FactPie> factPie, Usuario user) {
        System.out.println("::::::::: Ejecuta ----------------------> grabarFactVentasSybase()  -> nroComprobante: " + factCab.getNumero() + " | Tipo SisComp: " + factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes());
        ServicioResponse respuesta = new ServicioResponse();
        //Seteo la fecha de hoy
        Calendar calendario = new GregorianCalendar();
        Date fechaHoy = calendario.getTime();
        // categoria sybase de iva
        List<ModeloDetalle> modeloDetalleArray = new ArrayList<>();
        Padron condiIva = padronFacade.find(factCab.getIdPadron());
        ParametrosFacSybase param = parametrosFacSybaseFacade.getParams();
        CerealSisaSybase cerealSisa = cerealSisaSybaseFacade.getByCodPadron(factCab.getIdPadron());
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
            BigDecimal totalPercep2 = new BigDecimal(0);
            BigDecimal netoIva21 = new BigDecimal(0);
            BigDecimal netoIva105 = new BigDecimal(0);
            BigDecimal netoIva27 = new BigDecimal(0);
            BigDecimal cotizacionDolar = new BigDecimal(1);
            // dolarizo o pesifico
            if (factCab.getIdCteTipo().getcTipoOperacion() < 17) {
                // factura
                if (factCab.getIdmoneda().getIdMoneda() > 1) {
                    cotizacionDolar = factCab.getCotDolar();
                    // System.out.println("ES EN DOLARES LA DEBO DE PESIFICAR (cotizacion: " + cotizacionDolar + ") ");
                } else {
                    cotizacionDolar = new BigDecimal(1);
                }
                System.out.println("COTIZACION DOLAR: " + cotizacionDolar);

            }
            Integer formaPagoSeleccionada = 0;
            for (FactDetalle det : factDetalle) {
                System.out.println("::::::::: PASA FACT DETALLE -> PRODUCTO:" + det.getCodProducto());
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

                FacVentasSybase facVentasDetalle = new FacVentasSybase(det.getCodProducto(),
                        Short.valueOf(Integer.toString(det.getIdFactCab().getIdCteTipo().getcTipoOperacion())),
                        det.getIdFactCab().getFechaEmision(),
                        Short.valueOf(Integer.toString(det.getIdFactCab().getIdCteTipo().getcTipoOperacion())),
                        Long.valueOf(nroComp),
                        det.getIdFactCab().getIdPadron(),
                        Short.valueOf(Integer.toString(paseDetalle)),
                        Short.valueOf(Integer.toString(ptoVta)));
                if (cerealSisa.getEstado() == Short.valueOf("0")) {
                    facVentasDetalle.setVCuotas(param.getPSisaCeroPorc().shortValue());
                    facVentasDetalle.setVPerce24591(det.getImporte().multiply(param.getPSisaCeroPorc()).divide(new BigDecimal(100)).doubleValue());
                }
                if (cerealSisa.getEstado() == Short.valueOf("1")) {
                    facVentasDetalle.setVCuotas(param.getPSisaUnoPorc().shortValue());
                    facVentasDetalle.setVPerce24591(det.getImporte().multiply(param.getPSisaUnoPorc()).divide(new BigDecimal(100)).doubleValue());
                }
                if (cerealSisa.getEstado() == Short.valueOf("2")) {
                    facVentasDetalle.setVCuotas(param.getPSisaDosPorc().shortValue());
                    facVentasDetalle.setVPerce24591(det.getImporte().multiply(param.getPSisaDosPorc()).divide(new BigDecimal(100)).doubleValue());
                }
                if (cerealSisa.getEstado() == Short.valueOf("3")) {
                    facVentasDetalle.setVCuotas(param.getPSisaTresPorc().shortValue());
                    facVentasDetalle.setVPerce24591(det.getImporte().multiply(param.getPSisaTresPorc()).divide(new BigDecimal(100)).doubleValue());
                }
                // valores 0 por defecto para que no se graben en nulo
                facVentasDetalle.setVRetencion1(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVRetencion2(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVIvaRi(totalIva21.doubleValue());
                facVentasDetalle.setVIvaRni(totalIva105.doubleValue());
                facVentasDetalle.setVPercepcion1(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVPercepcion2(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVRetencion2(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVNombre(det.getIdFactCab().getNombre());
                facVentasDetalle.setVCantidad(det.getCantidad().doubleValue());
                Double precioUnitario = det.getPrecio().multiply(signo).multiply(cotizacionDolar).doubleValue();
                facVentasDetalle.setVPrecioUnitario(new BigDecimal(precioUnitario).doubleValue());
                facVentasDetalle.setVFechaVencimiento(det.getIdFactCab().getFechaVto());
                facVentasDetalle.setVFacturadoSn(facturadoSn.charAt(0));
                facVentasDetalle.setVCodigoOperador(user.getUsuarioSybase());
                facVentasDetalle.setVHora(fechaHoy);
                //facVentasDetalle.setBarra(det.getIdFactCab().getCodBarra());

                for (FactFormaPago fp : factFormaPago) {
                    formaPagoSeleccionada = fp.getIdFormaPago().getCodigoSysbase();
                }
                facVentasDetalle.setVFormaPago((Short.valueOf(Integer.toString(formaPagoSeleccionada))));
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
                    facVentasDetalle.setVRetencion1(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVRetencion2(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVIvaRi(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVIvaRni(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVPercepcion2(BigDecimal.ZERO.doubleValue());
                    netoIva105 = det.getImporte();
                    totalDetalleFactura = (netoIva105.add(totalIva105).add(totalPercep1)).multiply(cotizacionDolar);

                } else if (det.getIvaPorc().equals(new BigDecimal(21))) {
                    // System.out.println("IVA 21 ivaRi -> " + det.getIvaPorc());
                    totalIva21 = det.getImporte().multiply(det.getIvaPorc()).divide(new BigDecimal(100));
                    //.multiply(signo).multiply(cotizacionDolar).doubleValue()
                    facVentasDetalle.setVIvaRi(totalIva21.multiply(cotizacionDolar).doubleValue());
                    facVentasDetalle.setVIvaRni(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVPercepcion2(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVRetencion1(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVRetencion2(BigDecimal.ZERO.doubleValue());

                    netoIva21 = det.getImporte();
                    totalDetalleFactura = (netoIva21.add(totalIva21).add(totalPercep1)).multiply(cotizacionDolar);

                } else if (det.getIvaPorc().equals(new BigDecimal(27))) {
                    // System.out.println("IVA 27 c_percepcion2 -> " + det.getIvaPorc());
                    facVentasDetalle.setVIvaRi(BigDecimal.ZERO.doubleValue());
                    totalIva27 = det.getImporte().multiply(det.getIvaPorc()).divide(new BigDecimal(100));
                    facVentasDetalle.setVPercepcion2(totalIva27.multiply(cotizacionDolar).doubleValue());
                    facVentasDetalle.setVIvaRni(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVRetencion1(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVRetencion2(BigDecimal.ZERO.doubleValue());

                    netoIva27 = det.getImporte();
                    totalDetalleFactura = (netoIva27.add(totalIva27).add(totalPercep1)).multiply(cotizacionDolar);

                } else if (det.getIvaPorc().equals(0)) {
                    facVentasDetalle.setVIvaRi(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVIvaRni(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVPercepcion2(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVRetencion1(BigDecimal.ZERO.doubleValue());
                    facVentasDetalle.setVRetencion2(BigDecimal.ZERO.doubleValue());

                    totalPercep1 = new BigDecimal(0);
                    totalDetalleFactura = new BigDecimal(0);
                }

                facVentasDetalle.setVBonificacion(totalDetalleFactura.doubleValue());
                totalDetalleFactura = new BigDecimal(0);
                facVentasDetalle.setVCondicionIva(Short.valueOf(Integer.toString(condiIva.getCondIva().getCondiva())));
                facVentasDetalle.setVDeposito(det.getIdDepositos().getCodigoDep());
                if (det.getIdFactCab().getFactFormaPagoCollection() != null && det.getIdFactCab().getFactFormaPagoCollection().size() > 0 && det.getIdFactCab().getFactFormaPagoCollection().size() == 1) {
                    List<FactFormaPago> listaFFP = new ArrayList(det.getIdFactCab().getFactFormaPagoCollection());
                    if (listaFFP != null && listaFFP.size() == 1 && listaFFP.get(0).getIdFormaPago().getIdFormaPago() == 12) {
                        facVentasDetalle.setCanjeSn("S");
                    } else {
                        facVentasDetalle.setCanjeSn("N");
                    }
                } else {
                    facVentasDetalle.setCanjeSn("N");
                }
                //Valores que van en 0
                facVentasDetalle.setVDescuento(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVFormaPago((Short.valueOf(Integer.toString(0))));
                facVentasDetalle.setVImpuestoInterno(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVOtroImpuesto(BigDecimal.ZERO.doubleValue());
                facVentasDetalle.setVCodigoRelacion(0);
                facVentasDetalle.setVNumeroCuit(Long.valueOf(factCab.getCuit()));
                //facVentasDetalle.setVContabil(contabilSn);
                facVentasDetalle.setCanjeSn("N");
                facVentasDetalle.setCanjeNroCto("N");
                facVentasDetalle.setVDescripcion(det.getCodProducto());
                facVentasDetalle.setVTipoComprobanteAsoc(det.getIdFactCab().getIdCteTipo().getcTipoOperacion().shortValue());
                facVentasDetalle.setVNumeroComprobanteAsoc(det.getIdFactCab().getNumeroAfip());
                facVentasDetalle.setNroAutorizado(det.getIdFactCab().getNumeroAfip());
                if (factCab.getNumeroAfip() != null) {
                    facVentasDetalle.setAutorizadoSn("S".charAt(0));
                } else {
                    facVentasDetalle.setAutorizadoSn("N".charAt(0));
                }
                // si es factura no se graba detalle en facCompras Sybase y hacemos la persistencia
                if (det.getIdFactCab().getIdCteTipo().getcTipoOperacion() >= 17) {
                    boolean transaccionFacC = false;
                    transaccionFacC = facVentasSybaseFacade.setFacVentasSybaseNuevo(facVentasDetalle);
                    //si la transaccion fallo devuelvo el mensaje
                    if (!transaccionFacC) {
                        return false;
                    }
                    paseDetalle++;
                }
            }
            /* Movimiento 0 cierre */
            System.out.println("-------------> FAC VENTAS Movimiento 0 CIERRE L50 " + (cotizacionDolar));
            FacVentasSybase movCierre = new FacVentasSybase("CIERRE L50",
                    Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())),
                    factCab.getFechaEmision(),
                    Short.valueOf(Integer.toString(factCab.getIdCteTipo().getcTipoOperacion())),
                    Long.valueOf(nroComp),
                    factCab.getIdPadron(),
                    Short.valueOf(Integer.toString(0)),
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
            movCierre.setVDescuento(BigDecimal.ZERO.doubleValue());
            movCierre.setVImpuestoInterno(BigDecimal.ZERO.doubleValue());
            movCierre.setVOtroImpuesto(BigDecimal.ZERO.doubleValue());
            movCierre.setVCodigoRelacion(0);
            movCierre.setVTipoComprobanteAsoc(Short.valueOf(Integer.toString(0)));
            movCierre.setVNumeroComprobanteAsoc(Long.parseLong("0"));
            //movCierre.setVContabil(contabilSn);
            movCierre.setCanjeSn("N");
            movCierre.setCanjeNroCto("N");
            movCierre.setVIvaRni(BigDecimal.ZERO.doubleValue());
            movCierre.setVIvaRi(BigDecimal.ZERO.doubleValue());
            movCierre.setVDescuento(BigDecimal.ZERO.doubleValue());
            movCierre.setVDescripcion("CIERRE");
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
                        System.out.println("-------------> ES IVA 10.5 " + (totalIva105.multiply(cotizacionDolar).doubleValue()));
                    } else if (pie.getPorcentaje().equals(new BigDecimal(21))) {
                        totalIva21 = totalIva21.add(pie.getImporte());
                        movCierre.setVIvaRi(totalIva21.multiply(cotizacionDolar).doubleValue());

                        System.out.println("-------------> ES IVA 21 " + (totalIva21.multiply(cotizacionDolar).doubleValue()));
                    } else if (pie.getPorcentaje().equals(new BigDecimal(27))) {
                        totalIva27 = totalIva27.add(pie.getImporte());
                        movCierre.setVPercepcion2(totalIva27.multiply(cotizacionDolar).doubleValue());
                        System.out.println("-------------> ES IVA 27 " + (totalIva27.multiply(cotizacionDolar).doubleValue()));
                    }
                }
                totalPieFactura = (pie.getBaseImponible().add(totalIva21).add(totalIva27).add(totalIva105).add(totalPercep1).add(totalPercep2)).multiply(cotizacionDolar);
            }
            for (FactDetalle det : factDetalle) {
                totalPrecioUnitario = totalPrecioUnitario.add(det.getCantidad().multiply(det.getPrecio())).multiply(cotizacionDolar);
                totalCantidad = new BigDecimal(0); //totalCantidad.add(det.getCantidad());
                movCierre.setVDeposito(det.getIdDepositos().getCodigoDep());
            }
            for (FactFormaPago fp : factFormaPago) {
                formaPagoSeleccionada = fp.getIdFormaPago().getCodigoSysbase();

            }

            if (cerealSisa.getEstado() == Short.valueOf("0")) {
                movCierre.setVCuotasInteres(param.getPSisaCeroPorc().doubleValue());
                movCierre.setVPerce24591(totalPrecioUnitario.multiply(param.getPSisaCeroPorc()).divide(new BigDecimal(100)).doubleValue());
            }
            if (cerealSisa.getEstado() == Short.valueOf("1")) {
                movCierre.setVCuotasInteres(param.getPSisaUnoPorc().doubleValue());
                movCierre.setVPerce24591(totalPrecioUnitario.multiply(param.getPSisaUnoPorc()).divide(new BigDecimal(100)).doubleValue());
            }
            if (cerealSisa.getEstado() == Short.valueOf("2")) {
                movCierre.setVCuotasInteres(param.getPSisaDosPorc().doubleValue());
                movCierre.setVPerce24591(totalPrecioUnitario.multiply(param.getPSisaDosPorc()).divide(new BigDecimal(100)).doubleValue());
            }
            if (cerealSisa.getEstado() == Short.valueOf("3")) {
                movCierre.setVCuotasInteres(param.getPSisaTresPorc().doubleValue());
                movCierre.setVPerce24591(totalPrecioUnitario.multiply(param.getPSisaTresPorc()).divide(new BigDecimal(100)).doubleValue());
            }
            movCierre.setVFormaPago((Short.valueOf(Integer.toString(formaPagoSeleccionada))));

            if (factCab.getFactFormaPagoCollection() != null && factCab.getFactFormaPagoCollection().size() > 0 && factCab.getFactFormaPagoCollection().size() == 1) {
                List<FactFormaPago> listaFFP = new ArrayList(factCab.getFactFormaPagoCollection());
                if (listaFFP != null && listaFFP.size() == 1 && listaFFP.get(0).getIdFormaPago().getIdFormaPago() == 12) {
                    movCierre.setCanjeSn("S");
                } else {
                    movCierre.setCanjeSn("N");
                }
            } else {
                movCierre.setCanjeSn("N");
            }
            ///////////////////////////////////////////////
            movCierre.setVNombre(factCab.getNombre());
            if (factDetalle.get(0).getDetalle().length() > 35) {
                movCierre.setVDescripcion(factDetalle.get(0).getDetalle().substring(0, 34));
            } else {
                movCierre.setVDescripcion(factDetalle.get(0).getDetalle());
            }
            movCierre.setVFechaVencimiento(factCab.getFechaVto());
            movCierre.setVFacturadoSn(facturadoSn.charAt(0));
            movCierre.setVCodigoOperador(user.getUsuarioSybase());
            movCierre.setVHora(fechaHoy);
            //movCierre.setVFechaContabil(factCab.getFechaConta());
            //movCierre.setBarra("");
            movCierre.setVCondicionIva(Short.valueOf(Integer.toString(condiIva.getCondIva().getCondiva())));
            //
            movCierre.setVPercepcion1(totalPercep1.doubleValue());
            movCierre.setVBonificacion(totalPieFactura.doubleValue());
            movCierre.setVPrecioUnitario(totalPrecioUnitario.doubleValue());
            movCierre.setVCantidad(Double.valueOf(1));
            movCierre.setVRetencion1(totalPercep1.add(totalPercep2).doubleValue());
            movCierre.setVPercepcion1(BigDecimal.ZERO.doubleValue());
            movCierre.setVPercepcion2(BigDecimal.ZERO.doubleValue());
            movCierre.setVTipoComprobanteAsoc(factCab.getIdCteTipo().getcTipoOperacion().shortValue());
            movCierre.setVNumeroComprobanteAsoc(factCab.getNumeroAfip());
            movCierre.setNroAutorizado(factCab.getNumeroAfip());
            if (factCab.getNumeroAfip() != null) {
                movCierre.setAutorizadoSn("S".charAt(0));
            } else {
                movCierre.setAutorizadoSn("N".charAt(0));
            }
            movCierre.setVNumeroCuit(Long.valueOf(factCab.getCuit()));

            boolean transaccion0;
            transaccion0 = facVentasSybaseFacade.setFacVentasSybaseNuevo(movCierre);
            //si la trnsaccion fallo devuelvo el mensaje
            if (!transaccion0) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la master con la imputacion: ");
                return false;
            }

            // fin movimiento 0  cierre
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(AppCodigo.ERROR + " | FacVentas Sybase():::::::::::::::::: ----> " + ex.toString());
            return false;
        }
        System.out.println("::::::::: FIN  ----------------------> FacVentas Sybase() :: Stock pasado exitosamente !!! > ");
        return true;
    }

    // PASAJES
    @POST
    @Path("/pasajes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pasaje(@HeaderParam("token") String token, @HeaderParam("idModulo") Integer idModulo, @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException, Exception {
        ServicioResponse respuesta = new ServicioResponse();
        System.out.println("<----- pasajes()");
        JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
        Boolean esPesificado = (Boolean) Utils.getKeyFromJsonObject("marcaPesificado", jsonBody, "boolean");
        Boolean esPesificadoPersisteSn = (Boolean) Utils.getKeyFromJsonObject("pesificadoPersisteSn", jsonBody, "boolean");
        BigDecimal nroCompPesificado = (BigDecimal) Utils.getKeyFromJsonObject("nroCompPesificado", jsonBody, "BigDecimal");

        //Obtengo los atributos del body
        //Datos de FactCab
        //valido valido parametros
        if (idModulo == null) {
            respuesta.setControl(AppCodigo.ERROR, "Error, Parametros nulos, no se pudo especificar el modulo.");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }

        if (token == null || token.trim().isEmpty()) {
            respuesta.setControl(AppCodigo.ERROR, "Error, token vacio");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }

        //Busco el token
        Acceso userToken = accesoFacade.findByToken(token);

        //valido que Acceso no sea null
        if (userToken == null) {
            respuesta.setControl(AppCodigo.ERROR, "Error, Acceso nulo");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }

        //Busco el usuario
        Usuario user = usuarioFacade.getByToken(userToken);

        //valido que el Usuario no sea null
        if (user == null) {
            respuesta.setControl(AppCodigo.ERROR, "Error, Usuario nulo");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();

        }

        //valido vencimiento token
        if (!accesoFacade.validarToken(userToken, user)) {
            respuesta.setControl(AppCodigo.ERROR, "Credenciales incorrectas");
            return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
        }

        Integer idFactCab = (Integer) Utils.getKeyFromJsonObject("idFactCab", jsonBody, "Integer");
        FactCab objFactCab = factCabFacade.getByIdFactCab(idFactCab);
        List<FactDetalle> objFactDetalle = factDetalleFacade.getFactDetalleByIdFactCab(idFactCab);
        List<FactFormaPago> objFactFormaPago = (List<FactFormaPago>) factFormaPagoFacade.getFactFormaPago(idFactCab);
        List<FactPie> objFactPie = factPieFacade.getFactFie(idFactCab);
        // caso contrario verifico el curso legal si es verdadero (true) contabiliza y paso a factComprasSybase
        if (objFactCab.getIdCteTipo().getCursoLegal()) {
            if (objFactCab.getIdCteTipo().getIdSisComprobante().getIdSisModulos().getIdSisModulos() == 2) {
                Response respGrabarMaster = this.grabarMaster(objFactCab, objFactDetalle, objFactFormaPago, objFactPie, user, esPesificado);
                if (respGrabarMaster.getStatusInfo().equals(Response.Status.CREATED) || respGrabarMaster.getStatusInfo().equals(Response.Status.BAD_REQUEST)) {
                    Boolean respGrabaMasterSybase = this.grabarMasterSybase(objFactCab, objFactDetalle, objFactFormaPago, objFactPie, user, esPesificado, nroCompPesificado);
                    if (respGrabaMasterSybase == true) {
                        this.grabarFactVentasSybase(objFactCab, objFactDetalle, objFactFormaPago, objFactPie, user);

                    } else {
                        respuesta.setControl(AppCodigo.ERROR, "El pasaje a contabilidad ha fallado");
                        return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
                    }
                }
            }

        }
        respuesta.setControl(AppCodigo.OK, "El pasaje de ventas se realizo con Ã©xito.");
        return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
    }

}
