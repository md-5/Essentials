package net.ess3.settings.geoip;

import net.ess3.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Database implements StorageObject
{
	boolean showCities = false;
	boolean downloadIfMissing = true;
	String downloadUrlCity = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz";
	String downloadUrl = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz";
}
