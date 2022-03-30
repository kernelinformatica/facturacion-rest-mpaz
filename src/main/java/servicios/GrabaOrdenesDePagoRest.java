package servicios;

import entidades.OrdenesPagosDetalle;
import entidades.OrdenesPagosPCab;
import entidades.OrdenesPagosFormaPago;
import entidades.OrdenesPagosPie;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import compra.GrabaFacCompraSybase;
import datos.AppCodigo;
import datos.DatosResponse;
import datos.DatosOrdenPagoResponse;
import datos.ServicioResponse;
import entidades.Acceso;

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

import entidades.Cereales;
import entidades.MasterSybase;
import entidades.Padron;
import entidades.Producto;
import entidades.Produmo;

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
import persistencia.PadronFacade;
import persistencia.ProductoFacade;
import persistencia.ProdumoFacade;
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
import persistencia.OrdenesPagosDetalleFacade;
import persistencia.OrdenesPagosFormaPagoFacade;
import persistencia.OrdenesPagosPCabFacade;
import persistencia.OrdenesPagosPieFacade;
import utils.Utils;

/**
 *
 * @author Kernel informatica
 */
@Stateless
@Path("grabaOrdenesDePago")
public class GrabaOrdenesDePagoRest {
    
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
    ParametroGeneralFacade parametro;
    
    @Inject
    MasterSybaseFacade masterSybaseFacade;
    @Inject
    FacComprasSybaseFacade factComprasSybaseFacade;
    @Inject
    ParametrosFacSybaseFacade parametrosFacSybaseFacade;
    @Inject
    FacVentasSybaseFacade facVentasSybaseFacade;
    @Inject
    OrdenesPagosDetalleFacade ordenesPagosDetalleFacade;
    @Inject
    OrdenesPagosPCabFacade ordenesPagoCabFacade;
    @Inject
    OrdenesPagosFormaPagoFacade ordenesPagosFormaPagoFacade;
    @Inject
    OrdenesPagosPieFacade ordenesPagosPieFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkHttp(@Context HttpServletRequest request) {
        ServicioResponse respuesta = new ServicioResponse();
        respuesta.setControl(AppCodigo.OK, "Servicio de Ordenes de Pago, fuciona correctamente.");
        return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setComprobante(
            @HeaderParam("token") String token,
            @Context HttpServletRequest request) throws Exception {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            System.out.println("<----- grabaOrdenDePagoComprobanteRest ----->");
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);

            //Obtengo los atributos del body
            //Datos de OpCab
            Integer idOpCab = (Integer) Utils.getKeyFromJsonObject("idOpCab", jsonBody, "Integer");
            Integer idFactCab = (Integer) Utils.getKeyFromJsonObject("idFactCab", jsonBody, "Integer");
            Integer idCteTipo = (Integer) Utils.getKeyFromJsonObject("idCteTipo", jsonBody, "Integer");
            BigDecimal numero = (BigDecimal) Utils.getKeyFromJsonObject("numero", jsonBody, "BigDecimal");
            Date fechaEmision = (Date) Utils.getKeyFromJsonObject("fechaEmision", jsonBody, "Date");
            Integer idPadron = (Integer) Utils.getKeyFromJsonObject("idPadron", jsonBody, "Integer");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            String nombre = (String) Utils.getKeyFromJsonObject("nombre", jsonBody, "String");
            String cuit = (String) Utils.getKeyFromJsonObject("cuit", jsonBody, "String");
            String codigoPostal = (String) Utils.getKeyFromJsonObject("codigoPostal", jsonBody, "String");
            BigDecimal cotDolar = (BigDecimal) Utils.getKeyFromJsonObject("cotDolar", jsonBody, "BigDecimal");
            Integer idSisTipoOperacion = (Integer) Utils.getKeyFromJsonObject("idSisTipoOperacion", jsonBody, "Integer");
            Integer idSisOperacionComprobante = (Integer) Utils.getKeyFromJsonObject("idSisOperacionComprobante", jsonBody, "Integer");
            Integer idUsuarioAutorizante = (Integer) Utils.getKeyFromJsonObject("idUsuario", jsonBody, "Integer");
            Date fechaAutorizacion = (Date) Utils.getKeyFromJsonObject("fechaAutorizacion", jsonBody, "Date");
            BigDecimal numeroReciboCaja = (BigDecimal) Utils.getKeyFromJsonObject("numeroReciboCaja", jsonBody, "BigDecimal");
            Integer pagoCerrado = (Integer) Utils.getKeyFromJsonObject("idSisTipoOperacion", jsonBody, "pagoCerrado");
            // fin OpCab //

            // Booleanos para ver que se guarda y que no 
            boolean ordenPagoCabecera = (Boolean) Utils.getKeyFromJsonObject("ordenPagoCabecera", jsonBody, "boolean");
            boolean ordenPagoDetalle = (Boolean) Utils.getKeyFromJsonObject("ordenPagoDetalle", jsonBody, "boolean");
            boolean ordenPagoFormaPago = (Boolean) Utils.getKeyFromJsonObject("ordenPagoFormaPago", jsonBody, "boolean");
            boolean ordenPagoPie = (Boolean) Utils.getKeyFromJsonObject("ordenPagoPie", jsonBody, "boolean");

            //Grillas de comprobantes para la orden de pago, subTotales, elementos trazables y formas de pago
            List<JsonElement> grillaComprobantes = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("grillaComprobantes", jsonBody, "ArrayList");
            List<JsonElement> grillaSubTotales = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("grillaSubTotales", jsonBody, "ArrayList");
            List<JsonElement> grillaFormaPago = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("grillaFormaPago", jsonBody, "ArrayList");
            //
            Integer idNumero = (Integer) Utils.getKeyFromJsonObject("idNumero", jsonBody, "Integer");
            // validaciones de datos
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
            
            if (idUsuarioAutorizante == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Usuario Autorizante nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            } else {
                Usuario usuarioAutorizante = usuarioFacade.getByIdUsuario(idUsuarioAutorizante);
            }

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
            if (numero == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, numero nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (fechaEmision == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, fecha emision nula");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if (idPadron == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, padron nulo");
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
            //Pregunto si la grilla de comprobantes  no esta vacia
            if (grillaComprobantes == null) {
                respuesta.setControl(AppCodigo.ERROR, "Lista de Articulos Vacia");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            String codPostal = codigoPostal;
            if (idPadron != null && idPadron != 0) {
                Padron padron = padronFacade.find(idPadron);
            }

            // comienzo a rellenar los objetos antes de persistirlos
            OrdenesPagosPCab ordenPagosCab = new OrdenesPagosPCab();
            //Pregunto si se guarda una cabecera
            // numero de id 
            CteNumerador cteNumerador = null;
            if (idNumero != null) {
                cteNumerador = cteNumeradorFacade.find(idNumero);
                if (cteNumerador == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar la orden, no existe el numero de comprobante");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                String ptoVenta = String.valueOf(cteNumerador.getIdPtoVenta().getPtoVenta());
                String numeroVentaFormat = String.format("%08d", cteNumerador.getNumerador());
                String concatenado = ptoVenta.concat(numeroVentaFormat);
                if (ordenPagoCabecera) {
                    ordenPagosCab.setIdCteTipo(idCteTipo);
                    ordenPagosCab.setNumero(numero.longValue());
                    ordenPagosCab.setFechaEmision(fechaEmision);
                    ordenPagosCab.setIdPadron(idPadron);
                    ordenPagosCab.setIdMoneda(sisMonedas);
                    ordenPagosCab.setNombre(nombre);
                    ordenPagosCab.setCuit(cuit);
                    ordenPagosCab.setCodigoPostal(codigoPostal);
                    ordenPagosCab.setCotDolar(cotDolar);
                    ordenPagosCab.setIdSisTipoOperacion(sisTipoOperacion);
                    ordenPagosCab.setIdSisOperacionComprobantes(idSisOperacionComprobante);
                    ordenPagosCab.setIdUsuarios(idNumero);
                    ordenPagosCab.setIdUsuariosAutorizante(idUsuarioAutorizante);
                    ordenPagosCab.setFechaAutorizacion(fechaAutorizacion);
                    ordenPagosCab.setNumeroReciboCaja(numeroReciboCaja.longValue());
                    ordenPagosCab.setPagoCerrado(Boolean.FALSE);
                    
                } else {
                    if (idOpCab == null) {
                        respuesta.setControl(AppCodigo.ERROR, "El parametro de cabecera no puede ser nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    //busco busco orde de pago cab
                    ordenPagosCab = ordenesPagoCabFacade.find(idOpCab);
                    if (ordenPagosCab == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No existe Fact Cab");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }                    
                }
                
            } else {
                respuesta.setControl(AppCodigo.ERROR, "No existe idNumero");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            /*
             detalles
                    
             */
            List<OrdenesPagosDetalle> listaDetalles = new ArrayList<>();
            List<OrdenesPagosPie> listaPie = new ArrayList<>();
            List<OrdenesPagosFormaPago> listaFormaPago = new ArrayList<>();
            //Pregunto si guarda Detalles
            if (ordenPagoDetalle) {
                //Armo la listas de Fact par luego parsearlas todas juntas

                //Contador para factDetalle
                int item = 0;
                //Recorro el array de GRILLA COMPROBANES y creo facDetalle

                for (JsonElement j : grillaComprobantes) {
                    //Obtengo los atributos del body
                    Integer idOPDetalle = (Integer) Utils.getKeyFromJsonObject("idDetalle", j.getAsJsonObject(), "Integer");
                    Integer itemDetalle = (Integer) Utils.getKeyFromJsonObject("item", j.getAsJsonObject(), "Integer");
                    BigDecimal pagadoDolar = (BigDecimal) Utils.getKeyFromJsonObject("pagadoDolar", j.getAsJsonObject(), "BigDecimal");
                    BigDecimal importePesificado = (BigDecimal) Utils.getKeyFromJsonObject("importePesificado", j.getAsJsonObject(), "BigDecimal");
                    Integer ideFormaPago = (Integer) Utils.getKeyFromJsonObject("idFormaPago", j.getAsJsonObject(), "Integer");
                    BigDecimal cotizacionDolarFact = (BigDecimal) Utils.getKeyFromJsonObject("cotDolarFact", j.getAsJsonObject(), "BigDecimal");
                    BigDecimal difContizacion = (BigDecimal) Utils.getKeyFromJsonObject("difContizacion", j.getAsJsonObject(), "BigDecimal");
                    Integer idIva = (Integer) Utils.getKeyFromJsonObject("idIva", j.getAsJsonObject(), "Integer");
                    BigDecimal ivaDifContizacion = (BigDecimal) Utils.getKeyFromJsonObject("ivaDifContizacion", j.getAsJsonObject(), "BigDecimal");
                    Integer idFactCabComp = (Integer) Utils.getKeyFromJsonObject("idFactCabComp", j.getAsJsonObject(), "Integer");
                    
                    if (importePesificado.intValue() == 0) {
                        System.out.println("da 0 esto " + importePesificado.intValue());
                    } else {
                        System.out.println("no da 0 esto " + importePesificado.intValue());
                    }
                    //Pregunto por los campos que son NOTNULL
                    if (pagadoDolar == null || importePesificado == null || ideFormaPago == null || cotizacionDolarFact == null || difContizacion == null
                            || idIva == null || ivaDifContizacion == null) {
                        respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    if (idSisOperacionComprobante == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la orden, Comprobante no encontrado -> idSisOperacionComprobante: " + idSisOperacionComprobante);
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    SisOperacionComprobante sisOperacionComprobante = sisOperacionComprobanteFacade.find(idSisOperacionComprobante);
                    System.out.println("sisOperacionComprobante: " + sisOperacionComprobante);
                    if (sisOperacionComprobante == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta , Comprobante no encontrado -> idSisOperacionComprobante: " + idSisOperacionComprobante);
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    /////////////////////////////////////////////////////////
                    //Creo el factDetalle nuevo y seteo los valores
                    OrdenesPagosDetalle ordenesPagosDetalle = new OrdenesPagosDetalle();
                    ordenesPagosDetalle.setIdOPCab(ordenPagosCab);
                    ordenesPagosDetalle.setItem(item);
                    ordenesPagosDetalle.setIdFactCab(idFactCabComp);
                    ordenesPagosDetalle.setPagadoDolar(pagadoDolar);
                    ordenesPagosDetalle.setImportePesificado(importePesificado);
                    ordenesPagosDetalle.setCotDolarFact(cotDolar);
                    ordenesPagosDetalle.setDifCotizacion(difContizacion);
                    ordenesPagosDetalle.setIdIVA(idIva);
                    ordenesPagosDetalle.setDifCotizacion(difContizacion);
                    
                    listaDetalles.add(ordenesPagosDetalle);
                    
                }
                
            }

            // fin detalles
            //Termina el recorrido de la Grilla de comprobantes y empiezo con la de factFormaPago
            System.out.println("::::::::: grillaFormaPago ::::::::::::: -> " + grillaFormaPago);
            
            if (ordenPagoFormaPago && grillaFormaPago != null) {
                
                for (JsonElement je : grillaFormaPago) {
                    Integer idOpFormaPago = (Integer) Utils.getKeyFromJsonObject("idOpFormaPago", je.getAsJsonObject(), "Integer");
                    //Integer idOPcab = (Integer) Utils.getKeyFromJsonObject("idOPCab", je.getAsJsonObject(), "Integer");
                    Integer idFormaPago = (Integer) Utils.getKeyFromJsonObject("idFormaPago", je.getAsJsonObject(), "Integer");
                    BigDecimal importe = (BigDecimal) Utils.getKeyFromJsonObject("importe", je.getAsJsonObject(), "BigDecimal");
                    Date fechaAcreditacion = (Date) Utils.getKeyFromJsonObject("fechaAcreditacion", je.getAsJsonObject(), "Date");
                    BigDecimal numeroComp = (BigDecimal) Utils.getKeyFromJsonObject("numero", je.getAsJsonObject(), "BigDecimal");
                    String detalle = (String) Utils.getKeyFromJsonObject("detalle", je.getAsJsonObject(), "String");
                    //Pregunto si son nulos 
                    if (importe == null || idFormaPago == null || fechaAcreditacion == null || numeroComp == null || detalle == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta Forma de Pago, algun campo de la grilla es nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    if (importe.compareTo(BigDecimal.ZERO) == 0) {
                        continue;
                    }
                    
                    FormaPagoDet formaPagoDet = formaPagoDetFacade.getByidFormaPagoDet(idFormaPago);
                    
                    if (formaPagoDet == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta Forma de Pago, la forma de pago no existe");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Creo FacForma de pago
                    OrdenesPagosFormaPago opFpago = new OrdenesPagosFormaPago();
                    opFpago.setIdOPCab(ordenPagosCab);
                    opFpago.setIdFormaPago(idFormaPago);
                    opFpago.setImporte(importe);
                    opFpago.setFechaAcreditacion(fechaAcreditacion);
                    opFpago.setNumero(numeroComp.longValue());
                    opFpago.setDetalle(formaPagoDet.getDetalle());
                    opFpago.setObservaciones(detalle);
                    listaFormaPago.add(opFpago);
                }
            }

            //Empiezo con la grilla de SubTotales para grabar ordenPagoPie
            if (ordenPagoPie) {
                for (JsonElement je : grillaSubTotales) {
                    Integer idImpuesto = (Integer) Utils.getKeyFromJsonObject("idImpuesto", je.getAsJsonObject(), "Integer");
                    String detalle = (String) Utils.getKeyFromJsonObject("detalle", je.getAsJsonObject(), "String");
                    BigDecimal alicuota = (BigDecimal) Utils.getKeyFromJsonObject("alicuota", je.getAsJsonObject(), "BigDecimal");
                    BigDecimal importeBase = (BigDecimal) Utils.getKeyFromJsonObject("importeBase", je.getAsJsonObject(), "BigDecimal");
                    BigDecimal importeImpuesto = (BigDecimal) Utils.getKeyFromJsonObject("importeImpuesto", je.getAsJsonObject(), "BigDecimal");
                    BigDecimal numeroRetencion = (BigDecimal) Utils.getKeyFromJsonObject("numeroRetencion", je.getAsJsonObject(), "BigDecimal");
                    String operador = (String) Utils.getKeyFromJsonObject("operador", je.getAsJsonObject(), "String");
                    if (importeBase.equals(0)) {
                        System.out.println("Error al llevar el importe Base, venia en 0, " + detalle);
                    }
                    if (importeImpuesto.equals(0)) {
                        System.out.println("Error al llevar el importe Impuesto, venia en 0, " + detalle);
                    }

                    //Pregunto por los que no pueden ser Null
                    if (detalle == null || importeBase == null || importeImpuesto == null || numeroRetencion == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la de la orden de pago, algun campo de la grilla es nulo");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    //Creo el pie 
                    OrdenesPagosPie ordenesPagosPie = new OrdenesPagosPie();
                    ordenesPagosPie.setIdOPCab(ordenPagosCab);
                    ordenesPagosPie.setIdImpuesto(idImpuesto);
                    ordenesPagosPie.setDetalle(detalle);
                    ordenesPagosPie.setAlicuota(alicuota);
                    ordenesPagosPie.setImporteBase(importeBase);
                    ordenesPagosPie.setImporteImpuesto(importeImpuesto);
                    ordenesPagosPie.setNumeroRetencion(numeroRetencion.longValue());
                    listaPie.add(ordenesPagosPie);
                    
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

//            respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
//            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            return this.persistirObjetos(ordenPagosCab, listaDetalles, listaFormaPago, listaPie, cteNumerador, user);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
        
    }

    /*
    
     PERSISTIR OBJETOS
    
     */
    public Response persistirObjetos(OrdenesPagosPCab opPCab,
            List<OrdenesPagosDetalle> opDetalle,
            List<OrdenesPagosFormaPago> opFormaPago,
            List<OrdenesPagosPie> opPie,
            CteNumerador cteNumerador,
            Usuario user
    ) {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            
            OrdenesPagosPCab transaccion;
            
            try {
                transaccion = ordenesPagoCabFacade.setOrdenePagoCabNuevo(opPCab);
                if (transaccion == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la Cabecera, la transaccion es nula");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                } else {
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error: " + ex.getMessage());
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la Cabecera, clave primaria repetida");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Comienzo con la transaccion de FacDetalle
            if (!opDetalle.isEmpty()) {
                for (OrdenesPagosDetalle d : opDetalle) {
                    boolean transaccion2;
                    transaccion2 = ordenesPagosDetalleFacade.setOrdenPagoDetalleNuevo(d);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Detalle de la orden de pago: " + d.getIdOPDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            if (!opFormaPago.isEmpty()) {
                for (OrdenesPagosFormaPago p : opFormaPago) {
                    boolean transaccion4;
                    transaccion4 = ordenesPagosFormaPagoFacade.setOrdenPagoFormaPagoNuevo(p);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion4) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la Orden de pago : " + p.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            if (!opPie.isEmpty()) {
                for (OrdenesPagosPie p : opPie) {
                    boolean transaccion4;
                    transaccion4 = ordenesPagosPieFacade.setOrdenPagoPieNuevo(p);
                    //si la trnsaccion fallo devuelvo el mensaje
                    if (!transaccion4) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el pie de la Orden de pago " + p.getIdOPCab().getIdOPCab());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            // ......................
            //Edito el numero si es distinto de null
            if (cteNumerador != null) {
                cteNumerador.setNumerador(cteNumerador.getNumerador() + 1);
                cteNumeradorFacade.edit(cteNumerador);
            }
            if (opPCab.getIdOPCab() != null) {
                DatosOrdenPagoResponse r = new DatosOrdenPagoResponse(opPCab.getIdOPCab());
                respuesta.setDatos(r);
            }
            respuesta.setControl(AppCodigo.CREADO, "Orden de Pago creada con exito, con detalles (" + opPCab.getIdOPCab() + ")");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
        
    }
}
