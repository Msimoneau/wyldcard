package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.layouts.ScriptEditor;
import com.defano.wyldcard.window.layouts.StackWindow;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WindowsMenu extends HyperCardMenu {

    public static WindowsMenu instance = new WindowsMenu();

    public WindowsMenu() {
        super("Windows");
        WindowManager.getInstance().getVisibleWindowsProvider().subscribe(wyldCardFrames -> reset());
    }

    public void reset() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WindowsMenu.super.removeAll();

                MenuItemBuilder.ofDefaultType()
                        .named("Minimize")
                        .withAction(a -> WindowManager.getInstance().getFocusedStackWindow().getWindow().setState(Frame.ICONIFIED))
                        .build(WindowsMenu.this);

                MenuItemBuilder.ofDefaultType()
                        .named("Zoom")
                        .withAction(a -> {
                            JFrame focusedFrame = WindowManager.getInstance().getFocusedStackWindow().getWindow();
                            focusedFrame.setExtendedState(focusedFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                        })
                        .build(WindowsMenu.this);

                MenuItemBuilder.ofDefaultType()
                        .named("Next Window")
                        .withShortcut('.')
                        .withAction(a -> WindowManager.getInstance().nextWindow().getWindow().requestFocus())
                        .build(WindowsMenu.this);

                MenuItemBuilder.ofDefaultType()
                        .named("Previous Window")
                        .withShortcut(',')
                        .withAction(a -> WindowManager.getInstance().prevWindow().getWindow().requestFocus())
                        .build(WindowsMenu.this);

                MenuItemBuilder.ofDefaultType()
                        .named("Restore Default Layout")
                        .withAction(a -> WindowManager.getInstance().restoreDefaultLayout())
                        .build(WindowsMenu.this);

                addSeparator();

                addPalettes(MenuItemBuilder.ofHierarchicalType()
                        .named("Palettes")
                        .build(WindowsMenu.this));

                addScriptEditors(MenuItemBuilder.ofHierarchicalType()
                        .named("Script Editors")
                        .build(WindowsMenu.this));

                addSeparator();

                addStacks();
            }
        });
    }

    private void addPalettes(JMenuItem parent) {
        MenuItemBuilder.ofCheckType()
                .named("Dock to Card Window")
                .withShiftShortcut('D')
                .withAction(a -> WindowManager.getInstance().toggleDockPalettes())
                .withCheckmarkProvider(WindowManager.getInstance().getPalettesDockedProvider())
                .build(parent);

        ((JMenu) parent).addSeparator();

        WindowManager.getInstance().getPalettes(false)
                .forEach(wyldCardFrame -> MenuItemBuilder.ofCheckType()
                        .named(wyldCardFrame.getTitle())
                        .withAction(a -> wyldCardFrame.toggleVisible())
                        .withCheckmarkProvider(wyldCardFrame.getWindowVisibleProvider())
                        .build(parent));
    }

    private void addScriptEditors(JMenuItem parent) {
        List<WyldCardFrame> windows = WindowManager.getInstance().getWindows(true);

        parent.setEnabled(windows.stream().anyMatch(p -> p instanceof ScriptEditor));

        windows.stream()
                .filter(w -> w instanceof ScriptEditor)
                .forEach(wyldCardFrame -> MenuItemBuilder.ofCheckType()
                        .named(wyldCardFrame.getTitle())
                        .withCheckmarkProvider(wyldCardFrame.getWindowFocusedProvider())
                        .withAction(a -> wyldCardFrame.getWindow().requestFocus())
                        .build(parent));
    }

    private void addStacks() {
        WindowManager.getInstance().getWindows(true)
                .stream()
                .filter(w -> w instanceof StackWindow)
                .forEach(wyldCardFrame -> MenuItemBuilder.ofCheckType()
                        .named(wyldCardFrame.getTitle())
                        .withCheckmarkProvider(wyldCardFrame.getWindowFocusedProvider())
                        .withAction(a -> wyldCardFrame.getWindow().requestFocus())
                        .build(WindowsMenu.this));
    }

}
