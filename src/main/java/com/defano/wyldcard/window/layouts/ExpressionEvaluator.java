package com.defano.wyldcard.window.layouts;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.editor.HyperTalkTextEditor;
import com.defano.wyldcard.editor.SyntaxParserDelegate;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.interpreter.CompilationUnit;
import com.defano.wyldcard.runtime.interpreter.Interpreter;
import com.defano.wyldcard.runtime.interpreter.MessageEvaluationObserver;
import com.defano.wyldcard.window.WyldCardDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

import javax.swing.*;
import java.awt.*;

public class ExpressionEvaluator extends WyldCardDialog<Object> implements SyntaxParserDelegate {
    private JButton evaluateButton;
    private JTextArea resultField;
    private JPanel windowPanel;
    private JLabel contextField;
    private JButton closeButton;
    private JPanel editorArea;

    private ExecutionContext context = new ExecutionContext();
    private HyperTalkTextEditor editor;

    public ExpressionEvaluator() {
        context.unbind();

        editor = new HyperTalkTextEditor(this);
        editorArea.setLayout(new BorderLayout());
        editorArea.add(editor);

        closeButton.addActionListener(a -> setVisible(false));

        evaluateButton.addActionListener(a -> {
            Interpreter.asyncInContextEvaluate(context, editor.getScriptField().getText(), new MessageEvaluationObserver() {
                @Override
                public void onMessageEvaluated(String result) {
                    SwingUtilities.invokeLater(() -> resultField.setText(result));
                }

                @Override
                public void onEvaluationError(HtException exception) {
                    SwingUtilities.invokeLater(() -> resultField.setText("Error: " + exception.getMessage()));
                }
            });
        });
    }

    @Override
    public JComponent getWindowPanel() {
        return windowPanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    public ExecutionContext getContext() {
        return context;
    }

    public void setContext(ExecutionContext context) {
        this.context = context;
        contextField.setText(context.toString());
    }

    @Override
    public JButton getDefaultButton() {
        return evaluateButton;
    }

    @Override
    public CompilationUnit getParseCompilationUnit() {
        return CompilationUnit.SCRIPTLET;
    }

    @Override
    public void onRequestParse(Parser syntaxParser) {
        editor.getScriptField().forceReparsing(syntaxParser);
    }

    @Override
    public void onCompileStarted() {
        // Nothing to do
    }

    @Override
    public void onCompileCompleted(Script compiledScript, String resultMessage) {
        // Nothing to do
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        windowPanel = new JPanel();
        windowPanel.setLayout(new GridLayoutManager(4, 3, new Insets(10, 10, 10, 10), -1, -1));
        contextField = new JLabel();
        contextField.setEnabled(false);
        contextField.setText("Context");
        windowPanel.add(contextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        evaluateButton = new JButton();
        evaluateButton.setText("Evaluate");
        windowPanel.add(evaluateButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        closeButton = new JButton();
        closeButton.setText("Close");
        windowPanel.add(closeButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editorArea = new JPanel();
        editorArea.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        windowPanel.add(editorArea, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(400, 200), null, 0, false));
        final Spacer spacer1 = new Spacer();
        windowPanel.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        windowPanel.add(scrollPane1, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 60), null, 0, false));
        resultField = new JTextArea();
        resultField.setEditable(false);
        resultField.setEnabled(true);
        scrollPane1.setViewportView(resultField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return windowPanel;
    }
}
