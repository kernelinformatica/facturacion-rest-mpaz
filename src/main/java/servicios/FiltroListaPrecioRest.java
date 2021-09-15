package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ListaPrecioDetResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.ListaPrecioDet;
import entidades.Producto;
import entidades.SisCotDolar;
import entidades.SisMonedas;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.ListaPrecioDetFacade;
import persistencia.ListaPrecioFacade;
import persistencia.ProductoFacade;
import persistencia.SisCotDolarFacade;
import persistencia.SisMonedasFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author Franco Sili
 */

@Stateless
@Path("filtroListaPrecios")
public class FiltroListaPrecioRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject SisMonedasFacade sisMonedasFacade;
    @Inject ListaPrecioFacade listaPrecioFacade;
    @Inject ProductoFacade productoFacade;
    @Inject ListaPrecioDetFacade listaPrecioDetFacade; 
    @Inject SisCotDolarFacade sisCotDolarFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFiltroListaPrecios(  
        @HeaderParam ("token") String token,  
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            String codProdDesde = (String) Utils.getKeyFromJsonObject("codProdDesde", jsonBody, "String");
            String codProdHasta = (String) Utils.getKeyFromJsonObject("codProdHasta", jsonBody, "String");
            Integer codProvedor = (Integer) Utils.getKeyFromJsonObject("codProvedor", jsonBody, "Integer");
            Integer rubro = (Integer) Utils.getKeyFromJsonObject("rubro", jsonBody, "Integer");
            Integer subRubro = (Integer) Utils.getKeyFromJsonObject("subRubro", jsonBody, "Integer");
            BigDecimal porcentajeCabecera = (BigDecimal) Utils.getKeyFromJsonObject("porcentajeCabecera", jsonBody, "BigDecimal");
            BigDecimal porcentajeInf = (BigDecimal) Utils.getKeyFromJsonObject("porcentajeInf", jsonBody, "BigDecimal");
            BigDecimal porcentajeSup = (BigDecimal) Utils.getKeyFromJsonObject("porcentajeSup", jsonBody, "BigDecimal");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            
            //Lista de productos filtrados
            List<Producto> productos = new ArrayList<>();
            
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
                       
            //Filtro por codigo de producto, devuelve un solo producto
            if(codProdDesde != null && codProdHasta == null && codProvedor == null && rubro == null && subRubro == null) {
                Producto producto = productoFacade.getByCodigoProdEmpresa(codProdDesde, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(producto == null) {
                    respuesta.setControl(AppCodigo.ERROR, "No existe el producto con el codigo: " + codProdDesde);
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.add(producto);
            }
            
            //Filtro desde y hasta
             if(codProdDesde != null && codProdHasta != null && codProvedor == null && rubro == null && subRubro == null) {
                List<Producto> provLista = productoFacade.getByCodigoProdDesdeHasta(codProdDesde, codProdHasta, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos con codigo desde: " + codProdDesde + " y hasta: " + codProdHasta);
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro desde, hasta y proveedor 
            if(codProdDesde != null && codProdHasta != null && codProvedor != null && rubro == null && subRubro == null) {
                List<Producto> provLista = productoFacade.getByCodigoProdDesdeHastaProv(codProdDesde, codProdHasta, codProvedor, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos con codigo desde: " + codProdDesde + ", hasta: " + codProdHasta + " y proveedor: " + codProvedor);
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro desde, hasta, proveedor y rubro, 
            if(codProdDesde != null && codProdHasta != null && codProvedor != null && rubro != null && subRubro == null) {
                List<Producto> provLista = productoFacade.getByCodigoProdDesdeHastaProvRubro(codProdDesde, codProdHasta, codProvedor, rubro, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos con codigo desde: " + codProdDesde + ", hasta: " + codProdHasta + ", proveedor: " + codProvedor + " y el rubro seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro desde, hasta, proveedor, rubro y subrubro
            if(codProdDesde != null && codProdHasta != null && codProvedor != null && rubro != null && subRubro != null) {
                List<Producto> provLista = productoFacade.getByCodigoProdDesdeHastaProvRubroSubRubro(codProdDesde, codProdHasta, codProvedor, rubro, subRubro, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos con codigo desde: " + codProdDesde + ", hasta: " + codProdHasta + ", proveedor: " + codProvedor + " y el Subrubro seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro por codigo de provedor
            if(codProdDesde == null && codProdHasta == null && codProvedor != null && rubro == null && subRubro == null) {
                List<Producto> provLista = productoFacade.getByCodigoProvedorEmpresa(codProvedor, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos para el Proveedor con codigo: " + codProvedor);
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro por codigo de provedor y rubro
            if(codProdDesde == null && codProdHasta == null && codProvedor != null && rubro != null && subRubro == null) {
                List<Producto> provLista = productoFacade.getByCodigoProvedorEmpresaRubro(codProvedor, user.getIdPerfil().getIdSucursal().getIdEmpresa(), rubro);
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos para el Proveedor con codigo: " + codProvedor + " y el rubro seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro por codigo de provedor, rubro y subRubro
            if(codProdDesde == null && codProdHasta == null && codProvedor != null && rubro != null && subRubro != null) {
                List<Producto> provLista = productoFacade.getByCodigoProvedorEmpresaRubroSubRubro(codProvedor, user.getIdPerfil().getIdSucursal().getIdEmpresa(), rubro, subRubro);
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos para el Proveedor con codigo: " + codProvedor + ", rubro y subRubro seleccionados");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro por rubro 
            if(codProdDesde == null && codProdHasta == null && codProvedor == null && rubro != null && subRubro == null) {
                List<Producto> provLista = productoFacade.getByEmpresaRubro(user.getIdPerfil().getIdSucursal().getIdEmpresa(), rubro);
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos para el Rubro seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Filtro por subRubro 
            if(codProdDesde == null && codProdHasta == null && codProvedor == null && rubro != null && subRubro != null) {
                List<Producto> provLista = productoFacade.getByEmpresaRubroSubRubro(user.getIdPerfil().getIdSucursal().getIdEmpresa(), rubro, subRubro);
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos para el SubRubro seleccionado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            //Todos los Productos
            if(codProdDesde == null && codProdHasta == null && codProvedor == null && rubro == null && subRubro == null) {
                List<Producto> provLista = productoFacade.getProductosByEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                if(provLista.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "No hay productos");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                productos.addAll(provLista);
            }
            
            if(idMoneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Debe seleccionar un tipo de moneda");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisMonedas moneda = sisMonedasFacade.find(idMoneda);
            if(moneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe la moneda seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            BigDecimal cero = new BigDecimal(0);
            
            //Valido que el porcentaje de la cabecera sea distinto a 0
            if(porcentajeCabecera.equals(cero)) {
                respuesta.setControl(AppCodigo.ERROR, "El porcentaje de la cabecera tiene que ser distinto de 0");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(porcentajeInf == null || porcentajeSup == null) {
                respuesta.setControl(AppCodigo.ERROR, "El porcentaje de las cotas no debe ser nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            BigDecimal cien = new BigDecimal(100);
            
            //Armo la respuesta
            List<Payload> productosResponse = new ArrayList<>();
            if(!productos.isEmpty()) {
                if(porcentajeInf.equals(cero) && porcentajeSup.equals(cero)) {
                    for(Producto p : productos) {
                        //Multiplico el precio del producto por el porcentaje de la cabecera
                        BigDecimal precio = new BigDecimal(0);
                        precio = p.getCostoReposicion().multiply(porcentajeCabecera.divide(cien));
                        precio = precio.add(p.getCostoReposicion());
                        if(moneda.getDescripcion().equals("u$s") && p.getIdMoneda().getDescripcion().equals("$AR")) {
                            SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                            precio = precio.divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP);
                        } else if(moneda.getDescripcion().equals("$AR") && p.getIdMoneda().getDescripcion().equals("u$s")) {
                            SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                            precio = precio.multiply(cotDolar.getCotizacion());
                        }
                        ListaPrecioDetResponse pr = new ListaPrecioDetResponse(precio,p.getPrecioVentaProv(),p.getPrecioVentaProv(),p);
                        productosResponse.add(pr);
                    }
                } else if(porcentajeInf.compareTo(porcentajeSup) < 0 || porcentajeInf.equals(porcentajeSup)){ 
                    for(Producto p : productos) {
                        BigDecimal precio = new BigDecimal(0);
                        precio = p.getCostoReposicion().multiply(porcentajeCabecera.divide(cien));
                        precio = precio.add(p.getCostoReposicion());
                        if(moneda.getDescripcion().equals("u$s") && p.getIdMoneda().getDescripcion().equals("$AR")) {
                            SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                            precio = precio.divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP);
                        } else if(moneda.getDescripcion().equals("$AR") && p.getIdMoneda().getDescripcion().equals("u$s")) {
                            SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
                            precio = precio.multiply(cotDolar.getCotizacion());
                        }
                        BigDecimal precioInf = precio.multiply(porcentajeInf.divide(cien));
                        BigDecimal precioSup = precio.multiply(porcentajeSup.divide(cien));
                        precioInf = precio.subtract(precioInf);
                        precioSup = precioSup.add(precio);
                        ListaPrecioDetResponse pr = new ListaPrecioDetResponse(precio,precioInf,precioSup,p);
                        productosResponse.add(pr);
                    }
                } else { 
                    respuesta.setControl(AppCodigo.ERROR, "Los valores de las Cotas Inf y Sup son incorrectos");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();}
            } else {
                respuesta.setControl(AppCodigo.ERROR, "No hay productos con los criterios seleccionados");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setArraydatos(productosResponse);
            respuesta.setControl(AppCodigo.OK, "Lista de Precios");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @DELETE
    @Path ("/{idDetalle}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response deleteDetalleListaPrecio(  
        @HeaderParam ("token") String token,
        @PathParam ("idDetalle") int idDetalle,
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
            if(idDetalle == 0) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
           
            ListaPrecioDet listaPrecioDet = listaPrecioDetFacade.find(idDetalle);
            
            //Pregunto si existe el Producto
            if(listaPrecioDet == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe el detalle");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            boolean transaccion;
            transaccion = listaPrecioDetFacade.deleteListaPrecioDet(listaPrecioDet);
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "No se pudo borrar el detalle");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            respuesta.setControl(AppCodigo.OK, "Detalle borrado con exito");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        } 
    }    
}
