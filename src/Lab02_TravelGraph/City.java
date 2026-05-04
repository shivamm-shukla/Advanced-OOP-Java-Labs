package Lab02_TravelGraph;

import java.util.Objects;

public record City(String name, String country, String timezone) {

    public City {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }

        if (timezone == null || timezone.isBlank()) {
            throw new IllegalArgumentException("Timezone cannot be null or empty");
        }
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof City city)) return false;
        return Objects.equals(name, city.name);
    }

    public int hashCode(){
        return Objects.hash(name);
    }

    public String toString(){
        return "City{name=" + this.name + ", country=" + this.country + ", timzone=" + this.timezone + "}";
    }

}
