package servicios;

import datos.AppCodigo;
import datos.Payload;
import datos.ServicioResponse;
import datos.SisLetraSisCodAfipResponse;
import entidades.Acceso;
import entidades.CteTipo;
import entidades.CteTipoSisLetra;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.UsuarioFacade;

/**
 *
 * @author FrancoSili
 */
@Stateless
@Path("letraCodigo")
public class SisLetraCodigoAfipRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLetraCodigo(  
        @HeaderParam ("token") String token,
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
            
            //Valido que halla 
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getCteTipoCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay tipos de comprobantes disponibles");
                return Response.status(Response.Status.NOT_FOUND).entity(respuesta.toJson()).build();
            }
           
            //Armo la respuesta
            List<Payload> resp = new ArrayList<>();
            for(CteTipo c : user.getIdPerfil().getIdSucursal().getIdEmpresa().getCteTipoCollection()) {
                if(c.getIdSisComprobante().getPropio() == 1) {
                    for(CteTipoSisLetra cl : c.getCteTipoSisLetraCollection()) {
                        SisLetraSisCodAfipResponse sl = new SisLetraSisCodAfipResponse(cl);
                        resp.add(sl);
                    }
                }
            }
            
            respuesta.setArraydatos(resp);
            respuesta.setControl(AppCodigo.OK, "Lista de Comprobantes y letras");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();       
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
