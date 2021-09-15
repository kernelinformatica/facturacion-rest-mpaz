
package servicios;

import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.OrdenesPagosPCab;
import entidades.OrdenesPagosDetalle;
import entidades.Lote;
import entidades.Produmo;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.OrdenesPagosPCabFacade;
import persistencia.LoteFacade;

import persistencia.ProdumoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Dario Quiroga
 */

@Stateless
@Path("borraComprobanteOrdenPago")
public class BorraComprobanteOrdenPagoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils; 
    @Inject OrdenesPagosPCabFacade ordenPagoCabFacade;
    @Inject ProdumoFacade produmoFacade;
    
    
    @DELETE
    @Path ("/{idOpCab}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteComprobante(  
        @HeaderParam ("token") String token,
        @PathParam ("idOpCab") Integer idOpCab,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            //valido que token y el id no sea null
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
            
            if(idOpCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, idOpCab es nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            
            OrdenesPagosPCab ordenPagosPCab = ordenPagoCabFacade.find(idOpCab);
            if(idOpCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, Orden de pago no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            transaccion = ordenPagoCabFacade.deleteOrdenPagoCab(ordenPagosPCab); 
            if(!transaccion){
               respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo borrar la orden de pago");
               return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            respuesta.setControl(AppCodigo.OK, "Orden de Pago Nro: "+idOpCab+", borrada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
