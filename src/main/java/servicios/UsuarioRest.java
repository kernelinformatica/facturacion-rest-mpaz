package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.LoginResponse;
import datos.Payload;
import datos.PermisosResponse;
import datos.PtoVentaResponse;
import datos.ServicioResponse;
import datos.UsuarioResponse;
import entidades.Acceso;
import entidades.ListaPrecio;
import entidades.MenuSucursal;
import entidades.Perfil;
import entidades.Sucursal;
import entidades.Permiso;
import entidades.PtoVenta;
import entidades.Sucursal;
import entidades.Usuario;
import entidades.UsuarioListaPrecio;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.digest.DigestUtils;
import persistencia.AccesoFacade;
import persistencia.ListaPrecioFacade;
import persistencia.ParametroGeneralFacade;
import persistencia.PerfilFacade;
import persistencia.PtoVentaFacade;
import persistencia.SucursalFacade;
import persistencia.UsuarioFacade;
import persistencia.UsuarioListaPrecioFacade;
import utils.Utils;

/**
 *
 * @author Kernel Informática
 */
@Stateless
@Path("usuarios")
public class UsuarioRest {
    
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject PerfilFacade perfilFacade;
    @Inject SucursalFacade sucursalFacade;
    @Inject ParametroGeneralFacade parametros;
    @Inject Utils servicioUtils;
    @Inject ListaPrecioFacade listaPrecioFacade;
    @Inject UsuarioListaPrecioFacade usuarioListaPrecioFacade;
    @Inject PtoVentaFacade ptoVentaFacade;
    
    @POST
    @Path("/{usuario}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(@PathParam("usuario") String cuenta, @HeaderParam("clave") String clave, 
            @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        
        //Valido los datos de ingreso
        if (cuenta == null || clave == null || cuenta.trim().isEmpty() || clave.trim().isEmpty()) {
            respuesta.setControl(AppCodigo.ERROR, "Error en los parametros");
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
        //Busco el usuario en la base de datos        
        Usuario usuario = usuarioFacade.getByCuenta(cuenta);
        System.out.println("usuario");
        System.out.println(usuario);
        //Si no se encuentra, devuelvo error
        if (usuario == null) {
            respuesta.setControl(AppCodigo.ERROR, "Usuario Nulo: Usuario o contraseña invalida "+usuario+" - "+clave);
            return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
        }
        System.out.println("claveMandada: ");
        System.out.println(clave);
        System.out.println("Usuario: ");
        System.out.println(usuario.getUsuario());
        System.out.println("claveUser: ");
        System.out.println(usuario.getClave());
        
        // Si la clave es correcta
        if (usuario.getClave().equals(clave)) {
            // Si está todo ok, genero token y completo el response
            entidades.Acceso acceso = accesoFacade.pedirToken(usuario);
            
            System.out.println("::::: Acceso ::::");
            System.out.println(acceso);
            
            // Genero la respuesta
            LoginResponse lr = new LoginResponse(usuario, acceso);
            if(!usuario.getIdPerfil().getPermisoCollection().isEmpty()) {
                lr.getPerfil().getSucursal().agregarPermisos(usuario.getIdPerfil().getPermisoCollection());               
            }
            
            //Ordeno la lista
            Collections.sort(lr.getPerfil().getSucursal().getPermisos(), (o1, o2) -> o1.getMenu().getOrden().compareTo(o2.getMenu().getOrden()));
            respuesta.setDatos(lr);
            respuesta.setControl(AppCodigo.OK, "");
        } else {
            //Usuario existe pero está mal la clave, se devuelve mismo error que si no existiera
            respuesta.setControl(AppCodigo.ERROR, "Contraseña invalida "+usuario+"-"+clave);
            return Response.status(Response.Status.UNAUTHORIZED).entity(respuesta.toJson()).build();
        }
         return Response.ok(respuesta.toJson(), MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setUsuario( 
        @HeaderParam("clave") String clave, 
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            String nombre = (String) Utils.getKeyFromJsonObject("nombre", jsonBody, "String");
            String apellido = (String) Utils.getKeyFromJsonObject("apellido", jsonBody, "String");
            String username = (String) Utils.getKeyFromJsonObject("usuario", jsonBody, "String");
            Integer perfil = (Integer) Utils.getKeyFromJsonObject("perfil", jsonBody, "Integer");
            String telefono = (String) Utils.getKeyFromJsonObject("telefono", jsonBody, "String");
            String mail = (String) Utils.getKeyFromJsonObject("mail", jsonBody, "String");
            Integer idPtoVenta = (Integer) Utils.getKeyFromJsonObject("idPtoVenta", jsonBody, "Integer");
            String observ = (String) Utils.getKeyFromJsonObject("observaciones", jsonBody, "String");
            List<JsonElement> listasPrecios = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("listaPrecios", jsonBody, "ArrayList");

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

            //Me fijo que clave, nombre y perfil no sean nulos
            if(nombre == null || clave == null || perfil == 0 || username == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Concateno el nombre con el prefijo de la empresa
            String nombreConcatenado = user.getIdPerfil().getIdSucursal().getIdEmpresa().getPrefijoEmpresa().concat("-"+username);
            Usuario usuario = usuarioFacade.getByUsuarioLogin(username);
            //usuario.getIdPerfil().getIdSucursal().getIdEmpresa()
            if(usuario != null) {
                respuesta.setControl(AppCodigo.ERROR, "El usuario ya existe: "+username);
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Me fijo si hay un perfil 
            Perfil perfilEncontrado = perfilFacade.getPerfilById(perfil);
            if(perfilEncontrado == null) {
                respuesta.setControl(AppCodigo.ERROR, "El perfil no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
                    
            boolean transaccion;
            Usuario newUser = new Usuario();
            newUser.setClave(clave);
            newUser.setIdPerfil(perfilEncontrado);
            newUser.setMail(mail);
            newUser.setNombre(nombre);
            newUser.setTelefono(telefono);
            newUser.setIdPtoVenta(idPtoVenta);
            newUser.setApellido(apellido);
            newUser.setUsuario(nombreConcatenado);
            newUser.setObservaciones(observ);
            transaccion = usuarioFacade.setUsuarioNuevo(newUser);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el usuario");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(listasPrecios != null) {
                for(JsonElement j : listasPrecios) {
                    Integer idListaPrecio = (Integer) Utils.getKeyFromJsonObject("idListaPrecio", j.getAsJsonObject(),"Integer");
                    ListaPrecio listaPrecio = listaPrecioFacade.find(idListaPrecio);            
                    //Pregunto si existe el la lista de precios
                    if(listaPrecio == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No existe la lista de precios");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    UsuarioListaPrecio lf = new UsuarioListaPrecio();
                    lf.setIdUsuarios(newUser);
                    lf.setIdListaPrecios(listaPrecio);
                    boolean transaccion2;
                    transaccion2 = usuarioListaPrecioFacade.setUsuarioListaPrecioNuevo(lf);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo crear el usuario con la lista de precios" );
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            
            String cuerpoMail = "Usuario Creado con Exito!!! \n"+ "Nombre de Usuario: " + nombreConcatenado + "\n";
            //envio el mail
            servicioUtils.enviarMail(
                    parametros.get("KERNEL_SMTP_USER"),
                    parametros.get("KERNEL_SMTP_NOMBRE"),
                    mail,
                    cuerpoMail,
                    "Credenciales de acceso",
                    nombre);
            respuesta.setControl(AppCodigo.OK, "Usuario creado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }   
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarios(  
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
            
            //valido que la lista de usuarios no este vacia
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getSucursalCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay usuarios para esa empresa");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            List<Payload> listaUsuariosResponse = new ArrayList<>();
            //genero la respuesta
            for(Sucursal s : user.getIdPerfil().getIdSucursal().getIdEmpresa().getSucursalCollection()) {
                for(Perfil p : s.getPerfilCollection()) {
                    for(Usuario u : p.getUsuarioCollection()) {
                        UsuarioResponse parseUser = new UsuarioResponse(u);
                        if(u.getUsuarioListaPrecioCollection() != null && !u.getUsuarioListaPrecioCollection().isEmpty()) {
                            parseUser.agregarListaPrecios(u.getUsuarioListaPrecioCollection());
                        }
                        if(u.getIdPtoVenta() == null && !s.getCteNumeroCollection().isEmpty()) {
                            parseUser.agregarPtoVentas(s.getCteNumeroCollection());
                        } else if(u.getIdPtoVenta() != null && !s.getCteNumeroCollection().isEmpty()){
                            PtoVenta ptoVentaObj = ptoVentaFacade.find(u.getIdPtoVenta());
                            if(ptoVentaObj == null) {
                                respuesta.setControl(AppCodigo.ERROR, "No existe el ptoVenta asignado al Usuario");
                                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                            }
                            PtoVentaResponse r = new PtoVentaResponse(ptoVentaObj);
                            parseUser.getPtoVentas().add(r);
                        }
                        listaUsuariosResponse.add(parseUser);
                    }
                }
            }
            respuesta.setArraydatos(listaUsuariosResponse);
            respuesta.setControl(AppCodigo.OK, "Lista de usuarios");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUsuarios(  
        @HeaderParam ("token") String token,
        @HeaderParam("clave") String clave,   
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idUsuario = (Integer) Utils.getKeyFromJsonObject("idUsuario", jsonBody, "Integer");
            String nombre = (String) Utils.getKeyFromJsonObject("nombre", jsonBody, "String");
            String apellido = (String) Utils.getKeyFromJsonObject("apellido", jsonBody, "String");
            String username = (String) Utils.getKeyFromJsonObject("usuario", jsonBody, "String");
            Integer perfil = (Integer) Utils.getKeyFromJsonObject("perfil", jsonBody, "Integer");
            String telefono = (String) Utils.getKeyFromJsonObject("telefono", jsonBody, "String");
            String mail = (String) Utils.getKeyFromJsonObject("mail", jsonBody, "String");
            String observ = (String) Utils.getKeyFromJsonObject("observaciones", jsonBody, "String");
            Integer idPtoVenta = (Integer) Utils.getKeyFromJsonObject("idPtoVenta", jsonBody, "Integer");
            
            
            
            List<JsonElement> listasPrecios = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("listaPrecios", jsonBody, "ArrayList");

            
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
            
            //busco el usuario a editar
            Usuario usuario = usuarioFacade.getByIdUsuario(idUsuario);
            if(usuario == null) {
                respuesta.setControl(AppCodigo.ERROR, "El usuario no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            //Me fijo si hay un perfil 
            Perfil perfilEncontrado = perfilFacade.getPerfilById(perfil);
            if(perfilEncontrado == null) {
                respuesta.setControl(AppCodigo.ERROR, "El perfil no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            usuario.setClave(clave);
            usuario.setIdPerfil(perfilEncontrado);
            usuario.setMail(mail);
            usuario.setNombre(nombre);
            usuario.setTelefono(telefono);
            usuario.setIdPtoVenta(idPtoVenta);
            usuario.setUsuario(username);
            usuario.setApellido(apellido);
            usuario.setObservaciones(observ);
            usuario.getUsuarioListaPrecioCollection().clear();
            transaccion = usuarioFacade.editUsuario(usuario);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el usuario");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(listasPrecios != null) {
                for(JsonElement j : listasPrecios) {
                    Integer idListaPrecio = (Integer) Utils.getKeyFromJsonObject("idListaPrecio", j.getAsJsonObject(),"Integer");
                    ListaPrecio listaPrecio = listaPrecioFacade.find(idListaPrecio);            
                    //Pregunto si existe el la lista de precios
                    if(listaPrecio == null) {
                        respuesta.setControl(AppCodigo.ERROR, "No existe la lista de precios");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    
                    UsuarioListaPrecio lf = new UsuarioListaPrecio();
                    lf.setIdUsuarios(usuario);
                    lf.setIdListaPrecios(listaPrecio);
                    boolean transaccion2;
                    transaccion2 = usuarioListaPrecioFacade.editUsuarioListaPrecio(lf);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el usuario con la lista de precios" );
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            
            respuesta.setControl(AppCodigo.OK, "Usuario editado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }  
    }
    
    @DELETE
    @Path ("/{idUsuario}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteUsuarios(  
        @HeaderParam ("token") String token,
        @PathParam ("idUsuario") int idUsuario,
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
            
            //busco el usuario a borrar
            Usuario usuario = usuarioFacade.getByIdUsuario(idUsuario);
            if(usuario == null) {
                respuesta.setControl(AppCodigo.ERROR, "El usuario no existe");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = usuarioFacade.deleteUsuario(usuario);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar el usuario");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Usuario borrado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
