package nl.cwi.examples.patterns.observer;


import nl.cwi.managed_data_4j.data_managers.IFactory;
import nl.cwi.managed_data_4j.managed_object.MObject;
import nl.cwi.managed_data_4j.managed_object.managed_object_field.errors.InvalidFieldValueException;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Klass;

import java.util.ArrayList;
import java.util.List;

public class ObservableMObject extends MObject implements Observable {

    private List<Observe> observers;

    public ObservableMObject(Klass schemaKlass, IFactory factory, Object... initializers) {
        super(schemaKlass, factory, initializers);
        observers = new ArrayList<Observe>();
    }

    public void observe(Observe _observer) {
        observers.add(_observer);
    }

    @Override
    protected void _set(String _name, Object _value) throws NoSuchFieldError, InvalidFieldValueException {
        super._set(_name, _value);

        observers.forEach(
            observer -> observer.observe(this, _name, _value)
        );
    }
}