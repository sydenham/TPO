/**
 *
 *  @author Szarek Marcin S15541
 *
 */

package zad1;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;
import com.sun.xml.internal.ws.util.StringUtils;

public class Service {

	String countryISOCode;
	String charset = "UTF-8";
	String APPID = "7fedc44b334bb6605166803662663411";
	String currencyOfSearchedCountry;

	public Service(String country) {
		Pattern pattern = Pattern.compile(StringUtils.capitalize(country.toLowerCase()));
		List<CountryCode> listOfCountryCodes = CountryCode.findByName(pattern);
		if (listOfCountryCodes.isEmpty()) {
			System.out.println("Kraju nie znaleziono");
			System.exit(0);
		}
		countryISOCode = listOfCountryCodes.get(0).toString().toLowerCase();
		currencyOfSearchedCountry = listOfCountryCodes.get(0).getCurrency().toString();
	}

	public InputStream getDataFromWeb(String url, String query) {

		URLConnection connection = null;
		InputStream response = null;
		try {
			connection = new URL(url + "?" + query).openConnection();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Nie udalo sie uzyskac danych");
			return null;
		}
		connection.setRequestProperty("Accept-Charset", charset);
		try {
			response = connection.getInputStream();
		} catch (IOException e) {
			System.out.println("Nie udalo sie uzyskac danych");
			return null;
		}
		return response;
	}

	public String getWeather(String city) {
		String url = "http://api.openweathermap.org/data/2.5/weather";
		String cityAndCountry = new StringBuilder().append(city).append(",").append(countryISOCode).toString();
		String query = null;
		try {
			query = String.format("q=%s,%s&APPID=%s", city, countryISOCode, APPID, URLEncoder.encode(city, charset),
					URLEncoder.encode(countryISOCode, charset), URLEncoder.encode(APPID, charset));

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream JSONweatherInput = getDataFromWeb(url, query);
		Scanner s = new Scanner(JSONweatherInput).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public Double getRateFor(String currencyCodeToCompare) {
		if (currencyOfSearchedCountry.equals(currencyCodeToCompare)) {
			return 1d;
		}
		String url = "https://api.exchangeratesapi.io/latest";
		String query = null;
		try {
			query = String.format("base=%s&symbols=%s", currencyCodeToCompare, currencyOfSearchedCountry,
					URLEncoder.encode(currencyCodeToCompare, charset),
					URLEncoder.encode(currencyOfSearchedCountry, charset));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream JSONRateInfo = getDataFromWeb(url, query);
		HashMap<String, HashMap<String, Double>> mapRateInfo = null;
		try {
			mapRateInfo = new ObjectMapper().readValue(JSONRateInfo, HashMap.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			System.out.println("Nie udalo sie pobrac danych");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Double rate = mapRateInfo.get("rates").get(currencyOfSearchedCountry);
		return rate;
	}

	public Double getNBPRate() {
		if (currencyOfSearchedCountry.equals("PLN")) {
			return 1d;
		}
		Document JsoupDocument = null;
		try {
			JsoupDocument = Jsoup.connect("http://www.nbp.pl/kursy/kursya.html").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element currencyCodeElement = JsoupDocument
				.select(String.format("td:containsOwn( %s)", currencyOfSearchedCountry)).first();
		if (currencyCodeElement == null) {
			try {
				JsoupDocument = Jsoup.connect("http://www.nbp.pl/kursy/kursyb.html").get();
				currencyCodeElement = JsoupDocument
						.select(String.format("td:containsOwn( %s)", currencyOfSearchedCountry)).first();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String rate = currencyCodeElement.nextElementSibling().text();
		String currencyCodeWithMultiplicator = currencyCodeElement.text();
		Integer currencyMultiplicator = Integer.parseInt(currencyCodeWithMultiplicator.replaceAll("\\D+", ""));
		rate = rate.replaceAll(",", ".");
		Double result = (new BigDecimal(rate).divide(new BigDecimal(currencyMultiplicator)))
				.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().doubleValue();

		return result;
	}
}
