package servicios;

import com.google.gson.JsonObject;
import datos.AppCodigo;
import datos.ContratoResponse;
import datos.Payload;
import datos.ServicioResponse;
import entidades.Acceso;
import entidades.Contrato;
import entidades.ContratoDet;
import entidades.SisCanje;
import entidades.Usuario;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.AccesoFacade;
import persistencia.ContratoFacade;
import persistencia.SisCanjeFacade;
import persistencia.UsuarioFacade;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.ws.rs.PathParam;
import org.glassfish.jersey.media.multipart.FormDataParam;
import utils.Utils;

/**
 *
 * @author FrancoSili
 */

@Stateless
@Path("contratos")
public class ContratoRest {
    @Inject UsuarioFacade usuarioFacade;
    @Inject AccesoFacade accesoFacade;
    @Inject SisCanjeFacade sisCanjeFacade;
    @Inject ContratoFacade contratoFacade;
    
    @Inject Utils utils;
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContratos(  
        @HeaderParam ("token") String token,
        @QueryParam("idPadron") Integer idPadron,
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
            
            //valido que haya contratos disponibles para la empresa
            if(user.getIdPerfil().getIdSucursal().getIdEmpresa().getContratoCollection().isEmpty()) {
                respuesta.setControl(AppCodigo.ERROR, "No hay contratos disponibles");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            List<Payload> contratos = new ArrayList<>();
            
            if(idPadron == null) {            
                for(Contrato c : user.getIdPerfil().getIdSucursal().getIdEmpresa().getContratoCollection()) {
                    ContratoResponse cr = new ContratoResponse(c);
                    if(!c.getContratoDetCollection().isEmpty()) {
                        cr.agregarDetalles(c.getContratoDetCollection());
                    }
                    contratos.add(cr);
                }
            } else {
                List<Contrato> seleccionados = new ArrayList<>();
                List<Contrato> encontrados = contratoFacade.findByPadronEmpresa(idPadron, user.getIdPerfil().getIdSucursal().getIdEmpresa());
                
                if(encontrados == null || encontrados.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no hay contratos disponibles para ese cliente");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
                }
                
                for(Contrato encontrado : encontrados) {
                    if(!encontrado.getContratoDetCollection().isEmpty()) {
                        Integer sumatoriaCantidad = 0;
                        for(ContratoDet d : encontrado.getContratoDetCollection()) {
                            sumatoriaCantidad = sumatoriaCantidad + d.getKilos();
                        }
                        
                        // filtro por la cantidad y la fecha de vencimiento
                        if(sumatoriaCantidad.equals(encontrado.getKilos()) && encontrado.getFechaVto().after(new Date())) {
                            continue;
                        } else {
                            seleccionados.add(encontrado);
                        }
                        
                    } else {
                        seleccionados.add(encontrado);
                    }
                }
                
                if(seleccionados.isEmpty()) {
                    respuesta.setControl(AppCodigo.ERROR, "Error, no hay contratos disponibles para ese cliente");
                    return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
                }
                
                for(Contrato c : seleccionados) {
                    ContratoResponse cr = new ContratoResponse(c);
                    if(!c.getContratoDetCollection().isEmpty()) {
                        cr.agregarDetalles(c.getContratoDetCollection());
                    }
                    contratos.add(cr);
                }
            }
            
            respuesta.setArraydatos(contratos);
            respuesta.setControl(AppCodigo.OK, "Contratos");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception e) {
            respuesta.setControl(AppCodigo.ERROR, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setContrato (  
        @HeaderParam ("token") String token,
        @FormDataParam("file") InputStream file,
        @FormDataParam("contratoNro") String contratoNro,
        @FormDataParam("idPadron") Integer idPadron,
        @FormDataParam("padronNombre") String padronNombre,
        @FormDataParam("padronApelli") String padronApelli,
        @FormDataParam("fechaNacimiento") String fechaNacimiento,
        @FormDataParam("nacionalidad") String nacionalidad,
        @FormDataParam("profesion") String profesion,
        @FormDataParam("documento") String documento,
        @FormDataParam("padre") String padre,
        @FormDataParam("madre") String madre,
        @FormDataParam("kilos") Integer kilos,
        @FormDataParam("cosecha") Integer cosecha,
        @FormDataParam("observaciones") String observaciones,
        @FormDataParam("idSisCanje") Integer idSisCanje,
        @FormDataParam("fechaVto") String fechaVto) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
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
            
            //Me fijo que  descripcion, idRubro e idEmpresa no sean nulos
            if(idSisCanje == null || contratoNro == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisCanje sisCanje = sisCanjeFacade.find(idSisCanje);
            
            if(sisCanje == null){
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Canje seleccionado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }          
                        
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateNacimiento = format.parse(fechaNacimiento);   
            Date dateVencimiento = format.parse(fechaVto);
               
            Contrato encontrado = contratoFacade.findByNroEmpresa(contratoNro, user.getIdPerfil().getIdSucursal().getIdEmpresa());
            if(encontrado != null) {
               respuesta.setControl(AppCodigo.ERROR, "Error, el numero de contrato ingresado ya existe");
               return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }            
            
            File path = new File("contratos " + user.getIdPerfil().getIdSucursal().getIdEmpresa().getNombre()); 
            path.mkdir(); 
            String fileLocation = path.getAbsolutePath() + "/"+ user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa() + "-" + contratoNro+ ".docx";
            System.out.println(fileLocation);
            saveFile(file, fileLocation);                          
           
            Contrato contrato = new Contrato();
            contrato.setContratoNro(contratoNro);
            contrato.setCosecha(cosecha);
            contrato.setDocumento(documento);
            contrato.setFechaNacimiento(dateNacimiento);
            contrato.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            contrato.setIdPadron(idPadron);
            contrato.setIdSisCanje(sisCanje);
            contrato.setKilos(kilos);
            contrato.setMadre(madre);
            contrato.setNacionalidad(nacionalidad);
            contrato.setObservaciones(observaciones);
            contrato.setPadre(padre);
            contrato.setProfesion(profesion);
            contrato.setFechaVto(dateVencimiento);
            contrato.setDirectorio(fileLocation);
            contrato.setApellidoCliente(padronApelli);
            contrato.setNombreCliente(padronNombre);
            boolean transaccion;
            transaccion = contratoFacade.setContratoNuevo(contrato);
            
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo dar de alta el Contrato");
               return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }
            
            respuesta.setControl(AppCodigo.CREADO, "Contrato creado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();        
        } catch (IOException | ParseException ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
	
    private void saveFile(InputStream is, String fileLocation) throws IOException {
        OutputStream os = new FileOutputStream(new File(fileLocation));
        byte[] buffer = new byte[256];
        int bytes = 0;
        while ((bytes = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
    
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editContrato (  
        @HeaderParam ("token") String token,
        @FormDataParam("idContrato") Integer idContrato,
        @FormDataParam("file") InputStream file,
        @FormDataParam("contratoNro") String contratoNro,
        @FormDataParam("idPadron") Integer idPadron,
        @FormDataParam("padronNombre") String padronNombre,
        @FormDataParam("padronApelli") String padronApelli,
        @FormDataParam("fechaNacimiento") String fechaNacimiento,
        @FormDataParam("nacionalidad") String nacionalidad,
        @FormDataParam("profesion") String profesion,
        @FormDataParam("documento") String documento,
        @FormDataParam("padre") String padre,
        @FormDataParam("madre") String madre,
        @FormDataParam("kilos") Integer kilos,
        @FormDataParam("cosecha") Integer cosecha,
        @FormDataParam("observaciones") String observaciones,
        @FormDataParam("idSisCanje") Integer idSisCanje,
        @FormDataParam("fechaVto") String fechaVto,
        @FormDataParam("editaArchivo") Integer editaArchivo) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
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
            
            //Me fijo que  descripcion, idRubro e idEmpresa no sean nulos
            if(idSisCanje == null || contratoNro == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }
            
            SisCanje sisCanje = sisCanjeFacade.find(idSisCanje);
            
            if(sisCanje == null){
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Canje seleccionado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }          
                        
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateNacimiento = format.parse(fechaNacimiento);   
            Date dateVencimiento = format.parse(fechaVto);

            Contrato contrato = contratoFacade.find(idContrato);
            if(contrato == null) {
               respuesta.setControl(AppCodigo.ERROR, "Error, no existe el contrato seleccionado");
               return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }
            
            
            if(editaArchivo == 1) {
                File path = new File("contratos " + user.getIdPerfil().getIdSucursal().getIdEmpresa().getNombre()); 
                path.mkdir(); 
                String fileLocation = path.getAbsolutePath() + "/"+ user.getIdPerfil().getIdSucursal().getIdEmpresa().getIdEmpresa() + "-" + contratoNro+ ".docx";
                System.out.println(fileLocation);
                saveFile(file, fileLocation);
                contrato.setDirectorio(fileLocation);
            }
            
            contrato.setContratoNro(contratoNro);
            contrato.setCosecha(cosecha);
            contrato.setDocumento(documento);
            contrato.setFechaNacimiento(dateNacimiento);
            contrato.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
            contrato.setIdPadron(idPadron);
            contrato.setIdSisCanje(sisCanje);
            contrato.setKilos(kilos);
            contrato.setMadre(madre);
            contrato.setNacionalidad(nacionalidad);
            contrato.setObservaciones(observaciones);
            contrato.setPadre(padre);
            contrato.setProfesion(profesion);
            contrato.setFechaVto(dateVencimiento);
            contrato.setApellidoCliente(padronApelli);
            contrato.setNombreCliente(padronNombre);
            boolean transaccion;
            transaccion = contratoFacade.editContrato(contrato);
            
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo editar el Contrato");
               return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }
            
            respuesta.setControl(AppCodigo.GUARDADO, "Contrato editado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();        
        } catch (IOException | ParseException ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }     
    
    @DELETE    
    @Path ("/{idContrato}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteContrato (  
        @HeaderParam ("token") String token,
        @PathParam("idContrato") Integer idContrato,
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
            
            Contrato contrato = contratoFacade.find(idContrato);
            if(contrato == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el contrato ingresado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }
            
            boolean transaccion;
            transaccion = contratoFacade.deleteContrato(contrato);
            
            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo eliminar el Contrato");
               return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }
            
            respuesta.setControl(AppCodigo.BORRADO, "Contrato eliminado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();
        } catch (Exception ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/generar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generarContratoByComprobanteNuevo (  
        @HeaderParam ("token") String token,
        @Context HttpServletRequest request
    ) throws Exception {

        ServicioResponse respuesta = new ServicioResponse();
        try { 

            // Obtengo el body de la request
            JsonObject jsonBody = Utils.getJsonObjectFromRequest(request);

            Integer idPadron = (Integer) Utils.getKeyFromJsonObject("idPadron", jsonBody, "Integer");
            Integer kilos = (Integer) Utils.getKeyFromJsonObject("kilos", jsonBody, "Integer");
            String documento = (String) Utils.getKeyFromJsonObject("documento", jsonBody, "String");
            Integer idSisCanje = (Integer) Utils.getKeyFromJsonObject("idSisCanje", jsonBody, "Integer");
            Date fechaVto = (Date) Utils.getKeyFromJsonObject("fechaVto", jsonBody, "Date");
            String padronNombre = (String) Utils.getKeyFromJsonObject("padronNombre", jsonBody, "String");
            String padronApelli = (String) Utils.getKeyFromJsonObject("padronApelli", jsonBody, "String");

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

            //Me fijo que idSisCanje no sea nulo
            if(idSisCanje == null) {
                respuesta.setControl(AppCodigo.ERROR, "Error, algun campo esta vacio");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }

            SisCanje sisCanje = sisCanjeFacade.find(idSisCanje);

            if(sisCanje == null){
                respuesta.setControl(AppCodigo.ERROR, "Error, no existe el Canje seleccionado");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
            }          
            
            boolean transaccion;
            
            // Genero el contrato .doc a partir del template de la empresa
            try {
                //String dirTemplate = "/opt/payara5/glassfish/domains/domain1/config/template/modelo-"+user.getIdPerfil().getIdSucursal().getIdEmpresa()+".docx";
                String dirTemplate = "/home/administrador/test-doc/modelo-"+user.getIdPerfil().getIdSucursal().getIdEmpresa()+".docx";
                String dirOutput = "/home/administrador/test-doc/";

                utils.replaceTextDocFile(
                    dirTemplate, 
                    dirOutput,
                    "{{nombreCliente}}", 
                    padronNombre + " " + padronApelli
                );
                
                transaccion = true;
            }
            catch(Exception ex) {
                System.out.println(ex);
                transaccion = false;
            }
            
            // Si se creó bien el doc sin errores, continuo
            if (transaccion == true) {
                // Se crean sin nro los contratos acá
                String contratoNro = "0";
                Contrato contrato = new Contrato();
                contrato.setContratoNro(contratoNro);
                contrato.setDocumento(documento);
                contrato.setIdEmpresa(user.getIdPerfil().getIdSucursal().getIdEmpresa());
                contrato.setIdPadron(idPadron);
                contrato.setIdSisCanje(sisCanje);
                contrato.setKilos(kilos);
                contrato.setFechaVto(fechaVto);
                contrato.setApellidoCliente(padronApelli);
                contrato.setNombreCliente(padronNombre);
                // Le seteo la fecha nacimiento a hoy porque sino tira Nullpointerexception
                contrato.setFechaNacimiento(new Date());

                transaccion = contratoFacade.setContratoNuevo(contrato);
            }


            if(!transaccion) {
                respuesta.setControl(AppCodigo.ERROR, "Error, no se pudo dar de alta el Contrato");
                return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build(); 
            }

            respuesta.setControl(AppCodigo.CREADO, "Contrato creado con exito");
            return Response.status(Response.Status.CREATED).entity(respuesta.toJson()).build();        
        } catch (IOException | ParseException ex) { 
            respuesta.setControl(AppCodigo.ERROR, ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(respuesta.toJson()).build();
        }
    
    }
    
        
    public boolean generateDocFile (Integer idEmpresa) throws Exception {
        
        utils.replaceTextDocFile(
            "/home/administrador/test-doc/test.docx", 
            "/home/administrador/test-doc/resultado.docx", 
            "{{testVariable}}", 
            "german"
        );
        
        return true;
    }
}
