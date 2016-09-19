package net.locortes.cmis

import net.locortes.cmis.folder.FolderCRUD
import net.locortes.cmis.helper.FileHelpers
import org.apache.chemistry.opencmis.client.api.Document
import org.apache.chemistry.opencmis.client.api.FileableCmisObject
import org.apache.chemistry.opencmis.client.api.Folder
import org.apache.chemistry.opencmis.client.api.ObjectId
import org.apache.chemistry.opencmis.client.api.ObjectType
import org.apache.chemistry.opencmis.client.api.OperationContext
import org.apache.chemistry.opencmis.client.api.Property
import org.apache.chemistry.opencmis.client.api.Session
import org.apache.chemistry.opencmis.client.api.Tree
import org.apache.chemistry.opencmis.commons.PropertyIds
import org.apache.chemistry.opencmis.commons.data.ContentStream
import org.apache.chemistry.opencmis.commons.enums.VersioningState
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl
import org.apache.log4j.Level
import org.apache.log4j.Logger


/**
 * Created by VICENC.CORTESOLEA on 13/09/2016.
 */

//####################################################################################################################
//Configuring resources file
def config = new ConfigSlurper().parse(FileHelpers.getFile('config.groovy').toURI().toURL())

Logger log = Logger.getInstance(getClass())
log.level = Level.DEBUG

//Getting params to connect to FileNet
def key = 'Sandbox'
String user = config."$key".user
String password = config."$key".password
String server = config."$key".server
String port = config."$key".port
String repository = config."$key".repository

//Checking a successful connection
WebServiceBinding binding = WebServiceBinding.newInstance(user: user, password: password, server: server, port: port)
Session session = binding.getSession(repository)
assert session != null
log.info("Connection to CMIS has been successful.")

//Getting repository information
def repositoryInfo = session.getRepositoryInfo()
log.info("=============================================")
log.info("Information relative to the repository:")
log.info("\t Cmis version: $repositoryInfo.cmisVersion")
log.info("\t Cmis description: $repositoryInfo.description")
log.info("\t Cmis ID: $repositoryInfo.id")
log.info("\t Cmis Product: $repositoryInfo.productName $repositoryInfo.productVersion")
log.info("=============================================")

OperationContext operationContext = session.createOperationContext()
operationContext.setCacheEnabled(false)

//####################################################################################################################
//Working with Document Types
//####################################################################################################################

ObjectType documentClass = session.getTypeDefinition("TestClass")
log.info("Information relative to document class:")
log.info("\t Document Class Name: ${documentClass.getDisplayName()}")
log.info("\t Document Class Local Name: ${documentClass.getLocalName()}")
log.info("\t Document Class ID: ${documentClass.getId()}")
log.info("\t Document Class Base Type: ${documentClass.getBaseType().getDisplayName()}")
log.info("\t Document Class Parent Type: ${documentClass.getParentType().getDisplayName()}")
//log.info("\t **********************")
//log.info("\t ${documentClass.getPropertyDefinitions()}")
//log.info("\t **********************")
log.info("=============================================")

//####################################################################################################################
//Working with Folders
//####################################################################################################################

//Getting Root folder by path
String rootPath = "/"
Folder root = FolderCRUD.getFolderByPath(session, rootPath)
log.info("Information relative to folders:")
log.info("\t Root Folder ID (by Path): ${root.getId() }")
log.info("\t Root Folder ID (by Session): ${session.getRootFolder().getId() }") // Obtaining root Folder ID another way

String path = "/VicencFolder";
Folder folder = FolderCRUD.getFolderByPath(session, path)
if (folder == null){
    folder = FolderCRUD.createFolder(session, root, "VicencFolder")
    assert folder != null
}
log.info("\t Folder $path ID: ${folder.getId() }")

log.info("\t Folder root TREE:")
def t = orderTree(root.getFolderTree(10))
t.each{ tree->
    printTree(tree, 1, log)
}

log.info("=============================================")

//####################################################################################################################
//Working with Documents
//####################################################################################################################

//Uploading a document
String name = "${System.currentTimeMillis()}.txt";

//Definint the values of the document. Document class, document title and one dropdown box value
Map<String, Object> properties = new HashMap<String, Object>()
properties.put(PropertyIds.OBJECT_TYPE_ID, documentClass.getLocalName())
properties.put(PropertyIds.NAME, name)
properties.put("PT_Colors", "Violet")

//Creating a plain file
byte[] content = "Hello World!".getBytes()
InputStream stream = new ByteArrayInputStream(content)
ContentStream contentStream = new ContentStreamImpl(name, BigInteger.valueOf(content.length), "text/plain", stream)

//Creating document in the folder we've just created and adding also to the ROOT folder
Document document = folder.createDocument(properties, contentStream, VersioningState.MAJOR)
document.addToFolder(root, false) //Now the document is contained by two different folders
log.info("Information relative to documents:")
log.info("\t Document ID: ${document.getId()}")
log.info("\t Document URL: ${document.getContentUrl()}")
log.info("\t Document Title: ${document.getName()}")
document.rename("${System.currentTimeMillis()}")
log.info("\t Document Title Changed to: ${document.getName()}")
log.info("\t Document Version: ${document.getVersionLabel()}")
log.info("\t Document Latest version?: ${document.isLatestVersion()}")
log.info("\t Document Major version?: ${document.isMajorVersion()}")
log.info("\t Document Versionable?: ${document.isVersionable()}")

ObjectId id = document.checkOut()
log.info("\t Document checked out id: ${id}")
log.info("\t Document content: ${document.getContentStream().getStream().getText()}")

//Mantaining the Metadata but changed the content
name = "${System.currentTimeMillis()}.txt"
HashMap<String, Object> map = new HashMap<String, Object>()
document.getProperties().each{ Property<?> p->
    map.put(p.getId(), p.getValue())
}
map.put(PropertyIds.NAME, name);

content = "Bye World!".getBytes()
stream = new ByteArrayInputStream(content)
contentStream = new ContentStreamImpl(name, BigInteger.valueOf(content.length), "text/plain", stream)

//Checking in as a major version
id = document.checkIn(false, map, contentStream, "Changed content")
log.info("\t Document id after checkin: ${id.getId()}")
document.refresh()

//Getting the last version
document = document.getObjectOfLatestVersion(false)
log.info("\t Document id: ${document.getId()}")
log.info("\t Document Title: ${document.getName()}")
log.info("\t Document Version: ${document.getVersionLabel()}")
log.info("\t Document Latest version?: ${document.isLatestVersion()}")
log.info("\t Document Major version?: ${document.isMajorVersion()}")
log.info("\t Document content: ${document.getContentStream().getStream().getText()}")

document.deleteAllVersions()
log.info("\t Document deleted.")
log.info("=============================================")

/**
 * Method that prints on screen the tree structure existing on Filenet
 * @param tree
 * @param level
 * @param log
 */
static void printTree(Tree tree, int level, Logger log){
    def children = orderTree(tree.getChildren())

    def var = '\t' * level
    if(children.empty)
        var = var + " . "
    else
        var = var + " + "
    var = var + tree.getItem().getName()
    log.info(var)

    children.each { t ->
        printTree(t, (level+1), log)
    }
}

/**
 * This method orders the trees
 * @param t
 * @return
 */
static List<Tree<FileableCmisObject>> orderTree(List<Tree<FileableCmisObject>> t){
    Collections.sort(t, new Comparator<Tree<FileableCmisObject>>() {
        @Override
        int compare(Tree<FileableCmisObject> o1, Tree<FileableCmisObject> o2) {
            return o1.getItem().getName()<o2.getItem().getName() ? -1 : (o1.getItem().getName()>o2.getItem().getName() ? 1 : 0)
        }
    })

    t
}