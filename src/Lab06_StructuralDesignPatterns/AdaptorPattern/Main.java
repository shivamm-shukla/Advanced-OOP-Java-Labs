package Lab06_StructuralDesignPatterns.AdaptorPattern;


interface WeatherProvider{
    double getTemperature(String location);
    String getWeatherCondition(String location);
    int getHumidityPercent(String location);
}

class OpenWeatherMapAPI {
    private String apiKey;

    public OpenWeatherMapAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    // Returns temperature in Celsius
    public double getTemperatureCelsius(String city) {
        System.out.println("[OpenWeatherMap] Fetching " + city);
        // Simulated API call
        if (city.equals("Delhi")) return 42.5;
        if (city.equals("Mumbai")) return 33.0;
        if (city.equals("Bangalore")) return 28.5;
        return 25.0;
    }

    // Returns condition as a human-readable string
    public String getWeatherCondition(String city) {
        if (city.equals("Delhi")) return "Sunny";
        if (city.equals("Mumbai")) return "Rainy";
        return "Cloudy";
    }

    // Returns humidity as a percentage
    public int getHumidityPercent(String city) {
        if (city.equals("Delhi")) return 25;
        if (city.equals("Mumbai")) return 89;
        return 60;
    }
}


class OpenWeatherMapAdapter implements WeatherProvider{

    private OpenWeatherMapAPI o;

    public OpenWeatherMapAdapter(String apiKey) {
        this.o = new OpenWeatherMapAPI(apiKey);
    }

    @Override
    public double getTemperature(String location) {
        return o.getTemperatureCelsius(location);
    }

    @Override
    public String getWeatherCondition(String location) {
        return o.getWeatherCondition(location);
    }

    @Override
    public int getHumidityPercent(String location) {
        return o.getHumidityPercent(location);
    }
}

class WeatherStackAPI {
    private String accessKey;

    public WeatherStackAPI(String accessKey) {
        this.accessKey = accessKey;
    }

    // Returns temperature in FAHRENHEIT (not Celsius!)
    public double queryTempFahrenheit(String location) {
        System.out.println("[WeatherStack] Querying " + location);
        if (location.equals("Delhi")) return 108.5;
        if (location.equals("Mumbai")) return 91.4;
        if (location.equals("Bangalore")) return 83.3;
        return 77.0;
    }

    // Returns condition as a NUMERIC CODE (not a string!)
    // 0=Clear, 1=Partly Cloudy, 2=Cloudy, 3=Rain, 4=Storm
    public int queryConditionCode(String location) {
        if (location.equals("Delhi")) return 0;
        if (location.equals("Mumbai")) return 3;
        return 2;
    }

    // Returns humidity as a decimal (0.0 to 1.0, not percentage!)
    public double queryHumidityRatio(String location) {
        if (location.equals("Delhi")) return 0.25;
        if (location.equals("Mumbai")) return 0.89;
        return 0.60;
    }
}


class WeatherStackAdapter implements WeatherProvider{
    private WeatherStackAPI ws;
    public WeatherStackAdapter(String accessKey) {
        this.ws = new WeatherStackAPI(accessKey);
    }
    @Override
    public double getTemperature(String location) {
        double f =  ws.queryTempFahrenheit(location);
        return ((f-32)*5.0)/9.0;
    }
    @Override
    public String getWeatherCondition(String location) {
        int code =  ws.queryConditionCode(location);
        switch (code) {
            case 0: return "Clear";
            case 1: return "Partly Cloudy";
            case 2: return "Cloudy";
            case 3: return "Rain";
            case 4: return "Storm";
            default:
                return "Cloudy";
        }
    }
    @Override
    public int getHumidityPercent(String location) {
        double r = ws.queryHumidityRatio(location);
        return (int)(r*100);
    }
}

class WeatherDashboard{
    private WeatherProvider wp;

    public WeatherDashboard(WeatherProvider wp) {
        this.wp = wp;
    }

    public void displayWeather(String city) {
        double tempC = wp.getTemperature(city);
        String condition = wp.getWeatherCondition(city);
        int humidity = wp.getHumidityPercent(city);

        System.out.printf("=== %s ===%n", city);
        System.out.printf("  Temp: %.1f°C%n", tempC);
        System.out.printf("  Condition: %s%n", condition);
        System.out.printf("  Humidity: %d%%%n", humidity);
    }

}



public class Main {
    public static void main(String[] args) {

        // --- OpenWeatherMap Provider ---
        System.out.println("=== OpenWeatherMap Provider ===\n");
        WeatherDashboard dash = new WeatherDashboard(new OpenWeatherMapAdapter("OWM-KEY-123"));
        dash.displayWeather("Delhi");
        dash.displayWeather("Mumbai");
        dash.displayWeather("Bangalore");

        // --- WeatherStack Provider ---
        System.out.println("\n=== WeatherStack Provider ===\n");
        dash = new WeatherDashboard(new WeatherStackAdapter("WS-KEY-456"));
        dash.displayWeather("Delhi");
        dash.displayWeather("Mumbai");
        dash.displayWeather("Bangalore");

        // --- Mock Provider (for testing — no real API call) ---
        System.out.println("\n=== Mock Provider (Testing) ===\n");
        WeatherProvider mock = new WeatherProvider() {
            @Override
            public double getTemperature(String location) { return 30.0; }

            @Override
            public String getWeatherCondition(String location) { return "Sunny"; }

            @Override
            public int getHumidityPercent(String location) { return 50; }
        };
        dash = new WeatherDashboard(mock);
        dash.displayWeather("TestCity");
    }
}