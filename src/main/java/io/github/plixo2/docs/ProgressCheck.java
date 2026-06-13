package io.github.plixo2.docs;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

/// Compares implemented Symbols and generates a report
public class ProgressCheck {
    IgnoreUndocumented ignoreUndocumented = IgnoreUndocumented.of(

    );

    String MD_END = "<!-- END CATEGORY LIST -->";
    Path SDL_API_MARKDOWN = Path.of("CategoryAPI.md");
    Path SODALITE_SRC = Path.of("src/main/java/io/github/plixo2/sodalite/category");
    Path OUT_MD = Path.of("Progress.md");


    String MD_TEMPLATE = """
            [Progress](#sdl-api)
            
            <%FunctionsProgress>% Functions implemented \\
            <%DatatypesProgress>% Datatypes implemented \\
            <%StructsProgress>% Structs implemented \\
            <%EnumsProgress>% Enums implemented \\
            <%MacrosProgress>% Macros implemented \\
            <%Progress>% total
            
            - [Categories](#categories)
            - [Functions](#functions)
            - [Functions](#functions)
            - [Datatypes](#datatypes)
            - [Structs](#structs)
            - [Enums](#enums)
            - [Macros](#macros)
            
            [Implemented](#implemented)
            
            - [Categories](#implemented-categories)
            - [Functions](#implemented-functions)
            - [Datatypes](#implemented-datatypes)
            - [Structs](#implemented-structs)
            - [Enums](#implemented-enums)
            - [Macros](#implemented-macros)
            
            [Missing](#missing)
            
            - [Categories](#missing-categories)
            - [Functions](#missing-functions)
            - [Datatypes](#missing-datatypes)
            - [Structs](#missing-structs)
            - [Enums](#missing-enums)
            - [Macros](#missing-macros)
            
            """;


    void main() throws IOException {
        var apiTypes = loadSDLMarkdownTypes(this.SDL_API_MARKDOWN);
        var implemented = loadImplementedTypes(apiTypes, this.SODALITE_SRC);


        createMD(apiTypes, implemented);
        System.out.println("Output written to " + toClickableURI(this.OUT_MD));
        checkUndocumented(this.SODALITE_SRC, apiTypes, implemented);
    }



    void createMD(
            Map<DataType, List<String>> apiTypes,
            Set<String> implemented
    ) throws IOException {

        var header = this.MD_TEMPLATE;
        for (var value : DataType.values()) {
            var percent = percentImplemented(value, apiTypes, implemented);
            header = header.replace("<%" + value.readableName() + "Progress>", percent);
            System.out.println(value.readableName() + ": " + percent + "%");
        }
        var totalPercentImplemented = totalPercentImplemented(apiTypes, implemented);
        header = header.replace("<%Progress>", totalPercentImplemented);

        System.out.println("Total: " + totalPercentImplemented + "%");

        var sb = new StringBuilder(header);
        sb.append("\n");

        sb.append("## SDL API\n\n");
        createMD(
            sb,
            apiTypes, implemented,
            GenType.ALL
        );
        sb.append("\n");
        sb.append("## Implemented\n\n");
        createMD(
                sb,
                apiTypes, implemented,
                GenType.IMPLEMENTED
        );
        sb.append("\n");
        sb.append("## Missing\n\n");
        createMD(
                sb,
                apiTypes, implemented,
                GenType.MISSING
        );

        Files.writeString(this.OUT_MD, sb.toString());
    }

    void createMD(
            StringBuilder sb,
            Map<DataType, List<String>> apiTypes,
            Set<String> implemented,
            GenType genType
    ) throws IOException {
        for (var value : DataType.values()) {
            sb.append("### ");
            if (genType == GenType.IMPLEMENTED) {
                sb.append("Implemented ");
            } else if (genType == GenType.MISSING) {
                sb.append("Missing ");
            }
            sb.append(value.readableName());
            sb.append("\n\n");

            var types = apiTypes.get(value);
            for (var type : types) {
                if (genType != GenType.ALL) {
                    if (implemented.contains(type) == (genType == GenType.MISSING)) {
                        continue;
                    }
                    sb.append("- ").append(type).append("\n");
                } else {
                    var check = implemented.contains(type) ? "x" : " ";
                    sb.append("- [").append(check).append("] ").append(type).append("\n");
                }
            }
            sb.append("\n");
        }

        Files.writeString(this.OUT_MD, sb.toString());
    }

    enum GenType {
        ALL,
        IMPLEMENTED,
        MISSING,

        ;

    }

    String totalPercentImplemented(
            Map<DataType, List<String>> apiTypes,
            Set<String> implemented
    ) {
        var allTypes = new HashSet<String>();
        for (var list : apiTypes.values()) {
            allTypes.addAll(list);
        }
        var totalCount = allTypes.size();
        var implementedCount = 0;
        for (var type : allTypes) {
            if (implemented.contains(type)) {
                implementedCount++;
            }
        }
        var total = ((double) implementedCount) / totalCount;
        return String.format("%.1f", total * 100);
    }

    String percentImplemented(
            DataType dataType,
            Map<DataType, List<String>> apiTypes,
            Set<String> implemented
    ) {
        var allTypes = new HashSet<>(apiTypes.get(dataType));
        var totalCount = allTypes.size();
        var implementedCount = 0;
        for (var type : allTypes) {
            if (implemented.contains(type)) {
                implementedCount++;
            }
        }
        var total = ((double) implementedCount) / totalCount;
        return String.format("%.1f", total * 100);
    }

    Set<String> loadImplementedTypes(
            Map<DataType, List<String>> apiTypes,
            Path srcPath
    ) throws IOException {
        var types = new HashSet<String>();

        var flat = new HashSet<String>();
        for (var list : apiTypes.values()) {
            flat.addAll(list);
        }


        try (var stream = Files.walk(srcPath)) {
            var list = stream.filter(Files::isRegularFile).toList();
            for (var path : list) {
                var name = path.getFileName().toString();
                if (name.endsWith(".java")) {
                    var typesInFile = annotatedType(path);
                    types.addAll(typesInFile);

                    for (var s : typesInFile) {
                        if (!flat.contains(s)) {
                            throw invalidApiFormat(path, "Unknown SDL Symbol: '" + s + "'");
                        }
                    }

                }
            }
        }

        return types;
    }

    void checkUndocumented(
            Path srcPath,
            Map<DataType, List<String>> apiTypes,
            Set<String> implemented
    ) throws IOException {

        var undocumented = getUndocumented(srcPath, apiTypes, implemented);
        if (undocumented.isEmpty()) {
            return;
        }
        var builder = new StringBuilder();
        builder.append("Found undocumented SDL API Symols\n\n");

        for (var pathListEntry : undocumented.entrySet()) {
            var path = pathListEntry.getKey();
            var types = pathListEntry.getValue();

            builder.append(toClickableURI(path)).append(":\n");
            for (var type : types) {
                builder.append("    - ").append(type).append("\n");
            }

        }

        var message = builder.toString();
        System.err.println(message);
        System.err.flush();

    }

    Map<Path, List<String>> getUndocumented(
            Path srcPath,
            Map<DataType, List<String>> apiTypes,
            Set<String> implemented
    ) throws IOException {

        var notImplemented = new HashSet<String>();
        for (var value : DataType.values()) {
            if (value == DataType.MACROS) {
                continue; // for enum constants
            }
            notImplemented.addAll(apiTypes.get(value));
        }
        for (var type : implemented) {
            notImplemented.remove(type);
        }

        var undocumented = new HashMap<Path, List<String>>();

        try (var stream = Files.walk(srcPath)) {
            var list = stream.filter(Files::isRegularFile).toList();
            for (var path : list) {
                var name = path.getFileName().toString();
                if (name.endsWith(".java")) {
                    var undocumentedTypes = checkUndocumented(notImplemented, path);
                    if (!undocumentedTypes.isEmpty()) {
                        undocumented.put(path, undocumentedTypes);
                    }
                }
            }
        }

        return undocumented;
    }
    List<String> checkUndocumented(
            Collection<String> notImplemented,
            Path javaFilePath
    ) throws IOException {
        var file = Files.readString(javaFilePath);
        var undocumented = new ArrayList<String>();

        for (var type : notImplemented) {
            if (containsType(file, type)) {
                undocumented.add(type);
            }
        }

        return undocumented;
    }
    boolean containsType(
            String file,
            String type
    ) {
        var index = file.indexOf(type);
        if (index == -1) {
            return false;
        }
        // test for word boundary
        var limit = file.length();
        var indexAfter = index + type.length();
        var characterAfter = indexAfter < limit ? file.charAt(indexAfter) : '?';
        if (isValidIDChar(characterAfter)) {
            return false;
        }
        if (this.ignoreUndocumented.ignore(type)) {
            return false;
        }

        return true;
    }

    List<String> annotatedType(Path javaFilePath) throws IOException {
        var types = new ArrayList<String>();
        var file = Files.readAllLines(javaFilePath);

        for (var line : file) {
            var apiNote =  line.indexOf("@apiNote");
            if (apiNote != -1) {
                throw invalidApiFormat(javaFilePath, "Found '@apiNote'");
            }

            var tagAPI = apiType(
                    "@sdlAPI",
                    javaFilePath, line
            );
            if (tagAPI != null) {
                types.add(tagAPI);
            }
            var tagOther = apiType(
                    "@sdlOther",
                    javaFilePath, line
            );
            if (tagOther != null) {
                types.add(tagOther);
            }
            var categorty = apiType(
                    "@sdlCategory",
                    javaFilePath, line
            );
            if (categorty != null) {
                types.add(categorty);
            }
        }

        return types;
    }
    @Nullable String apiType(
            String tag,
            Path filePath,
            String line
    ) throws IOException {

        var sdlApiNoteIndex = line.indexOf(tag);
        if (sdlApiNoteIndex == -1) {
            return null;
        }
        var formatMessage = "Expected format '/// " + tag + " SDL_<...>'";
        if (tag.equals("@sdlCategory")) {
            formatMessage = "Expected format '/// @sdlCategory Category<...>'";
        }

        if (sdlApiNoteIndex < 4) {
            throw invalidApiFormat(filePath, formatMessage);
        }
        var commentStart = line.substring(sdlApiNoteIndex - 4, sdlApiNoteIndex);
        if (!commentStart.equals("/// ")) {
            throw invalidApiFormat(filePath, formatMessage);
        }
        var afterApiNote = line.substring(sdlApiNoteIndex + tag.length());
        if (!afterApiNote.startsWith(" ")) {
            throw invalidApiFormat(filePath, "Missing space after '" + tag + "' ");
        }
        var typeName = afterApiNote.substring(1).trim();
        if (tag.equals("@sdlCategory")) {
            if (!isValidCategory(typeName)) {
                throw invalidApiFormat(filePath, "Invalid sdl category name: '" + typeName+"'");
            }
        } else {
            if (!isValidSymbol(typeName)) {
                throw invalidApiFormat(filePath, "Invalid sdl type name: '" + typeName+"'");
            }
        }

        return typeName;
    }
    boolean isValidSymbol(String type) {
        if (!type.startsWith("SDL_")) {
            return false;
        }
        for (var c : type.toCharArray()) {
            if (!isValidIDChar(c)) {
                return false;
            }
        }
        return true;
    }
    boolean isValidCategory(String type) {
        if (!type.startsWith("Category")) {
            return false;
        }
        for (var c : type.toCharArray()) {
            if (!isLetter(c)) {
                return false;
            }
        }
        return true;
    }
    boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }
    boolean isValidIDChar(char c) {
        return isLetter(c) || isDigit(c) || c == '_';
    }


    Map<DataType, List<String>> loadSDLMarkdownTypes(Path mkPath) throws IOException {
        var file = Files.readAllLines(mkPath);

        var map = new HashMap<DataType, List<String>>();
        DataType currentType = null;

        for (var line : file) {
            if (line.startsWith(this.MD_END)) {
                currentType = null;
                continue;
            }
            if (currentType == null) {
                for (var value : DataType.values()) {
                    if (line.startsWith(value.mdBegin)) {
                        currentType = value;
                        if (map.containsKey(currentType)) {
                            throw new IllegalStateException("Duplicate category: " + currentType);
                        }
                        map.put(currentType, new ArrayList<>());
                        break;
                    }
                }
                continue;
            }
            var typeName = typeName(line);
            if (typeName != null) {
                map.get(currentType).add(typeName);
            }
        }

        for (var value : DataType.values()) {
            if (!map.containsKey(value)) {
                throw new IllegalStateException("Missing category: " + value);
            }
        }


        return map;
    }

    @Nullable String typeName(String line) {
        var open = line.indexOf('[');
        var close = line.indexOf(']');
        if (open == -1 || close == -1) {
            throw new IllegalStateException("Invalid line: " + line);
        }
        var name = line.substring(open + 1, close);
        if (!name.startsWith("SDL_") && !name.startsWith("Category")) {
            return null;
        }
        return name;
    }

    IOException invalidApiFormat(Path path, String message) {
        var stateException = new IllegalStateException(message);
        return new IOException("Invalid format in " + toClickableURI(path), stateException);
    }
    String toClickableURI(Path path) {
        return "file:///" + path.toAbsolutePath().toString().replace("\\", "/");
    }

    @RequiredArgsConstructor
    enum DataType {
        CATEGORIES ("<!-- BEGIN CATEGORY LIST: CategoryAPICategory -->"),
        FUNCTIONS  ("<!-- BEGIN CATEGORY LIST: CategoryAPI, CategoryAPIFunction -->"),
        DATATYPES  ("<!-- BEGIN CATEGORY LIST: CategoryAPI, CategoryAPIDatatype -->"),
        STRUCTS    ("<!-- BEGIN CATEGORY LIST: CategoryAPI, CategoryAPIStruct -->"),
        ENUMS      ("<!-- BEGIN CATEGORY LIST: CategoryAPI, CategoryAPIEnum -->"),
        MACROS     ("<!-- BEGIN CATEGORY LIST: CategoryAPI, CategoryAPIMacro -->"),

        ;

        final String mdBegin;

        String readableName() {
            var name = this.name().toLowerCase();
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }

    }


    @AllArgsConstructor
    static class IgnoreUndocumented {
        Set<String> ignoredSymbols;

        static IgnoreUndocumented of(String... symbols) {
            var set = new HashSet<String>();
            for (var symbol : symbols) {
                if (!set.add(symbol)) {
                    throw new IllegalArgumentException("Duplicate symbol: " + symbol);
                }
            }

            return new IgnoreUndocumented(set);
        }

        boolean ignore(String symbol) {
            return this.ignoredSymbols.contains(symbol);
        }
    }

}
