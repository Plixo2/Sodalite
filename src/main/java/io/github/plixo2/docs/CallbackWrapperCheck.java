package io.github.plixo2.docs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;

class CallbackWrapperCheck {

    Path WRAPPER_PATH = Path.of("src/main/java/io/github/plixo2/sodalite/category/main/CallbackWrapper.java");
    Path CONSUMER_PATH = Path.of("src/main/java/io/github/plixo2/sodalite/category/events/EventConsumer.java");

    Pattern WRAPPER_PREFIX = Pattern.compile("\\s*public void");
    Pattern WRAPPER_METHOD_NAME = Pattern.compile("\\s*public void (\\w*)\\s+.*");
    Pattern CONSUMER_PREFIX = Pattern.compile("\\s*default void");


    void main() throws IOException {
        var lines = Files.readAllLines(this.WRAPPER_PATH);

        int wrapperMethods = 0;
        for (var line : lines) {
            if (!this.WRAPPER_PREFIX.matcher(line).find()) {
                continue;
            }
            wrapperMethods++;

            if (line.contains("@ArrayLength(3)")) {
                line = line.replace("@ArrayLength(3)", "");
            }
            if (line.contains("@ArrayLength(6)")) {
                line = line.replace("@ArrayLength(6)", "");
            }

            var matcher = this.WRAPPER_METHOD_NAME.matcher(line);
            if (!matcher.find()) {
                throw new RuntimeException("Failed to find method name in line: " + line);
            }
            var name = matcher.group(1);

            var split = line.split("[()]");
            if (split.length != 5) {
                throw new RuntimeException("Unexpected method: " + Arrays.toString(split));
            }
            if (!split[0].contains(name)) {
                throw new RuntimeException("Method name not found in method declaration: " + line);
            }
            if (!split[2].contains(name)) {
                throw new RuntimeException("Method name not found in method call: " + line);
            }
            var params = Arrays.stream(split[1].split(",")).toList();
            var paramsNames = params.stream().map(ref -> {
                var s = ref.split(" ");
                if (s.length < 2) {
                    throw new RuntimeException("Unexpected argument: " + ref);
                }
                return s[s.length - 1].trim();
            }).toList();

            var args = split[3].split(",");
            var argsNames = Arrays.stream(args).map(String::trim).toList();
            if (args.length != paramsNames.size()) {
                throw new RuntimeException("Argument count mismatch! args: " + argsNames + " params: " + paramsNames);
            }
            for (var i = 0; i < args.length; i++) {
                var arg = argsNames.get(i);
                var param = paramsNames.get(i);
                if (!arg.equals(param)) {
                    throw new RuntimeException("Argument name mismatch in " + name + ":  arg: " + arg + " param: " + param);
                }
            }

        }

        var consumerMethods = Files.readAllLines(this.CONSUMER_PATH)
                                   .stream()
                                   .filter(line -> this.CONSUMER_PREFIX.matcher(line).find())
                                   .count();


        if (wrapperMethods != consumerMethods) {
            throw new RuntimeException("Wrapper and consumer method count mismatch! wrapper: " + wrapperMethods + " consumer: " + consumerMethods);
        }
        System.out.println("Checked " + wrapperMethods + " methods successfully!");

    }
}





