package net.locortes.cmis.folder;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VICENC.CORTESOLEA on 10/05/2016.
 */
public class FolderCRUD {

    /**
     * Create a Folder in a Concrete Path. If no Path is provided then on the root folder
     *
     * @param session
     * @return
     */
    public static Folder createFolder(Session session, Folder folder, String name) {
        if (folder == null)
            folder = session.getRootFolder();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, name);


        return folder.createFolder(properties);
    }

    /**
     * Method to retrieve Folder by its Path
     *
     * @param session
     * @param path
     * @return
     */
    public static Folder getFolderByPath(Session session, String path) {
        CmisObject object = session.getObjectByPath(path);
        return (Folder) object;
    }

    /**
     * Method to retrieve a Folder by its ID
     *
     * @param session
     * @param id
     * @return
     */
    public static Folder getFolderById(Session session, String id) {
        return (Folder) session.getObject(id);
    }
}
