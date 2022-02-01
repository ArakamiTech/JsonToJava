package co.com.jsontojava;

import co.com.jsontojava.services.IJsonToJava;
import co.com.jsontojava.services.JsonToJavaImpl;

/**
 *
 * @author Cristhian Torres
 */
public class JsonToJava {

    public static void main(String[] args) {
        IJsonToJava jsonToJava = new JsonToJavaImpl();
        String jsonString = jsonToJava.readArchive(jsonToJava.getRoute());
        jsonToJava.convert(jsonString);
    }

}
