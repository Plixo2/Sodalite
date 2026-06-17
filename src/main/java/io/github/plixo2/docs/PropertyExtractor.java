package io.github.plixo2.docs;

import io.github.plixo2.sodalite.category.properties.PropertyType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class PropertyExtractor {
    Path SDL_API_MARKDOWN = Path.of("CategoryAPIMacro.md");
    Path FIELDS_OUT = Path.of("PropertyKeys.java");

    Set<String> ignore = Set.of(
            "SDL_PROP_GPU_DEVICE_CREATE_METAL_ALLOW_MACFAMILY1",
            "SDL_PROP_GPU_DEVICE_CREATE_VULKAN_REQUIRE_HARDWARE_ACCELERATION",
            "SDL_PROP_IOSTREAM_MEMORY_FREE_FUNC",
            "SDL_PROP_WINDOW_CREATE_EMSCRIPTEN_CANVAS_ID",
            "SDL_PROP_WINDOW_EMSCRIPTEN_CANVAS_ID",
            "SDL_PROP_WINDOW_CREATE_EMSCRIPTEN_KEYBOARD_ELEMENT",
            "SDL_PROP_WINDOW_EMSCRIPTEN_KEYBOARD_ELEMENT"
    );

    void main(String[] args) throws IOException {
        var lines = Files.readAllLines(this.SDL_API_MARKDOWN);
        int fieldCount = 0;
        var builder = new StringBuilder();
        builder.append("package io.github.plixo2.sodalite.category.properties;\n\n");
        builder.append("import java.lang.foreign.MemorySegment;\n\n");
        builder.append("import static io.github.plixo2.sodalite.category.properties.Property.*;\n\n");
        builder.append("import static org.libsdl.sdl.SDL3_h.*;\n\n");
        builder.append("/// This file is auto-generated. Do not edit manually.\n");
        builder.append("public interface PropertyKeys {\n");
        for (var line : lines) {

            if (!line.startsWith("- [SDL_PROP_")) {
                continue;
            }
            var split = line.indexOf("](");
            if (split == -1) {
                throw new IllegalStateException("Invalid line: " + line);
            }
            var probName = line.substring(3, split);
            if (this.ignore.contains(probName)) {
                continue;
            }

            var prop = new Prop(probName, getType(probName));

            builder.append("    ");
            prop.genFieldString(builder);
            builder.append('\n');
            fieldCount += 1;
        }
        builder.append("}\n");
        System.out.println("Extracted " + fieldCount + " properties.");
        if (args.length == 0 || !args[0].equals("--dry")) {
            Files.writeString(this.FIELDS_OUT, builder.toString());
            System.out.println("Output written to " + this.FIELDS_OUT.toAbsolutePath());
        }
    }


    record Prop(
            String name,
            PropertyType type
    ) {
        void genFieldString(StringBuilder builder) {
            builder.append("PropertyKey<");
            builder.append(typeArgument(this.type));
            builder.append("> ");
            builder.append(readableName());
            builder.append(" = new ");
            builder.append(className(this.type));
            builder.append("(");
            builder.append(this.name);
            builder.append("());");
        }

        String readableName() {
            var suffixLen = switch (this.type) {
                case POINTER -> "_POINTER".length();
                case STRING -> "_STRING".length();
                case NUMBER -> "_NUMBER".length();
                case FLOAT -> "_FLOAT".length();
                case BOOLEAN -> "_BOOLEAN".length();
            };
            var prefixLen = "SDL_PROP_".length();
            return this.name.substring(prefixLen, this.name.length() - suffixLen);
        }

        String typeArgument(PropertyType type) {
            return switch (type) {
                case POINTER -> "MemorySegment";
                case STRING -> "String";
                case NUMBER -> "Long";
                case FLOAT -> "Float";
                case BOOLEAN -> "Boolean";
            };
        }
        String className(PropertyType type) {
            return switch (type) {
                case POINTER -> "PointerProperty";
                case STRING -> "StringProperty";
                case NUMBER -> "NumberProperty";
                case FLOAT -> "FloatProperty";
                case BOOLEAN -> "BooleanProperty";
            };
        }

    }


    PropertyType getType(String prop) {
        if (prop.endsWith("_POINTER")) {
            return PropertyType.POINTER;
        } else if (prop.endsWith("_STRING")) {
            return PropertyType.STRING;
        } else if (prop.endsWith("_NUMBER")) {
            return PropertyType.NUMBER;
        } else if (prop.endsWith("_FLOAT")) {
            return PropertyType.FLOAT;
        } else if (prop.endsWith("_BOOLEAN")) {
            return PropertyType.BOOLEAN;
        } else {
            throw new IllegalStateException("Unknown property type: " + prop);
        }
    }


}



