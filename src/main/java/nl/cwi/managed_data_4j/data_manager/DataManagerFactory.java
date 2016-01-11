package nl.cwi.managed_data_4j.data_manager;

import nl.cwi.managed_data_4j.schema.models.definition.Schema;

import java.lang.reflect.Proxy;

public class DataManagerFactory {

    /**
     * Used to build data managers which build managed objects.
     *
     * More specifically, it takes as an input the data manager, a factory class and a schema,
     * the data manager interprets the schema, and returns a Proxied Factory
     * that creates Managed Objects which are described with that schema.
     *
     * @param dataManager the data manager.
     * @param moInstanceFactoryClass The class of the managedObject instance factory.
     * @param schema The schema of the managed object instance.
     *
     * @return a new factory which creates managed objects.
     */
    @SuppressWarnings("unchecked")
    public static <T> T make(IFactory dataManager, Class<?> moInstanceFactoryClass, Schema schema) {
        return (T) Proxy.newProxyInstance(
            moInstanceFactoryClass.getClassLoader(),
            new Class<?>[]{moInstanceFactoryClass},
            (proxy, method, args) -> dataManager.createProxiedManagedObject(moInstanceFactoryClass, schema, method, args)
        );
    }
}
