package servicios;

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
import entidades.ListaPrecio;
import entidades.Usuario;
import entidades.UsuarioListaPrecio;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.ContPlanCuentaFacade;
import persistencia.ListaPrecioFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("buscaListaPrecio")
public class BuscaListaPrecio {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject ListaPrecioFacade listaPrecioFacade;
    @Inject ContPlanCuentaFacade contPlanCuentaFacade;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFormaPago(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Boolean activa = (Boolean) Utils.getKeyFromJsonObject("activa", jsonBody, "boolean");
            Boolean todas = (Boolean) Utils.getKeyFromJsonObject("todas", jsonBody, "boolean");
            Date fecha = (Date) Utils.getKeyFromJsonObject("fecha", jsonBody, "Date");
            Integer idPadronDesde = (Integer) Utils.getKeyFromJsonObject("idPadronDesde", jsonBody, "Integer");
            Integer idPadronHasta = (Integer) Utils.getKeyFromJsonObject("idPadronHasta", jsonBody, "Integer");
            
            if(token == null ||token.trim().isEmpty()) {
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
            
            //Armo la lista de precios dependiendo los filtros
            List<ListaPrecio> listaPrecios = new ArrayList<>();
            
            //Filtro por lista activa
            if(activa == null) {
                if(user.getUsuarioListaPrecioCollection() == null || user.getUsuarioListaPrecioCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No existen listas de precios disponibles para el usuario logeado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                } 
                for(UsuarioListaPrecio ul : user.getUsuarioListaPrecioCollection()) {
                    listaPrecios.add(ul.getIdListaPrecios());
                }
            } else {
                //valido que la lista no venga vacia o nula 
                if(user.getUsuarioListaPrecioCollection() == null || user.getUsuarioListaPrecioCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No existen listas de precios activas disponibles para el usuario logeado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }  
                for(UsuarioListaPrecio ul : user.getUsuarioListaPrecioCollection()) {
                    if(ul.getIdListaPrecios().getActiva()) {
                        listaPrecios.add(ul.getIdListaPrecios());
                    }
                }
                
                if(listaPrecios.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No existen listas de precios activas disponibles para el usuario logeado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
            }
            
            List<ListaPrecio> listaVigente = new ArrayList<>();
            //Filtro por fechas
            if(fecha != null) {
                for(ListaPrecio l : listaPrecios) {
                    if((l.getVigenciaDesde().before(fecha) && l.getVigenciaHasta().after(fecha)) || l.getVigenciaDesde().equals(fecha) || l.getVigenciaHasta().equals(fecha)){
                        listaVigente.add(l);
                    } else {
                        continue;
                    }
                    //Corto el bucle para que no rompa si no hay mas registros en la lista
                    if(listaPrecios.isEmpty()) {
                        break;
                    }
                }
            }
            
            //Si la lista esta vacia devuelvo respuesta
            if(listaVigente.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No existen listas de precios disponibles con la fecha ingresada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //filtro por todas e idPadron desde y hasta
            if(todas && idPadronDesde != null && idPadronHasta != null) {
                for(ListaPrecio l : listaPrecios) {
                    if((l.getIdPadronCliente() >= idPadronDesde && l.getIdPadronCliente() <= idPadronHasta) || l.getIdPadronCliente() == 0) {
                        if(listaVigente.contains(l)) {
                            continue;
                        } else {
                            listaVigente.add(l);
                        }
                    } else {
                        listaVigente.remove(l);
                    }
                    //Corto el bucle para que no rompa si no hay mas registros en la lista
                    if(listaPrecios.isEmpty()) {
                        break;
                    }
                }
            }
            
            //Si la lista esta vacia devuelvo respuesta
            if(listaVigente.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No existen listas de precios disponibles para el cliente ingresado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Filtro por todas
            if(todas && idPadronDesde == null && idPadronHasta == null) {
                for(ListaPrecio l : listaPrecios) {
                    if((l.getIdPadronCliente() == 0)) {
                        if(listaVigente.contains(l)) {
                            continue;
                        } else {
                            listaVigente.add(l);
                        }
                    } else {
                        listaVigente.remove(l);
                    }
                    //Corto el bucle para que no rompa si no hay mas registros en la lista
                    if(listaPrecios.isEmpty()) {
                        break;
                    }
                }
            }
            
            //Si la lista esta vacia devuelvo respuesta
            if(listaVigente.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No existen listas de precios disponibles con cliente igual 0");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Armo el array de la respuesta con las formas de pago
            List<Payload> lista = new ArrayList<>();
            List<ListaPreciosResponse> listPreciosFinal = new ArrayList<>();
            for(ListaPrecio l : listaVigente) {
                ListaPreciosResponse lr = new ListaPreciosResponse(l);
                if(l.getListaPrecioFormaPagoCollection() != null && !l.getListaPrecioFormaPagoCollection().isEmpty()) {
                    lr.agregarFormasPago(l.getListaPrecioFormaPagoCollection());
                    
                }
                if(l.getListaPrecioDetCollection() != null && !l.getListaPrecioDetCollection().isEmpty()) {
                    lr.agregarListaPrecioDet(l.getListaPrecioDetCollection());
                }
                listPreciosFinal.add(lr);
            }
            
            //Si la lista esta vacia devuelvo respuesta
            if(listPreciosFinal.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No existen listas de precios disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            try {
                if(!listPreciosFinal.isEmpty()) {
                    for(ListaPreciosResponse p : listPreciosFinal) {
                        if(p.getFormasPago() != null && !p.getFormasPago().isEmpty()) {
                            for(FormaPagoResponse fp : p.getFormasPago()) {
                                if(fp.getFormaPagoDet() != null && !fp.getFormaPagoDet().isEmpty()) {
                                    for(FormaPagoDetResponse fpd : fp.getFormaPagoDet()) {
                                        if(fpd.getCtaContable() == null) {
                                            continue;
                                        }
                                        ContPlanCuenta cont = contPlanCuentaFacade.getCuentaContable(Integer.parseInt(fpd.getCtaContable()));
                                        if(cont != null){
                                            fpd.setPlanCuenta(new ContPlanCuentaResponse(cont));
                                        } else {
                                            ContPlanCuentaResponse conta = new ContPlanCuentaResponse(Integer.parseInt(fpd.getCtaContable()),fpd.getDetalle());
                                            fpd.setPlanCuenta(conta);
                                        }
                                    }
                                }
                            }                        
                        }
                    }
                }
                //Añado toda la coleccion a la respuesta
                lista.addAll(listPreciosFinal);
            } catch (Exception e){
                respuesta.setControl(AppCodigo.ERROR, "No ese pudieron asignar las formas de pago a las listas de precios");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }           
            respuesta.setArraydatos(lista);
            respuesta.setControl(AppCodigo.OK, "Listas de Precios");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
