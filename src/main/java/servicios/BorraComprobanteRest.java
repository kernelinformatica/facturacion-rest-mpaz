
package servicios;

import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.FactCab;
import entidades.FactDetalle;
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
import persistencia.FactCabFacade;
import persistencia.LoteFacade;
import persistencia.ProdumoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("borraComprobante")
public class BorraComprobanteRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils; 
    @Inject FactCabFacade factCabFacade;
    @Inject ProdumoFacade produmoFacade;
    @Inject LoteFacade loteFacade;
    
    @DELETE
    @Path ("/{idFactCab}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteComprobante(  
        @HeaderParam ("token") String token,
        @PathParam ("idFactCab") Integer idFactCab,
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
            
            if(idFactCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, idFactCab nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            
            FactCab factCab = factCabFacade.find(idFactCab);
            if(factCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, FactCab no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            //elimino Produmo
            if(!factCab.getFactDetalleCollection().isEmpty()) {
                for(FactDetalle d : factCab.getFactDetalleCollection()) {
                    Produmo prod = produmoFacade.findByNroComprobante(d.getIdFactDetalle());
                    if(prod != null) {
                        if(prod.getNroLote() != null) {
                            List<Produmo> produmos = produmoFacade.findByNroLote(prod.getNroLote());
                            //si es distinto de vacio y si solamente hay un lote lo borro
                            if(!produmos.isEmpty() && produmos.size() == 1) {
                                Lote lote = loteFacade.findByNroEmpresaProducto(prod.getNroLote(),user.getIdPerfil().getIdSucursal().getIdEmpresa(),prod.getIdProductos());
                                if(lote != null) {
                                    boolean transaccionLote;
                                    transaccionLote = loteFacade.deleteLote(lote);
                                    if(!transaccionLote) {
                                        respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo borrar el lote");
                                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                                    }        
                                }
                            }
                        }
                        boolean transaccion1;
                        transaccion1 = produmoFacade.deleteProdumo(prod);
                        if(!transaccion1) {
                            respuesta.setControl(AppCodigo.ERROR, "Error, idFactCab nulo");
                            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                        }
                    }
                }
            }
            
            transaccion = factCabFacade.deleteFactCab(factCab); 
            if(!transaccion){
               respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo borrar el comprobante");
               return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            respuesta.setControl(AppCodigo.OK, "Comprobante borrado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
