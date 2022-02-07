package co.com.jsontojava.enums;

/**
 *
 * @author Cristhian Torres
 */
public enum Enum {

    DESCRIPTION_FILE("Archivos Json (.json)"), FILE("Documentos"), SELECTED_NONE("No se ha seleccionado ningún fichero"), VERB("Verbo: "),
    CLASS("class "), CLOSE_BRAKET("}"), DATA("@Data"), DATE("Date"), DIRECTORY("C:\\dto"), ERROR("Error"), EXTENSION(".txt"),
    IMPORT_LIST("import java.util.List;"), IMPORT_DATA("import lombok.Data;"), IMPORT_DATE("import java.util.Date;"), IMPORT_DTO("import dto."),
    OPEN_BRAKET("{"), PRIVATE("private "), PUBLIC("public "), SPLIT("\\n"), UTF("UTF-8"), TYPE_FILE("json");

    private String value;

    Enum(String value) {
        this.setValue(value);
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        this.value = value;
    }

}
