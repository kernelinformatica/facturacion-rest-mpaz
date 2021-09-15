package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import servicios.GrabaComprobanteRest;
import compra.GrabaFacCompraSybase;
import compra.GrabaComprasMaster;
import compra.GrabaComprasMasterSybase;
import utils.Utils;

/**
 *
 * @author Dario Quiroga
 */

@Stateless
@Path("grabaComprobanteCompra")
public class GrabaComprobanteCompraRest {

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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setComprobante(
            @HeaderParam("token") String token,
            @Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            System.out.println("<----- grabaComprobanteCompraRest ----->");
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);

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
            //Para canje
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
                List<Produmo> listaProdumo = new ArrayList<>();
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

                    //Actualizo el precio del aritulo si es compra, si es distinto de 0 y si el precio es distinto. Ademas lo convierto de acuerdo al tipo de moneda
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

                    // Busco listaPrecioDet
                    if (listaPrecio != null) {
                        ListaPrecioDet detalleProd = listaPrecioFacade.findByIdProductoAndIdLista(listaPrecio, idProducto);
                        factDetalle.setAuxListaPrecioDet(detalleProd);
                    }

                    listaDetalles.add(factDetalle);

                    //Empiezo la transaccion para la grabacion de FactImputa
                    if (factImputa && idFactDetalleImputa != null) {
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
                        // si no proviene de un comprobante pendiente no se graba ////////////////
                        // si pendiente es mayor a 0 es pendiente y si es 0 no pendiente  ///////
                        if (pendiente.compareTo(BigDecimal.ZERO) == 0) {
                            // no es pendiente
                        } else {
                            if (factDetalle.getIdFactCab().getNumero() != imputa.getIdFactCab().getNumero()) {
                                FactImputa facturaImputaExtra = new FactImputa();
                                facturaImputaExtra.setCantidadImputada(factDetalle.getCantidad());
                                facturaImputaExtra.setIdFactDetalle(factDetalle);
                                facturaImputaExtra.setIdFactDetalleImputa(imputa);
                                facturaImputaExtra.setImporteImputado(pendiente.multiply(porCalc).multiply(precio));
                                facturaImputaExtra.setMasAsiento(0);
                                facturaImputaExtra.setMasAsientoImputado(0);
                                listaImputa.add(facturaImputaExtra);
                            }

                        }

                    }

                    //Pregunto si se graba produmo y empiezo con la transaccion
                    if (produmo && (cteTipo.getIdSisComprobante().getStock().equals(1) || cteTipo.getIdSisComprobante().getStock().equals(2))) {
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
                        prod.setItem(item);

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
                        if (factCab.getIdSisTipoOperacion().getIdSisTipoOperacion() != 5 && (precio.compareTo(det.getAuxListaPrecioDet().getCotaInf()) < 0
                                || precio.compareTo(det.getAuxListaPrecioDet().getCotaSup()) > 0)
                                && precio.compareTo(BigDecimal.ZERO) != 0) {
                            detallesCotas.add(det);
                        }
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
                    for (JsonElement gt : grillaTrazabilidad) {
                        //Obtengo los atributos del body
                        String nroLote = (String) Utils.getKeyFromJsonObject("nroLote", gt.getAsJsonObject(), "String");
                        String serie = (String) Utils.getKeyFromJsonObject("serie", gt.getAsJsonObject(), "String");
                        Date fechaElab = (Date) Utils.getKeyFromJsonObject("fechaElab", gt.getAsJsonObject(), "Date");
                        Date fechaVto = (Date) Utils.getKeyFromJsonObject("fechaVto", gt.getAsJsonObject(), "Date");
                        Boolean vigencia = (Boolean) Utils.getKeyFromJsonObject("vigencia", gt.getAsJsonObject(), "boolean");
                        Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", gt.getAsJsonObject(), "Integer");

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

                        //Recorro produmo y si es el mismo producto le agreego el lote
                        for (Produmo p : listaProdumo) {
                            if (p.getIdProductos().equals(prod)) {
                                p.setNroLote(nroLote);
                            }
                        }
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
                        Date fechaElab = (Date) Utils.getKeyFromJsonObject("fechaElab", gt.getAsJsonObject(), "Date");
                        Date fechaVto = (Date) Utils.getKeyFromJsonObject("fechaVto", gt.getAsJsonObject(), "Date");
                        Boolean vigencia = (Boolean) Utils.getKeyFromJsonObject("vigencia", gt.getAsJsonObject(), "boolean");
                        Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", gt.getAsJsonObject(), "Integer");

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

                        //Recorro produmo y si es el mismo producto le agreego el lote
                        for (Produmo p : listaProdumo) {
                            if (p.getIdProductos().equals(prod)) {
                                p.setNroLote(nroLote);
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
                    return this.persistirObjetos(factCab, contratoDet, listaDetalles, listaImputa, listaProdumo, listaPie, listaLotes, listaFormaPago, cteNumerador, user);
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

                    //Persisto Primero los objetos del remito
                    this.persistirObjetos(factCab, contratoDet, listaDetalles, listaImputa, listaProdumo, listaPie, listaLotes, listaFormaPago, cteNumerador, user);

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

                    return this.generarFacturaRelacionada(fc, contratoDet, listaDetalles, listaImputa, listaProdumo, listaPie, listaLotes, listaFormaPago, cteNumeradorRel, user, request);
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
            List<Produmo> produmo,
            List<FactPie> factPie,
            List<Lote> listaLotes,
            List<FactFormaPago> factFormaPago,
            CteNumerador cteNumerador,
            Usuario user) {
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
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion3) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la imputacion con el articulo: " + i.getIdFactDetalle().getCodProducto());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
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
            // Verifico si son remitos y paso solamente a facComprasSybase //
            if (factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(1)
                    || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(28)
                    || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(29)
                    || factCab.getIdCteTipo().getIdSisComprobante().getIdSisComprobantes().equals(31)) {

                GrabaFacCompraSybase fcSybase = new GrabaFacCompraSybase();
                fcSybase.grabarFactComprasSybase(factCab, factDetalle, factFormaPago, factPie, user);
                // reemplazar por la clase: this.grabarFactComprasSybase(factCab, factDetalle, factFormaPago, factPie, user);
            } else {
                // caso contrario verifico el curso legal si es verdadero (true) contabiliza y paso a factComprasSybase
                // reemplazar por las clases correspondientes

                if (factCab.getIdCteTipo().getCursoLegal()) {
                    GrabaComprasMaster compraMaster = new GrabaComprasMaster();
                    Response respGrabarMaster = compraMaster.grabarMaster(factCab, factDetalle, factFormaPago, factPie, user);
                    if (respGrabarMaster.getStatusInfo().equals(Response.Status.CREATED) || respGrabarMaster.getStatusInfo().equals(Response.Status.BAD_REQUEST)) {
                        GrabaComprasMasterSybase compraMasterSybase = new GrabaComprasMasterSybase();
                        Boolean respGrabaMasterSybase = compraMasterSybase.grabarMasterSybase(factCab, factDetalle, factFormaPago, factPie, user);
                        if (respGrabaMasterSybase == true) {
                            GrabaFacCompraSybase fcSybase = new GrabaFacCompraSybase();
                            fcSybase.grabarFactComprasSybase(factCab, factDetalle, factFormaPago, factPie, user);
                        }
                    }
                }

            }

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

    public Response generarFacturaRelacionada(FactCab factCab, ContratoDet contratoDet, List<FactDetalle> factDetalle, List<FactImputa> factImputa, List<Produmo> produmo, List<FactPie> factPie, List<Lote> listaLotes, List<FactFormaPago> factFormaPago, CteNumerador cteNumerador, Usuario user, HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            List<FactDetalle> listaDetalles = new ArrayList<>();
            List<FactImputa> listaImputa = new ArrayList<>();
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
            return this.persistirObjetos(factCab, contratoDet, listaDetalles, listaImputa, produmo, listaPie, listaLotes, listaFormaPago, cteNumerador, user);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }

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
                //bytes = utilidadesFacade.generateJasperReportPDF(request, nombreReporte, hm, user, outputStream);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error: " + ex.getMessage());
            }

            Integer idEmpresa = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa();
            String nombreEmpresa = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getDescripcion();
            String nombreSucursal = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getNombre();
            String emailOrigen = parametro.get("KERNEL_SMTP_USER");
            String emailDestino = sisOperacionComprobante.getMail1();
            String nombreDestino = sisOperacionComprobante.getNombreApellidoParaMail1();
            String asunto = "Sistema de Facturación: Alta de " + cteTipo.getDescripcion();
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
                    + "<title>Sistema Facturación</title>\n"
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
                    + "		<li>Fecha Emsión: " + fechaEmi + " </li>\n"
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
                utilidadesFacade.enviarMailPdf(emailOrigen, nombreEmpresa + " : " + nombreSucursal, emailDestino, contenido, asunto, nombreDestino, bytes);
            } catch (Exception ex) {
                Logger.getLogger(GrabaComprobanteRest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No se envia mail");
        }
    }

    // fin factCompras Sybase
}
