package com.kronae.roost;

import com.kronae.roost.custom.RoostScript;
import com.kronae.roost.event.*;
import com.kronae.roost.exception.AlreadyOpenException;
import com.kronae.roost.exception.NotOpenException;
import com.kronae.roost.status.CloseType;
import com.kronae.roost.status.WindowStatus;
import com.kronae.roost.structure.ImageStructure;
import com.kronae.roost.structure.RoostStructure;
import com.kronae.roost.structure.SquareStructure;
import com.kronae.roost.structure.TextStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Window2D implements Window {
    /*============================== Field Variables ==============================*/
    private volatile JFrame frame;
    private final ArrayList<RoostScript> scripts;
    private final ArrayList<EventObj> listeners;
    private final ArrayList<RoostStructure> structures;
    private Timer timer;
    private WindowStatus windowStatus = WindowStatus.CLOSE;
    private boolean debugMode;
    private Graphics lastGraphics = null;
    private float opacity = 1.0F;
    private boolean keyMode;

    /*============================== Constructors ==============================*/
    /**
     * Constructor
     * @param debugMode The default DebugMode of the window.
     */
    public Window2D(boolean debugMode) {
        this(new JFrame(), debugMode);
    }
    /**
     * Constructor
     * @param frame The main frame of the window.
     * @param debugMode The default DebugMode of the window.
     */
    public Window2D(JFrame frame, boolean debugMode) {
        this.debugMode = debugMode;
        scripts = new ArrayList<>();
        listeners = new ArrayList<>();
        structures = new ArrayList<>();
        SwingUtilities.invokeLater(() -> {
            this.frame = frame;
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.frame.add(new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponents(g);
                    newFrame(g);
                }
            });
        });
    }

    /*============================== Add ==============================*/
    /**
     * Add script into the window.
     * @param script Roost script
     */
    public void addScript(RoostScript script) {
        scripts.add(script);
    }

    /*============================== Set ==============================*/
    /**
     * Set debug mode(print mode) of the window
     * It will not show at the window
     * It is show at the console(System.out or System.err)
     * @param mode Debug mode
     */
    public void setDebugMode(boolean mode) {
        debugMode = mode;
    }
    public void addStructure(@NotNull RoostStructure structure) {
        structures.add(structure);
    }
    /**
     * Set size of the window
     * @param width Width of the window
     * @param height Height of the window
     */
    public void setSize(int width, int height) {
        while(frame == null)
            Thread.onSpinWait();

        SwingUtilities.invokeLater(() -> frame.setSize(width, height));
    }
    /**
     * Set title of the window
     * @param title Title of the window
     */
    public void setTitle(String title) {
        while(frame == null)
            Thread.onSpinWait();

        SwingUtilities.invokeLater(() -> {
            frame.setTitle(title);
            frame.setName(title);
        });
    }
    /**
     * Set window is resizeable
     * @param b Whether the window can be resized
     */
    public void setResizable(boolean b) {
        while(frame == null)
            Thread.onSpinWait();

        SwingUtilities.invokeLater(() -> frame.setResizable(b));
    }
    /**
     * Set the background color of the window.
     * @param color the background color
     */
    public void setBackgroundColor(Color color) {
        frame.setBackground(color);
    }
    /**
     * Set the cursor shape.
     * @param cursor cursor shape.
     */
    public void setCursor(Cursor cursor) {
        frame.setCursor(cursor);
    }
    /**
     * Set key listening mode of the window.
     * If you turns on, Key...Event will called.
     * @param keyMode keyMode of the window.
     */
    public void setKeyMode(boolean keyMode) {
        this.keyMode = keyMode;
    }
    /**
     * Set the window icon.
     * @param image icon image
     */
    public void setIcon(Image image) {
        frame.setIconImage(image);
    }
    /**
     * Set the font of the window.
     * @param font Font of the window
     */
    public void setFont(Font font) {
        frame.setFont(font);
    }

    /*============================== Get ==============================*/

    /**
     * Get raw JFrame of the window.
     * @return JFrame
     */
    public JFrame getRawFrame() {
        return frame;
    }

    /*============================== Open / Close ==============================*/
    /**
     * Open the window
     * @throws AlreadyOpenException If the window is already open
     */
    public void open() throws AlreadyOpenException {
        while(frame == null)
            Thread.onSpinWait();

        SwingUtilities.invokeLater(() -> {
            // ================================================== OPEN() ================================================== \\
            if(windowStatus == WindowStatus.OPEN) {
                throwError("0x000001", AlreadyOpenException.class, "Open FAILED because it is ALREADY open.", """
                        Do not use open() twice!
                        Why don't you try close() before open() twice?""", true);
                return;
            }
            windowStatus = WindowStatus.OPEN;
            setup();

            // ================================================== Show window ================================================== \\
            frame.setVisible(true);
            scripts.forEach(script -> script.open(this));

            print("Window2D: Open is successfully!");
        });
    }

    private void setup() {
        // ================================================== Window Listener ================================================== \\
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                timer.cancel();
                if(windowStatus == WindowStatus.OPEN) {
                    print("Window2D: WindowListener: Window closing...");
                    close(true, CloseType.NORMAL); // Window Listener
                } else {
                    print("Window2D: WindowAdapter: Close queued.");
                }
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                callEvent(new WindowDeactivateEvent(Window2D.this));
            }

            @Override
            public void windowActivated(WindowEvent e) {
                callEvent(new WindowActivateEvent(Window2D.this));
            }

            @Override
            public void windowClosed(WindowEvent e) {
                print("Window2D: WindowAdapter: Closed.");
            }
        });

        // ================================================== Shutdown Hook ================================================== \\
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(windowStatus == WindowStatus.LOADING) {
                System.out.println("Roost: ShutdownHook: Program is closing.");
            } else if(windowStatus == WindowStatus.OPEN) {
                System.out.println("Roost: ShutdownHook: Program is UNEXPECTED closing.");
                closeUnexpected();
                System.out.println("Roost: ShutdownHook: Program is UNEXPECTED closed.");
            }
        }));

        // ================================================== Timer ================================================== \\
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(windowStatus == WindowStatus.CLOSE)
                    System.exit(0);

                if(windowStatus == WindowStatus.OPEN)
                    newFrame(null);
            }
        }, 0, 1);
        // KEY
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(keyMode)
                    callEvent(new KeyTypeEvent(Window2D.this, e.getKeyChar(), e.getKeyCode(), e.isActionKey()));
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(keyMode)
                    callEvent(new KeyPressEvent(Window2D.this, e.getKeyChar(), e.getKeyCode(), e.isActionKey()));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(keyMode)
                    callEvent(new KeyReleaseEvent(Window2D.this, e.getKeyChar(), e.getKeyCode(), e.isActionKey()));
            }
        });
    }

    /**
     * Close the window with CloseType.
     * @param cancelable Whether cancellation is possible
     * @param closeType The type of close.
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
        print("Window2D: close(): Close queued: " + closeType );
        windowStatus = WindowStatus.LOADING;

        for (int i = 0; i < scripts.toArray().length; i++) {
            if ((!scripts.get(i).closeQueue()) && cancelable) { // If closeQueue return false, It means oppose. and it will not stop.
                windowStatus = WindowStatus.OPEN;
                print("Window2D: close(): Close canceled by scripts.");
                return false;
            }
        }

        print("Window2D: close(): Executing close script...");
        for (int i = 0; i < scripts.toArray().length; i++) {
            scripts.get(i).close(closeType);
        }

        print("Window2D: close(): Timer canceling...");
        timer.cancel();
        timer.purge();

        print("Window2D: close(): Closing...");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        print("Window2D: close(): Set null...");
        frame = null;

        print("Window2D: close(): Setting window status to CLOSE.");
        windowStatus = WindowStatus.CLOSE;
        print("Window2D: close(): Closed.");

        return windowStatus == WindowStatus.CLOSE;
    }

    /**
     * Close unexpected.
     */
    public void closeUnexpected() {
        windowStatus = WindowStatus.LOADING;
        for (int i = 0; i < scripts.toArray().length; i++) {
            scripts.get(i).closeQueue();
        }
        print("Window2D: close(): Executing close script...");
        for (int i = 0; i < scripts.toArray().length; i++) {
            scripts.get(i).close(CloseType.UNEXPECTED);
        }
        timer.cancel();
        timer.purge();
        windowStatus = WindowStatus.CLOSE;
    }
    /**
     * Close the window
     * @param cancelable Whether cancellation is possible
     * @return Is closed
     */
    public boolean close(boolean cancelable) {
        return close(cancelable, CloseType.SUCCESSFULLY);
    }
    /*============================== Show / Hide / Opacity ==============================*/
    /**
     * Show the window.
     */
    public void show() {
        frame.setOpacity(opacity);
    }
    /**
     * Hide the window.
     */
    public void hide() {
        frame.setOpacity(0.0F);
    }
    /**
     * Set the opacity of the window.
     * It will work at the window is showing.
     * @param opacity opacity
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
        if(frame.isShowing())
            frame.setOpacity(opacity);
    }
    /**
     * is Showing?
     * @return isShowing?
     */
    public boolean isShowing() {
        return frame.isShowing();
    }
    /*============================== Draw frame ==============================*/

    /**
     * Show new frame. It will auto run.
     * @return success or failed
     */
    public boolean newFrame() {
        return newFrame(null);
    }
    /**
     * Show new frame with graphics. It will auto run.
     * @return success or failed
     */
    public boolean newFrame(@Nullable Graphics g) {
        scripts.forEach(script -> script.update(windowStatus));
        return draw(g);
    }

    /**
     * Draw structures. It runs from newFrame(Graphics) method.
     * @param g Graphics. Probably, it will the lastGraphic cache.
     * @return success or failed.
     */
    private boolean draw(Graphics g) {
        if(lastGraphics == null && g == null) return false;
        if(g == null)
            g = lastGraphics;
        else
            lastGraphics = g;

        for (RoostStructure structure : structures) {
            if(structure instanceof SquareStructure squareStructure) {
                g.drawRect(squareStructure.getLocationX(), squareStructure.getLocationY(), squareStructure.getSizeX(), squareStructure.getSizeY());
            } else if (structure instanceof ImageStructure imageStructure) {
                if(imageStructure.getSizeX() == -1 && imageStructure.getSizeY() == -1) {
                    g.drawImage(imageStructure.getImage(), imageStructure.getLocationX(), imageStructure.getLocationY(), null);
                } else {
                    g.drawImage(imageStructure.getImage(), imageStructure.getLocationX(), imageStructure.getLocationY(), imageStructure.getSizeX(), imageStructure.getSizeY(), null);
                }
            } else if (structure instanceof TextStructure textStructure) {
                try {
                    g.drawString(textStructure.getContent(), textStructure.getX(), textStructure.getY());
                } catch(Exception e) {
                    throwError("0x000005", RuntimeException.class, "An exception is occurred while drawing window(Drawing String)", """
                            This is unknown error. We can't help you.
                            However, since we know under what conditions this error occurred at the time,
                            We will deliver information about it.
                            Time: {TIME}
                            TextStructure:""" + " " + textStructure, true);
                }
            } else {
                System.err.println("Roost: Window2D: Unknown roost structure: " + structure.getClass().getName());
            }
        }
        return true;
    }
    /*============================== Event ==============================*/
    /**
     * Add event listeners
     * @param eventListener Listener
     */
    public void addEventListener(@NotNull EventListener eventListener) {
        if(eventListener.getClass().isAnonymousClass()) {
            System.err.println("Roost: Window2D: WARNING: You are added anonymous eventListener.");
            listeners.add(new EventObj(eventListener.getClass(), eventListener));
        } else {
            listeners.add(new EventObj(eventListener.getClass(), eventListener));
        }
    }
    /**
     * Call event
     * @param event RoostEvent
     */
    public void callEvent(RoostEvent event) {
        if(windowStatus != WindowStatus.OPEN) throwError("0x000002", NotOpenException.class, "Cannot call event because the window is not open. (Status: " + windowStatus + ")", "Cannot call event because the window is not open.", false);

        for (EventObj obj : listeners) {
            Method[] methods = obj.clazz().getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(EventHandler.class)) {
                    Class<?>[] params = method.getParameterTypes();
                    if(params.length == 1) {
                        if(params[0] == event.getClass()) {
                            try {
                                method.invoke(obj.instance(), event);
                            } catch (IllegalAccessException e) {
                                if(obj.clazz().isAnonymousClass()) {
                                    throwError("0x000004-1", IllegalAccessError.class, "Cannot invoke the method because Window2D CANNOT access to the method.", """
                                            Cannot invoke the method because Window2D class CANNOT access to the @EventHandler method.
                                            If you wanna solve this error, you have to remove anonymous.
                                            Hope it helps!""", true);
                                } else {
                                    throwError("0x000004-2", IllegalAccessError.class, "Cannot invoke the method because Window2D CANNOT access to the method.", """
                                            Cannot invoke the method because Window2D class CANNOT access to the @EventHandler method.
                                            If you wanna solve this error, you have to add 'public' access modifier in front of Class or Method.
                                            Hope it helps!""", true);
                                }
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }
    /*============================== DIALOG ==============================*/
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
    /*============================== PRIVATE ==============================*/
    /**
     * Throw new Roost error.
     * @param errorCode error code of the error
     * @param errorType error type of the error
     * @param simpleMsg shorter msg
     * @param errorMsg logger msg
     */
    private <EXCEPTION extends Throwable> void throwError(String errorCode, @NotNull Class<EXCEPTION> errorType, String simpleMsg, String errorMsg, boolean closeWindow) throws EXCEPTION {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.err.println("Roost: ERROR: " + errorMsg.replace("\n", "\nRoost: ERROR: ").replace("{TIME}", System.currentTimeMillis() + ""));
                System.err.println("Error Code: " + errorCode);
                if(closeWindow) {
                    if(windowStatus != WindowStatus.OPEN) {
                        System.err.println("==============================================================================");
                        System.err.println("Roost: ERROR: Cannot close because it is not open!");
                        System.err.println("Error Code: " + "0x000003");
                        return;
                    }
                    close(false, CloseType.ERROR);
                }
            }
        }, 5);
        try {
            throw errorType.getDeclaredConstructor(String.class).newInstance("Roost: ERROR: " + simpleMsg);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(System.err);
            System.err.printf("Roost: ERROR: throwError(): Cannot throw exception '%s' because an error is occurred. Error Code: %s\n", errorType.getName(), "0x000001-1");
            close(false, CloseType.ERROR);
        } catch (NoSuchMethodException e) {
            e.printStackTrace(System.err);
            System.err.printf("Roost: ERROR: throwError(): Cannot throw exception '%s' because an error is occurred. Error Code: %s\n", errorType.getName(), "0x000001-2");
            close(false, CloseType.ERROR);
        }
    }
    /**
     * Output Roost messages to System.out.
     * @param msg Message to send
     */
    private void print(String msg) {
        if(debugMode)
            System.out.println("Roost: " + msg);
    }
}
