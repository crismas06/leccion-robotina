package ramos.leccion_robotica;

        import android.app.IntentService;
        import android.content.Intent;
        import android.nfc.Tag;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.os.ResultReceiver;
        import android.text.TextUtils;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;


public class Downloader extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static final String TAG = "DownloadService";

    public Downloader() {
        super(Downloader.class.getName());
    }

    public Downloader(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Buscando...");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)){
            //EStatus de inicio de servicio
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);


            try{
                String[] results = downloadData(url);

                //ENviar resultados de consulta
                if (null != results && results.length > 0){
                    bundle.putStringArray("result", results);
                    receiver.send(STATUS_FINISHED, bundle);
                }

            } catch (Exception e){

            }

        }

    }

    private String[] downloadData(String requestUrl) throws IOException, DownloadException {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        /* forming th java.net.URL object */
        URL url = new URL(requestUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        /* optional request header */
        urlConnection.setRequestProperty("Content-Type", "application/json");

        /* optional request header */
        urlConnection.setRequestProperty("Accept", "application/json");

        /* for Get request */
        urlConnection.setRequestMethod("GET");
        int statusCode = urlConnection.getResponseCode();

        /* 200 represents HTTP OK */
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            String[] results = parseResult(response);

            return results;
        } else {
            throw new DownloadException("Failed to fetch data!!");
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

            /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        return result;
    }

    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private String[] parseResult(String result) {

        String[] mvp_list = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts"); //Obtener el listado del Json


            if (posts.length() == 0){
                Log.d("Num", " "+posts.length());
                mvp_list = new String[1];
                mvp_list[0] = "None found";
                return mvp_list;
            }


            mvp_list = new String[posts.length()];

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String mvp_id = post.optString("id"); //Obtener el id
                mvp_list[i] = mvp_id;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mvp_list;
    }
}
