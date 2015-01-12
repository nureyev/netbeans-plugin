/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.forge.netbeans.ui.wizard.component;

import java.awt.Container;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.openide.util.ChangeSupport;

/**
 * Generates a JTextField with a JLabel
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class PasswordComponentBuilder extends ComponentBuilder<JPasswordField> {

    @Override
    public JPasswordField build(final Container container, final InputComponent<?, Object> input, final CommandController controller, final ChangeSupport changeSupport) {
        JLabel label = new JLabel(InputComponents.getLabelFor(input, true));
        final JPasswordField txt = new JPasswordField();
        txt.setEnabled(input.isEnabled());
        txt.setToolTipText(input.getDescription());

        final ConverterFactory converterFactory = getConverterFactory();
        if (converterFactory != null) {
            Converter<Object, String> converter = (Converter<Object, String>) converterFactory
                    .getConverter(input.getValueType(), String.class);
            String value = converter
                    .convert(InputComponents.getValueFor(input));
            txt.setText(value == null ? "" : value);
        }

        txt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                controller.setValueFor(input.getName(), new String(txt.getPassword()));
                changeSupport.fireChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                controller.setValueFor(input.getName(), new String(txt.getPassword()));
                changeSupport.fireChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                controller.setValueFor(input.getName(), new String(txt.getPassword()));
                changeSupport.fireChange();
            }
        });

        // Add components to container
        container.add(label);
        container.add(txt);

        return txt;
    }

    @Override
    protected Class<String> getProducedType() {
        return String.class;
    }

    @Override
    protected String getSupportedInputType() {
        return InputType.SECRET;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes() {
        return new Class<?>[]{UIInput.class};
    }
}