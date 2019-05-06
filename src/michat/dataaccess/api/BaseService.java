package michat.dataaccess.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import michat.dataaccess.model.User;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class BaseService {

    private String BASE_PATH = "https://ivanmarincic.com/mi-chat";
    private User loggedInUser;
    private ObjectMapper objectMapper = new ObjectMapper();
    private OkHttpClient client = new OkHttpClient();

    protected static final MediaType REQUEST_TYPE_PLAIN
            = MediaType.parse("text/plain");
    protected static final MediaType REQUEST_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    private Call get(String path, Map<String, Object> requestParams) {
        HttpUrl.Builder url = HttpUrl
                .parse(BASE_PATH + path)
                .newBuilder();
        if (requestParams != null) {
            for (String key : requestParams.keySet()) {
                url.addEncodedQueryParameter(key, requestParams.get(key).toString());
            }
        }
        if (loggedInUser != null) {
            url.addQueryParameter("api_token", loggedInUser.getApiToken());
        }
        Request request = new Request.Builder()
                .url(url.build())
                .get()
                .build();
        return client.newCall(request);
    }

    protected void getByteStream(String path, Map<String, Object> requestParams, ResponseListener<InputStream> responseListener) {
        get(path, requestParams).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (responseListener != null) {
                    responseListener.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (responseListener != null && response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            responseListener.onResponse(body.byteStream());
                        } else {
                            responseListener.onError(new NullPointerException("Response body is null"));
                        }
                    }
                } catch (Exception e) {
                    response.close();
                    responseListener.onError(e);
                }
            }
        });
    }

    protected <T> void getJsonObject(String path, Map<String, Object> requestParams, Class<T> returnType, ResponseListener<T> responseListener) {
        get(path, requestParams).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (responseListener != null) {
                    responseListener.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (responseListener != null && response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            responseListener.onResponse(objectMapper.readValue(body.string(), returnType));
                        } else {
                            responseListener.onError(new NullPointerException("Response body is null"));
                        }
                    }
                } catch (Exception e) {
                    response.close();
                    responseListener.onError(e);
                }
            }
        });
    }

    protected <T> void getJsonObject(String path, Class<T> returnType, ResponseListener<T> responseListener) {
        getJsonObject(path, null, returnType, responseListener);
    }

    protected <T> void getJsonList(String path, Map<String, Object> requestParams, Class<T> returnType, ResponseListener<List<T>> responseListener) {
        get(path, requestParams).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (responseListener != null) {
                    responseListener.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (responseListener != null && response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            responseListener.onResponse(objectMapper.readValue(body.string(), objectMapper.getTypeFactory().constructCollectionType(List.class, returnType)));
                        } else {
                            responseListener.onError(new NullPointerException("Response body is null"));
                        }
                    }
                } catch (Exception e) {
                    response.close();
                    responseListener.onError(e);
                }
            }
        });
    }

    protected <T> void getJsonList(String path, Class<T> returnType, ResponseListener<List<T>> responseListener) {
        getJsonList(path, null, returnType, responseListener);
    }

    protected void getString(String path, Map<String, Object> requestParams, ResponseListener<String> responseListener) {
        get(path, requestParams).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (responseListener != null) {
                    responseListener.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (responseListener != null && response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            responseListener.onResponse(body.string());
                        } else {
                            responseListener.onError(new NullPointerException("Response body is null"));
                        }
                    }
                } catch (Exception e) {
                    response.close();
                    responseListener.onError(e);
                }
            }
        });
    }

    protected void getString(String path, ResponseListener<String> responseListener) {
        getString(path, null, responseListener);
    }

    private Call post(String path, Map<String, Object> requestParams, RequestBody body) {
        HttpUrl.Builder url = HttpUrl
                .parse(BASE_PATH + path)
                .newBuilder();
        if (requestParams != null) {
            for (String key : requestParams.keySet()) {
                url.addEncodedQueryParameter(key, requestParams.get(key).toString());
            }
        }
        if (loggedInUser != null) {
            url.addQueryParameter("api_token", loggedInUser.getApiToken());
        }
        Request request = new Request.Builder()
                .url(url.build())
                .post(body)
                .build();
        return client.newCall(request);
    }

    protected <T> void postJsonObject(String path, Map<String, Object> requestParams, RequestBody requestBody, Class<T> returnType, ResponseListener<T> responseListener) {
        post(path, requestParams, requestBody).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (responseListener != null) {
                    responseListener.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (responseListener != null && response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            responseListener.onResponse(objectMapper.readValue(body.string(), returnType));
                        } else {
                            responseListener.onError(new NullPointerException("Response body is null"));
                        }
                    }
                } catch (Exception e) {
                    response.close();
                    responseListener.onError(e);
                }
            }
        });
    }

    protected <T> void postJsonObject(String path, RequestBody requestBody, Class<T> returnType, ResponseListener<T> responseListener) {
        postJsonObject(path, null, requestBody, returnType, responseListener);
    }

    protected <T> void postJsonList(String path, Map<String, Object> requestParams, RequestBody requestBody, Class<T> returnType, ResponseListener<List<T>> responseListener) {
        post(path, requestParams, requestBody).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (responseListener != null) {
                    responseListener.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (responseListener != null && response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            responseListener.onResponse(objectMapper.readValue(body.string(), objectMapper.getTypeFactory().constructCollectionType(List.class, returnType)));
                        } else {
                            responseListener.onError(new NullPointerException("Response body is null"));
                        }
                    }
                } catch (Exception e) {
                    response.close();
                    responseListener.onError(e);
                }
            }
        });
    }

    protected <T> void postJsonList(String path, RequestBody requestBody, Class<T> returnType, ResponseListener<List<T>> responseListener) {
        postJsonList(path, null, requestBody, returnType, responseListener);
    }

    protected void postString(String path, Map<String, Object> requestParams, RequestBody requestBody, ResponseListener<String> responseListener) {
        post(path, requestParams, requestBody).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (responseListener != null) {
                    responseListener.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (responseListener != null && response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            responseListener.onResponse(body.string());
                        } else {
                            responseListener.onError(new NullPointerException("Response body is null"));
                        }
                    }
                } catch (Exception e) {
                    response.close();
                    responseListener.onError(e);
                }
            }
        });
    }

    protected void postString(String path, RequestBody requestBody, ResponseListener<String> responseListener) {
        postString(path, null, requestBody, responseListener);
    }

    public interface ResponseListener<T> {
        void onResponse(T response);

        void onError(Throwable throwable);
    }
}
