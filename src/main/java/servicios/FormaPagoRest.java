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
import entidades.FormaPago;
import entidades.FormaPagoDet;
import entidades.ListaPrecio;
import entidades.ListaPrecioFormaPago;
import entidades.Padron;
import entidades.SisFormaPago;
import entidades.Usuario;
import entidades.UsuarioListaPrecio;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import persistencia.FormaPagoDetFacade;
import persistencia.FormaPagoFacade;
import persistencia.ListaPrecioFacade;
import persistencia.ListaPrecioFormaPagoFacade;
import persistencia.PadronFacade;
import persistencia.SisFormaPagoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Franco Dario
 */

@Stateless
@Path("formaPago")
public class FormaPagoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject SisFormaPagoFacade sisFormaPagoFacade;
    @Inject FormaPagoFacade formaPagoFacade;
    @Inject ListaPrecioFacade listaPrecioFacade;
    @Inject FormaPagoDetFacade formaPagoDetFacade;
    @Inject ContPlanCuentaFacade contPlanCuentaFacade;
    @Inject ListaPrecioFormaPagoFacade listaPrecioFormaPagoFacade;
    @Inject PadronFacade padronFacade;
    @Inject CtacteCategoriaFacade ctacteCategoriaFacade;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFormaPago(  
        @HeaderParam ("token") String token,
        @QueryParam("codPadron") Integer codPadron,
        @QueryParam("idSisModulo") Integer idSisModulo,
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
            
            //valido que tenga Rubros disponibles
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getListaPrecioCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay Formas de Pago disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            List<Payload> formaPagos = new ArrayList<>();
            //devuelvo para el ABM
            if(idSisModulo == null){
                if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getFormaPagoCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no hay formas de pagos disponibles");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                for(FormaPago p :user.getIdPerfil().getIdSucursal().getIdEmpresa().getFormaPagoCollection()) {
                    FormaPagoResponse fp = new FormaPagoResponse(p);
                    for(FormaPagoDet d : p.getFormaPagoDetCollection()) {
                        FormaPagoDetResponse fpd = new FormaPagoDetResponse(d);
                        //si es nula continuo en el for
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
                            fpd.setPlanCuenta(new ContPlanCuentaResponse(cont));
                        } else {
                            ContPlanCuentaResponse conta = new ContPlanCuentaResponse(Integer.parseInt(d.getCtaContable()),d.getDetalle());
                            fpd.setPlanCuenta(conta);
                        }
                        fp.getFormaPagoDet().add(fpd);
                    }
                    formaPagos.add(fp);
                }
            //Aca devuelvo para compra y venta    
            } else {
                if(idSisModulo == 2) {
                List<FormaPagoResponse> formaPagosEncontradas = new ArrayList<>();
                //busco las formas de pago de acuerdo a las listas de precios dadas de alta para el usuario
                    for(UsuarioListaPrecio lu : user.getUsuarioListaPrecioCollection()){ 
                        for(ListaPrecioFormaPago lp : lu.getIdListaPrecios().getListaPrecioFormaPagoCollection()) {                                              
                            //Si no pertenece al modulo seleccionado continuo
                            if(!lp.getIdFormaPago().getTipo().getIdSIsModulo().getIdSisModulos().equals(idSisModulo)){
                                continue;
                            }
                            FormaPagoResponse fp = new FormaPagoResponse(lp.getIdFormaPago());
                            for(ListaPrecioFormaPago lpfp : lp.getIdFormaPago().getListaPrecioFormaPagoCollection()) {
                                ListaPreciosResponse lr = new ListaPreciosResponse(lpfp.getIdListaPrecio());                   
                                fp.getListaPrecios().add(lr);
                            }
                            if(!lp.getIdFormaPago().getFormaPagoDetCollection().isEmpty()) {
                                for(FormaPagoDet d : lp.getIdFormaPago().getFormaPagoDetCollection()) {
                                    FormaPagoDetResponse fpd = new FormaPagoDetResponse(d);
                                    //si es nula continuo en el for
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
                                        fpd.setPlanCuenta(new ContPlanCuentaResponse(cont));
                                    } else {
                                        ContPlanCuentaResponse conta = new ContPlanCuentaResponse(Integer.parseInt(d.getCtaContable()),d.getDetalle());
                                        fpd.setPlanCuenta(conta);
                                    }
                                    fp.getFormaPagoDet().add(fpd);
                                }
                            }
                            // Aca busca si ya exsite
                            FormaPagoResponse yaExiste = formaPagosEncontradas.stream()
                                .filter(
                                    a -> a.getIdFormaPago() == fp.getIdFormaPago()
                                )
                                .findFirst()
                                .orElse(null);
                            if (yaExiste == null) {
                                formaPagosEncontradas.add(fp);
                                formaPagos.add(fp);
                            }                    
                        }
                    }//termina el for
                    if(formaPagosEncontradas.isEmpty()) {
                        respuesta.setControl(AppCodigo.ERROR, "No hay Formas de Pago disponibles");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                //devuelvo para compras sin las listas de precios
                } else if(idSisModulo == 1) {
                    for(FormaPago p :user.getIdPerfil().getIdSucursal().getIdEmpresa().getFormaPagoCollection()) {
                        if(p.getTipo().getIdSIsModulo().getIdSisModulos() != idSisModulo) {
                            continue;
                        }
                        FormaPagoResponse fp = new FormaPagoResponse(p);
                        for(FormaPagoDet d : p.getFormaPagoDetCollection()) {
                            FormaPagoDetResponse fpd = new FormaPagoDetResponse(d);
                            //si es nula continuo en el for
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
                                fpd.setPlanCuenta(new ContPlanCuentaResponse(cont));
                            } else {
                                ContPlanCuentaResponse conta = new ContPlanCuentaResponse(Integer.parseInt(d.getCtaContable()),d.getDetalle());
                                fpd.setPlanCuenta(conta);
                            }
                            fp.getFormaPagoDet().add(fpd);
                        }
                        formaPagos.add(fp);
                    }
                }               
            }
            respuesta.setArraydatos(formaPagos);
            respuesta.setControl(AppCodigo.OK, "Lista de Formas de Pago");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setFormaPago(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer tipo = (Integer) Utils.getKeyFromJsonObject("tipo", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            List<JsonElement> listasPrecios = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("listaPrecios", jsonBody, "ArrayList");
            List<JsonElement> formaPagoDet = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("formaPagoDet", jsonBody, "ArrayList");
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
            if(descripcion == null || tipo == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisFormaPago sisFormaPago = sisFormaPagoFacade.getByIdSisFormaPago(tipo);
           
            //Pregunto si existe el sisFormaPago
            if(sisFormaPago == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el Tipo de Forma de Pago");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
                       
            FormaPago formaPago = new FormaPago();
            formaPago.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            formaPago.setTipo(sisFormaPago);
            formaPago.setDescripcion(descripcion);
            boolean transaccion;
            transaccion = formaPagoFacade.setFormaPagoNuevo(formaPago);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se puido crear la Forma de Pago");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(formaPagoDet != null) {
                for(JsonElement j : formaPagoDet) {
                    //Obtengo los atributos del body
                    Integer cantDias = (Integer) Utils.getKeyFromJsonObject("cantDias", j.getAsJsonObject(), "Integer");                  
                    BigDecimal porcentaje = (BigDecimal) Utils.getKeyFromJsonObject("porcentaje", j.getAsJsonObject(), "BigDecimal");
                    String detalle = (String) Utils.getKeyFromJsonObject("detalle", j.getAsJsonObject(), "String");
                    String ctaContable = (String) Utils.getKeyFromJsonObject("ctaContable", j.getAsJsonObject(), "String");

                    if(cantDias == null || porcentaje == null || detalle == null ) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta, algun campo esta vacio");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }

                    FormaPagoDet formaPagoDetalles = new FormaPagoDet();
                    formaPagoDetalles.setCantDias(cantDias);
                    formaPagoDetalles.setCtaContable(ctaContable);
                    formaPagoDetalles.setDetalle(detalle);
                    formaPagoDetalles.setIdFormaPago(formaPago);
                    formaPagoDetalles.setPorcentaje(porcentaje);
                    boolean transaccion2;
                    transaccion2 = formaPagoDetFacade.setFormaPagoDetNuevo(formaPagoDetalles);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo crear el detalle:" + formaPagoDetalles.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }                
                }
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
                    
                    ListaPrecioFormaPago lf = new ListaPrecioFormaPago();
                    lf.setIdFormaPago(formaPago);
                    lf.setIdListaPrecio(listaPrecio);
                    boolean transaccion2;
                    transaccion2 = listaPrecioFormaPagoFacade.setListaPrecioFormaPagoNuevo(lf);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo crear la forma de pago con la lista de precios" );
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            respuesta.setControl(AppCodigo.CREADO, "Forma de Pago creada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editFormaPago(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idFormaPago = (Integer) Utils.getKeyFromJsonObject("idFormaPago", jsonBody, "Integer");
            Integer tipo = (Integer) Utils.getKeyFromJsonObject("tipo", jsonBody, "Integer");
            String descripcion = (String) Utils.getKeyFromJsonObject("descripcion", jsonBody, "String");
            List<JsonElement> listasPrecios = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("listaPrecios", jsonBody, "ArrayList");
            List<JsonElement> formaPagoDet = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("formaPagoDet", jsonBody, "ArrayList");
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

            //Me fijo que descCorta y idRubro no sean nulos
            if(idFormaPago == 0 ||  descripcion == null || tipo == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisFormaPago sisFormaPago = sisFormaPagoFacade.getByIdSisFormaPago(tipo);
           
            //Pregunto si existe el Tipo
            if(sisFormaPago == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el Tipo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            FormaPago formaPago = formaPagoFacade.find(idFormaPago);
            
            //Pregunto si existe la Forma de Pago
            if(formaPago == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la Forma de Pago");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            boolean transaccion;
            formaPago.setTipo(sisFormaPago);
            formaPago.setDescripcion(descripcion);
            formaPago.getFormaPagoDetCollection().clear();
            formaPago.getListaPrecioFormaPagoCollection().clear();
            transaccion = formaPagoFacade.editFormaPago(formaPago);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo editar la Forma de Pago");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            if(formaPagoDet != null) {
                for(JsonElement j : formaPagoDet) {
                    //Obtengo los atributos del body
                    Integer cantDias = (Integer) Utils.getKeyFromJsonObject("cantDias", j.getAsJsonObject(), "Integer");                  
                    BigDecimal porcentaje = (BigDecimal) Utils.getKeyFromJsonObject("porcentaje", j.getAsJsonObject(), "BigDecimal");
                    String detalle = (String) Utils.getKeyFromJsonObject("detalle", j.getAsJsonObject(), "String");
                    String ctaContable = (String) Utils.getKeyFromJsonObject("ctaContable", j.getAsJsonObject(), "String");

                    if(cantDias == null || porcentaje == null || detalle == null ) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo dar de alta, algun campo esta vacio");
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                    FormaPagoDet formaPagoDetalles = new FormaPagoDet();
                    formaPagoDetalles.setCantDias(cantDias);
                    formaPagoDetalles.setCtaContable(ctaContable);
                    formaPagoDetalles.setDetalle(detalle);
                    formaPagoDetalles.setIdFormaPago(formaPago);
                    formaPagoDetalles.setPorcentaje(porcentaje);
                    boolean transaccion2;
                    transaccion2 = formaPagoDetFacade.editFormaPagoDet(formaPagoDetalles);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo editar el detalle:" + formaPagoDetalles.getDetalle());
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }                
                }
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
                    
                    ListaPrecioFormaPago lf = new ListaPrecioFormaPago();
                    lf.setIdFormaPago(formaPago);
                    lf.setIdListaPrecio(listaPrecio);
                    boolean transaccion2;
                    transaccion2 = listaPrecioFormaPagoFacade.editListaPrecioFormaPago(lf);
                    if(!transaccion2) {
                        respuesta.setControl(AppCodigo.ERROR, "No se pudo crear la forma de pago con la lista de precios" );
                        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                    }
                }
            }
            respuesta.setControl(AppCodigo.GUARDADO, "Forma de Pago editada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }   
    
    
    @DELETE
    @Path ("/{idFormaPago}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteFormaPago(  
        @HeaderParam ("token") String token,
        @PathParam ("idFormaPago") int idFormaPago,
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
            
            //Me fijo que idFormaPago no sean nulos
            if(idFormaPago == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            FormaPago formaPago = formaPagoFacade.find(idFormaPago);
            
            //Pregunto si existe la Forma de PAgo
            if(formaPago == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el SubRubro");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = formaPagoFacade.deleteFormaPago(formaPago);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar la Forma de Pago");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.BORRADO, "Forma de Pago borrada con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
