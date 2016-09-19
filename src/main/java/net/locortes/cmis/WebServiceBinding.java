package net.locortes.cmis;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VICENC.CORTESOLEA on 10/05/2016.
 */
public class WebServiceBinding {
    private static Session session = null;

    private static String user = null;
    private static String password = null;
    private static String server = null;
    private static String port = null;

    /**
     * Constructor
     */
    public WebServiceBinding(String user, String password, String server, String port) {
        this.user = user;
        this.password = password;
        this.server = server;
        this.port = port;
    }

    /**
     * Constructor with a hashmap for the values
     * @param params
     */
    public WebServiceBinding(HashMap params) {
        this.user = params.get("user").toString();
        this.password = params.get("password").toString();
        this.server = params.get("server").toString();
        this.port = params.get("port").toString();
    }

    public static Session getSession(String repository) {
        if (session == null) {
            // default factory implementation
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Map<String, String> parameter = new HashMap<String, String>();

            // user credentials
            parameter.put(SessionParameter.USER, user);
            parameter.put(SessionParameter.PASSWORD, password);


            // connection settings
            parameter.put(SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES.value());
            parameter.put(SessionParameter.WEBSERVICES_ACL_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/ACLService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/DiscoveryService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/MultiFilingService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/NavigationService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/ObjectService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/PolicyService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/RelationshipService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/RepositoryService?wsdl").toString());
            parameter.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, new StringBuilder("http://").append(server).append(":").append(port).append("/fncmis/VersioningService?wsdl").toString());
            parameter.put(SessionParameter.REPOSITORY_ID, repository);

            // create session
            session = factory.createSession(parameter);
        }

        return session;
    }
}
