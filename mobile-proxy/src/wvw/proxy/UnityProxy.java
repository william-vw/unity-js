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

import javax.servlet.annotation.WebServlet;
import com.google.gson.JsonObject;

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
