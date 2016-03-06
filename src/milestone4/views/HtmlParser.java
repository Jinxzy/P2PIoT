package milestone4.views;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;


public class HtmlParser {
    private final String TEMPLATES_FOLDER = "src/templates";

    // java always so helpful :(
    public enum Templates{
        INDEX {
            public String toString() {
                return "index.ftl";
            }
        },
        PHOTON {
            public String toString() {
                return "photon.ftl";
            }
        }

    }
    Configuration cfg;

    public HtmlParser(){
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.ENGLISH);
        cfg.setNumberFormat("0.######");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        try {
            cfg.setDirectoryForTemplateLoading(new File(TEMPLATES_FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parse(Templates templateType, Map<String, Object> data){

        try {
            // loads the template, and process it to a string writer
            Template template = cfg.getTemplate(templateType.toString());
            Writer out = new StringWriter();
            template.process(data, out);
            return out.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return "";
    }
}
