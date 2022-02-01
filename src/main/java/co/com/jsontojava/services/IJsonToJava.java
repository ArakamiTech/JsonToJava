package co.com.jsontojava.services;

/**
 *
 * @author Cristhian Torres
 */
public interface IJsonToJava {

    public String getRoute();

    public String readArchive(String route);
    
    public void convert(String jsonString);
    
}
