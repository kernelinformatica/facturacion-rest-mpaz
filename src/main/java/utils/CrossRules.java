package utils;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;


@Provider
public class CrossRules implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        cres.getHeaders().clear();
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, clave, token, idUsuario");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");    
    }   
    
//    @Override
//    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
//        
//        response.getHeaders().clear();
//        response.getHeaders().add("Access-Control-Allow-Origin", "*");
//        response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, clave, token, idUsuario");
//        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
//        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
//        response.getHeaders().add("Access-Control-Max-Age", "1209600");  
//    }  
}
