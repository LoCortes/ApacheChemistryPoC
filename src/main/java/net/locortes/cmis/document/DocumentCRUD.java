package net.locortes.cmis.document;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by VICENC.CORTESOLEA on 10/05/2016.
 */
public class DocumentCRUD {

    /**
     * This method creates a plain document to test simply
     * @param session
     * @param parent
     * @return
     */
    public static Document createDocument(Session session, Folder parent){
        String name = "myNewDocument.txt";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "TestClass");
        properties.put(PropertyIds.NAME, name);
        properties.put("PT_Colors", "Violet");

        byte[] content = "Hello World!".getBytes();
        InputStream stream = new ByteArrayInputStream(content);
        ContentStream contentStream = new ContentStreamImpl(name, BigInteger.valueOf(content.length), "text/plain", stream);

        return parent.createDocument(properties, contentStream, VersioningState.MAJOR);
    }

    public static Document getDocumentById(Session session, String id){
        return (Document)session.getObject(id);
    }

}
