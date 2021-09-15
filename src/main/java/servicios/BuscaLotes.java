package servicios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.LoteResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Usuario;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("buscaLotes")
public class BuscaLotes {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLotes(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException {
        ServicioResponse respuesta = new ServicioResponse();
        try {  
            
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            String nroLote = (String) Utils.getKeyFromJsonObject("nroLote", jsonBody, "String");
            String serie = (String) Utils.getKeyFromJsonObject("serie", jsonBody, "String");
            Date fechaVtoDesde = (Date) Utils.getKeyFromJsonObject("fechaVtoDesde", jsonBody, "Date");
            Date fechaVtoHasta = (Date) Utils.getKeyFromJsonObject("fechaVtoHasta", jsonBody, "Date");
            Integer vigencia = (Integer) Utils.getKeyFromJsonObject("vigencia", jsonBody, "Integer");                      
            Integer idPadron = (Integer) Utils.getKeyFromJsonObject("idPadron", jsonBody, "Integer");
            Integer idCteTipo = (Integer) Utils.getKeyFromJsonObject("idCteTipo", jsonBody, "Integer");
            BigDecimal facNumero = (BigDecimal) Utils.getKeyFromJsonObject("facNumero", jsonBody, "BigDecimal");
            Integer stock = (Integer) Utils.getKeyFromJsonObject("stock", jsonBody, "Integer");
            List<JsonElement> productos = (List<JsonElement>) Utils.getKeyFromJsonObjectArray("productos", jsonBody, "ArrayList");
            
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
            
            //Armo la lista de lotes
            List<LoteResponse> lotesResponse = new ArrayList<>();
            List<Payload> lotes = new ArrayList<>();
            for(JsonElement j : productos) { 
                //Obtengo los atributos del body
                Integer idProducto = (Integer) Utils.getKeyFromJsonObject("idProducto", j.getAsJsonObject(), "Integer"); 
                
                //seteo el nombre del store cabecera
                String nombreSP = "call s_buscaLotes(?,?,?,?,?,?,?,?,?,?,?)";

                //invoco al store
                CallableStatement callableStatement = this.utils.procedimientoAlmacenado(user, nombreSP);


                //valido que el Procedimiento Almacenado no sea null
                if(callableStatement == null) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no existe el procedimiento");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Seteo los parametros para la cabecera 
                callableStatement.setInt(1,user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa());
                callableStatement.setString(2,nroLote);
                callableStatement.setString(3,serie);

                //Parseo las fechas a sql.date
                java.sql.Date sqlFechaDesde = new java.sql.Date(fechaVtoDesde.getTime());
                java.sql.Date sqlFechaHasta = new java.sql.Date(fechaVtoHasta.getTime());

                callableStatement.setDate(4, sqlFechaDesde);
                callableStatement.setDate(5, sqlFechaHasta);
                callableStatement.setInt(6, vigencia);
                callableStatement.setInt(7, idProducto);
                callableStatement.setInt(8, idPadron);
                callableStatement.setInt(9, idCteTipo);
                callableStatement.setBigDecimal(10, facNumero);
                callableStatement.setInt(11, stock);

                //Reccorro los resultados para la cabecera
                ResultSet rs = callableStatement.executeQuery();

                while (rs.next()) {
                    LoteResponse lote = new LoteResponse(
                            rs.getInt("idLotes"),
                            rs.getString("nroLote"),
                            rs.getInt("item"),
                            rs.getString("serie"),
                            rs.getDate("fechaElab"),
                            rs.getDate("fechaVto"),
                            rs.getBoolean("vigencia"),
                            rs.getString("comprobante"),
                            rs.getBigDecimal("numero"),
                            rs.getString("codProducto"),
                            rs.getString("descripcionProd"),
                            rs.getBigDecimal("stock"),
                            rs.getBigDecimal("ingresos"),
                            rs.getBigDecimal("egresos"),
                            rs.getBigDecimal("stockNegativo"),
                            rs.getInt("idProducto"));
                    lotesResponse.add(lote);
                }
                callableStatement.getConnection().close();
            }
            if(lotesResponse.isEmpty()) {
                respuesta.setArraydatos(lotes);
                respuesta.setControl(AppCodigo.OK, "Lotes");
                return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
            }
            Collections.sort(lotesResponse, (o1, o2) -> o1.getFechaVto().compareTo(o2.getFechaVto()));
            lotes.addAll(lotesResponse);
            respuesta.setArraydatos(lotes);
            respuesta.setControl(AppCodigo.OK, "Lotes");
            return Response.status(Response.Status.OK).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
}
