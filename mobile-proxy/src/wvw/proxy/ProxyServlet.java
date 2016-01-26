package wvw.proxy;

/**
 * Copyright 2016 William Van Woensel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 * 
 * 
 * @author wvw (william.van.woensel@gmail.com)
 * 
 */

/**
 * Generic mobile proxy to deal with situations where a remote API does not return CORS headers.
 * Forwards the request to the indicated API URL and returns the response, with the appropriate 
 * CORS headers inserted. Subclasses need to implement authenticate() method to avoid abuse.
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class ProxyServlet extends HttpServlet {

	protected Configuration config;
	protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		loadConfig(config.getServletContext());
	}

	private void loadConfig(ServletContext ctx) {
		try {
			config = new PropertiesConfiguration(
					ctx.getRealPath("/proxy.properties"));

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.getWriter().write(
				"<html><body>Welcome to the Mobile Proxy!</body></html>");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String contents = readFromReader(request.getReader());

		JsonElement el = new JsonParser().parse(contents);
		JsonObject obj = el.getAsJsonObject();

		String dest = obj.get("url").toString();
		dest = dest.replaceAll("\"", "");

		String data = gson.toJson(obj);

		System.out.println("> forwarding request to: " + dest);
		System.out.println(data);
		
		if (!authenticate(obj)) {
			response.setStatus(401);
			System.out.println(">> authentication failed");

			return;
		}

		obj.remove("url");

		// forward request to given URL
		String res = forward(dest, data);
		// System.out.println("result: " + res);

		response.getWriter().write(res);
		response.getWriter().close();

		System.out.println();
	}

	protected String forward(String url, String data) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type",
				"application/json; charset=utf-8");

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);

		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("response: " + responseCode + " ("
				+ con.getResponseMessage() + ")");

		return readFromStream(con.getInputStream());
	}

	// authentication to make sure proxy isn't being abused
	// to be filled in depending on remote API
	protected abstract boolean authenticate(JsonObject data);

	private String readFromReader(BufferedReader reader) throws IOException {
		StringBuffer str = new StringBuffer();
		String line = null;

		while ((line = reader.readLine()) != null) {
			str.append(line);
		}

		return str.toString();
	}

	private String readFromStream(InputStream in) throws IOException {
		BufferedReader bIn = new BufferedReader(new InputStreamReader(in));
		StringBuffer response = new StringBuffer();

		String line;
		while ((line = bIn.readLine()) != null) {
			response.append(line);
		}

		in.close();

		return response.toString();
	}
}
