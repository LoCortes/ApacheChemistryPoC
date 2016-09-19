package net.locortes.cmis.helper

/**
 * Created by VICENC.CORTESOLEA on 13/09/2016.
 */
class FileHelpers {

    static InputStream getResource(String name) {
        FileHelpers.class.classLoader.getResourceAsStream(name)
    }

    static File getFile(String name) {
        new File(FileHelpers.class.classLoader.getResource(name).toURI())
    }
}

