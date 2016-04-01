package nl.cwi.managed_data_4j.ccconcerns.patterns.observer;

import nl.cwi.managed_data_4j.language.data_manager.BasicFactory;
import nl.cwi.managed_data_4j.language.managed_object.MObject;
import nl.cwi.managed_data_4j.language.schema.models.definition.Klass;
import nl.cwi.managed_data_4j.language.schema.models.definition.Schema;

public class ObservableFactory extends BasicFactory {

    public ObservableFactory(Class<?> moSchemaFactoryClass, Schema schema) {

        // Add the observable class in order to use it in the managed object.
        super(moSchemaFactoryClass, schema, Observable.class);
    }

    @Override
    protected MObject createManagedObject(Klass klass, Object... _inits) {
        return new ObservableMObject(klass, this, _inits);
    }
}