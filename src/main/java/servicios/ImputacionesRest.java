package servicios;

import datos.AppCodigo;
import datos.FactImputaResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.FactCab;
import entidades.FactDetalle;
import entidades.FactImputa;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.FactCabFacade;
import persistencia.FactDetalleFacade;
import persistencia.FactImputaFacade;
import persistencia.UsuarioFacade;
import utils.Utils;


/**
 *
 * @author FrancoSili
 */
@Stateless
@Path("imputaciones")
public class ImputacionesRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject FactCabFacade factCabFacade;
    @Inject Utils utils;
    @Inject FactDetalleFacade factDetalleFacade;
    @Inject FactImputaFacade factImputaFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImputaciones(  
        @HeaderParam ("token") String token,
        @QueryParam("idFactCab") Integer idFactCab,    
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            //valido que token no sea null
            if(token == null || token.trim().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Error, token vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Busco el token
            Acceso userToken = accesoFacade.findByToken(token);

            //valido que Acceso no sea null
            if(userToken == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Acceso nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Busco el usuario
            Usuario user = usuarioFacade.getByToken(userToken);

            //valido que el Usuario no sea null
            if(user == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Usuario nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //valido vencimiento token
            if(!accesoFacade.validarToken(userToken, user)) {
                respuesta.setControl(AppCodigo.ERROR, "Credenciales incorrectas");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }
            
            //valido que tenga idFactCab no venga nulo
            if(idFactCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "No selecciono un comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            FactCab factCab = factCabFacade.find(idFactCab);            
            if(factCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "No selecciono un comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            //seteo el nombre del store
            String noombreSP = "call s_comprobantesImputados(?,?,?,?,?)";
            
            //invoco al store
            CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, noombreSP);
            
            //valido que el Procedimiento Almacenado no sea null
            if(callableStatement == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
            callableStatement.setInt(2,factCab.getIdCteTipo().getIdCteTipo());
            callableStatement.setString(3,factCab.getLetra());
            callableStatement.setLong(4, factCab.getNumero());
            callableStatement.setInt(5, factCab.getIdFactCab());
            
            //Ejecuto el procedimiento
            ResultSet rs = callableStatement.executeQuery();
            List<Payload> imputados = new ArrayList<>();
            while (rs.next()) {
                FactImputaResponse factImputa = new FactImputaResponse(
                        rs.getInt("idFactCab"),
                        rs.getInt("idCteTipo"),
                        rs.getString("descCorta"),
                        rs.getLong("numero"),
                        rs.getDate("fechaEmision"),
                        rs.getInt("idPadron"),
                        rs.getInt("idSisTipoOperacion")
                );
                imputados.add(factImputa);
            }
            
            //Cierro la conexion    
            callableStatement.getConnection().close();
            
            //genero la respuesta
            respuesta.setArray(imputados);
            respuesta.setControl(AppCodigo.OK, "Imputaciones");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setImputacion(  
        @HeaderParam ("token") String token,
        @QueryParam("idFactDetalle") Integer idFactDetalle,
        @QueryParam("idFactDetalleImputa") Integer idFactDetalleImputa,
        @QueryParam("cantidadImputada") BigDecimal cantidadImputada,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            //valido que token no sea null
            if(token == null || token.trim().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Error, token vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Busco el token
            Acceso userToken = accesoFacade.findByToken(token);

            //valido que Acceso no sea null
            if(userToken == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Acceso nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Busco el usuario
            Usuario user = usuarioFacade.getByToken(userToken);

            //valido que el Usuario no sea null
            if(user == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Usuario nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //valido vencimiento token
            if(!accesoFacade.validarToken(userToken, user)) {
                respuesta.setControl(AppCodigo.ERROR, "Credenciales incorrectas");
                return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
            }
            
            //valido que tenga idFactCab no venga nulo
            if(idFactDetalle == null) {
                respuesta.setControl(AppCodigo.ERROR, "No selecciono un comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            FactDetalle factDetalleImputador = factDetalleFacade.getFactDetalle(idFactDetalle);
            
            if(factDetalleImputador == null) {
                respuesta.setControl(AppCodigo.ERROR, "No se encontró el comprobante imputador");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idFactDetalleImputa == null) {
                respuesta.setControl(AppCodigo.ERROR, "No selecciono un comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            FactDetalle factDetalleImputado = factDetalleFacade.getFactDetalle(idFactDetalleImputa);
            
            if(factDetalleImputado == null) {
                respuesta.setControl(AppCodigo.ERROR, "No se encontró el comprobante imputado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            FactImputa factImputa = new FactImputa();
            factImputa.setCantidadImputada(cantidadImputada);
            factImputa.setIdFactDetalle(factDetalleImputador);
            factImputa.setIdFactDetalleImputa(factDetalleImputado);
            factImputa.setImporteImputado(BigDecimal.ZERO);
            factImputa.setMasAsiento(0);
            factImputa.setMasAsientoImputado(0);
            Boolean transaccion = factImputaFacade.setFactImputaNuevo(factImputa);
            if(transaccion) {
                respuesta.setControl(AppCodigo.OK, "La imputacion se grabó correctamente");
                return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.ERROR, "La imputacion no se grabó correctamente");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
