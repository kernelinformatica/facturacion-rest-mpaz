/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.CteNumerador;
import entidades.CteTipo;
import entidades.FactCab;
import entidades.FactPie;
import entidades.Produmo;
import entidades.SisOperacionComprobante;
import entidades.Usuario;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.HashMap;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.FactCabFacade;
import persistencia.ParametroGeneralFacade;
import persistencia.SisOperacionComprobanteFacade;
import persistencia.UsuarioFacade;
import utils.Utils;

/**
 *
 * @author administrador
 */

@Stateless
@Path("mandaMailPdf")
public class MandaMailPdfRest extends HttpServlet{
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject Utils utils;
    @Inject FactCabFacade factCabFacade;
    @Inject SisOperacionComprobanteFacade sisOperacionComprobanteFacade;
    @Inject
    ParametroGeneralFacade parametro;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response enviaPdfMail(  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServicioResponse respuesta = new ServicioResponse();
        try {
            
            System.out.println("---EnviaPdfMail----");
            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);
            
            // Obtengo los atributos del body
            Integer idFactCab = (Integer) Utils.getKeyFromJsonObject("idFactCab", jsonBody, "Integer");
            FactCab factCab = factCabFacade.find(idFactCab);

            if(factCab == null) {
                respuesta.setControl(AppCodigo.ERROR, "No existe ese comprobante");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            SisOperacionComprobante sisOperacionComprobante = sisOperacionComprobanteFacade.find(factCab.getIdSisOperacionComprobantes());


            
            if(sisOperacionComprobante.getEnviaMail()) {
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
                if(idFactCab == null) {
                    respuesta.setControl(AppCodigo.ERROR, "IdFacCab esta vacio");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                

                String nombreReporte = factCab.getIdCteTipo().getIdReportes().getNombre();
                System.out.println(nombreReporte);

                 if(nombreReporte == null) {
                    respuesta.setControl(AppCodigo.ERROR, "El comprobante nro: "+factCab.getNumero()+" no tiene un reporte asociado");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
                }

                //Codigo verificador
                String codigoVerificador = "";
                String numero = "";
                if(factCab.getNumeroAfip() != null && factCab.getIdCteTipo().getCursoLegal() && !" ".equals(factCab.getCai())) {
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd");
                    Formatter obj = new Formatter();
                    String txtCuit = factCab.getCuit();
                    String txtCodComp = String.valueOf(obj.format("%02d",factCab.getCodigoAfip()));
                    numero = String.valueOf(factCab.getNumero());
                    String ptoVenta = numero.substring(0, numero.length()-8);                
                    String txtPtoVta  = String.valueOf(obj.format("%04d", Integer.parseInt(ptoVenta)));
                    String txtCae = factCab.getCai();
                    String txtVtoCae = formatoFecha.format(factCab.getFechaVto());
                    codigoVerificador = utils.calculoDigitoVerificador(txtCuit, txtCodComp, txtPtoVta, txtCae, txtVtoCae);
                } 

                HashMap hm = new HashMap();
                hm.put("idFactCab", idFactCab);
                hm.put("codigoVerificador", codigoVerificador);
                hm.put("prefijoEmpresa", "05");
                System.out.println(idFactCab + " - " + codigoVerificador);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] bytes = utils.generateJasperReportPDF(request, nombreReporte, hm, user, outputStream, 1, false, null, null);
                System.out.println(request.toString() + " - " + nombreReporte + " - " + user.toString());
                String nomeRelatorio= nombreReporte + ".pdf";
                
                Integer idEmpresa = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa();
                String nombreEmpresa = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getDescripcion();
                String nombreSucursal = accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getNombre();
                String emailOrigen = parametro.get("KERNEL_SMTP_USER");
                String emailDestino = sisOperacionComprobante.getMail1();
                String nombreDestino = sisOperacionComprobante.getNombreApellidoParaMail1();
                String asunto = "Sistema de Facturación: Alta de " + factCab.getIdCteTipo().getDescripcion();
                String detallePieComprobante = "";
                BigDecimal baseImponible = new BigDecimal(0);
                BigDecimal totalComprobante = new BigDecimal(0);
                BigDecimal porcentaje = new BigDecimal(0);
                // armo nro de comprobante
            
                String nroCompString = numero;
                // Fechas:
                String fechaEmi = new SimpleDateFormat("dd-MM-yyyy").format(factCab.getFechaEmision());
                String fechaVence = new SimpleDateFormat("dd-MM-yyyy").format(factCab.getFechaVto());
                // Armo el cuerpo del mail 
                String contenido = "<!doctype html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "<meta charset=\"utf-8\">\n"
                        + "<title>Sistema Facturación</title>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "<div  style='font-size:14px;'>\n"
                        + "<hr>\n"
                        + "<div><strong>" + accesoFacade.findByToken(token).getIdUsuario().getIdPerfil().getIdSucursal().getIdEmpresa().getDescripcion() + "</strong></div>\n"
                        + "<div> Sucursal: " + nombreSucursal + "</div>"
                        + "<div>Asunto: " + asunto + "</div>"
                        + "<div>Para: " + nombreDestino + "</div>\n"
                        + "<hr>\n"
                        + "<div  style='font-size:12px; padding: 20px;'>"
                        + "	<div><strong>Detalle del Comprobante Cargado</strong></div>\n"
                        + "	<div>\n"
                        + "		<li>Comprobante emitido a: " + factCab.getNombre() + " (" + factCab.getCuit() + ")" + "</li>\n"
                        + "		<li>Nro Cuenta Corriente: " + factCab.getIdPadron() + "</li>\n"
                        + "		<li>Tipo Comprobante: " + factCab.getIdCteTipo().getDescripcion() + "\n"
                        + "		\n"
                        + "	</div>\n"
                        + "</div>\n"
                        + "</div>\n"
                        + "\n"
                        + "</body>\n"
                        + "</html>";
                utils.enviarMailPdf(emailOrigen, nombreEmpresa + " : " + nombreSucursal, emailDestino, contenido, asunto, nombreDestino, bytes);
                respuesta.setControl(AppCodigo.CREADO, "Mandamo mail");
                return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
      }  else {
               respuesta.setControl(AppCodigo.ERROR, "este no manda mail");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }
            
    } catch (Exception e) {
                respuesta.setControl(AppCodigo.ERROR, e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
    }
   }
  }

