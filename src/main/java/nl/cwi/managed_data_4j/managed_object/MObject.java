package nl.cwi.managed_data_4j.managed_object;

import nl.cwi.managed_data_4j.data_managers.IFactory;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.MObjectField;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.MObjectFieldMany;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.MObjectFieldPrimitive;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.MObjectFieldRef;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.errors.InvalidFieldValueException;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.errors.UnknownPrimitiveTypeException;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Field;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Klass;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Primitive;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Type;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

public class MObject implements InvocationHandler {

    // Store props for the object: <Name, Field>
    protected Map<String, MObjectField> props = new HashMap<>();

    // Keeps the types (schemaKlass pointer)
    protected Klass schemaKlass;

    protected IFactory factory;

    public MObject(Klass schemaKlass, IFactory factory, Object... initializers) {
        this.schemaKlass = schemaKlass;
        this.factory = factory;

        if (this.schemaKlass.fields() != null) {

            // setup fields and properties / set default values.
            this.schemaKlass.fields().stream().forEach(this::safeSetupField);

            // initialize fields with actual values.
            if (initializers != null) {
                this.safeInitializeProps(initializers);
            }
        }
    }

    /**
     * Wrapper to handle exceptions.
     */
    private void safeSetupField(Field _field) {
        try {
            this.setupField(_field);
        } catch (InvalidFieldValueException | UnknownPrimitiveTypeException e) {
            e.printStackTrace();
        }
    }

    protected void setupField(Field field) throws UnknownPrimitiveTypeException, InvalidFieldValueException {
        MObjectField prop;

        if (!field.many()) {
            if (field.type() instanceof Primitive) {
                prop = new MObjectFieldPrimitive(this, field);
            } else {
                prop = new MObjectFieldRef(this, field);
            }
        } else {
            prop = new MObjectFieldMany(this, field);
        }

        this.props.put(field.name(), prop);
    }

    /**
     * Wrapper to handle exceptions.
     */
    private void safeInitializeProps(Object... initializers) {
        try {
            this.initializeProps(initializers);
        } catch (InvalidFieldValueException e) {
            e.printStackTrace();
        }
    }

    protected void initializeProps(Object... initializers) throws InvalidFieldValueException {
        List<Field> fieldList = new ArrayList<>();
        fieldList.addAll(this.schemaKlass.fields());

        for (int i = 0; i < fieldList.size(); i++) {
            if (i < initializers.length) {
                final Field fld = fieldList.get(i);
                final MObjectField mObjectField = this.props.get(fld.name());

                if (fld.many()) {
                    // it's an array since it's many
                    Object [] inits = ((Object[]) initializers[i]);
                    mObjectField.init(inits);
                } else {
                    mObjectField.init(initializers[i]);
                }
            }
        }
    }

    protected Object _get(String _name) throws NoSuchFieldError {
        MObjectField mObjectField = this.props.get(_name);
        return mObjectField.get(); // return the field's value
    }

    protected void _set(String _name, Object _value) throws NoSuchFieldError, InvalidFieldValueException {
        MObjectField mObjectField = this.props.get(_name);
        if (mObjectField == null) {
            throw new NoSuchFieldError("No field with the name " + _name + " in class " + schemaKlass.name());
        }

        mObjectField.init(_value);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String fieldName = method.getName();

        // TODO: Check this.
        // This is a way to execute the "attached" methods of the derived Managed Objects,
        // from the proxied objects. (e.g. point.observe()).
        //
        // In case there is already the method declared
        // (in one of the sub-classes/sub managedObjects),
        // then invoke it dynamically, and return.
        for (Method declaredMethod : this.getClass().getMethods()) {
            if (declaredMethod.getName().equals(fieldName)) {
                method.invoke(this, args);
                return null;
            }
        }

        // if no args given, just return the field's value
        if (args == null) {
            // If is not an assignment, get the value.
            return _get(fieldName); // return the field's value
        }

        boolean isAssignment = false;

        Object fieldArgs = args[0];

        // If there is an argument then is considered as assignment.
        if (fieldArgs.getClass().isArray() && ((Object [])fieldArgs).length > 0) {
            isAssignment = true;
        }

        // If it is an assignment, set the value
        if (isAssignment) {

            // in case have 1 arg means that is a single field
            if (((Object [])fieldArgs).length == 1) {
                _set(fieldName, ((Object [])fieldArgs)[0]);
            } else {
                _set(fieldName, fieldArgs);
            }

            return null;
        }

        // If is not an assignment, get the value.
        return _get(fieldName); // return the field's value
    }

    public Klass getSchemaKlass() {
        return schemaKlass;
    }
}
