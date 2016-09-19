package net.locortes.cmis;

import net.locortes.cmis.document.DocumentCRUD;
import net.locortes.cmis.folder.FolderCRUD;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;

/**
 * Created by VICENC.CORTESOLEA on 10/05/2016.
 */
public class CMISTester {

    /**
     * This method runs different tests to check that connection is working.
     * @param args
     */
    public static void main(String[] args){
        System.out.println("##################################################################");
        System.out.println("##################################################################");

        System.out.println("Starting test...");

        //Sandbox Information
        String user = "";
        String password = "";
        String server = "";
        String port = "";
        String repository = "";

        WebServiceBinding webServiceBinding = new WebServiceBinding(user, password, server, port);
        Session session = webServiceBinding.getSession(repository);
        RepositoryInfo repositoryInfo = session.getRepositoryInfo();

        System.out.println("Cmis version: " + repositoryInfo.getCmisVersion());
        System.out.println("Cmis description: " + repositoryInfo.getDescription());
        System.out.println("Cmis ID: " + repositoryInfo.getId());

        String path = "/VicencFolder/Bulk";
        Folder root = FolderCRUD.getFolderByPath(session, path);
        Folder fold = FolderCRUD.createFolder(session, root, "Folder " + System.currentTimeMillis());
        System.out.println("Folder created with ID: " + fold.getId());

        Document doc = DocumentCRUD.createDocument(session, fold);
        System.out.println("Document created with ID: " + doc.getId());

        System.out.println("Retrieving document with ID: " + doc.getId());
        doc = DocumentCRUD.getDocumentById(session, doc.getId());
        System.out.println("Document retrieved: " + doc.toString());

        System.out.println("Test finished.");

        System.out.println("##################################################################");
        System.out.println("##################################################################");
    }

}
