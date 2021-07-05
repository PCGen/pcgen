package pcgen.gui2.csheet;

import pcgen.facade.core.CharacterFacade;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLInputElement;

/**
 * A {@link ChangeListener} that decorates a new document in context to automatically save inputs tagged with "target_var"
 */
public class PreviewVariablesHandler implements ChangeListener<Document>
{
    private CharacterFacade character;

    public void setCharacter(CharacterFacade character)
    {
        this.character = character;
    }

    @Override
    public void changed(ObservableValue<? extends Document> observableValue, Document oldDoc, Document newDoc)
    {
        if (newDoc == null)
        {
            return;
        }
        NodeList elements = newDoc.getElementsByTagName("input");
        for (int i = 0; i < elements.getLength(); i++)
        {
            HTMLInputElement element = (HTMLInputElement) elements.item(i);
            String key = getInputKey(element);
            if (key == null || key.isEmpty())
            {
                continue;
            }
            setInputValue(element, character.getPreviewSheetVar(key));
            ((EventTarget) element).addEventListener("change", evt ->
            {
                HTMLInputElement input = (HTMLInputElement) evt.getCurrentTarget();
                character.addPreviewSheetVar(getInputKey(input), getInputValue(input));
            }, false);
        }
    }

    private boolean isCheckable(HTMLInputElement input)
    {
        String type = input.getAttribute("type").toLowerCase();
        return "checkbox".equals(type) || "radio".equals(type);
    }

    private String getInputKey(HTMLInputElement input)
    {
        return input.getAttribute("target_var");
    }

    private String getInputValue(HTMLInputElement input)
    {
        if (isCheckable(input))
        {
            return Boolean.toString(input.getChecked());
        } else
        {
            return input.getValue();
        }
    }

    private void setInputValue(HTMLInputElement input, String previewSheetVar)
    {
        if (isCheckable(input))
        {
            input.setChecked(Boolean.parseBoolean(previewSheetVar));
        } else
        {
            input.setValue(previewSheetVar);
        }
    }
}
