package io.github.plixo2.docs;


import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SanityTests {

    @Test
    public void progressCheck() throws IOException {
        new ProgressCheck().main(new String[]{"--dry"});
    }

    @Test
    public void propertyExtractor() throws IOException {
        new PropertyExtractor().main(new String[]{"--dry"});
    }

    @Test
    public void callbackWrapperCheck() throws IOException {
        new CallbackWrapperCheck().main();
    }

}
