package servicios;

import com.google.common.io.ByteStreams;
import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Contrato;
import entidades.Usuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;
import persistencia.AccesoFacade;
import persistencia.ContratoFacade;
import persistencia.UsuarioFacade;

/**
 *
 * @author FrancoSili
 */


@Stateless
@Path("descargarContrato")
public class DescargarContratoRest {
    
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject ContratoFacade contratoFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(  
        @HeaderParam ("token") String token,
        @QueryParam("idContrato") Integer idContrato) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
            
            Contrato contrato = contratoFacade.find(idContrato);
            if(contrato == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el contrato ingresado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }
            
            String fileLocation = contrato.getDirectorio();
            File file = new File(fileLocation);
            System.out.println(file.getName());
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
                    .build();
//            String fileLocation = contrato.getDirectorio();
//            String nombreArchivo = user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa() + "-" + contrato.getContratoNro()+ ".docx";
//            InputStream archivo = new FileInputStream(new File(fileLocation));
//            byte[] bytes = ByteStreams.toByteArray(archivo);
//            System.out.println(bytes);
//            return Response.ok(bytes).type("application/vnd.openxmlformats-officedocument.wordprocessingml.document").header("Content-Disposition", "form-data;  name=\"file\"; filename=\"" +nombreArchivo+ "\"").build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
