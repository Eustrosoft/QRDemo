package org.eustrosoft.configurations;

import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.enums.FormFieldType;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Configuration
public class DefaultTemplateConfig {
    private static final String DEFAULT_TEMPLATE_NAME = "Шаблон по-умолчанию";

    public Form getDefaultTemplate() {
        Form f = new Form();
        f.setName(DEFAULT_TEMPLATE_NAME);
        f.setDescription(EMPTY);
        f.setFields(getDefaultFields());
        return f;
    }

    public List<FormField> getDefaultFields() {
        List<FormField> ff = new ArrayList<>();
        ff.add(getFormField("cnum", "№ Договора", true, false, FormFieldType.TEXT, 1));
        ff.add(getFormField("cdate", "Дата договора", true, false, FormFieldType.DATE, 2));
        ff.add(getFormField("price_gpl", "Цена", true, false, FormFieldType.TEXT, 3));
        ff.add(getFormField("prodtype", "Тип продукта", true, false, FormFieldType.TEXT, 4));
        ff.add(getFormField("prodmodel", "Модель продукта", true, false, FormFieldType.TEXT, 5));
        ff.add(getFormField("pmrevision", "Ревизия модели", true, false, FormFieldType.TEXT, 6));
        ff.add(getFormField("sn", "SN", true, false, FormFieldType.TEXT, 7));
        ff.add(getFormField("prodate", "Дата производства", true, false, FormFieldType.DATE, 8));
        ff.add(getFormField("gtd", "Номер ГТД", true, false, FormFieldType.TEXT, 9));
        ff.add(getFormField("saledate", "Дата продажи", true, false, FormFieldType.DATE, 10));
        ff.add(getFormField("sendate", "Дата отправки клиенту", true, false, FormFieldType.DATE, 11));
        ff.add(getFormField("wstart", "Дата начала гарантии", true, false, FormFieldType.DATE, 12));
        ff.add(getFormField("wend", "Дата окончания гарантии", true, false, FormFieldType.DATE, 13));
        ff.add(getFormField("gis_long", "Долгота", true, false, FormFieldType.TEXT, 14));
        ff.add(getFormField("gis_lat", "Широта", true, false, FormFieldType.TEXT, 15));
        ff.add(getFormField("gis_alt", "Высота (м)", true, false, FormFieldType.TEXT, 16));
        ff.add(getFormField("comment", "Комментарий", true, false, FormFieldType.TEXT, 17));
        return ff;
    }

    private FormField getFormField(
            String name, String caption,
            boolean isPublic, boolean isStatic,
            FormFieldType fieldType, int fieldOrder
    ) {
        FormField ff = new FormField();
        ff.setName(name);
        ff.setCaption(caption);
        ff.setIsPublic(isPublic);
        ff.setIsStatic(isStatic);
        ff.setFieldType(fieldType);
        ff.setFieldOrder(fieldOrder);
        return ff;
    }
}
