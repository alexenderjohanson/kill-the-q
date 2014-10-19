package com.gsy.femstoria.restful.Formatter;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Converts standard CamelCase field and method names to
 * typical JSON field names having all upper case characters
 * with an underscore separating different words.  For
 * example, all of the following are converted to JSON field
 * name "SomeName"
 */
public class UpperCamelCaseNamingStrategy extends PropertyNamingStrategy {
    /**
	 * 
	 */
	private static final long	serialVersionUID	= 687713296049180705L;

	@Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return translate(defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return translate(defaultName);
    }

    @Override
    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
        return translate(defaultName);
    }

    private String translate(String defaultName) {
        char[] nameChars = defaultName.toCharArray();
        StringBuilder nameTranslated = new StringBuilder(nameChars.length * 2);

        // First case should be upper
        boolean isUpper = true;
        for (char c : nameChars) {
            if (c == '_') {
                isUpper = true;
                continue;
            }

            if (isUpper) {
                c = Character.toUpperCase(c);
                isUpper = false;
            }

            nameTranslated.append(c);
        }
        return nameTranslated.toString();
    }
}
