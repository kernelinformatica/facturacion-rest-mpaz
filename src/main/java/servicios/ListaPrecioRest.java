package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ContPlanCuentaResponse;
import datos.FormaPagoDetResponse;
import datos.FormaPagoResponse;
import datos.ListaPreciosResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.ContPlanCuenta;
import entidades.CtacteCategoria;
import entidades.FormaPagoDet;
import entidades.ListaPrecio;
import entidades.ListaPrecioDet;
import entidades.Padron;
import entidades.Producto;
import entidades.SisMonedas;
import entidades.Usuario;
import entidades.UsuarioListaPrecio;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
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
import persistencia.ContPlanCuentaFacade;
import persistencia.CtacteCategoriaFacade;
import persistencia.ListaPrecioDetFacade;
import persistencia.ListaPrecioFacade;
import persistencia.PadronFacade;
import persistencia.ProductoFacade;
import persistencia.SisMonedasFacade;
import persistencia.UsuarioFacade;
import persistencia.UsuarioListaPrecioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("listaPrecios")
public class ListaPrecioRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject SisMonedasFacade sisMonedasFacade;
    @Inject ListaPrecioFacade listaPrecioFacade;
    @Inject ProductoFacade productoFacade;
    @Inject ListaPrecioDetFacade listaPrecioDetFacade; 
    @Inject ContPlanCuentaFacade contPlanCuentaFacade;
    @Inject UsuarioListaPrecioFacade usuarioListaPrecioFacade;
    @Inject PadronFacade padronFacade;
    @Inject CtacteCategoriaFacade ctacteCategoriaFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListaPrecios(  
        @HeaderParam ("token") String token,
        @QueryParam("codPadron") Integer codPadron,
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
            
            //Valido que haya Listas de precios para esa empresa.
            if(user.getUsuarioListaPrecioCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay Listas de precios disponibles para el usuario logueado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //busco los SubRubros de la empresa del usuario
            List<Payload> listaPreciosResponse = new ArrayList<>();
            for(UsuarioListaPrecio ulp : user.getUsuarioListaPrecioCollection()) {
                ListaPreciosResponse sr = new ListaPreciosResponse(ulp.getIdListaPrecios());
                if(!ulp.getIdListaPrecios().getListaPrecioDetCollection().isEmpty()) {
                    sr.agregarListaPrecioDet(ulp.getIdListaPrecios().getListaPrecioDetCollection());
                }
                if(!ulp.getIdListaPrecios().getListaPrecioFormaPagoCollection().isEmpty()) {
                    sr.agregarFormasPago(ulp.getIdListaPrecios().getListaPrecioFormaPagoCollection());
                   
                    for(FormaPagoResponse p : sr.getFormasPago()) {
                        if(!p.getFormaPagoDet().isEmpty()) {
                            for(FormaPagoDetResponse d : p.getFormaPagoDet() ){
                                if(d.getCtaContable() == null) {
                                    continue;
                                }
                                ContPlanCuenta cont = new ContPlanCuenta();
                                if(!d.getCtaContable().equals("0")) {
                                    cont = contPlanCuentaFacade.getCuentaContable(Integer.parseInt(d.getCtaContable()));
                                } else if(codPadron != null){
                                    Padron padron = padronFacade.getPadronByCodigo(codPadron);
                                    if(padron != null && padron.getPadronCatego() != null) {
                                        CtacteCategoria categoria = ctacteCategoriaFacade.getCategoriaByCodigo(padron.getPadronCatego());
                                        if(categoria != null && categoria.getPlanCuentas() != null) {
                                            cont = contPlanCuentaFacade.getCuentaContable(categoria.getPlanCuentas());
                                        //categoria no encontrada o categoria con plan cuentas nula en sybase
                                        } else {
                                            cont = null;
                                        }
                                    //Padron no encontrado o no tiene categoria cargada en base sybase
                                    } else {
                                        cont = null;
                                    } 
                                //Padron no ingresado desde compra o venta
                                } else {
                                    cont = null;
                                }
                                //Me fijo si la cuenta es nula, de ser asi armo una como este en forma de pago
                                if(cont != null){
                                    d.setPlanCuenta(new ContPlanCuentaResponse(cont));
                                } else {
                                    ContPlanCuentaResponse conta = new ContPlanCuentaResponse(Integer.parseInt(d.getCtaContable()),d.getDetalle());
                                    d.setPlanCuenta(conta);
                                }                                                                
                            }
                        }
                    }                                       
                }
                listaPreciosResponse.add(sr);
            }
            respuesta.setArraydatos(listaPreciosResponse);
            respuesta.setControl(AppCodigo.OK, "Lista de Precios");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setListaPrecio(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer codLista = (Integer) Utils.getKeyFromJsonObject("codLista", jsonBody, "Integer");
            Date fechaAlta = (Date) Utils.getKeyFromJsonObject("fechaAlta", jsonBody, "Date");
            Date vigenciaDesde = (Date) Utils.getKeyFromJsonObject("vigenciaDesde", jsonBody, "Date");
            Date vigenciaHasta = (Date) Utils.getKeyFromJsonObject("vigenciaHasta", jsonBody, "Date");
            boolean activa = (Boolean) Utils.getKeyFromJsonObject("activa", jsonBody, "boolean");
            Integer idPadronCliente = (Integer) Utils.getKeyFromJsonObject("idPadronCliente", jsonBody, "Integer");
            Integer idPadronRepresentante = (Integer) Utils.getKeyFromJsonObject("idPadronRepresentante", jsonBody, "Integer");
            BigDecimal porc1 = (BigDecimal) Utils.getKeyFromJsonObject("porc1", jsonBody, "BigDecimal");
            String condiciones = (String) Utils.getKeyFromJsonObject("condiciones", jsonBody, "String");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            List<JsonElement> preciosDet = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("preciosDet", jsonBody, "ArrayList");           
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
            if(codLista == null || idPadronCliente == null || idPadronRepresentante == null || porc1 == null || condiciones == null || idMoneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta en nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            SisMonedas sisMonedas = sisMonedasFacade.find(idMoneda);
            //Pregunto si existe SisMonedas
            if(sisMonedas == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Moneda");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Valido fechas
             if(fechaAlta.after(vigenciaDesde) || vigenciaHasta.before(vigenciaDesde) ) {
                respuesta.setControl(AppCodigo.ERROR, "Error, vigenciaDesde debe ser igual o mayor que fechaAlta y debe ser igual o menor a vigenciaHasta.");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            boolean transa;
            ListaPrecio listaPrecio = new ListaPrecio();
            UsuarioListaPrecio usuarioLista = new UsuarioListaPrecio();
            
            listaPrecio.setCodigoLista(codLista);
            listaPrecio.setFechaAlta(fechaAlta);
            listaPrecio.setVigenciaDesde(vigenciaDesde);
            listaPrecio.setVigenciaHasta(vigenciaHasta);
            listaPrecio.setActiva(activa);
            listaPrecio.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            listaPrecio.setIdPadronCliente(idPadronCliente);
            listaPrecio.setIdPadronRepresentante(idPadronRepresentante);
            listaPrecio.setPorc1(porc1);
            listaPrecio.setCondiciones(condiciones);
            listaPrecio.setIdMoneda(sisMonedas);
            
            usuarioLista.setIdListaPrecios(listaPrecio);
            usuarioLista.setIdUsuarios(user);
            
            transaccion = listaPrecioFacade.setListaPrecioNuevo(listaPrecio);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta la Lsita de Precios, clave primaria repetida");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            transa = usuarioListaPrecioFacade.setUsuarioListaPrecioNuevo(usuarioLista);
            if(!transa) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo asignar la lista de precios al usuario logueado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(preciosDet.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Lista de Precios sin detalles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            for(JsonElement j : preciosDet) {
                boolean transaccion2;
                ListaPrecioDet listaPrecioDet = new ListaPrecioDet();
                BigDecimal precio = (BigDecimal) Utils.getKeyFromJsonObject("precio", j.getAsJsonObject(), "BigDecimal");
                BigDecimal cotaInf = (BigDecimal) Utils.getKeyFromJsonObject("cotaInf", j.getAsJsonObject(), "BigDecimal");
                BigDecimal cotaSup = (BigDecimal) Utils.getKeyFromJsonObject("cotaSup", j.getAsJsonObject(), "BigDecimal");
                BigDecimal cotaInfPorc = (BigDecimal) Utils.getKeyFromJsonObject("cotaInfPorc", j.getAsJsonObject(), "BigDecimal");
                BigDecimal cotaSupPorc = (BigDecimal) Utils.getKeyFromJsonObject("cotaSupPorc", j.getAsJsonObject(), "BigDecimal");
                String observaciones = (String) Utils.getKeyFromJsonObject("observaciones", j.getAsJsonObject(), "String");
                Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", j.getAsJsonObject(), "Integer"); 
                if(precio == null || cotaInf == null || cotaSup == null || idProducto == 0) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                Producto producto = productoFacade.find(idProducto);
                if(producto == null) {
                    listaPrecioFacade.deleteListaPrecio(listaPrecio);
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, el producto con id " + idProducto + " no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                listaPrecioDet.setIdListaPrecios(listaPrecio);
                listaPrecioDet.setPrecio(precio);
                listaPrecioDet.setCotaInf(cotaInf);
                listaPrecioDet.setCotaSup(cotaSup);
                listaPrecioDet.setObservaciones(observaciones);
                listaPrecioDet.setIdProductos(producto);
                listaPrecioDet.setCotaInfPorc(cotaInfPorc);
                listaPrecioDet.setCotaSupPorc(cotaSupPorc);
                transaccion2 = listaPrecioDetFacade.setListaPrecioDetNuevo(listaPrecioDet);
                if(!transaccion2) {
                    listaPrecioFacade.deleteListaPrecio(listaPrecio);
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta el Detalle con el producto con codigo: "+ producto.getCodProducto() +", clave primaria repetida");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
            respuesta.setControl(AppCodigo.OK, "Lista de Precios creado con exito, con detalles");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editListaPrecio(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idLista = (Integer) Utils.getKeyFromJsonObject("idLista", jsonBody, "Integer");            
            Date fechaAlta = (Date) Utils.getKeyFromJsonObject("fechaAlta", jsonBody, "Date");
            Date vigenciaDesde = (Date) Utils.getKeyFromJsonObject("vigenciaDesde", jsonBody, "Date");
            Date vigenciaHasta = (Date) Utils.getKeyFromJsonObject("vigenciaHasta", jsonBody, "Date");            
            String condiciones = (String) Utils.getKeyFromJsonObject("condiciones", jsonBody, "String");
            Integer idPadronCliente = (Integer) Utils.getKeyFromJsonObject("idPadronCliente", jsonBody, "Integer");
            Integer idPadronRepresentante = (Integer) Utils.getKeyFromJsonObject("idPadronRepresentante", jsonBody, "Integer");
            BigDecimal porc1 = (BigDecimal) Utils.getKeyFromJsonObject("porc1", jsonBody, "BigDecimal");
            List<JsonElement> preciosDet = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("preciosDet", jsonBody, "ArrayList");           
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
            if(condiciones == null || idLista == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta en nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            //Valido fechas
            if( vigenciaDesde.before(fechaAlta) && vigenciaHasta.after(vigenciaDesde)) {
                respuesta.setControl(AppCodigo.ERROR, "Error, vigenciaDesde debe ser igual o mayor que fechaAlta y debe ser igual o menor a vigenciaHasta.");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            boolean transaccion;
            ListaPrecio listaPrecio = listaPrecioFacade.find(idLista);
            
            //valido que exista la lista de precio
            if(listaPrecio == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe lista de precio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            listaPrecio.setFechaAlta(fechaAlta);
            listaPrecio.setVigenciaDesde(vigenciaDesde);
            listaPrecio.setVigenciaHasta(vigenciaHasta);                       
            listaPrecio.setCondiciones(condiciones);
            listaPrecio.setIdPadronCliente(idPadronCliente);
            listaPrecio.setIdPadronRepresentante(idPadronRepresentante);
            listaPrecio.setPorc1(porc1);
            listaPrecio.getListaPrecioDetCollection().clear();
            transaccion = listaPrecioFacade.editListaPrecio(listaPrecio);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar la Lsita de Precios");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(preciosDet.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Lista de Precios sin detalles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            for(JsonElement j : preciosDet) {
                boolean transaccion2;
                BigDecimal precio = (BigDecimal) Utils.getKeyFromJsonObject("precio", j.getAsJsonObject(), "BigDecimal");
                BigDecimal cotaInf = (BigDecimal) Utils.getKeyFromJsonObject("cotaInf", j.getAsJsonObject(), "BigDecimal");
                BigDecimal cotaSup = (BigDecimal) Utils.getKeyFromJsonObject("cotaSup", j.getAsJsonObject(), "BigDecimal");
                String observaciones = (String) Utils.getKeyFromJsonObject("observaciones", j.getAsJsonObject(), "String");
                Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", j.getAsJsonObject(), "Integer"); 
                if(precio == null || cotaInf == null || cotaSup == null || idProducto == 0) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                Producto producto = productoFacade.find(idProducto);
                if(producto == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, el producto con id " + idProducto + " no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                } else {
                    ListaPrecioDet listaPrecioDet = new ListaPrecioDet();
                    listaPrecioDet.setIdListaPrecios(listaPrecio);
                    listaPrecioDet.setPrecio(precio);
                    listaPrecioDet.setCotaInf(cotaInf);
                    listaPrecioDet.setCotaSup(cotaSup);
                    listaPrecioDet.setObservaciones(observaciones);
                    listaPrecioDet.setIdProductos(producto);
                    transaccion2 = listaPrecioDetFacade.setListaPrecioDetNuevo(listaPrecioDet);
                }
                if(!transaccion2) {
                    respuesta.setControl(AppCodigo.ERROR, "No se pudo modificar el Detalle con el producto con codigo: "+ producto.getCodProducto());
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }            
            respuesta.setControl(AppCodigo.OK, "Lista de Precios y detalles editada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @DELETE
    @Path ("/{idLista}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteListaPrecio(  
        @HeaderParam ("token") String token,
        @PathParam ("idLista") int idLista,
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
            if(idLista == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            ListaPrecio listaPrecio = listaPrecioFacade.find(idLista);
            
            //Pregunto si existe el Producto
            if(listaPrecio == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la lista de precios");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = listaPrecioFacade.deleteListaPrecio(listaPrecio);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar la lista de precios");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Lista de precios borrada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
