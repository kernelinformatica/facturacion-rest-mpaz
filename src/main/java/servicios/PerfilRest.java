package servicios;

import datos.AppCodigo;
import datos.Payload;
import datos.PerfilResponse;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Perfil;
import entidades.Sucursal;
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.PerfilFacade;
import persistencia.SucursalFacade;
import persistencia.UsuarioFacade;

/**
 *
 * @author FrancoSili
 */
@Stateless
@Path("perfiles")
public class PerfilRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject PerfilFacade perfilFacade;
    @Inject SucursalFacade sucursalFacade;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerfiles(  
        @HeaderParam ("token") String token,    
        @Context HttpServletRequest request,
        @QueryParam ("sucursal") Integer idSucursal
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            //JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            //Integer idSucursal = (Integer) Utils.getKeyFromJsonObject("idSucursal", jsonBody, "Integer");
            //valido que token no sea null
            if(token == null || idSucursal == 0 ||token.trim().isEmpty()) {
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
            
            Sucursal sucursal = sucursalFacade.getByIdSucursal(idSucursal);
            
            //valido que exista la sucursal
            if(sucursal == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe esa sucursal");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //valido que tenga perfiles disponibles
            if(sucursal.getPerfilCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay perfiles disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //busco los perfiles de la sucursal del usuario
            List<Payload> perfiles= new ArrayList<>();
            for(Perfil p : sucursal.getPerfilCollection()){
                PerfilResponse pr = new PerfilResponse(p);
                perfiles.add(pr);
            }
            respuesta.setArraydatos(perfiles);
            respuesta.setControl(AppCodigo.OK, "Lista de perfiles");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}    
