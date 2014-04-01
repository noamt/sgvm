package org._10ne.sgvm
/**
 * @author Noam Y. Tenne.
 */
class Gvm {

    public static Use use() {
        constructUse()
    }

    public static Use getUse() {
        constructUse()
    }

    public static Install install() {
        constructInstall()
    }

    public static Install getInstall() {
        constructInstall()
    }

    public static Uninstall uninstall() {
        constructUninstall()
    }

    public static Uninstall getUninstall() {
        constructUninstall()
    }

    private static Use constructUse() {
        new Use(context: Context.get())
    }

    private static Install constructInstall() {
        new Install(context: Context.get())
    }

    private static Uninstall constructUninstall() {
        new Uninstall(context: Context.get())
    }
}