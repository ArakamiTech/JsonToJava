package co.com.jsontojava.services;

import co.com.jsontojava.dto.ImportsDTO;
import co.com.jsontojava.dto.VariablesDto;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import co.com.jsontojava.enums.Enum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Cristhian Torres
 */
public class JsonToJavaImpl implements IJsonToJava {

	@Override
	public String getRoute() {
		JFileChooser fileChooser = new JFileChooser(Enum.FILE.getValue());
		FileFilter filtro = new FileNameExtensionFilter(Enum.DESCRIPTION_FILE.getValue(), Enum.TYPE_FILE.getValue());
		fileChooser.setFileFilter(filtro);
		int valor = fileChooser.showOpenDialog(fileChooser);
		if (valor == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile().getAbsolutePath();
		} else {
			JOptionPane.showMessageDialog(null, Enum.SELECTED_NONE.getValue(), "Error", 0);
		}
		return null;
	}

	@Override
	public String readArchive(String route) {
		File f = new File(route);
		if (f.exists()) {
			try (InputStream is = new FileInputStream(route);) {
				return IOUtils.toString(is, StandardCharsets.UTF_8);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), Enum.ERROR.getValue(), 0);
			}
		}
		return null;
	}

	@Override
	public void convert(String jsonString) {
		deleteDirectory(Enum.DIRECTORY.getValue());
		createDirectory();
		var lines = jsonString.split(Enum.SPLIT.getValue());
		var mainObject = new JSONObject(jsonString);
		createClass(lines, mainObject, "RequestDto");
	}

	private static void createDirectory() {
		var directorio = new File(Enum.DIRECTORY.getValue());		
		if (!directorio.exists()) {
			if (directorio.mkdirs()) {
				JOptionPane.showMessageDialog(null, "Directorio creado", "Exitoso", 3);
			} else {
				JOptionPane.showMessageDialog(null, "Error creando el directorio", Enum.ERROR.getValue(), 0);
			}
		}
	}

	private static void deleteDirectory(String ruta) {
		Path directory = Paths.get(ruta);
		try {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			//Nothing to control
		}
	}

	private static void createClass(String[] lines, JSONObject mainObject, String nameClass) {
		try {
			var importsDto = new ImportsDTO();
			var variablesDto = new VariablesDto();
			List<String> importsDtoList = new ArrayList<>();
			List<String> variablesDtoList = new ArrayList<>();
			importsDtoList.add(Enum.IMPORT_DATA.getValue());
			List<JSONObject> objects = new ArrayList<>();
			var flag = 0;
			List<String> classesNames = new ArrayList<>();
			for (var i = 0; i < lines.length; i++) {
				var variables = lines[i].split("\"");
				if (i == 0) {
					importsDtoList.add(Enum.IMPORT_DTO.getValue().concat(nameClass).concat(";"));
				} else if (i != lines.length - 1) {
					if (flag == 0 && lines[i].contains(Enum.OPEN_BRAKET.getValue())) {
						classesNames.add(variables[1].substring(0, 1).toUpperCase()
								.concat(variables[1].substring(1, variables[1].length())));
						var tipeObject = classesNames.get(classesNames.size() - 1);
						if (lines[i].contains("[")) {
							variablesDtoList.add(Enum.PRIVATE.getValue().concat("List<").concat(tipeObject).concat("> ")
									.concat(variables[1]).concat(";"));
							var object = (JSONArray) mainObject.get(variables[1]);
							objects.add(object.getJSONObject(0));
							importsDtoList.add(Enum.IMPORT_LIST.getValue());
						} else {
							variablesDtoList.add(Enum.PRIVATE.getValue().concat(tipeObject).concat(" ")
									.concat(variables[1]).concat(";"));
							objects.add((JSONObject) mainObject.get(variables[1]));
						}
						flag = flag + 1;
						importsDtoList.add(Enum.IMPORT_DTO.getValue().concat(tipeObject).concat(";"));
					} else {
						flag = openBraketCondition(flag, i, lines);
					}
					variablesCondition(flag, i, lines, variables, variablesDtoList, importsDtoList);
				}
				flag = closeBraketCondition(flag, i, lines);
			}
			importsDto.setImportsName(importsDtoList.stream().distinct().collect(Collectors.toList()));
			variablesDto.setVariablesName(variablesDtoList);
			buildDto(nameClass, importsDto, variablesDto);
			newCall(objects, classesNames);
		} catch (JSONException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Enum.ERROR.getValue(), 0);
		}
	}

	private static void variablesCondition(int flag, int i, String[] lines, String[] variables,
			List<String> variablesDtoList, List<String> importsDtoList) {
		if (flag == 0 && !lines[i].contains(Enum.OPEN_BRAKET.getValue())
				&& !lines[i].contains(Enum.CLOSE_BRAKET.getValue())) {
			variablesDtoList
					.add(Enum.PRIVATE.getValue().concat(variables[3]).concat(" ").concat(variables[1]).concat(";"));
			dateCondition(variables, importsDtoList);
		}
	}

	private static void newCall(List<JSONObject> objects, List<String> classesNames) {
		if (!objects.isEmpty()) {
			for (var i = 0; i < objects.size(); i++) {
				var linesNew = Enum.OPEN_BRAKET.getValue()
						.concat(objects.get(i).toString().substring(1, objects.get(i).toString().length() - 1))
						.concat(Enum.CLOSE_BRAKET.getValue()).replace(Enum.OPEN_BRAKET.getValue(), "{\n")
						.replace("\",", "\",\n").replace(Enum.CLOSE_BRAKET.getValue(), "}\n").replace("\"}", "\"\n}")
						.split(Enum.SPLIT.getValue());
				var mainObjectNew = new JSONObject(Enum.OPEN_BRAKET.getValue().concat(objects.get(i).toString()
						.substring(1, objects.get(i).toString().length() - 1).concat(Enum.CLOSE_BRAKET.getValue())));
				createClass(linesNew, mainObjectNew, classesNames.get(i));
			}
		}
	}

	private static int openBraketCondition(int flag, int i, String[] lines) {
		if (lines[i].contains(Enum.OPEN_BRAKET.getValue())) {
			flag = flag + 1;
		}
		return flag;
	}

	private static void dateCondition(String[] variables, List<String> importsDtoList) {
		if (variables[3].contains(Enum.DATE.getValue())) {
			importsDtoList.add(Enum.IMPORT_DATE.getValue());
		}
	}

	private static int closeBraketCondition(int flag, int i, String[] lines) {
		if (lines[i].contains(Enum.CLOSE_BRAKET.getValue())) {
			flag = flag - 1;
		}
		return flag;
	}

	private static void buildDto(String classesNames, ImportsDTO importsDto, VariablesDto variablesDto) {
		try {
			try (var writer = new PrintWriter(
					Enum.DIRECTORY.getValue().concat("/").concat(classesNames).concat(Enum.EXTENSION.getValue()),
					Enum.UTF.getValue())) {
				importsDto.getImportsName().stream().forEach(writer::println);
				writer.println();

				writer.println(Enum.DATA.getValue());
				writer.println(Enum.PUBLIC.getValue().concat(Enum.CLASS.getValue()).concat(classesNames).concat(" {"));
				writer.println();
				variablesDto.getVariablesName().stream().forEach(values -> writer.println("\t".concat(values)));
				writer.println();
				writer.println(Enum.CLOSE_BRAKET.getValue());
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Enum.ERROR.getValue(), 0);
		}
	}

}
