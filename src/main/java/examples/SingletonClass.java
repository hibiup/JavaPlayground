package examples;

import org.apache.log4j.Logger;

public class SingletonClass {
    private static final Logger logger = Logger.getLogger(SingletonClass.class);

    private static volatile SingletonClass instance;

    private SingletonClass(){
        logger.debug("Singleton instance is creating.");
    }

    /**
     * Example 1:
     *   Lazy thread safe singleton
     * */
    public static synchronized SingletonClass getInstance() {
        if (instance == null) {
            instance = new SingletonClass();
        }
        return instance;
    }


    /**
     * Example 2:
     *   Eager eval singleton (Thread safe)
     * */
    private static class Keeper {
        final static SingletonClass instance = new SingletonClass();
    }

    public static SingletonClass getSingleton() {
        return Keeper.instance;
    }
}
