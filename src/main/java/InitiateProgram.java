/*
   InitiateProgram is the entry point to the program.
   All it does when the program is executed is create an instance of
   a GUIManager object. The GUIManager will manage and run
   the GUI and handle all user events until the program is closed.
 */

package main.java;

public class InitiateProgram {
    public static void main(String[] args) {
        new GUIManager();
    }
}
