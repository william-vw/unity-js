package wvw.proxy;

import javax.servlet.annotation.WebServlet;

import com.google.gson.JsonObject;

/**
 * 
 * 
 * @author wvw (william.van.woensel@gmail.com)
 *
 */

@WebServlet("/AllScriptsProxy")
@SuppressWarnings("serial")
public class UnityProxy extends ProxyServlet {

	protected boolean authenticate(JsonObject data) {
		if (!(data.has("Appname") && data.has("SvcUsername") && data
				.has("SvcPassword")))

			return false;

		String inAppname = data.get("Appname").toString();
		String reqAppname = config.getString("Appname");

		// System.out.println(inAppname + " - " + reqAppname);

		if (!inAppname.equals(reqAppname))
			return false;

		String inSvcUsername = data.get("SvcUsername").toString();
		String reqSvcUsername = config.getString("SvcUsername");

		// System.out.println(inSvcUsername + " - " + reqSvcUsername);

		if (!inSvcUsername.equals(reqSvcUsername))
			return false;

		String inSvcPassword = data.get("SvcPassword").toString();
		String reqSvcPassword = config.getString("SvcPassword");

		// System.out.println(inSvcPassword + " - " + reqSvcPassword);

		if (!inSvcPassword.equals(reqSvcPassword))
			return false;

		return true;
	}
}
