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

// IMPORTANT fill in appname, svc.username & svc.password into 
// proxy.properties file in mobile proxy

var appname = "";
var unity = {
	// fill in proxy IP address here
	proxy : "http://localhost:8080/mobile-proxy/",
	endpoint : "http://twlatestga.unitysandbox.com"
};

var svc = {
	username : "",
	password : ""
};

// callback is invoked with: { status: [success/error], data: [data] }
function getToken(username, password, callback) {
	$.post({
		url : unity.proxy,
		data : JSON.stringify({
			// request will be forwarded by the mobile proxy to this URL
			"url" : unity.endpoint + "/Unity/unityservice.svc/json/GetToken",
			"Username" : username,
			"Password" : password,
			// NOTE forward this information for authentication by proxy
			"Appname" : appname,
			"SvcUsername" : svc.username,
			"SvcPassword" : svc.password,
			
		}),

		success : function(data, textStatus, jqXHR) {
			// console.log("onSuccess: " + textStatus);
			// console.log(JSON.stringify(data, null, 4));

			callback({
				status : 'success',
				data : data
			});
		},

		error : function(data, textStatus, jqXHR) {
			// console.log("onError: " + textStatus);
			// console.log(JSON.stringify(data, null, 4));

			callback({
				status : 'error',
				data : textStatus
			});
		},

		contentType : "application/json",
		processData : false
	});
}

// callback is invoked with: { status: [success/error], data: [data] }
function magic(action, appuser, appname, patientid, token, param1, param2,
		param3, param4, param5, param6, data, callback) {

	var json = {
		// request will be forwarded by the mobile proxy to this URL
		"url" : unity.endpoint + "/Unity/unityservice.svc/json/MagicJson",
		"Action" : action,
		"Appname" : appname,
		"AppUserID" : appuser,
		"PatientID" : patientid,
		"Token" : token,
		"Parameter1" : param1,
		"Parameter2" : param2,
		"Parameter3" : param3,
		"Parameter4" : param4,
		"Parameter5" : param5,
		"Parameter6" : param6,
		"Data" : data,
		// NOTE forward this information too for authentication by proxy
		"SvcUsername" : svc.username,
		"SvcPassword" : svc.password
	}

	// console.log("sending: " + JSON.stringify(json, null, 4));

	$.post({
		url : unity.proxy,
		data : JSON.stringify(json),

		success : function(data, textStatus, jqXHR) {
			// console.log("onSuccess: " + textStatus);
			// console.log(JSON.stringify(data, null, 4));

			callback({
				status : 'success',
				data : data
			});
		},

		error : function(data, textStatus, jqXHR) {
			// console.log("onError: " + textStatus);
			// console.log(JSON.stringify(data, null, 4));

			callback({
				status : 'error',
				data : textStatus
			});
		},

		contentType : "application/json",
		processData : false
	});
}

// callback is invoked with: error / success / invalid
// when login is successful, username & password are put in global scope
function doLogin(username, password, callback) {
	magic(
			"GetUserAuthentication",
			username,
			appname,
			"",
			token,
			password,
			"",
			"",
			"",
			"",
			"",
			"",

			function(res) {
				if (res.status == "error") {
					console.log("error logging in: " + res.data);

					callback("error");

				} else {
					var data = JSON.parse(res["data"]);

					var validUser = data[0]["getuserauthenticationinfo"][0]["ValidUser"];
					if (validUser.indexOf("YES") != -1) {
						window.ehrUsername = username;
						window.ehrPassword = password;

						callback("success");

					} else
						callback("invalid");
				}
			});
};

// callback is invoked with: { status: [success/error], data: [data] }
function saveProblemsData(patientId, data, callback) {
	var xml = 
		"<saveproblemsdatarequest>" 
			+ "<saveproblemsdata setid=\"" + data.id + "\" fieldid=\"problem\" attributeid=\"title\" value1=\"" + data.title + "\"/>" 
			+ "<saveproblemsdata setid=\"" + data.id + "\" fieldid=\"problem\" attributeid=\"code\" value1=\"" + data.code + "\" />" 
			+ "<saveproblemsdata setid=\"" + data.id + "\" fieldid=\"problem\" attributeid=\"source\" value1=\"" + data.source + "\"/>" 
			+ "<saveproblemsdata setid=\"" + data.id + "\" fieldid=\"status\" value1=\"" + data.status + "\"/>" 
			+ "<saveproblemsdata setid=\"" + data.id + "\" fieldid=\"severity\" value1=\"" + data.severity + "\"/>"
		+ "</saveproblemsdatarequest>";

	magic("SaveProblemsData", ehrUsername, appname, patientId, token, xml, "",
			"", "", "", "", "",

			function(res) {
				callback(res);
			});
}

// callback is invoked with: { status: [success/error], data: [data] }
function saveVital(patientId, data, callback) {
	var xml = 
		"<savevitalsdatarequest>" 
			+ "<savevitalsdata fieldid=\"" + data.type + "\" value1=\"" + data.value + "\"";

	if (data.unit != null)
		xml += " units=\"" + data.unit + "\"";

	xml += " />" + "</savevitalsdatarequest>";

	magic("SaveVitalsData", ehrUsername, appname, patientId, token, xml, "",
			"", "", "", "", "",

			function(res) {
				callback(res);
			});
}

var token = null;
getToken(svc.username, svc.password,

function(res) {
	if (res.status == "error") {
		console.log("error: " + res.data);

		return;
	}

	token = res.data;
	console.log("token: " + token);

	doLogin("jmedici", "password01", function(res) {
		// prompt for patient id (or obtain in some other way)
		patientId = 39;
		console.log("login: " + res);

		// do your thing here ..

		// saveProblemsData(patientId, {
		// id : 0,
		// title : "Cough (786.2)",
		// code : "fb3ca873-87e4-4531-ba99-73297c21f39e",
		// source : "AllscriptsGUID",
		// status : "Active",
		// severity : "grade 4"
		//
		// }, function(res) {
		// console.log("saved: " + JSON.stringify(res, null, 4));
		//		})

		// saveVital(patientId, {
		// type : "resp",
		// value : 26
		//
		// }, function(res) {
		// console.log("saved: " + JSON.stringify(res, null, 4));
		// });
	});
});