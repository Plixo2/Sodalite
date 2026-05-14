package io.github.plixo2;


import io.github.plixo2.sodalite.log.Log;
import io.github.plixo2.sodalite.log.LogCategory;
import io.github.plixo2.sodalite.log.LogPriority;


public class Main {


    static void main() {
        Log.setLogPriority(LogCategory.GPU, LogPriority.DEBUG);
    }

}
