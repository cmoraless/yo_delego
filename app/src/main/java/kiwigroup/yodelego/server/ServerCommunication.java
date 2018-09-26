package kiwigroup.yodelego.server;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
    private Response.Listener<NetworkResponse> multipartListener;
    private Bitmap image;
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
        private Response.Listener<NetworkResponse> multipartListener;
        private Bitmap image;
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

        public ServerCommunicationBuilder multipartListener(Response.Listener<NetworkResponse> multipartListener, Bitmap image){
            this.multipartListener = multipartListener;
            this.image = image;
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
            server.multipartListener = multipartListener;
            server.errorListener = errorListener;
            server.parameters = parameters;
            server.withToken = withToken;
            server.withParametersOnUrl = withParametersOnUrl;
            server.image = image;
            return server;
        }
    }

    @Override
    protected String doInBackground(String... args) {
        mQueue = Volley.newRequestQueue(mContext);

        if(jsonObjectListener != null){
                if(withToken){
                if(parameters != null)
                    Log.d("******", "*********** parameters" + new JSONObject(parameters));
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
                            return headers;
                        }

                        /*@Override
                        public String getBodyContentType(){
                            return "application/json";
                        }*/

                };
                mQueue.add(request);
            } else {
                Log.d("startLoginProcess", "jsonObjectListener == null");
                JsonObjectRequest request = new JsonObjectRequest(
                    method,
                    mContext.getString(R.string.server_base_url) + service,
                    parameters != null ? new JSONObject(parameters) : null,
                    jsonObjectListener,
                    errorListener);
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
                            //headers.put("Content-Type", "application/json; charset=utf-8");
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
                            //headers.put("Content-Type", "application/json; charset=utf-8");
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
        } else if (multipartListener != null){
            if(withToken){
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                        method,
                    mContext.getString(R.string.server_base_url) + service,
                    parameters,
                    multipartListener,
                    errorListener) {
                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            if(image != null){
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                                params.put("profile_picture", new DataPart(
                                        "profile_picture.png",
                                        byteArrayOutputStream.toByteArray(),
                                        "image/jpeg"));
                            }
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Token " + TOKEN);
                            //headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                mQueue.add(multipartRequest);
            } else {
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                        method,
                        mContext.getString(R.string.server_base_url) + service,
                        parameters,
                        multipartListener,
                        errorListener) {
                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        if(image != null){
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                            params.put("profile_picture", new DataPart(
                                    "profile_picture.png",
                                    byteArrayOutputStream.toByteArray(),
                                    "image/jpeg"));
                        }
                        return params;
                    }
                };
                mQueue.add(multipartRequest);
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



    public class VolleyMultipartRequest extends Request<NetworkResponse> {
        private final String twoHyphens = "--";
        private final String lineEnd = "\r\n";
        private final String boundary = "apiclient-" + System.currentTimeMillis();

        private Response.Listener<NetworkResponse> mListener;
        private Response.ErrorListener mErrorListener;
        private Map<String, String> mHeaders;
        private Map<String, Object> mParameters;

        /**
         * Default constructor with predefined header and post method.
         *
         * @param url           request destination
         * @param headers       predefined custom header
         * @param listener      on success achieved 200 code from request
         * @param errorListener on error http or library timeout
         */
        public VolleyMultipartRequest(String url, Map<String, String> headers,
                                      Response.Listener<NetworkResponse> listener,
                                      Response.ErrorListener errorListener) {
            super(Method.POST, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
            this.mHeaders = headers;
        }

        /**
         * Constructor with option method and default header configuration.
         *
         * @param method        method for now accept POST and GET only
         * @param url           request destination
         * @param listener      on success event handler
         * @param errorListener on error event handler
         */
        public VolleyMultipartRequest(int method, String url,
                                      Map<String, Object> parameters,
                                      Response.Listener<NetworkResponse> listener,
                                      Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mParameters = parameters;
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return (mHeaders != null) ? mHeaders : super.getHeaders();
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data; charset=utf-8;boundary=" + boundary;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                // populate text payload
                Map<String, Object> params = mParameters;
                if (params != null && params.size() > 0) {
                    textParse(dos, params, getParamsEncoding());
                }
                // populate data byte payload
                Map<String, DataPart> data = getByteData();
                if (data != null && data.size() > 0) {
                    dataParse(dos, data);
                }

                // close multipart form data after text and file data
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                return bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Custom method handle data payload.
         *
         * @return Map data part label with data byte
         * @throws AuthFailureError
         */
        protected Map<String, DataPart> getByteData() throws AuthFailureError {
            return null;
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(
                        response,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }

        /**
         * Parse string map into data output stream by key and value.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param params           string inputs collection
         * @param encoding         encode the inputs, default UTF-8
         * @throws IOException
         */
        private void textParse(DataOutputStream dataOutputStream, Map<String, Object> params, String encoding) throws IOException {
            try {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
                }
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + encoding, uee);
            }
        }

        /**
         * Parse data into data output stream.
         *
         * @param dataOutputStream data output stream handle file attachment
         * @param data             loop through data
         * @throws IOException
         */
        private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
            for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
            }
        }

        /**
         * Write string data into header and data output stream.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param parameterName    name of input
         * @param parameterValue   value of input
         * @throws IOException
         */
        private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, Object parameterValue) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
            dataOutputStream.writeBytes("Content-Type: text/plain; charset=utf-8" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(parameterValue + lineEnd);
        }

        /**
         * Write data file into header and data output stream.
         *
         * @param dataOutputStream data output stream handle data parsing
         * @param dataFile         data byte as DataPart from collection
         * @param inputName        name of data input
         * @throws IOException
         */
        private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
            if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
                dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
            }
            dataOutputStream.writeBytes(lineEnd);

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
        }

        /**
         * Simple data container use for passing byte file
         */
        public class DataPart {
            private String fileName;
            private byte[] content;
            private String type;

            /**
             * Default data part
             */
            public DataPart() {
            }

            /**
             * Constructor with data.
             *
             * @param name label of data
             * @param data byte data
             */
            public DataPart(String name, byte[] data) {
                fileName = name;
                content = data;
            }

            /**
             * Constructor with mime data type.
             *
             * @param name     label of data
             * @param data     byte data
             * @param mimeType mime data like "image/jpeg"
             */
            public DataPart(String name, byte[] data, String mimeType) {
                fileName = name;
                content = data;
                type = mimeType;
            }

            /**
             * Getter file name.
             *
             * @return file name
             */
            public String getFileName() {
                return fileName;
            }

            /**
             * Setter file name.
             *
             * @param fileName string file name
             */
            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            /**
             * Getter content.
             *
             * @return byte file data
             */
            public byte[] getContent() {
                return content;
            }

            /**
             * Setter content.
             *
             * @param content byte file data
             */
            public void setContent(byte[] content) {
                this.content = content;
            }

            /**
             * Getter mime type.
             *
             * @return mime type
             */
            public String getType() {
                return type;
            }

            /**
             * Setter mime type.
             *
             * @param type mime type
             */
            public void setType(String type) {
                this.type = type;
            }
        }
    }

    public static void setTOKEN(String TOKEN) {
        ServerCommunication.TOKEN = TOKEN;
    }

}