package co.com.jsontojava;

import co.com.jsontojava.services.IJSONToJava;
import co.com.jsontojava.services.JSONToJavaImpl;

/**
 *
 * @author Cristhian Torres
 */
public class JsonToJava {

    public static void main(String[] args) {
        IJSONToJava jSONToJava = new JSONToJavaImpl();
        String jsonString = jSONToJava.readArchive(jSONToJava.getRoute());
        jSONToJava.convert(jsonString);
    }

}
