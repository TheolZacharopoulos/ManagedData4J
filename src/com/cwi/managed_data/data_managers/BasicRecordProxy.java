package com.cwi.managed_data.data_managers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class BasicRecordProxy implements InvocationHandler {

    protected Map<String, Class> types = new HashMap<String, Class>();
    protected Map<String, Object> values = new HashMap<String, Object>();

    public static Object newInstance(Class _schema) {
        return Proxy.newProxyInstance(
                _schema.getClassLoader(),
                new Class<?>[]{_schema},
                new BasicRecordProxy(_schema));
    }

    protected BasicRecordProxy(Class _schema) {

        // Initialize types.
        for (Method schemaType : _schema.getMethods()) {
            types.put(schemaType.getName(), schemaType.getReturnType());
        }

        // Initialize values.
        for (String name : types.keySet()) {

            // TODO: Better way to do this?
            if (types.get(name) == Integer.class) {
                values.put(name, 0);
            } else if (types.get(name) == String.class) {
                values.put(name, "");
            } else if (types.get(name) == Double.class) {
                values.put(name, 0.0);
            } else if (types.get(name) == Object.class) {
                values.put(name, new Object());
            }
        }
    }

    protected void set(String _name, Object _value) {
        values.put(_name, _value);
    }

    protected Object get(String _name) {
        return values.get(_name);
    }

    /**
     * Handle all fields.
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        Object [] proxyMethodVarArgs = (Object []) args[0];
        String methodName = method.getName();
        boolean isAssignment = false;

        // TODO: Find a better strategy to decide if it is an assignment.
        // If there is an argument then is considered as assignment.
        if (proxyMethodVarArgs.length > 0) {
            isAssignment = true;
        }

        // If is assignment
        if (isAssignment) {

            // If there is no such type.
            if (!types.containsKey(methodName)) {
                throw new NoSuchFieldError();
            }

            // If the argument is of the right type.
            if (proxyMethodVarArgs[0].getClass() != values.get(methodName).getClass()) {
                throw new IllegalArgumentException();
            }

            set(methodName, proxyMethodVarArgs[0]);

        } else { // If is not assignment, return the value.
            return get(methodName);
        }

        return null;
    }
}