package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ServicioResponse;
import datos.SubTotalResponse;
import entidades.Acceso;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
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
import persistencia.ProductoFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("calculoSubtotales")
public class CalculoImportesSubtotales {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject ProductoFacade productoFacade;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportes(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        ServicioResponse respuesta = new ServicioResponse();
        try {
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
                       
            // Obtengo los atributos del body
            BigDecimal precio = (BigDecimal) Utils.getKeyFromJsonObject("precio", jsonBody.getAsJsonObject(), "BigDecimal");
            BigDecimal cantidad = (BigDecimal) Utils.getKeyFromJsonObject("cantidad", jsonBody.getAsJsonObject(), "BigDecimal");
            BigDecimal iva = (BigDecimal) Utils.getKeyFromJsonObject("iva", jsonBody.getAsJsonObject(), "BigDecimal");
            String descuento = (String) Utils.getKeyFromJsonObject("descuento", jsonBody.getAsJsonObject(), "String");
            String tipoDescuento = (String) Utils.getKeyFromJsonObject("tipoDescuento", jsonBody.getAsJsonObject(), "String");
            
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
            
            if(precio == null || cantidad == null || descuento == null || tipoDescuento == null || iva == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, los campos no pueden ser nulos");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            //Alto invento mio jeje
            Map map = new HashMap();
            char[] arreglo = descuento.toCharArray();
            if(arreglo[0] != '+' && arreglo[0] != '-') {
                String desc = "+".concat(descuento);
                arreglo = desc.toCharArray();
            }
            int i = 0;
            for(char a : arreglo) {
                if(a == '+' || a == '-') {
                    i++;
                    map.put(i,a);
                } else {
                    map.put(i, map.get(i).toString()+a);
                }
            }
            
            Map<Integer, BigDecimal> mapInteger = new HashMap();
            Iterator entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Integer key = (Integer)entry.getKey();
                BigDecimal value = new BigDecimal(entry.getValue().toString());
                mapInteger.put(key, value);
            }
            SubTotalResponse subTotal = new SubTotalResponse();
            if("%".equals(tipoDescuento)) {
                BigDecimal total = new BigDecimal(0);
                BigDecimal totalIva = new BigDecimal(0);
                BigDecimal unitario = new BigDecimal(0); 
                unitario = precio;
                for(Map.Entry<Integer, BigDecimal> entry : mapInteger.entrySet()) {
                    BigDecimal porcentaje = new BigDecimal(entry.getValue().toString());
                    porcentaje = porcentaje.divide(new BigDecimal(100));
                    unitario = unitario.add(unitario.multiply(porcentaje));
                    unitario = unitario.setScale(3, BigDecimal.ROUND_HALF_EVEN);
                    total = unitario.multiply(cantidad);
                }
                
                BigDecimal porcentajeIva = new BigDecimal(0);
                porcentajeIva = porcentajeIva.add(iva.divide(new BigDecimal(100)));
                totalIva = totalIva.add(total.multiply(porcentajeIva.add(new BigDecimal(1))));
                subTotal.setSubTotal(total.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                subTotal.setSubTotalIva(totalIva.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                subTotal.setPrecioDesc(unitario);
            } else {
                BigDecimal total = new BigDecimal(0);
                BigDecimal totalIva = new BigDecimal(0);
                BigDecimal unitario = new BigDecimal(0);
                unitario = precio;
                for(Map.Entry<Integer, BigDecimal> entry : mapInteger.entrySet()) {
                    BigDecimal desc = new BigDecimal(entry.getValue().toString());
                    unitario = unitario.add(desc);
                    unitario = unitario.setScale(3, BigDecimal.ROUND_HALF_EVEN);
                    total = unitario.multiply(cantidad);                   
                }
                BigDecimal porcentajeIva = new BigDecimal(0);
                porcentajeIva = porcentajeIva.add(iva.divide(new BigDecimal(100)));
                totalIva = totalIva.add(total.multiply(porcentajeIva.add(new BigDecimal(1))));
                subTotal.setSubTotal(total.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                subTotal.setSubTotalIva(totalIva.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                subTotal.setPrecioDesc(unitario);
            }

            respuesta.setDatos(subTotal);
            respuesta.setControl(AppCodigo.OK, "Subtotales");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    } 
                     
}
