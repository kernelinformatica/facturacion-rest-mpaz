/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.ServicioResponse;
import javax.ejb.Stateless;
import javax.inject.Inject;
import datos.AppCodigo;
import datos.FactCabResponse;
import datos.OrdenPagoCabResponse;
import datos.OrdenPagoDetalleResponse;
import datos.OrdenPagoFormaPagoResponse;
import datos.OrdenPagoPieResponse;
import datos.OrdenesPagoResponse;
import datos.Payload;
import entidades.Acceso;
import entidades.FormaPago;
import entidades.FactCab;
import entidades.OrdenesPagosDetalle;
import entidades.OrdenesPagosFormaPago;
import entidades.OrdenesPagosPCab;
import entidades.OrdenesPagosPie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import persistencia.AccesoFacade;
import utils.Utils;
import entidades.Usuario;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import javax.ws.rs.POST;
import persistencia.FactCabFacade;
import persistencia.OrdenesPagosDetalleFacade;
import persistencia.OrdenesPagosFormaPagoFacade;
import persistencia.OrdenesPagosPCabFacade;
import persistencia.OrdenesPagosPieFacade;
import persistencia.UsuarioFacade;

/**
 *
 * @author Dario
 */
@Stateless
@Path("buscarOrdenesPago")
public class BuscarOrdenesPagoRest {

    @Inject
    UsuarioFacade usuarioFacade;
    @Inject
    AccesoFacade accesoFacade;
    @Inject
    FactCabFacade factCabFacade;
    @Inject
    OrdenesPagosPCabFacade ordenesPagosPCabFacade;
    @Inject
    OrdenesPagosDetalleFacade ordenesPagosDetalleFacade;
    @Inject
    OrdenesPagosFormaPagoFacade ordenesPagosFormaPagoFacade;
    @Inject
    OrdenesPagosPieFacade ordenesPagosPieFacade;
    @Inject
    Utils utils;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdenesPago(
            @HeaderParam("token") String token,
            // Si idFactCab viene con valor tgraigo las orgenes de pago relacionadas a un comprobante de compra
            // Si idOpCab viene con valor traigo una orden de pago especifica, independientemente que idFacCab traiga valor
            @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            // Obtengo los atributos del body

            Integer idEmpresa = (Integer) Utils.getKeyFromJsonObject("idEmpresa", jsonBody, "Integer");
            Integer idOpCab = (Integer) Utils.getKeyFromJsonObject("idOpCab", jsonBody, "Integer");
            Integer idProveedor = (Integer) Utils.getKeyFromJsonObject("codProveedor", jsonBody, "Integer");
            Date fechaDesde = (Date) Utils.getKeyFromJsonObject("fechaDesde", jsonBody, "Date");
            Date fechaHasta = (Date) Utils.getKeyFromJsonObject("fechaHasta", jsonBody, "Date");

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
            java.sql.Date sqlFechaDesde = new java.sql.Date(fechaDesde.getTime());
            java.sql.Date sqlFechaHasta = new java.sql.Date(fechaHasta.getTime());
            List<Payload> comprobantes = new ArrayList<>();
            List<Payload> detalles = new ArrayList<>();
            List<Payload> formaPago = new ArrayList<>();
            List<Payload> pie = new ArrayList<>();
            List<OrdenPagoCabResponse> ordenPagoCabList = new ArrayList<>();
            List<OrdenPagoDetalleResponse> ordenPagoDetalleList = new ArrayList<>();
            List<OrdenPagoFormaPagoResponse> ordenPagoFormaPagoList = new ArrayList<>();
            List<OrdenPagoPieResponse> ordenPagoPieList = new ArrayList<>();
            //
            List<OrdenesPagosPCab> ordenesPagosList = new ArrayList<>();
            List<OrdenesPagosDetalle> ordensPagoDetList = new ArrayList<>();
            List<OrdenesPagosFormaPago> ordenesPagoFormaPagoList = new ArrayList<>();
            List<OrdenesPagosPie> ordenesPagoPieList = new ArrayList<>();
            //Valido datos busqueda

            if (idOpCab == null || idOpCab == 0) {
                // busco todos los comprobantes

                if (idProveedor == null || idProveedor == 0) {
                    // busco todos los proovedores
                    ordenesPagosList = ordenesPagosPCabFacade.getFindAllEntreFechas(sqlFechaDesde, sqlFechaHasta);
                    for (OrdenesPagosPCab p : ordenesPagosList) {
                        OrdenPagoCabResponse pr = new OrdenPagoCabResponse(p);
                        // Detalle
                        ordensPagoDetList = ordenesPagosDetalleFacade.getByIdOrdenPago(pr.getIdOPCab());
                        for (OrdenesPagosDetalle d : ordensPagoDetList) {
                            OrdenPagoDetalleResponse det = new OrdenPagoDetalleResponse(d);
                            pr.getDetalle().add(det);
                        }
                        // Forma Pago
                        ordenesPagoFormaPagoList = ordenesPagosFormaPagoFacade.getByIdOrdePago(pr.getIdOPCab());
                        for (OrdenesPagosFormaPago f : ordenesPagoFormaPagoList) {
                            OrdenPagoFormaPagoResponse fp = new OrdenPagoFormaPagoResponse(f);
                            pr.getFormaPago().add(fp);
                        }
                        // Pie
                        ordenesPagoPieList = ordenesPagosPieFacade.getByIdOrdePago(pr.getIdOPCab());
                        for (OrdenesPagosPie h : ordenesPagoPieList) {
                            OrdenPagoPieResponse pi = new OrdenPagoPieResponse(h);
                            pr.getPie().add(pi);
                        }

                        comprobantes.add(pr);
                    }

                    respuesta.setArraydatos(comprobantes);
                    respuesta.setControl(AppCodigo.OK, "Listado de Comprobantes entre fechas");
                    return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();

                } else {
                    // BUSCA UN SOLO proovedor
                    ordenesPagosList = ordenesPagosPCabFacade.getFindAllEntreFechasPadron(sqlFechaDesde, sqlFechaHasta, idProveedor);
                    for (OrdenesPagosPCab p : ordenesPagosList) {
                        OrdenPagoCabResponse pr = new OrdenPagoCabResponse(p);
                        // Detalle
                        ordensPagoDetList = ordenesPagosDetalleFacade.getByIdOrdenPago(pr.getIdOPCab());
                        for (OrdenesPagosDetalle d : ordensPagoDetList) {
                            OrdenPagoDetalleResponse det = new OrdenPagoDetalleResponse(d);
                            pr.getDetalle().add(det);
                        }
                        // Forma Pago
                        ordenesPagoFormaPagoList = ordenesPagosFormaPagoFacade.getByIdOrdePago(pr.getIdOPCab());
                        for (OrdenesPagosFormaPago f : ordenesPagoFormaPagoList) {
                            OrdenPagoFormaPagoResponse fp = new OrdenPagoFormaPagoResponse(f);
                            pr.getFormaPago().add(fp);
                        }
                        // pie
                        ordenesPagoPieList = ordenesPagosPieFacade.getByIdOrdePago(pr.getIdOPCab());
                        for (OrdenesPagosPie h : ordenesPagoPieList) {
                            OrdenPagoPieResponse pi = new OrdenPagoPieResponse(h);
                            pr.getPie().add(pi);
                        }

                        comprobantes.add(pr);

                    }
                    respuesta.setArraydatos(comprobantes);
                    respuesta.setControl(AppCodigo.OK, "Listado de Comprobantes entre fechas y padron");
                    return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();

                }

            } else {
                // busco un comprobante especifico por idopCab

                ordenesPagosList = ordenesPagosPCabFacade.getByNumeroCab(idOpCab);
                for (OrdenesPagosPCab p : ordenesPagosList) {
                    OrdenPagoCabResponse pr = new OrdenPagoCabResponse(p);
                    // Detalle
                    ordensPagoDetList = ordenesPagosDetalleFacade.getByIdOrdenPago(pr.getIdOPCab());
                    for (OrdenesPagosDetalle d : ordensPagoDetList) {
                        OrdenPagoDetalleResponse det = new OrdenPagoDetalleResponse(d);
                        pr.getDetalle().add(det);
                    }
                    // Forma Pago
                    ordenesPagoFormaPagoList = ordenesPagosFormaPagoFacade.getByIdOrdePago(pr.getIdOPCab());
                    for (OrdenesPagosFormaPago f : ordenesPagoFormaPagoList) {
                        OrdenPagoFormaPagoResponse fp = new OrdenPagoFormaPagoResponse(f);
                        pr.getFormaPago().add(fp);
                    }
                    // pie
                    ordenesPagoPieList = ordenesPagosPieFacade.getByIdOrdePago(pr.getIdOPCab());
                    for (OrdenesPagosPie h : ordenesPagoPieList) {
                        OrdenPagoPieResponse pi = new OrdenPagoPieResponse(h);
                        pr.getPie().add(pi);
                    }

                    comprobantes.add(pr);
                }
                respuesta.setArraydatos(comprobantes);
                respuesta.setControl(AppCodigo.OK, "Comprobante " + idOpCab + " se encontro con éxito !!!");
                return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
            }

        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }

}
