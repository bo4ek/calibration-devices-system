<%@ WebHandler Language="C#" Class="ProxyHandler" %>

using System;
using System.Web;
using System.Net;
using System.IO;

public class ProxyHandler : IHttpHandler
{
	private static string HttpRequestParameterAddress = "address";
	private static string HttpContentTypeMultipart = "multipart/form-data";
	private static string HttpContentTypeBase64 = "X-user/base64-data";
	private static int HttpMaxContentSize = 10000000;
	private static int HttpBufferChunk = 0xFFFF;

	private byte[] SafeReadDataStream(Stream stream)
	{
		byte[] buffer;
		int count;
		MemoryStream memoryStream;
		StreamReader streamReader;

		buffer = new byte[HttpBufferChunk];
		memoryStream = new MemoryStream();
		streamReader = new StreamReader(stream);

		while ((count = streamReader.BaseStream.Read(buffer, 0, buffer.Length)) > 0)
		{
			memoryStream.Write(buffer, 0, count);

			if (memoryStream.Length > HttpMaxContentSize)
				return null;
		}

		return memoryStream.ToArray();
	}

	private HttpStatusCode HandleRequest(HttpContext context)
	{
		HttpWebRequest serverRequest;
		HttpWebResponse serverResponse;
		byte[] clientResponseData;

		if (context.Request[HttpRequestParameterAddress] == null ||
			context.Request[HttpRequestParameterAddress] == "")
		{
			return HttpStatusCode.BadRequest;
		}

		serverRequest = (HttpWebRequest)WebRequest.Create(
			context.Request[HttpRequestParameterAddress]);
		serverRequest.Method = context.Request.RequestType;
		serverRequest.ServicePoint.Expect100Continue = false;

		if (serverRequest.Method == "POST")
		{
			byte[] requestData;
			string requestDataBase64String;
			byte[] serverRequestData;

			if (!context.Request.ContentType.Contains(HttpContentTypeBase64))
				return HttpStatusCode.BadRequest;

			requestData = SafeReadDataStream(context.Request.InputStream);
			if (requestData == null)
				return HttpStatusCode.RequestEntityTooLarge;

			requestDataBase64String = 
				System.Text.Encoding.UTF8.GetString(requestData);
			serverRequestData = Convert.FromBase64String(
				requestDataBase64String);
			
			serverRequest.ContentType = "";
			serverRequest.ContentLength = serverRequestData.Length;

			serverRequest.GetRequestStream().Write(
				serverRequestData, 0, serverRequestData.Length);
		}

		serverResponse = (HttpWebResponse)serverRequest.GetResponse();
		if (serverResponse.StatusCode != HttpStatusCode.OK)
			return serverResponse.StatusCode;

		clientResponseData = SafeReadDataStream(
			serverResponse.GetResponseStream());
		if (clientResponseData == null)
		{
			serverResponse.Close();

			return HttpStatusCode.RequestEntityTooLarge;
		}

		serverResponse.Close();

		context.Response.ContentType = HttpContentTypeBase64;
		context.Response.StatusCode = (int)HttpStatusCode.OK;
		context.Response.Write(Convert.ToBase64String(clientResponseData));

		return HttpStatusCode.OK;
	}

	public void ProcessRequest(HttpContext context)
	{
		HttpStatusCode status = HttpStatusCode.InternalServerError;

		try
		{
			string requestType = context.Request.RequestType;

			if (requestType == "GET" || requestType == "POST")
				status = HandleRequest(context);
			else
				status = HttpStatusCode.BadRequest;
		}
		finally
		{
			if (status != HttpStatusCode.OK)
			{
				context.Response.Write("Виникла помилка при обробці запиту");
				context.Response.StatusCode = (int) status;
			}
		}
	}

	public bool IsReusable
	{
		get
		{
			return false;
		}
	}
}