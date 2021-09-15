package servicios;

import datos.AppCodigo;
import datos.Payload;
import datos.ServicioResponse;
import datos.SisTipoOperacionResponse;
import entidades.Acceso;
import entidades.SisModulo;
import entidades.SisTipoOperacion;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.SisModuloFacade;
import persistencia.SisTipoOperacionFacade;
import persistencia.UsuarioFacade;

/**
 *
 * @author DarioQuiroga
 */
@Stateless
@Path("sisTipoOperacion")
public class SisTipoOperacionRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject SisTipoOperacionFacade sisTipoOperacionFacade;
    @Inject SisModuloFacade sisModuloFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSisMonedas(  
        @HeaderParam ("token") String token,
        @QueryParam("sisModulo") Integer idSisModulo,
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
            
            List<Payload> sisTipo = new ArrayList<>();
            
            if(idSisModulo != null) {
                SisModulo sisModulo = sisModuloFacade.find(idSisModulo);

                if(sisModulo == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe sisModulo con ese id");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Valido que la lista de SisFormaPago no este vacia.
                if(sisModulo.getSisTipoOperacionCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No tipo de operaciones disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //busco los SubRubros de la empresa del usuario
                for(SisTipoOperacion s : sisModulo.getSisTipoOperacionCollection()) {
                    SisTipoOperacionResponse sr = new SisTipoOperacionResponse(s);
                    sisTipo.add(sr);
                }
            } else {           
                List<SisTipoOperacion> sisTipoOperacion = sisTipoOperacionFacade.getTodos();

                //Valido que la lista de SisFormaPago no este vacia.
                if(sisTipoOperacion.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay tipo de operaciones disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                for(SisTipoOperacion s : sisTipoOperacion) {
                    SisTipoOperacionResponse sr = new SisTipoOperacionResponse(s);
                    sisTipo.add(sr);
                }
            }
            respuesta.setArraydatos(sisTipo);
            respuesta.setControl(AppCodigo.OK, "Lista de Operaciones");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
