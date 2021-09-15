package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.DepositoResponse;
import datos.Payload;
import datos.ServicioResponse;
import datos.TiposOpDepositosResponse;
import entidades.Acceso;
import entidades.Deposito;
import entidades.TiposOpDepositos;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.DepositoFacade;
import persistencia.TiposOpDepositosFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Franco Sili
 */

@Stateless
@Path("deposito")
public class DepositoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject DepositoFacade depositoFacade;
    @Inject Utils utils; 
    @Inject TiposOpDepositosFacade tiposOpDepositosFacade;
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDepositos(  
        @HeaderParam ("token") String token,
        @QueryParam ("todos") boolean todos,    
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
            
            List<Payload> depositos = new ArrayList<>();
            if(!todos) {
                //valido que la sucursal tenga Depositos disponibles
                /* consulta de datos via objetos java 
                if(user.getIdPerfil().getIdSucursal().getDepositoCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay Depositos disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //busco los depositos de la sucursal del usuario
                for(Deposito p : user.getIdPerfil().getIdSucursal().getDepositoCollection()){
                    DepositoResponse fp = new DepositoResponse(p);
                    depositos.add(fp);
                }
                */
                 
                // consulta de datos via store procedure de base de datos
               String nombreSP = "call s_buscaDepositos(?,?,?,?)";
               CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, nombreSP);
               //valido que el Procedimiento Almacenado no sea null
               if(callableStatement == null) {
                   respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento, "+callableStatement+" | "+user.getIdPerfil().getIdSucursal().getIdSucursal());
                   return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                //Seteo los parametros del SP
              
               callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
               callableStatement.setInt(2, user.getIdPerfil().getIdSucursal().getIdSucursal());
               callableStatement.setInt(3, 0);
               callableStatement.setInt(4,user.getIdUsuarios());
               ResultSet rs = callableStatement.executeQuery();
                  while (rs.next()) {
                       DepositoResponse dep = new DepositoResponse(
                               rs.getInt("idDeposito"),
                               rs.getInt("codigoDep"),
                               rs.getString("descripcion"),
                               rs.getString("domicilio"),
                               rs.getString("codigoPostal"));
                       depositos.add(dep);
                   }
             
                callableStatement.getConnection().close();
            } else {
                //valido que la empresa tenga Depositos disponibles
                
                /*if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getDepositoCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay Depositos disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //busco los depositos de la empresa del usuario
                
                for(Deposito p : user.getIdPerfil().getIdSucursal().getIdEmpresa().getDepositoCollection()){
                    DepositoResponse fp = new DepositoResponse(p);
                    depositos.add(fp);
                }*/
               
                
               // consulta de datos via store procedure de base de datos
              
               String nombreSP = "call s_buscaDepositos(?,?,?,?)";
               CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, nombreSP);
               //valido que el Procedimiento Almacenado no sea null
               if(callableStatement == null) {
                   respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                   return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
               //Seteo los parametros del SP
               callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
               callableStatement.setInt(2,  user.getIdPerfil().getIdSucursal().getIdSucursal());
               callableStatement.setInt(3, 0);
               callableStatement.setInt(4,user.getIdUsuarios());
             //Integer aInt, Integer aInt0, String string, String string0, String string1
               ResultSet rs = callableStatement.executeQuery();
                   while (rs.next()) {
                       DepositoResponse dep = new DepositoResponse(
                               rs.getInt("idDeposito"),
                               rs.getInt("codigoDep"),
                               rs.getString("descripcion"),
                               rs.getString("domicilio"),
                               rs.getString("codigoPostal"));
                       depositos.add(dep);
                   }
               
               
               
               
               
               
              
               callableStatement.getConnection().close();
               
            }
            // Dario
            respuesta.setArraydatos(depositos);
            respuesta.setControl(AppCodigo.OK, "Lista de Depositos");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDepositos(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer codigoDep = (Integer) Utils.getKeyFromJsonObject("codigoDep", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            String domicilio = (String) Utils.getKeyFromJsonObject("domicilio", jsonBody, "String");
            String codigoPostal = (String) Utils.getKeyFromJsonObject("codigoPostal", jsonBody, "String");
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
           
            //valido que tenga Rubros disponibles
            if(codigoDep == 0 || descripcion == null || domicilio == null || codigoPostal == null) {
                respuesta.setControl(AppCodigo.ERROR, "Algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            } 
            boolean transaccion;            
            Deposito deposito = new Deposito();
            deposito.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            deposito.setCodigoDep(codigoDep);
            deposito.setCodigoPostal(codigoPostal);
            deposito.setDescripcion(descripcion);
            deposito.setDomicilio(domicilio);
            deposito.setIdSucursal(user.getIdPerfil().getIdSucursal());
            transaccion = depositoFacade.setDepositoNuevo(deposito);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Deposito, clave primaria repetida");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Deposito creado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editDepositos(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idDeposito = (Integer) Utils.getKeyFromJsonObject("idDeposito", jsonBody, "Integer");
            Integer codigoDep = (Integer) Utils.getKeyFromJsonObject("codigoDep", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            String domicilio = (String) Utils.getKeyFromJsonObject("domicilio", jsonBody, "String");
            String codigoPostal = (String) Utils.getKeyFromJsonObject("codigoPostal", jsonBody, "String");
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
            
            //valido que los campos del body no vengan nulos
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getDepositoCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay Depositos disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //valido que tenga Rubros disponibles
            if(codigoDep == 0 || descripcion == null || domicilio == null || codigoPostal == null || idDeposito == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Pregunto si el deposito existe
            Deposito deposito = depositoFacade.find(idDeposito);
            if(deposito == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el deposito");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;            
            deposito.setCodigoDep(codigoDep);
            deposito.setCodigoPostal(codigoPostal);
            deposito.setDescripcion(descripcion);
            transaccion = depositoFacade.editDeposito(deposito);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el Deposito");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Deposito editado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @DELETE
    @Path ("/{idDeposito}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteSubRubro(  
        @HeaderParam ("token") String token,
        @PathParam ("idDeposito") int idDeposito,
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
            
            //Me fijo que descCorta y idRubro no sean nulos
            if(idDeposito == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            Deposito deposito = depositoFacade.find(idDeposito);
            
            //Pregunto si existe el Producto
            if(deposito == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el Deposito");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = depositoFacade.deleteDeposito(deposito);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar el Deposito");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Deposito borrado con exito");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }

    @GET
    @Path("/tiposop")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response getTiposOpDepositos(
            @HeaderParam ("token") String token,
            @Context HttpServletRequest request) {
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
            List<TiposOpDepositos> depositosList = tiposOpDepositosFacade.getTiposOpDepositos();
            List<Payload> depositos = new ArrayList<>();
            for(TiposOpDepositos dep : depositosList) {
                TiposOpDepositosResponse tempObj = new TiposOpDepositosResponse(dep.getIdTipoOpDeposito(), dep.getIdSisTipoOperacion().getIdSisTipoOperacion(), dep.getIdDepositos().getIdDepositos());
                depositos.add(tempObj);
            }
            respuesta.setArraydatos(depositos);
            respuesta.setControl(AppCodigo.OK, "Lista de Depositos");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
