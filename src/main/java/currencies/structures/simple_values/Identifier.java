package currencies.structures.simple_values;

import currencies.reader.CharPosition;

import java.util.Objects;

public class Identifier {
    private String name;
    private CharPosition position;

    public Identifier(String id, CharPosition position) {
        this.name = id;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CharPosition getPosition() {
        return position;
    }

    public void setPosition(CharPosition position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
