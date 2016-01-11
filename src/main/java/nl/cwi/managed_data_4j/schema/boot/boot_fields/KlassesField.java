package nl.cwi.managed_data_4j.schema.boot.boot_fields;

import nl.cwi.managed_data_4j.schema.boot.boot_types.boot_klasses.TypeKlass;
import nl.cwi.managed_data_4j.schema.models.definition.Klass;
import nl.cwi.managed_data_4j.schema.models.definition.Schema;
import nl.cwi.managed_data_4j.schema.models.definition.Type;
import nl.cwi.managed_data_4j.schema.models.implementation.AbstractField;

public class KlassesField extends AbstractField {

    public static final String NAME = "klasses";

    public KlassesField(Schema schema, Klass owner) {
        super(schema, owner);
    }

    @Override
    public String name(String... name) {
        return NAME;
    }

    @Override
    public Type type(Type... type) {
        return new TypeKlass(schema);
    }

    @Override
    public Boolean many(Boolean... many) {
        return true;
    }

    @Override
    public Boolean optional(Boolean... optional) {
        return true;
    }
}
