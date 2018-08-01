package lucasduete.github.io.mobilehub.models;

public class UserLocation {

    private Double altitude;
    private Double latitude;
    private Double longitude;

    public UserLocation() {

    }

    public UserLocation(Double altitude, Double latitude, Double longitude) {
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static UserLocation of(Double altitude, Double latitude, Double longitude) {
        return new UserLocation(altitude, latitude, longitude);
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserLocation that = (UserLocation) o;

        if (altitude != null ? !altitude.equals(that.altitude) : that.altitude != null)
            return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null)
            return false;
        return longitude != null ? longitude.equals(that.longitude) : that.longitude == null;
    }

    @Override
    public int hashCode() {

        int result = altitude != null ? altitude.hashCode() : 0;
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        final StringBuffer sb = new StringBuffer("UserLocation{");
        sb.append("altitude=").append(altitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
