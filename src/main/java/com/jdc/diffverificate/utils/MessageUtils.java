package com.jdc.diffverificate.utils;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.DataManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class MessageUtils implements Disposable {
    public static String FLAG = "\033";

    public static final String NOTIFICATION_GROUP = "jdc annotation";

    private Project project;
    private ConsoleView consoleView;
    private ToolWindow toolWindow;

    public MessageUtils(Project project) {
        this.project = project;
        this.toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ConsoleWindowFactory.ID);
    }

    @NotNull
    public static MessageUtils getInstance(Project project) {
        return project.getService(MessageUtils.class);
    }


    public static void showMsg(JComponent component, MessageType messageType, String title, String body) {
        JBPopupFactory factory = JBPopupFactory.getInstance();
        BalloonBuilder builder = factory.createHtmlTextBalloonBuilder(body, messageType, null);
        builder.setTitle(title);
        builder.setFillColor(JBColor.background());
        Balloon b = builder.createBalloon();
        Rectangle r = component.getBounds();
        RelativePoint p = new RelativePoint(component, new Point(r.x + r.width, r.y + 30));
        b.show(p, Balloon.Position.atRight);
    }

    public void showInfoMsg(String title, String body) {
        showConsole(() -> {
            printTitle(title, ConsoleViewContentType.NORMAL_OUTPUT);
            printBody(body, ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
        });
    }

    public void showWarnMsg(String title, String body) {
        showConsole(() -> {
            printTitle(title, ConsoleViewContentType.LOG_INFO_OUTPUT);
            printBody(body, ConsoleViewContentType.LOG_INFO_OUTPUT);
            consoleView.print("\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        });
    }

    public void showErrorMsg(String title, String body) {
        showConsole(() -> {
            printTitle(title, ConsoleViewContentType.ERROR_OUTPUT);
            printBody(body, ConsoleViewContentType.ERROR_OUTPUT);
            consoleView.print("\n", ConsoleViewContentType.ERROR_OUTPUT);
        });
    }

    private void printTitle(String title, ConsoleViewContentType contentType) {
        if (title.equals("info") || title.equals("warning") || title.equals("error")) {
            consoleView.print("> " + DateFormatUtils.format(new Date(), "yyyy/MM/dd' 'HH:mm:ss") + "\n", contentType);
        } else {
            consoleView.print("> " + DateFormatUtils.format(new Date(), "yyyy/MM/dd' 'HH:mm:ss") + "\t" + title + "\n", contentType);
        }
    }

    private void printBody(String body, ConsoleViewContentType contentType) {
        String[] bodys = body.split("\n");
        for (String s : bodys) {
            if (s.contains(FLAG)) {
                String[] sc = s.split(FLAG);
                for (int i = 0; i < sc.length; i++) {
                    if (i % 2 == 0) {
                        consoleView.print(sc[i], contentType);
                    } else {
                        String childStr = sc[i];
                        if (childStr.startsWith("I")) {
                            consoleView.print(sc[i].substring(1), ConsoleViewContentType.NORMAL_OUTPUT);
                        } else if (childStr.startsWith("W")) {
                            consoleView.print(sc[i].substring(1), ConsoleViewContentType.LOG_INFO_OUTPUT);
                        } else if (childStr.startsWith("E")) {
                            consoleView.print(sc[i].substring(1), ConsoleViewContentType.ERROR_OUTPUT);
                        } else {
                            consoleView.print(sc[i].substring(1), contentType);
                        }
                    }
                }
                consoleView.print("\n", contentType);
            } else {
                consoleView.print(s + "\n", contentType);
            }

        }
    }

    public static void showAllWarnMsg(String title, String body) {
        Notifications.Bus.notify(new Notification(NOTIFICATION_GROUP, title, body, NotificationType.WARNING));
    }

    public String getComponentName() {
        return this.getClass().getName();
    }

    public static String format(String body, String type) {
        return FLAG + type + body.replace("\n", FLAG + "\n" + FLAG + type) + FLAG;
    }

    public static String formatDiff(String expected, String output) {
        if ((StringUtils.isBlank(expected) && StringUtils.isNotBlank(output)) || (StringUtils.isNotBlank(expected) && StringUtils.isBlank(output))) {
            return FLAG + "E" + output + FLAG;
        } else if (StringUtils.isBlank(expected) || StringUtils.isBlank(output) || output.equals(expected)) {
            return output;
        } else {
            boolean isDiff = false;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < output.length(); i++) {
                if (i >= expected.length()) {
                    if (!isDiff) {
                        sb.append(FLAG).append("E");
                    }
                    sb.append(output.substring(i)).append(FLAG);
                    isDiff = true;
                    break;
                } else {
                    if (output.charAt(i) == expected.charAt(i)) {
                        if (isDiff) {
                            sb.append(FLAG);
                            isDiff = false;
                        }
                        sb.append(output.charAt(i));
                    } else {
                        if (!isDiff) {
                            sb.append(FLAG).append("E");
                            isDiff = true;
                        }
                        sb.append(output.charAt(i));
                    }
                }

            }
            if (isDiff) {
                sb.append(FLAG);
            }
            return sb.toString();
        }
    }

    private void showConsole(Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (toolWindow == null) {
                toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ConsoleWindowFactory.ID);
            }
            if (toolWindow == null) {
                return;
            }
            if (!toolWindow.isAvailable()) {
                toolWindow.setAvailable(true);
            }
            if (!toolWindow.isActive()) {
                toolWindow.activate(null);
            }
            if (consoleView == null) {
                this.consoleView = ConsoleWindowFactory.getDataContext(project).getData(DataKey.create("CONSOLE_VIEW"));
            }
            consoleView.requestScrollingToEnd();
            runnable.run();
        });


    }

    @Override
    public void dispose() {
        if (consoleView != null) {
            Disposer.dispose(consoleView);
        }
    }
}


 class ConsoleWindowFactory implements ToolWindowFactory, DumbAware {

    public static String ID = "Annotation Console";


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        ConsolePanel consolePanel = new ConsolePanel(toolWindow, project);
        Content content = toolWindow.getContentManager().getFactory().createContent(consolePanel, "", true);
        toolWindow.getContentManager().addContent(content);
//        toolWindow.setIcon("");
    }

    public static DataContext getDataContext(@NotNull Project project) {
        ToolWindow toolWindows = ToolWindowManager.getInstance(project).getToolWindow(ID);
        ConsolePanel consolePanel = (ConsolePanel) toolWindows.getContentManager().getContent(0).getComponent();
        return DataManager.getInstance().getDataContext(consolePanel);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }
}

class ConsolePanel extends SimpleToolWindowPanel implements DataProvider {

    private ConsoleView consoleView;

    public ConsolePanel(ToolWindow toolWindow, Project project) {
        super(Boolean.FALSE, Boolean.TRUE);
        this.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        SimpleToolWindowPanel toolWindowPanel = new SimpleToolWindowPanel(Boolean.FALSE, Boolean.TRUE);
        toolWindowPanel.setContent(consoleView.getComponent());
        setContent(toolWindowPanel);
        final DefaultActionGroup consoleGroup = new DefaultActionGroup(consoleView.createConsoleActions());
        ActionToolbar consoleToolbar = ActionManager.getInstance().createActionToolbar("jdc" + " ConsoleToolbar", consoleGroup, true);
        consoleToolbar.setTargetComponent(toolWindowPanel);
        setToolbar(consoleToolbar.getComponent());
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        return consoleView;
//        return super.getData(dataId);
    }

    public void dispose() {
        if (consoleView != null) {
            Disposer.dispose(consoleView);
        }
    }
}