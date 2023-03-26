package com.kronae.roost;

import com.kronae.roost.custom.RoostScript;
import com.kronae.roost.custom.RoostStructure;
import com.kronae.roost.event.*;
import com.kronae.roost.exception.AlreadyOpenException;
import com.kronae.roost.status.CloseType;
import com.kronae.roost.status.WindowStatus;
import com.kronae.roost.structure.SquareStructure;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Timer;

public class Window2D implements Window {
    /**
     * The main RoostWindowFrame(JFrame) of the window
     */
    private RoostWindowFrame frame;

    /**
     * Scripts of the window
     */
    private ArrayList<RoostScript> scripts;

    /**
     * Event Listeners of the window
     */
    private ArrayList<EventObj> listeners = new ArrayList<>();

    /**
     * Structures of the window
     */
    private ArrayList<RoostStructure> structures;

    /**
     * Timer to run Update scripts.
     */
    private @Getter Timer timer;

    /**
     * It will show the status of the window.
     * It can be OPEN, LOADING, CLOSE.
     */
    private WindowStatus windowStatus = WindowStatus.CLOSE;

    /**
     * Debug mode of the window.
     * Default value is FALSE.
     */
    private boolean debugMode = false;


    /**
     * Constructor
     */
    public Window2D() {
    }

    /**
     * Setup window.
     * @param debugMode The default debugMode
     */
    public void setup(boolean debugMode) {
        frame = new RoostWindowFrame();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        scripts = new ArrayList<>();
        listeners = new ArrayList<>();
        structures = new ArrayList<>();
        this.debugMode = debugMode;
    }

    /**
     * Add script into the window.
     * @param script Roost script
     */
    public void addScript(RoostScript script) {
        scripts.add(script);
    }

    /**
     * Add square structure.
     * @param square Square structure
     */
    public void addSquare(SquareStructure square) {
        structures.add(square);
    }

    /**
     * Set debug mode(print mode) of the window
     * It will not show at the window
     * It is show at the console(System.out or System.err)
     * @param mode Debug mode
     */
    public void setDebugMode(boolean mode) {
        debugMode = mode;
    }

    /**
     * Open the window
     * @throws AlreadyOpenException If the window is already open
     */
    public void open() throws AlreadyOpenException {
        // ================================================== OPEN() ================================================== \\
        if(windowStatus == WindowStatus.OPEN) throw new AlreadyOpenException("Roost: Window2D: Open FAILED because it is ALREADY open.\nError Code: 0x000001");
        windowStatus = WindowStatus.OPEN;
        // ================================================== Window Listener ================================================== \\
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if(windowStatus == WindowStatus.OPEN) {
                    print("Window2D: WindowListener: Window closing...");
                    close(true, CloseType.SUCCESSFULLY); // Window Listener
                }
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                callEvent(new WindowDeactivateEvent());
            }

            @Override
            public void windowActivated(WindowEvent e) {
                callEvent(new WindowActivateEvent());
            }
        });

        // ================================================== Shutdown Hook ================================================== \\
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(windowStatus == WindowStatus.LOADING) {
                System.out.println("Roost: ShutdownHook: Program is ALREADY queued to close.");
            } else if(windowStatus == WindowStatus.OPEN) {
                close(false, CloseType.FORCIBLY);    // Shutdown Hook
                System.out.println("Roost: ShutdownHook: Program is FORCIBLY closed.");
            }
        }));

        // ================================================== Show window ================================================== \\
        frame.setVisible(true);

        // ================================================== Run scripts ================================================== \\
        scripts.forEach(script -> script.open(this));

        // ================================================== Timer ================================================== \\
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            if(windowStatus == WindowStatus.CLOSE) System.exit(0);

            if(windowStatus == WindowStatus.OPEN) {
                Graphics g = frame.getGraphic();
                if (g != null) {
                    frame.clearWindow();
                    Graphics2D graphics = (Graphics2D) g;

                    graphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
                    structures.forEach(structure -> {
                        if (structure instanceof SquareStructure square) {
                            graphics.drawRect(square.getLocationX(), square.getLocationY(), square.getSizeX(), square.getSizeY());
                        }
                    });
                    frame.paint(graphics);
                }
            }
            scripts.forEach(script -> script.update(windowStatus));
            }
        }, 0, 1);

        print("Window2D: Open is successfully!");
    }

    /**
     * Add event listeners
     * @param eventListener Listener
     */
    public void addEventListener(@NotNull EventListener eventListener) {
        addEventListener(eventListener, constructor -> {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                System.err.println("""
                            Roost: Window2D:   Are you using addEventListener(new EventListener() {...}) ?
                            Roost: Window2D: - If so, don't use it.
                            Roost: Window2D:   That's against OOP, and it doesn't work!
                            Roost: Window2D:   Create a file that implements Listener and use it.
                            Roost: Window2D: - If there is no code using that method,
                            Roost: Window2D:   When creating a class or method, write the access modifier public in front!
                            Roost: Window2D:   Hope this helps.
                            Roost: Window2D:   Error Code: 0x000002-2""");
            }
            return null;
        });
    }

    /**
     * Add event listeners
     * @param eventListener Listener
     * @param setter Parameter setter
     */
    public void addEventListener(@NotNull EventListener eventListener, EventConstructorArgumentSetter setter) {
        ArrayList<Method> methods = new ArrayList<>();
        for (Method method : eventListener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {

                Class<?>[] parameters = method.getParameterTypes();
                if (!(parameters.length == 1 && RoostEvent.class.isAssignableFrom(parameters[0]))) {
                    System.err.println("""
                            Roost: Window2D: callEvent(): @EventHandler methods cannot be added.
                            Roost: Window2D: callEvent(): This is because the method requires more than one parameter, or
                            Roost: Window2D: callEvent(): The first parameter does not require an event.
                            Roost: Window2D: callEvent(): This is just a warning message.
                            Roost: Window2D: callEvent(): To get rid of this, you can either make the @EventHandler method appropriate parameters or remove @EventHandler from the method so it is not an event handler.""");
                    continue;
                }

                methods.add(method);
            }
        }

        listeners.add(new EventObj(methods, setter));
    }

    /**
     * Call event
     * @param event Event
     */
    public boolean callEvent(RoostEvent event) {
        if(windowStatus != WindowStatus.OPEN) return false;

        for (EventObj obj : listeners) {
            for (Method method : obj.listeners) {
                if(method.getDeclaringClass().getDeclaredConstructors().length == 0) {
                    System.err.println("Roost: Window2D: Cannot call event '" + event.getClass().getName() + "' because there is NO constructor on the class(" + method.getDeclaringClass().getPackageName() + ".* extends Window2D)");
                    System.err.println("Roost: Window2D: *Maybe the class name is '" + method.getDeclaringClass().getName() + "'");
                    return false;
                }

                try {
                try {
                try {


                    Constructor<?> cons = method.getDeclaringClass().getDeclaredConstructors()[0];
                    Class<?>[] params = method.getParameterTypes();
                    if(params.length == 1) {
                        if(params[0].getName().equals(event.getClass().getName())) {
                            Object v = obj.setter.run(cons);
                            if(v == null) {
                                System.err.println("""
                                        Roost: Window2D: callEvent(): EventConstructorArgumentSetter.run returned null.
                                        Roost: Window2D: callEvent(): If not set, check if there is an error.
                                        Roost: Window2D: callEvent(): ErrorCode: 0x000006""");
                                return false;
                            }
                            method.invoke(v, event);
                        }
                    } else {
                        System.err.println("""
                                Roost: Window2D: callEvent(): A method could not be called.
                                Roost: Window2D: callEvent(): Because the method requires a number of parameters, not just one.
                                Roost: Window2D: callEvent(): This message is not an error. It is just a warning message.
                                Roost: Window2D: callEvent(): To prevent these messages from appearing,
                                Roost: Window2D: callEvent(): Set the number of parameters of the @EventHandler method to 1, and set the type of the parameter to the event type.
                                Roost: Window2D: callEvent(): ErrorCode: 0x000005""");
                    }


                } catch (InvocationTargetException e) {
                    System.err.println("Roost: Window2D: Unknown exception");
                    e.printStackTrace();
                    System.err.println("""
                            Roost: Window2D:   InvocationTargetException or InstantiationException is occurred.
                            Roost: Window2D:   Error Code: 0x000004""");
                    close(false, CloseType.FORCIBLY); // Error Code 0x000004
                }} catch (IllegalArgumentException e) {
                    System.err.println("Roost: Window2D: Cannot call event '" + event.getClass().getName() + "' Constructor of EventListener requires some arguments.");
                    e.printStackTrace();
                    System.err.println("""
                            Roost: Window2D:   Does the listener's constructor require multiple arguments?
                            Roost: Window2D: - If so, either create a new constructor that accepts nothing, or delete the old one.
                            Roost: Window2D:   And put the empty constructor on top of other constructors.
                            Roost: Window2D:   Hope this helps.
                            Roost: Window2D:   Error Code: 0x000003""");
                    close(false, CloseType.FORCIBLY); // Error Code 0x000003
                }} catch (IllegalAccessException e) {
                    System.err.println("Roost: Window2D: Cannot call event '" + event.getClass().getName() + "' because Window2D cannot access to class(" + method.getDeclaringClass().getPackageName() + ".* extends Window2D)");
                    System.err.println("Roost: Window2D: *Maybe the class name is '" + method.getDeclaringClass().getName() + "'");
                    e.printStackTrace();
                    System.err.println("""
                            Roost: Window2D:   Are you using addEventListener(new EventListener() {...}) ?
                            Roost: Window2D: - If so, don't use it.
                            Roost: Window2D:   That's against OOP, and it doesn't work!
                            Roost: Window2D:   Create a file that implements Listener and use it.
                            Roost: Window2D: - If there is no code using that method,
                            Roost: Window2D:   When creating a class or method, write the access modifier public in front!
                            Roost: Window2D:   Hope this helps.
                            Roost: Window2D:   Error Code: 0x000002-1""");
                    close(false, CloseType.FORCIBLY); // Error Code 0x000002
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Close the window
     * @param cancelable Whether cancellation is possible
     * @return Is closed
     */
    public boolean close(boolean cancelable) {
        return close(cancelable, CloseType.SUCCESSFULLY);
    }

    /**
     * Close the window
     * @param cancelable Whether cancellation is possible
     * @param closeType Closing form the script will receive
     * @return Is closed
     */
    public boolean close(boolean cancelable, CloseType closeType) {
        if (windowStatus == WindowStatus.CLOSE) {
            System.err.println("Roost: Window2D: close(): Already closed.");
            return false;
        } else if(windowStatus == WindowStatus.LOADING) {
            System.err.println("Roost: Window2D: close(): Already close queued.");
            return false;
        }
        print("Window2D: close(): Close queued.");
        windowStatus = WindowStatus.LOADING;

        if(cancelable) {
            for (int i = 0; i < scripts.toArray().length; i++) {
                if (!scripts.get(i).closeQueue()) {
                    windowStatus = WindowStatus.OPEN;
                    print("Window2D: close(): Close canceled by scripts.");
                    return false;
                }
            }
        }

        for (int i = 0; i < scripts.toArray().length; i++) {
            scripts.get(i).close(closeType);
        }

        windowStatus = WindowStatus.CLOSE;
        print("Window2D: close(): Closed.");

        return windowStatus == WindowStatus.CLOSE;
    }
    /**
     * Set size of the window
     * @param width Width of the window
     * @param height Height of the window
     */
    public void setSize(int width, int height) {
        frame.setSize(width, height);
    }

    /**
     * Set title of the window
     * @param title Title of the window
     */
    public void setTitle(String title) {
        frame.setTitle(title);
    }

    /**
     * Set window is resizeable
     * @param b Whether the window can be resized
     */
    public void setResizable(boolean b) {
        frame.setResizable(b);
    }

    /**
     * Output Roost messages to System.out.
     * @param title Title of the dialog
     * @param message Message of the dialog
     * @param optionType Type of options
     * @param messageType Type of Message
     * @return clicked button of dialog
     */
    public int showConfirmDialog(String title, Object message, int optionType, int messageType) {
        if(windowStatus != WindowStatus.CLOSE) return JOptionPane.showConfirmDialog(frame, message, title, optionType, messageType);
        return -1;
    }

    /**
     * Output Roost messages to System.out.
     * @param msg Message to send
     */
    private void print(String msg) {
        if(debugMode) System.out.println("Roost: " + msg);
    }
}
