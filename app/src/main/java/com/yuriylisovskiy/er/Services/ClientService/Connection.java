package com.yuriylisovskiy.er.Services.ClientService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class Connection {

	private Map<String, String> _headers;

	Connection() {
		this._headers = new HashMap<String, String>(){{
			put("Accept", "application/json; charset=UTF-8");
			put("User-Agent", "Java client");
			put("Content-Type", "application/json");
		}};
	}

	void setHeader(String key, String value) {
		this._headers.put(key, value);
	}

	void removeHeader(String key) {
		this._headers.remove(key);
	}

	boolean hasHeader(String key) {
		return this._headers.containsKey(key);
	}

	JsonResponse Get(String url, Map<String, String> parameters) throws IOException, JSONException {
		if (parameters != null) {
			url += "?" + buildGetParams(parameters);
		}

		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setRequestMethod("GET");

		// conn.setDoOutput(true);

		for (Map.Entry<String, String> cursor : this._headers.entrySet()) {
			conn.setRequestProperty(cursor.getKey(), cursor.getValue());
		}

		int responseCode = conn.getResponseCode();

		if (responseCode < 400) {
			return new JsonResponse(
				this.parseResponse(new InputStreamReader(conn.getInputStream())), conn.getResponseCode()
			);
		} else {
			return new JsonResponse(
				conn.getResponseCode(), this.parseResponse(new InputStreamReader(conn.getErrorStream()))
			);
		}
	}

	JsonResponse Post(String url, Map<String, String> parameters) throws IOException, JSONException {
		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		for (Map.Entry<String, String> cursor : this._headers.entrySet()) {
			conn.setRequestProperty(cursor.getKey(), cursor.getValue());
		}
		conn.setUseCaches(false);
		if (parameters != null) {
			byte[] data = buildPostParams(parameters).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));
			BufferedOutputStream writer = new BufferedOutputStream(conn.getOutputStream());
			writer.write(data);
			writer.flush();
			writer.close();
		}

		int responseCode = conn.getResponseCode();

		if (responseCode < 400) {
			return new JsonResponse(
					this.parseResponse(new InputStreamReader(conn.getInputStream())), conn.getResponseCode()
			);
		} else {
			return new JsonResponse(
					conn.getResponseCode(), this.parseResponse(new InputStreamReader(conn.getErrorStream()))
			);
		}
	}

	private String parseResponse(InputStreamReader reader) throws IOException {
		BufferedReader in = new BufferedReader(reader);
		String inputLine;
		StringBuilder content = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		return content.toString();
	}

	private static String buildPostParams(Map<String, String> params) {
		StringBuilder result = new StringBuilder();
		result.append("{");
		for (Map.Entry<String, String> cursor : params.entrySet()) {
			result.append("\"").append(cursor.getKey()).append("\": \"").append(cursor.getValue()).append("\",");
		}
		if (result.length() > 1) {
			result.deleteCharAt(result.length() - 1);
		}
		result.append("}");
		return result.toString();
	}

	private static String buildGetParams(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}
		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1)	: resultString;
	}

	static class JsonResponse {

		private JSONObject _data;
		private int _status;
		private JSONObject _error;

		JsonResponse(String data, int status) throws JSONException {
			this._data = new JSONObject(data);
			this._status = status;
		}

		JsonResponse(int status, String error) throws JSONException {
			this._error = new JSONObject(error);
			this._status = status;
		}

		JSONObject getData() {
			return this._data != null ? this._data : this._error;
		}

		int getStatus() {
			return this._status;
		}

		JSONObject getError() {
			return this._error;
		}
	}

	static class Status {

		final static int HTTP_200_OK = 200;
		final static int HTTP_201_CREATED = 201;

		final static int HTTP_400_BAD_REQUEST = 400;
		final static int HTTP_401_UNAUTHORIZED = 401;
	}
}
