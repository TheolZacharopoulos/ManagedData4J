package nl.cwi.managed_data_4j.schema.boot.boot_fields;

import nl.cwi.managed_data_4j.schema.models.schema_schema.Field;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Klass;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Schema;
import nl.cwi.managed_data_4j.schema.models.schema_schema.Type;

public abstract class AbstractField implements Field {

    protected Schema schema;
    protected Klass owner;

    public AbstractField(Schema schema, Klass owner) {
        this.schema = schema;
        this.owner = owner;
    }

    @Override
    public abstract String name(String... name);

    @Override
    public abstract Type type(Type... type);

    @Override
    public Boolean many(Boolean... many) {
        return false;
    }

    @Override
    public Boolean optional(Boolean... optional) {
        return false;
    }

    @Override
    public Field inverse(Field... field) {
        return new NullField();
    }

    @Override
    public Klass owner(Klass... owner) {
        return this.owner;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void setOwner(Klass owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractField that = (AbstractField) o;

        if (!this.name().equals(that.name())) return false;
        if (!this.owner().equals(that.owner())) return false;
        if (!this.optional().equals(that.optional())) return false;
        if (!this.many().equals(that.many())) return false;
        if (!this.inverse().equals(that.inverse())) return false;

        if (this.schema.klassInterfaces() != that.schema.klassInterfaces()) return false;
        if (this.schema.types() != that.schema.types()) return false;

        return true;
    }
}
