/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.converter.base;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author telman
 */
@FacesConverter("fmsDateTimeConverter")
public class FmsDateTimeConverter implements Converter {

    Map<String, DateFormat> dfs = new HashMap();

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        try {
            String pattern = uic.getAttributes().get("pattern").toString();
            DateFormat df = dfs.get(pattern);
            if (df == null) {
                df = new SimpleDateFormat(pattern);
                dfs.put(pattern, df);
            }
            return df.parse(string);
        } catch (ParseException ex) {
            Logger.getLogger(FmsDateTimeConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConverterException();
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        try {
            String pattern = uic.getAttributes().get("pattern").toString();
            DateFormat df = dfs.get(pattern);
            if (df == null) {
                df = new SimpleDateFormat(pattern);
                dfs.put(pattern, df);
            }
            String s = df.format((Date) o);
            return s;
        } catch (Exception ex) {
            Logger.getLogger(FmsDateTimeConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConverterException();
        }
    }

}
