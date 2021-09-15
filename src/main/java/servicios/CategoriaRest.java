package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.CategoriaResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Categoria;
import entidades.SisCategoria;
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.CategoriaFacade;
import persistencia.SisCategoriaFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("categorias")
public class CategoriaRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject CategoriaFacade categoriaFacade;
    @Inject SisCategoriaFacade sisCategoriaFacade;
        
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategorias(  
        @HeaderParam ("token") String token,
        @QueryParam("idSisCategoria") Integer idSisCategoria,
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
            
            List<Payload> categorias = new ArrayList<>();            
            
            if(idSisCategoria == null) {
                if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getCategoriaCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay categorias disponibles");
                    return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
                }
                for(Categoria c : user.getIdPerfil().getIdSucursal().getIdEmpresa().getCategoriaCollection()) {
                    CategoriaResponse sr = new CategoriaResponse(c);
                    categorias.add(sr);
                }
            } else {
                SisCategoria sisCatego = sisCategoriaFacade.find(idSisCategoria);                
                if(sisCatego == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe sisCategoria");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                List<Categoria> catego = categoriaFacade.findByEmpresaSisCategoria(user.getIdPerfil().getIdSucursal().getIdEmpresa(), sisCatego);
                if( catego == null || catego.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay categorias disponibles");
                    return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
                }
                
                for(Categoria c :catego) {
                    CategoriaResponse sr = new CategoriaResponse(c);
                    categorias.add(sr);
                }               
            }
            respuesta.setArraydatos(categorias);
            respuesta.setControl(AppCodigo.OK, "Lista de Categorias");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setCategoria(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idSisCategoria = (Integer) Utils.getKeyFromJsonObject("idSisCategoria", jsonBody, "Integer");
            Integer codigo = (Integer) Utils.getKeyFromJsonObject("codigo", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            
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

            //Me fijo que  descripcion, idRubro e idEmpresa no sean nulos
            if(descripcion == null || codigo == null || idSisCategoria == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisCategoria sisCategoria = sisCategoriaFacade.find(idSisCategoria);
            if(sisCategoria == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe sis Categoria");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            Categoria categoria = new Categoria();
            categoria.setDescripcion(descripcion);           
            categoria.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            categoria.setIdSisCategoria(sisCategoria);
            categoria.setCodigo(codigo);
            transaccion = categoriaFacade.setCategoriaNueva(categoria);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la Categoria");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.CREADO, "Categoria creada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
}
