package kiwigroup.yodelego.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kiwigroup.yodelego.R;

public class ServerCommunication extends AsyncTask<String, String, String> {

    private static String TOKEN;

    private String service;
    private Context mContext;
    private RequestQueue mQueue;
    private int method;
    private Response.Listener<JSONObject> jsonObjectListener;
    private Response.Listener<JSONArray> jsonArrayListener;
    private Response.Listener<JSONObject> nullableJsonObjectListener;
    private Response.ErrorListener errorListener;
    private Map<String, Object> parameters;
    private boolean withToken;
    private boolean withParametersOnUrl;

    public ServerCommunication(Context ctx, String service){
        this.mContext = ctx;
        this.service = service;
    }

    public static class ServerCommunicationBuilder {
        private String service;
        private Context mContext;
        private int method;
        private Response.Listener<JSONObject> jsonObjectListener;
        private Response.Listener<JSONArray> jsonArrayListener;
        private Response.Listener<JSONObject> nullableJsonObjectListener;
        private Response.ErrorListener errorListener;
        private Map<String, Object> parameters;
        private boolean withToken;
        private boolean withParametersOnUrl;

        public ServerCommunicationBuilder(Context ctx, String service){
            this.mContext = ctx;
            this.service = service;
        }

        public ServerCommunicationBuilder POST(){
            this.method = Request.Method.POST;
            return this;
        }

        public ServerCommunicationBuilder GET(){
            this.method = Request.Method.GET;
            return this;
        }

        public ServerCommunicationBuilder PATCH(){
            this.method = Request.Method.PATCH;
            Log.d("Server", "TOKEN: " + TOKEN);
            return this;
        }

        public ServerCommunicationBuilder PUT(){
            this.method = Request.Method.PUT;
            Log.d("Server", "TOKEN: " + TOKEN);
            return this;
        }

        public ServerCommunicationBuilder objectReturnListener(Response.Listener<JSONObject> jsonObjectListener){
            this.jsonObjectListener = jsonObjectListener;
            return this;
        }

        public ServerCommunicationBuilder arrayReturnListener(Response.Listener<JSONArray> jsonArrayListener){
            this.jsonArrayListener = jsonArrayListener;
            return this;
        }

        public ServerCommunicationBuilder nullableListener(Response.Listener<JSONObject> nullableJsonObjectListener){
            this.nullableJsonObjectListener = nullableJsonObjectListener;
            return this;
        }

        public ServerCommunicationBuilder errorListener(Response.ErrorListener errorListener){
            this.errorListener = errorListener;
            return this;
        }

        public ServerCommunicationBuilder parameters(Map<String, Object> parameters){
            this.parameters = parameters;
            return this;
        }

        public ServerCommunicationBuilder tokenized(boolean withToken){
            this.withToken = withToken;
            return this;
        }

        public ServerCommunicationBuilder parametersOnURL(boolean withParametersOnUrl){
            this.withParametersOnUrl = withParametersOnUrl;
            return this;
        }

        public ServerCommunication build(){
            ServerCommunication server = new ServerCommunication(mContext, service);
            server.method = method;
            server.jsonObjectListener = jsonObjectListener;
            server.jsonArrayListener = jsonArrayListener;
            server.nullableJsonObjectListener = nullableJsonObjectListener;
            server.errorListener = errorListener;
            server.parameters = parameters;
            server.withToken = withToken;
            server.withParametersOnUrl = withParametersOnUrl;
            return server;
        }
    }

    @Override
    protected String doInBackground(String... args) {
        mQueue = Volley.newRequestQueue(mContext);

        if(jsonObjectListener != null){
            if(withToken){
                JsonObjectRequest request = new JsonObjectRequest(
                    method,
                    mContext.getString(R.string.server_base_url) + service,
                    parameters != null ? new JSONObject(parameters) : null,
                    jsonObjectListener,
                    errorListener){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Token " + TOKEN);
                            //headers.put("Content-Type", "application/json");
                            return headers;
                        }
                };
                mQueue.add(request);
            } else {
                Log.d("startLoginProcess", "jsonObjectListener == null");
                JsonObjectRequest request = new JsonObjectRequest(method, mContext.getString(R.string.server_base_url) + service, parameters != null ? new JSONObject(parameters) : null, jsonObjectListener, errorListener);
                //CustomRequest request = new CustomRequest(Request.Method.POST, mContext.getString(R.string.server_base_url) + service, parameters, jsonObjectListener, errorListener);
                mQueue.add(request);
                return "";
            }
        } else if (jsonArrayListener != null){
            if(withToken){
                JsonArrayRequest request = new JsonArrayRequest(
                    method,
                     (withParametersOnUrl && parameters != null) ? createParams() : mContext.getString(R.string.server_base_url) + service,
                    null,
                    jsonArrayListener,
                    errorListener){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Token " + TOKEN);
                            return headers;
                        }

                };
                mQueue.add(request);
            } else {
                Log.d("startLoginProcess", "jsonArrayListener == null");
                JsonArrayRequest request = new JsonArrayRequest(method, mContext.getString(R.string.server_base_url) + service, null, jsonArrayListener, errorListener);
                mQueue.add(request);
            }
        } else if (nullableJsonObjectListener != null){
            if(withToken){
                JsonObjectRequestWithNull request = new JsonObjectRequestWithNull(
                    method,
                    mContext.getString(R.string.server_base_url) + service,
                    parameters != null ? new JSONObject(parameters) : null,
                    nullableJsonObjectListener,
                    errorListener){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Token " + TOKEN);
                            return headers;
                        }
                };
                mQueue.add(request);
            } else {
                Log.d("startLoginProcess", "nullableJsonObjectListener == null");
                JsonObjectRequestWithNull request = new JsonObjectRequestWithNull(
                    method,
                    mContext.getString(R.string.server_base_url) + service,
                    parameters != null ? new JSONObject(parameters) : null,
                    nullableJsonObjectListener,
                    errorListener);
                mQueue.add(request);
            }
        }
        return "";
    }

    private String createParams() {
        String mUrl;
        StringBuilder stringBuilder = new StringBuilder(mContext.getString(R.string.server_base_url) + service);
        if(parameters != null){
            Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                if (i == 1) {
                    stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                } else {
                    stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                iterator.remove(); // avoids a ConcurrentModificationException
                i++;
            }
        }
        mUrl = stringBuilder.toString();
        Log.d("ServerCommunication", "--> calling: " + mUrl);
        return mUrl;
    }

    private class JsonObjectRequestWithNull extends JsonRequest<JSONObject> {

        public JsonObjectRequestWithNull(int method, String url, JSONObject jsonRequest,
                                         Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest.toString(), listener, errorListener);
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                //Allow null
                if (jsonString == null || jsonString.length() == 0) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                }
                return Response.success(new JSONObject(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }
    }

    private class BooleanRequest extends Request<Boolean> {
        private final Response.Listener<Boolean> mListener;
        private final Response.ErrorListener mErrorListener;
        private final String mRequestBody;

        private final String PROTOCOL_CHARSET = "utf-8";
        private final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

        public BooleanRequest(int method, String url, String requestBody, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
            this.mRequestBody = requestBody;
        }

        @Override
        protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {
            Boolean parsed;
            try {
                parsed = Boolean.valueOf(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
            } catch (UnsupportedEncodingException e) {
                parsed = Boolean.valueOf(new String(response.data));
            }
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected VolleyError parseNetworkError(VolleyError volleyError) {
            return super.parseNetworkError(volleyError);
        }

        @Override
        protected void deliverResponse(Boolean response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }

        @Override
        public String getBodyContentType() {
            return PROTOCOL_CONTENT_TYPE;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            try {
                return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
            } catch (UnsupportedEncodingException uee) {
                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                        mRequestBody, PROTOCOL_CHARSET);
                return null;
            }
        }

    }

    public static void setTOKEN(String TOKEN) {
        ServerCommunication.TOKEN = TOKEN;
    }

}