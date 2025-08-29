package my.cvmanager.web.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;
import my.cvmanager.domain.Technology;

@Named("levelConverter")
@ApplicationScoped
public class LevelConverter implements Converter<Technology.Level> {

    @Override
    public Technology.Level getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        int n = Integer.parseInt(value);
        Technology.Level[] arr = Technology.Level.values();
        int idx = Math.max(1, Math.min(n, arr.length)) - 1;

        return arr[idx];
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Technology.Level value) {
        String val = String.valueOf(value.ordinal() + 1);
        return (value == null) ? "" : val;
    }
}