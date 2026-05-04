interface ToastInterface {
    add(args: {
        severity?: string;
        summary?: string;
        detail?: string;
        life?: number;
        closeable?: boolean;
        group?: string;
    }) : void;
    removeGroup(group: string) : void;
    removeAllGroups() : void;
}

class ToastMessageUtils {

    /** 
     * primevueのトースト
     *  */
    private static toast: ToastInterface;

    public static init(toast: ToastInterface): void {
        ToastMessageUtils.toast = toast;
    }

    public static success(message: string): void {
        ToastMessageUtils.toast.add({ 
            severity: 'success', 
            detail: message, 
            life: 5000 
        });
    }

    public static notice(message: string): void {
        ToastMessageUtils.toast.add({ 
            severity: 'info',
            detail: message,
            life: 5000
        });
    }

    public static error(message: string): void {
        ToastMessageUtils.toast.add({ 
            severity: 'error', 
            detail: message,
            life: 5000
        });
    }

    public static warning(message: string): void {
        ToastMessageUtils.toast.add({ 
            severity: 'warn',
            detail: message,
            life: 5000
        });
    }

    public static remove(): void {
        ToastMessageUtils.toast.removeAllGroups();
    }
}

export { ToastMessageUtils };