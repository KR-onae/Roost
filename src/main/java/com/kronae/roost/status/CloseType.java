package com.kronae.roost.status;

public enum CloseType {
    /**
     * The format in which a program usually exits
     */
    SUCCESSFULLY,
    /**
     * The form in which the program encountered an error and exited
     */
    ERROR,
    /**
     * The form in which the program is forcibly terminated
     * Ex) End with task manager
     */
    UNEXPECTED,
    /**
     * A form in which the user exits by pressing the X button
     */
    NORMAL;
}
