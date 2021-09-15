package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.FacturaResponse;
import datos.ModeloDetalleResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CerealSisaSybase;
import entidades.CerealSisaAlicuotasSybase;
import entidades.ModeloDetalle;
import entidades.PadronGral;
import entidades.PadronProveedor;
import entidades.Producto;
import entidades.SisCotDolar;
import entidades.SisModulo;
import entidades.SisMonedas;
import entidades.SisaPorcentaje;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;
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
import persistencia.CerealSisaSybaseFacade;
import persistencia.ModeloDetalleFacade;
import persistencia.PadronGralFacade;
import persistencia.ProductoFacade;
import persistencia.SisCotDolarFacade;
import persistencia.SisModuloFacade;
import persistencia.SisMonedasFacade;
import persistencia.SisTipoModeloFacade;
import persistencia.SisaPorcentajeFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("buscaModelo")
public class BuscaModeloRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject ProductoFacade productoFacade;
    @Inject ModeloDetalleFacade modeloFacade;
    @Inject SisTipoModeloFacade sisTipoModeloFacade;
    @Inject SisModuloFacade sisModuloFacade;
    @Inject SisMonedasFacade sisMonedasFacade;
    @Inject SisCotDolarFacade sisCotDolarFacade;
    @Inject PadronGralFacade padronGralFacade;
    @Inject CerealSisaSybaseFacade cerealSisaFacade;
    @Inject SisaPorcentajeFacade sisaPorcentajeFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModelo(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
                       
            // Obtengo los atributos del body
            Integer modulo = (Integer) Utils.getKeyFromJsonObject("modulo", jsonBody, "Integer");
            Integer idMoneda = (Integer) Utils.getKeyFromJsonObject("idMoneda", jsonBody, "Integer");
            Integer idProveedor = (Integer) Utils.getKeyFromJsonObject("idProveedor", jsonBody, "Integer");
            Integer idCliente = (Integer) Utils.getKeyFromJsonObject("idCliente", jsonBody, "Integer");
            Integer idSisTipoOperacion = (Integer) Utils.getKeyFromJsonObject("idSisTipoOperacion", jsonBody, "Integer");
            List<JsonElement> productos = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("productos", jsonBody, "ArrayList");
            Integer tipoComprobante = (Integer) Utils.getKeyFromJsonObject("tipoComprobante", jsonBody, "Integer");
            BigDecimal porcentajeSisa = (BigDecimal) Utils.getKeyFromJsonObject("porcentajeSisa", jsonBody, "BigDecimal");
            Boolean esCanje = (Boolean) Utils.getKeyFromJsonObject("esCanje", jsonBody, "boolean");
            
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

            if(productos.isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "Lista de Precios sin detalles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(modulo == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, modulo nulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisModulo mod = sisModuloFacade.find(modulo);
            
            if(mod == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el modulo");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            if(idMoneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no selecciono una moneda correctamente");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisMonedas moneda = sisMonedasFacade.find(idMoneda);
            if(moneda == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe la moneda seleccionada");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }         
            
            SisCotDolar cotDolar = sisCotDolarFacade.getLastCotizacion();
            
            
            List<ModeloDetalleResponse> modelos = new ArrayList<>();
            for(JsonElement j : productos) {
                Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", j.getAsJsonObject(), "Integer");
                BigDecimal precio = (BigDecimal) Utils.getKeyFromJsonObject("precio", j.getAsJsonObject(), "BigDecimal");
                BigDecimal cantidad = (BigDecimal) Utils.getKeyFromJsonObject("cantidad", j.getAsJsonObject(), "BigDecimal");
                BigDecimal subTotal = (BigDecimal) Utils.getKeyFromJsonObject("subTotal", j.getAsJsonObject(), "BigDecimal");
                
                if(idProducto == null || cantidad == null || precio == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error al cargar detalles, algun campo esta vacio");
                    return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
                }
                
                //Busco el producto
                Producto producto = productoFacade.find(idProducto);
                if(producto == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, el producto con id:  " + idProducto + " no existe");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Pregunto si tiene un modelo asociado
                if(producto.getIdModeloCab().getModeloDetalleCollection().isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, el producto:  " + producto.getDescripcion() + "no tiene un modelo asociado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }
                
                //Me fijo si el subTotal es cero o nulo 
                if(subTotal != null ) {
                    precio = subTotal;
                    cantidad = BigDecimal.ONE;
                }
                             
                //Si el precio por la cantidad es 0 no calculo nada
                if(precio.multiply(cantidad).compareTo(BigDecimal.ZERO) == 0){
                    continue;
                }
                
                
                //Agrego a la lista de modelos response y calculo dependiendo operadores y tipo de modelo
                // % porcentual
                // + sumar
                // - restar
                // * multiplicar
                // / dividir
                // n
                BigDecimal baseImponible = precio.multiply(cantidad);
                for(ModeloDetalle p : producto.getIdModeloCab().getModeloDetalleCollection()) {
                    BigDecimal porcentaje = BigDecimal.ZERO;
                    if(p.getIdSisTipoModelo().getIdSisTipoModelo() == 1 || !p.getIdSisModulo().equals(mod)) {
                        continue;
                    } else {
                        BigDecimal total = BigDecimal.ZERO;
                        BigDecimal cien = new BigDecimal(100);
                        //De acuerdo al operador tengo que realizar distintas operaciones.
                        if(p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(1).getTipo())) {
                            switch (p.getOperador()) {
                                case "+":
                                    total = total.add(precio.add(cantidad));
                                    break;
                                case "%":
                                    total = total.add(precio.multiply(cantidad)).divide(cien);
                                    break;
                                case "-":
                                    total = total.add(precio.subtract(cantidad));
                                    break;
                                case "*":
                                    total = total.add(precio.multiply(cantidad));
                                    break;
                                case "/":
                                    total = total.add(precio.divide(cantidad));
                                    break;
                                case "n":
                                    total = precio;
                                    break;
                                default:
                                    break;
                            }
                        } else if(!p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(3).getTipo()) 
                                && !p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(1).getTipo())
                                && !p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(6).getTipo())
                                && !p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(7).getTipo())
                                && !p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(8).getTipo())) {
                            // es 2
                           
                            total = total.add(precio.multiply(cantidad));
                            if(p.getValor().compareTo(BigDecimal.ZERO) == 0) {
                                porcentaje = porcentaje.add(producto.getIdIVA().getPorcIVA());
                                total = total.multiply(producto.getIdIVA().getPorcIVA().divide(new BigDecimal(100)));
                            } else {
                                porcentaje = porcentaje.add(p.getValor());
                                total = total.multiply(p.getValor().divide(cien));
                            }
                        } else if(p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(3).getTipo())) {
                            total = total.add(precio.multiply(cantidad));
                            porcentaje = p.getValor();
                            switch (p.getOperador()) {                                   
                                case "+":
                                    if((moneda.getDescripcion().equals("u$s"))) {
                                        porcentaje = p.getValor().divide(cotDolar.getCotizacion(),2, RoundingMode.HALF_UP);
                                    }
                                    total = porcentaje;
                                    break;
                                case "%":
                                    total = total.multiply(porcentaje.divide(cien));
                                    break;
                                case "-":
                                    total = porcentaje.negate();
                                    break;
                                case "*":
                                    total = total.multiply(porcentaje);
                                    break;
                                case "/":
                                    total = total.divide(porcentaje);
                                    break;
                                case "n":
                                    total = precio;
                                    break;
                                default:
                                    break;
                            }
                        //busco las percepciones del proveedor
                        } else if(p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(6).getTipo()) && idProveedor != null) {
                            //Busco en el padron general el proveedor
                            /*PadronGral padronGral = padronGralFacade.find(idProveedor);
                            if(padronGral == null) {
                                continue;
                            }
                            
                            //Me fijo si tiene la relacion en la Tabla PadronProveedor
                            if(padronGral.getPadronProveedorCollection().isEmpty()) {
                                continue;
                            }
                            
                            //Eligo el primero de la coleccion(es una relacion de uno a uno)
                            PadronProveedor prov = padronGral.getPadronProveedorCollection().iterator().next();
                            if(prov == null) {
                                continue;
                            }
                            
                            //si el valor de modelodetalle es igual a 0 busco el valor del proveedor
                            total = total.add(precio.multiply(cantidad));
                            if(p.getValor().compareTo(BigDecimal.ZERO) == 0) {
                                porcentaje = prov.getIibbPer();
                                total = total.multiply(porcentaje.divide(new BigDecimal(100)));
                            } else {
                                porcentaje = p.getValor();
                                total = total.multiply(porcentaje.divide(cien));
                            }*/
                            total = total.add(precio.multiply(cantidad));
                            porcentaje = p.getValor();
                            total = total.multiply(porcentaje.divide(cien));
                        //busco las retenciones del proveedoer    
                        } else if(p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(7).getTipo()) && idProveedor != null) {
                           //Busco en el padron general el proveedor
                            /*PadronGral padronGral = padronGralFacade.find(idProveedor);
                            if(padronGral == null) {
                                continue;
                            }
                            
                            //Me fijo si tiene la relacion en la Tabla PadronProveedor
                            if(padronGral.getPadronProveedorCollection().isEmpty()) {
                                continue;
                            }
                            
                            //Eligo el primero de la coleccion(es una relacion de uno a uno)
                            PadronProveedor prov = padronGral.getPadronProveedorCollection().iterator().next();
                            if(prov == null) {
                                continue;
                            }
                            
                            //si el valor de modelodetalle es igual a 0 busco el valor del proveedor
                            total = total.add(precio.multiply(cantidad));
                            if(p.getValor().compareTo(BigDecimal.ZERO) == 0) {
                                porcentaje = prov.getIibbRet();
                                total = total.multiply(porcentaje.divide(new BigDecimal(100)));
                            } else {
                                porcentaje = p.getValor();
                                total = total.multiply(porcentaje.divide(cien));
                            } */
                            total = total.add(precio.multiply(cantidad));
                            porcentaje = p.getValor();
                                total = total.multiply(porcentaje.divide(cien));
                        //Busco percepciones de iva para el cliente de acuerdo a la tabla del sisa en venas solamente si es canje
                        } else if(p.getIdSisTipoModelo().getTipo().equals(sisTipoModeloFacade.find(8).getTipo()) && idCliente != null && esCanje) {
                            //Busco en la base Sybasse el CerealSisa
                            if(tipoComprobante == null || tipoComprobante != 75) {
                                continue;
                            }
                            if(porcentajeSisa == null) {
                                CerealSisaSybase cerealSisa = cerealSisaFacade.getByCodPadron(idCliente);
                                if(cerealSisa == null) {
                                    continue;
                                }



                                //Busco en la base Mysql el porcentaje de acuerdo al estado
                                SisaPorcentaje sisaPorcentaje = sisaPorcentajeFacade.getByEstadoEmpresa(Integer.parseInt(cerealSisa.getEstado().toString()), user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                                if(sisaPorcentaje == null) {
                                    continue;
                                }
                                
                                total = total.add(precio.multiply(cantidad));
                                if(p.getValor().compareTo(BigDecimal.ZERO) == 0) {
                                    porcentaje = sisaPorcentaje.getPercepIva();
                                    total = total.multiply(porcentaje.divide(new BigDecimal(100)));
                                } else {
                                    porcentaje = p.getValor();
                                    total = total.multiply(porcentaje.divide(cien));
                                } 
                            } else {
                                total = total.add(precio.multiply(cantidad));
                                if(p.getValor().compareTo(BigDecimal.ZERO) == 0) {
                                    porcentaje = porcentajeSisa;
                                    total = total.multiply(porcentaje.divide(new BigDecimal(100)));
                                } else {
                                    porcentaje = p.getValor();
                                    total = total.multiply(porcentaje.divide(cien));
                                } 
                            }
                            
                            
                            //si el valor de modelodetalle es igual a 0 busco el valor del sisaPorcentaje
                            
                             /*total = total.add(precio.multiply(cantidad));
                            porcentaje = p.getValor();
                                total = total.multiply(porcentaje.divide(cien));*/
                        
                        }
                        ModeloDetalleResponse modeloResponse = new ModeloDetalleResponse(p, total, porcentaje, baseImponible);
                        modelos.add(modeloResponse);
                    }
                }
            }   
            
            //Instancio las nuevas listas
            List<FacturaResponse> listaFacturas = new ArrayList<>();
            List<FacturaResponse> listaFacturasLast = new ArrayList<>();
            List<Payload> lista = new ArrayList<>();
            Map<String, List<FacturaResponse>> map = new HashMap<>();
            
            //Armo la lista de facturas response a partir de la lista de modelos obtenidas para todos los productos
            for(ModeloDetalleResponse mdr : modelos) {
                FacturaResponse fr = new FacturaResponse(mdr.getCtaContable(), mdr.getDescripcion(), mdr.getTotalModelo(), mdr.getValor(), mdr.getTipoModelo().getOrden(), mdr.getTipoModelo().getIdTipoModelo(), mdr.getBaseImponible(),mdr.getOperador(), mdr.getLibro().getIdLibro());
                listaFacturas.add(fr);
            }
            //Separo por cuenta contable la lista de facturas y los seteo en el Map
            for(FacturaResponse fr : listaFacturas) {
                String key  = fr.getCuentaContable();
                if(map.containsKey(key)){
                    List<FacturaResponse> list = map.get(key);
                    list.add(fr);
                } else {
                    List<FacturaResponse> list = new ArrayList<>();
                    list.add(fr);
                    map.put(key, list);
                }
            }
            //Recorro el map
            for (Map.Entry<String,  List<FacturaResponse>> entry : map.entrySet()) {
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal porcentaje = BigDecimal.ZERO;
                BigDecimal baseImponible = BigDecimal.ZERO;
                String descripcion = "";
                String cuentaContable = "";
                String operador = "";
                Integer orden = 0;
                Integer idTipoModelo = 0;
                Integer idLibro = 0;
                //Recorro la lista dentro del Map
                for(FacturaResponse fr : entry.getValue()) {
                    //Sumo los totales de acuerdo a la cuenta contable.
                    total = total.add(fr.getImporteTotal().setScale(2, BigDecimal.ROUND_HALF_EVEN));
                    descripcion = fr.getDescripcion();
                    cuentaContable = fr.getCuentaContable();
                    porcentaje = fr.getPorcentaje();
                    orden = fr.getOrden();
                    idTipoModelo = fr.getIdSisTipoModelo();
                    baseImponible = baseImponible.add(fr.getBaseImponible());
                    operador = fr.getOperador();
                    idLibro = fr.getIdLibro();
                }
                //Armo la respuesta final
                FacturaResponse fr = new FacturaResponse(cuentaContable,descripcion,total,porcentaje,orden, idTipoModelo, baseImponible, operador,idLibro);
                listaFacturasLast.add(fr);
            }
            //Ordeno la lista de los detalles por numero de orden de la tabla sisTipoModelo            
            Collections.sort(listaFacturasLast, (o1, o2) -> o1.getOrden().compareTo(o2.getOrden()));
            //Agrego la lista ordenada a la respuesta
            for(FacturaResponse f : listaFacturasLast) {
                lista.add(f);
            }            
            respuesta.setArraydatos(lista);
            respuesta.setControl(AppCodigo.OK, "Lista de Modelos");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
}
