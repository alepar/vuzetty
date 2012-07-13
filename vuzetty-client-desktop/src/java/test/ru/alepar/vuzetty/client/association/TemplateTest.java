package ru.alepar.vuzetty.client.association;

import org.junit.Test;
import ru.alepar.vuzetty.client.os.JavaInstallation;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TemplateTest {

    @Test
    public void properlyReplacesValuesWithSpecialCharsInThem() throws Exception {
        final JavaInstallation javaInstallation = new JavaInstallation() {
            @Override
            public String getJavawsBinaryPath() {
                return "smth/abc$5";
            }

            @Override
            public String getJavawsExtArguments() {
                return "-somearg";
            }

            @Override
            public String getJavawsOpenArgument() {
                return "-open";
            }
        };

        final Template template = new Template("ru/alepar/vuzetty/client/association/TemplateTest.template");
        template.populateWith(javaInstallation);
        assertThat(template.get(), equalTo("\"smth/abc$5\" -somearg -open"));
    }

    @Test
    public void replacesBacslashInJavaWsPathWithDoubleBackslash() throws Exception {
        // this one is needed for registry files - reg.exe eats up one backslash on import
        final JavaInstallation javaInstallation = new JavaInstallation() {
            @Override
            public String getJavawsBinaryPath() {
                return "c:\\autoexec.bat";
            }

            @Override
            public String getJavawsExtArguments() {
                return "-somearg";
            }

            @Override
            public String getJavawsOpenArgument() {
                return "-open";
            }
        };

        final Template template = new Template("ru/alepar/vuzetty/client/association/TemplateTest.template");
        template.populateWith(javaInstallation);
        assertThat(template.get(), equalTo("\"c:\\\\autoexec.bat\" -somearg -open"));
    }
}
